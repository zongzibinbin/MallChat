# 用户名密码登录改造计划

## 目标

- 登录方式从“微信扫码登录”切换为“用户名 + 密码登录”。
- 新增“用户名 + 密码注册”能力。
- 保留现有 `JWT + Redis + WebSocket` 的会话和在线状态机制。
- 只移除“登录链路里的微信扫码依赖”，不默认删除其他与微信相关但非登录的能力。

## 当前现状

### 后端现有登录链路

当前仓库里，拿到登录态 `token` 的入口是微信扫码，不是 HTTP 账号密码接口：

1. 前端通过 WebSocket 发送 `LOGIN` 请求。
2. `NettyWebSocketServerHandler` 将请求分发到 `WebSocketServiceImpl.handleLoginReq()`。
3. `WebSocketServiceImpl` 调微信接口生成二维码并通过 `WSAdapter.buildLoginResp()` 返回给前端。
4. 用户扫码后，经 `WxMsgService`、`WxPortalController`、MQ 消费者完成授权和登录成功通知。
5. 最终仍然是 `LoginServiceImpl.login(uid)` 生成并返回 `token`。

### 现有可复用能力

- `LoginServiceImpl` 已经具备通用的 `token` 生成、校验、续期能力。
- `TokenInterceptor` 已经支持基于 `Bearer token` 的 HTTP 鉴权。
- WebSocket 握手时也支持从连接参数中读取 `token` 并自动鉴权。

这意味着：

- 现在缺的不是会话机制。
- 缺的是“用户名密码登录 / 注册”这套入口、数据模型和前端页面。

### 数据层现状

当前 `user` 表只有 `open_id`，没有账号密码字段：

- `name`：展示昵称，不适合直接作为登录账号。
- `open_id`：当前登录体系依赖的微信标识。
- 没有 `username`、`password_hash` 之类的字段。

另外，当前仓库没有前端源码；`README.md` 指向独立前端仓库 `MallChatWeb`。所以这次改造一定会分成：

- 当前仓库：后端与数据库改造。
- 前端仓库：登录页 / 注册页 / 调用链路改造。

## 关键设计决定

### 1. 登录账号不要复用 `user.name`

建议新增独立登录账号字段，例如：

- `username`
- `password_hash`

不要直接复用 `user.name`，原因是：

- `name` 是展示昵称。
- 当前系统已经有“改名卡 + 修改用户名”逻辑，说明 `name` 是业务昵称，不是稳定登录账号。

### 2. 密码必须存哈希，不存明文

建议使用 `BCrypt` 存储密码哈希，例如：

- 数据库存 `password_hash`
- 后端登录时 `matches(rawPassword, passwordHash)`

### 3. 注册时的昵称策略要提前定

推荐两种方案，优先建议第一种：

1. 注册时同时提交 `username + password + nickname`
2. 只提交 `username + password`，后端生成默认昵称，后续用户再走现有改名能力修改

如果你想最少改前端字段，可以先做第 2 种。

### 4. 历史微信账号如何迁移必须先确认

这是这次改造里最大的业务风险。

如果历史用户还要继续使用原账号，有两种策略：

1. 先保留一段时间微信登录，并增加“绑定用户名密码”能力，再下线扫码登录。
2. 为老用户补一套迁移方案（例如后台发临时密码、强制首次改密等）。

如果你当前只是本地项目、自测项目、没有历史数据压力，可以直接切到账号密码体系。

## 改造计划

### 第一阶段：数据库与实体改造

目标：先让数据模型支持账号密码。

计划项：

1. 调整 `user` 表结构。
2. 新增账号密码字段。
3. 处理 `open_id` 字段从“登录必需”改为“可选扩展信息”。

建议字段：

- `username varchar(32)`：登录账号，唯一索引。
- `password_hash varchar(100)`：密码哈希。
- 可选 `register_type` / `auth_type`：标记注册来源，便于后续扩展。

建议 SQL 处理：

- 在 `docs/version/` 下新增一份增量 SQL。
- 同步更新 `docs/mallchat.sql` 的全量表结构。
- 将 `open_id` 调整为允许为空，否则纯账号体系用户无法落库。

后端同步修改点：

- `common.user.domain.entity.User`
- `common.user.dao.UserDao`

新增 DAO 能力建议包括：

- `getByUsername(String username)`
- 用户名唯一性检查

### 第二阶段：新增账号密码注册接口

目标：提供公开注册能力。

建议新增公开接口：

- `POST /capi/user/public/auth/register`

建议请求字段：

- `username`
- `password`
- 可选 `nickname`

后端处理流程：

1. 校验用户名格式、长度、唯一性。
2. 校验密码强度。
3. 生成密码哈希。
4. 创建 `User`。
5. 沿用现有 `userService.register(user)`，复用已有注册事件和背包初始化逻辑。
6. 返回注册成功结果；可选“注册成功后直接登录”。

这里建议复用现有注册事件，因为当前 `UserRegisterEvent` 已经在注册后发放初始道具。

### 第三阶段：新增账号密码登录接口

目标：让用户通过 HTTP 直接拿到 `token`。

建议新增公开接口：

- `POST /capi/user/public/auth/login`

建议响应内容：

- `token`
- `uid`
- `name`
- `avatar`
- `power`

响应内容可以尽量向现有 `WSLoginSuccess` 靠拢，这样前端适配成本更低。

后端处理流程：

1. 根据 `username` 查询用户。
2. 校验密码哈希。
3. 调用 `loginService.login(uid)` 生成或复用现有 `token`。
4. 返回用户基础信息和 `token`。

