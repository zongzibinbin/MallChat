# AGENTS.md

此文件面向在 `MallChat` 仓库中工作的编码代理。
它记录了仓库当前真实可用的构建/测试流程，以及代码中已经形成的风格约定。

## 仓库概览
- 根工程：由 `pom.xml` 驱动的 Maven 多模块项目。
- 主应用模块：`mallchat-chat-server`。
- 共享模块：`mallchat-tools`，包含 `mallchat-common-starter`、`mallchat-frequency-control`、`mallchat-oss-starter`、`mallchat-redis` 和 `mallchat-transaction`。
- Spring Boot 主启动类：`mallchat-chat-server/src/main/java/com/abin/mallchat/common/MallchatCustomApplication.java`。
- 语言目标版本：根 POM 通过 `<java.version>1.8</java.version>` 指定为 Java 8。
- CI 在 `.github/workflows/maven-publish.yml` 中使用 JDK 11 构建；因此源码需保持 Java 8 兼容。
- 主要技术栈：Spring Boot、MyBatis-Plus、Lombok、Hutool、Swagger、Redisson、RocketMQ。

## 指令来源
- 未发现 `.cursorrules` 文件。
- 未发现 `.cursor/rules/` 目录。
- 未发现 `.github/copilot-instructions.md` 文件。
- 将本文件视为当前仓库的权威代理指引。

## 常用命令速查
- 使用系统安装的 Maven；仓库内没有 Maven Wrapper（`mvnw`）。
- 除非你明确只操作某个模块，否则请在仓库根目录执行命令。
- 根 `pom.xml` 中设置了 `<skipTests>true</skipTests>`，因此默认会跳过测试；需要测试时请显式加上 `-DskipTests=false`。

### 构建
- 构建全部模块，默认跳过测试：`mvn clean package`
- 按 CI 方式完整构建全部模块：`mvn -B package --file pom.xml`
- 构建全部模块并强制执行测试：`mvn clean test -DskipTests=false`
- 构建聊天服务及其所需依赖模块：`mvn -pl mallchat-chat-server -am package`
- 对聊天服务做快速编译检查：`mvn -pl mallchat-chat-server -am compile`

### 运行
- 在仓库根目录运行 Spring Boot 应用：`mvn -pl mallchat-chat-server -am spring-boot:run`
- 在 `mallchat-chat-server` 模块内运行应用：`mvn spring-boot:run`
- 如果启动因 profile 失败，优先检查 `mallchat-chat-server/src/main/resources/application.yml`。

### 测试
- 运行整个 reactor 的全部测试：`mvn test -DskipTests=false`
- 只运行应用模块测试：`mvn -pl mallchat-chat-server -am test -DskipTests=false`
- 在仓库根目录运行单个测试类：`mvn -pl mallchat-chat-server -am -DskipTests=false -Dtest=DaoTest test`
- 在仓库根目录运行单个测试方法：`mvn -pl mallchat-chat-server -am -DskipTests=false -Dtest=DaoTest#getUploadUrl test`
- 在 `mallchat-chat-server` 模块内运行单个测试类：`mvn test -DskipTests=false -Dtest=DaoTest`
- 在 `mallchat-chat-server` 模块内运行单个测试方法：`mvn test -DskipTests=false -Dtest=DaoTest#getUploadUrl`
- 现有测试基于 JUnit 4（`org.junit.Test`、`SpringRunner`、`@SpringBootTest`），不是 JUnit 5。
- 很多测试属于集成测试风格，可能依赖 MySQL、Redis、RocketMQ、MinIO 以及微信相关配置。

### Lint / 格式化
- 仓库中没有配置专门的 lint 命令。
- 未发现 Checkstyle、Spotless、PMD 或 `.editorconfig`。
- 实际校验方式通常是：先编译你修改过的模块，再在环境允许时运行针对性的测试。

## 工作方式
- 保持改动尽量小而准；这个仓库包含较多历史代码，格式并不完全统一。
- 优先遵循你正在编辑文件的现有风格，而不是顺手做全仓格式统一。
- 不要引入高于 Java 8 的语言特性。
- 除非改动本意就是更新文案，否则请保留现有中文注释、用户可见文本和报错信息。
- 不要在源码中硬编码密钥、令牌、密码或机器专属地址。

## 项目结构
- 代码主要按业务域组织，而不是按纯技术分层组织。
- 主应用中常见包包括 `common.chat`、`common.user`、`common.common`、`common.chatai` 和 `common.sensitive`。
- 单个业务域中常见放置方式：
- `controller`：REST 接口层。
- `service` 与 `service.impl`：业务接口与实现。
- `service.adapter`、`service.cache`、`service.helper`、`service.strategy`：配套逻辑。
- `dao`：基于 MyBatis-Plus 的数据访问封装。
- `mapper`：Mapper 接口与 SQL 映射。
- `domain.entity`：持久化实体。
- `domain.dto`：内部传输对象。
- `domain.vo.request` 与 `domain.vo.response`：接口请求/响应模型。
- 可复用的横切能力优先放在 `mallchat-tools/*` 中，不要在应用模块重复实现。

## 导入与格式
- 仓库没有严格统一的 import 排序规则。
- 很多文件会大致按“项目类 -> 框架/注解 -> `javax` -> `java`”分组；静态导入通常放最后。
- 新代码优先使用显式导入。
- 如果目标文件本身已经大量使用通配符导入，且你的改动很小，可以保持局部风格一致。
- 使用 4 个空格缩进，左花括号与声明同行。
- 在逻辑块之间保留空行，尤其是较长的 service 方法。
- 避免仅为格式整理而改动未触及的代码或大范围调整 import 顺序。

## Spring 约定
- 使用仓库中已广泛采用的注解，如 `@RestController`、`@Service`、`@Component`、`@Configuration`。
- 依赖注入主流写法是字段注入，使用 `@Autowired` 或 `@Resource`。
- 除非你在做完整重构，否则不要在一个字段注入风格的类中单独引入构造器注入。
- 需要日志的类通常使用 `@Slf4j`。
- 控制器应尽量保持轻量，把业务逻辑放到 service 或 helper 中。

## API 层
- 控制器通常位于 `...controller` 包下，并映射在 `/capi/...` 路径下。
- 控制器返回值使用 `ApiResult<T>`，而不是 `ResponseEntity<T>`。
- 成功响应通常使用 `ApiResult.success()` 或 `ApiResult.success(data)`。
- 在控制器边界使用 `@Valid` 对请求对象进行校验。
- 需要登录态的接口通常通过 `RequestHolder.get().getUid()` 获取当前用户。
- 邻近接口已使用 Swagger 注解（如 `@Api`、`@ApiOperation`）时，新接口应保持一致。

## Service 层
- 业务规则、权限判断、流程编排以及事件发布应放在 service 层。
- 写操作方法通常带有 `@Transactional`。
- 多步骤写操作很多使用 `@Transactional(rollbackFor = Exception.class)`；如果周边代码如此，优先保持一致。
- 副作用处理通常通过 `ApplicationEventPublisher` 与现有监听器模式完成。
- 现有链路已依赖的并发控制与频控注解包括 `@RedissonLock` 和 `@FrequencyControl`。
- 在新增逻辑前，优先复用现有 adapter、cache、helper、strategy 等抽象，而不是新开一套并行实现。

## 持久化
- MyBatis-Plus 是标准持久化方案。
- 实体通常使用 `@TableName`、`@TableId`、`@TableField` 等注解。
- DAO 类通常继承 `ServiceImpl<Mapper, Entity>`。
- 优先使用 `lambdaQuery()`、`lambdaUpdate()`、wrapper 对象以及 `updateById()`，避免手写样板式数据库代码。
- 游标分页优先复用 `CursorUtils` 与 `CursorPageBaseResp`。
- 不要把 SQL 取数逻辑写进 controller。

## DTO、实体与命名
- 后缀命名有明确语义并被广泛使用：`Req`、`Resp`、`DTO`、`Dao`、`Mapper`、`Service`、`ServiceImpl`、`Adapter`、`Cache`、`Helper`、`Enum`。
- 请求与响应类型通常放在 `domain.vo.request` 和 `domain.vo.response`。
- 内部传输对象通常放在 `domain.dto`。
- 持久化模型通常放在 `domain.entity`。
- 数据载体广泛使用 Lombok：`@Data`、`@Builder`、`@NoArgsConstructor`、`@AllArgsConstructor`，有时也会用 `@EqualsAndHashCode`。
- 在 Lombok 使用密集的包中，优先继续使用 Lombok，而不是手写样板代码。
- 包名小写，类名使用 UpperCamelCase，方法和字段使用 lowerCamelCase，常量使用 `UPPER_SNAKE_CASE`。
- 枚举类型通常以 `Enum` 结尾，且常带有 `of(...)` 之类的查找方法。
- ID 通常使用 `Long`，常见字段名包括 `uid`、`roomId`、`msgId`、`applyId`、`targetUid`。
- 方法命名偏向动词开头，如 `get*`、`list*`、`page*`、`save*`、`remove*`、`check*`、`build*`、`send*`、`recall*`。
- 布尔值命名应清晰明确，如 `isLast`、`hasPower`。

## 校验与异常处理
- 请求对象优先使用 Bean Validation 注解，例如 `@NotNull`、`@NotBlank`。
- service 和 helper 内部的业务断言优先使用本地 `AssertUtil`。
- `AssertUtil` 会抛出 `BusinessException`；不要到处散落手写业务错误判断。
- 异常响应转换交给 `GlobalExceptionHandler`，由其统一包装为 `ApiResult.fail(...)`。
- 比起泛化的 `Exception`，优先使用领域内已有异常，如 `BusinessException`、`FrequencyControlException`。
- 控制器不要返回依赖 `null` 含义的错误结果。

## 日志
- 优先使用 `log.info`、`log.warn`、`log.error`。
- 当有助于排查问题时，请带上 `uid`、`roomId`、记录 ID 等业务上下文。
- 严禁记录密钥、JWT、密码或第三方平台凭证。
- 仓库里已有历史遗留的 `System.out.println`；但不要在生产代码中继续新增。

## 测试与验证
- 现有测试风格为 JUnit 4 + `SpringRunner` + `@SpringBootTest`。
- 除非环境已经完全就绪，否则测试应尽量小范围、定向执行。
- 很多测试依赖 MySQL、Redis、RocketMQ、MinIO 和微信配置。
- 如果无法完整跑通测试，至少编译被修改模块，并明确说明还有哪些部分未验证。
- 若你新增测试，除非在做大范围迁移，否则应保持与现有测试风格一致。

## 配置与密钥
- 运行期配置位于 `application.yml` 和 `application-*.properties` 中。
- README 提到使用 `test` profile，但当前 `application.yml` 指向的是 `my-test`；运行前请核对 profile 是否真实可用。
- profile 相关值应放在配置文件中，而不是写死在 Java 常量里。
- 对仓库中的配置值保持谨慎，视为敏感占位信息，而不是真实可公开凭据。
- 永远不要提交真实密钥。

## 推荐改动策略
- 优先沿用现有抽象和现有实现路径。
- 横切能力优先复用 `mallchat-tools`。
- 业务特有行为保留在 `mallchat-chat-server` 中。
- 尽量减少无关清理。
- 如果你有意偏离当前局部风格，请在最终说明中解释原因。