建议新增内容：

- `LoginReq`
- `RegisterReq`
- `LoginResp`
- `AuthController` 或同类认证控制器
- 认证 service / helper

校验与报错风格建议沿用现有项目约定：

- 控制器入参用 Bean Validation
- 业务断言用 `AssertUtil`
- 返回值继续使用 `ApiResult<T>`

### 第四阶段：去掉微信扫码登录链路

目标：删除或下线“扫码拿 token”的旧入口。

当前登录相关的微信链路主要集中在：

- `WebSocketServiceImpl.handleLoginReq()`
- `WxMsgService`
- `WxPortalController`
- `MsgLoginConsumer`
- `ScanSuccessConsumer`
- `MQConstant.LOGIN_MSG_TOPIC`
- `MQConstant.SCAN_MSG_TOPIC`
- `RedisKey.OPEN_ID_STRING`
- `WSReqTypeEnum.LOGIN`
- `WSRespTypeEnum.LOGIN_URL`
- `WSRespTypeEnum.LOGIN_SCAN_SUCCESS`

处理建议：

1. 前端先切到 HTTP 登录。
2. WebSocket 连接改为登录成功后，带 `token` 建链。
3. 后端再移除二维码生成、扫码成功通知、授权回调这整条链路。

注意：

- `LoginServiceImpl` 本身不用删，它是通用登录态服务。
- WebSocket 的 `token` 鉴权能力也不用删。
- 不建议一上来就删除整个微信配置，因为仓库里还有非登录用途的微信代码，例如消息模板相关能力。

### 第五阶段：前端仓库改造

目标：把登录页从扫码页改成账号页。

由于当前仓库没有前端源码，这部分需要在外部前端仓库 `MallChatWeb` 中完成。

建议改造内容：

1. 移除微信二维码登录 UI。
2. 增加用户名密码登录表单。
3. 增加注册表单。
4. 对接新接口：
   - `POST /capi/user/public/auth/login`
   - `POST /capi/user/public/auth/register`
5. 登录成功后保存 `token`。
6. 使用 `token` 重新建立 WebSocket 连接。
7. 删除扫码中、已扫码待授权、二维码过期等前端状态。

### 第六阶段：联调与验证

目标：确认从注册到登录再到 WebSocket 在线链路全部可用。

建议验证项：

1. 新用户注册成功。
2. 重复用户名注册被正确拦截。
3. 正确密码可以登录并拿到 `token`。
4. 错误密码登录失败。
5. 登录后访问受保护的 `/capi/**` 接口正常。
6. 登录后建立 WebSocket 连接，能正常完成在线态鉴权。
7. 旧二维码登录入口已不可用或已从页面移除。

建议至少执行：

- `mvn -pl mallchat-chat-server -am compile`

如果本地依赖环境齐全，再补充：

- `mvn -pl mallchat-chat-server -am test -DskipTests=false`

## 建议实施顺序

1. 先改库表和 `User` 实体。
2. 再补 `UserDao` 查询能力。
3. 再做注册接口。
4. 再做登录接口。
5. 前端切到账号密码登录。
6. 联调 WebSocket 带 token 建链。
7. 最后清理微信扫码登录代码和配置。

## 预计会动到的文件

### 当前仓库

- `docs/mallchat.sql`
- `docs/version/*.sql`
- `mallchat-chat-server/src/main/java/com/abin/mallchat/common/user/domain/entity/User.java`
- `mallchat-chat-server/src/main/java/com/abin/mallchat/common/user/dao/UserDao.java`
- `mallchat-chat-server/src/main/java/com/abin/mallchat/common/user/service/LoginService.java`
- `mallchat-chat-server/src/main/java/com/abin/mallchat/common/user/service/impl/LoginServiceImpl.java`
- `mallchat-chat-server/src/main/java/com/abin/mallchat/common/user/controller/*`
- `mallchat-chat-server/src/main/java/com/abin/mallchat/common/user/domain/vo/request/*`
- `mallchat-chat-server/src/main/java/com/abin/mallchat/common/user/domain/vo/response/*`
- `mallchat-chat-server/src/main/java/com/abin/mallchat/common/user/service/impl/WebSocketServiceImpl.java`
- `mallchat-chat-server/src/main/java/com/abin/mallchat/common/websocket/NettyWebSocketServerHandler.java`
- 与微信扫码登录直接相关的 controller、service、consumer、常量类

### 前端仓库

- 登录页
- 注册页
- 认证 API 封装
- `token` 持久化逻辑
- WebSocket 建链逻辑

## 风险与注意事项

1. 历史微信用户迁移是最大风险，必须先明确要不要保留老账号可登录。
2. `name` 是昵称，不建议兼作登录账号。
3. 密码只能存哈希，不能存明文。
4. 如果你只想“去掉扫码登录”，不代表要删除全部微信能力，避免误伤其他功能。
5. 前端不在当前仓库，后端改完后仍需要前端仓库配合才能真正上线。

## 推荐结论

推荐按下面的最小可行路径落地：

1. `user` 表新增 `username` 和 `password_hash`，并让 `open_id` 可为空。
2. 后端新增公开的注册 / 登录 HTTP 接口。
3. 前端改成用户名密码登录页，并在登录成功后用 `token` 建立 WebSocket 连接。
4. 等新链路稳定后，再删除微信扫码登录代码。

这样改动最稳，也最符合当前项目已经存在的 `token` 和 WebSocket 结构。
