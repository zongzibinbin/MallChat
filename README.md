![](https://s1.ax1x.com/2023/05/04/p9NC50f.png)

MallChat的后端项目，是一个既能购物又能即时聊天的电商系统。致力于打造互联网企业级项目的最佳实践。电商该有的购物车，订单，支付，推荐，搜索，拉新，促活，推送，物流，客服，它都必须有。持续更新ing~~

<p align="center">
  <a href="#公众号"><img src="https://img.shields.io/badge/公众号-程序员阿斌-blue.svg?style=plasticr"></a>
    <a href="#公众号"><img src="https://img.shields.io/badge/交流群-加入开发-green.svg?style=plasticr"></a>
    <a href="https://github.com/zongzibinbin/MallChat"><img src="https://img.shields.io/badge/github-项目地址-yellow.svg?style=plasticr"></a>
    <a href="https://github.com/zongzibinbin/MallChat"><img src="https://img.shields.io/badge/码云-项目地址-orange.svg?style=plasticr"></a>
    <a href="https://github.com/Evansy/MallChatWeb"><img src="https://img.shields.io/badge/前端-项目地址-blueviolet.svg?style=plasticr"></a>
</p>


## 项目导航

1. **快速体验地址**：[抹茶聊天首页](https://mallchat.cn)
2. **前端项目仓库**：[MallChatWeb](https://github.com/Evansy/MallChatWeb)
3. **项目视频记录**：[Bilibili地址](https://space.bilibili.com/146719540) 全程分享项目进度，功能选型的思考，同时征集迭代建议。
4. **项目学习文档**：10w+字，保姆级教学路线，环境搭建、核心功能、基建轮子、接口压测、问题记录、一个不落。可点击[抹茶项目文档](https://www.yuque.com/snab/mallcaht)查看（内含500人交流大群）
5. **项目交流群**：对抹茶感兴趣的，可以加入[交流群](#公众号)。你的每一个举动，都会决定项目未来的方向。无论是提意见做产品经理，还是找bug做个测试人员，又或者加入开发小模块成为contributer，都欢迎你的加入。
6. **码云仓库**：[https://github.com/zongzibinbin/MallChat](https://github.com/zongzibinbin/MallChat) （国内访问速度更快）

## 项目介绍

抹茶聊天是一个IM项目，通过netty实现和前端的websocket连接。内含微信扫描登录，成员列表，上下线动画，消息列表，消息互动，还有很多实用的小轮子列如aop日志，分布式锁注解，频控注解，ip解析归属地等，持续更新中。。。

### 项目演示

#### C端项目

- 前端项目地址：[https://github.com/Evansy/MallChatWeb](https://github.com/Evansy/MallChatWeb)
- 项目演示地址：[https://mallchat.cn](https://mallchat.cn) (记住抹茶.cn，下次工作摸鱼可直接打开)

![p92nKne.png](https://s1.ax1x.com/2023/05/15/p92nKne.png)

### 技术选型

#### 后端技术

|        技术         | 说明                                       | 官网                                                         |
| :-----------------: | ------------------------------------------ | ------------------------------------------------------------ |
|     SpringBoot      | web开发必备框架                            | [https://spring.io/projects/spring-boot](https://spring.io/projects/spring-boot) |
|       MyBatis       | ORM框架                                    | http://www.mybatis.org/mybatis-3/zh/index.html               |
|     MyBatisPlus     | 零sql，简化数据库操作，分页插件            | [https://baomidou.com/](https://baomidou.com/)               |
|        Redis        | 缓存加速，多数据结构支持业务功能           | [https://redis.io](https://redis.io)                         |
|      Caffeine       | 本地缓存                                   | http://caffe.berkeleyvision.org/                             |
|        Nginx        | 负载均衡，https配置，websocket升级，ip频控 | [https://nginx.org](https://nginx.org)                       |
|       Docker        | 应用容器引擎                               | [https://www.docker.com](https://www.docker.com)             |
|         Oss         | 对象存储                                   | [https://letsencrypt.org/](https://letsencrypt.org/)         |
|         Jwt         | 用户登录，认证方案                         | [https://jwt.io](https://jwt.io)                             |
|       Lombok        | 简化代码                                   | [https://projectlombok.org](https://projectlombok.org)       |
|       Hutool        | Java工具类库                               | https://github.com/looly/hutool                              |
|     Swagger-UI      | API文档生成工具                            | https://github.com/swagger-api/swagger-ui                    |
| Hibernate-validator | 接口校验框架                               | [hibernate.org/validator/](hibernate.org/validator/)         |

#### 前端技术

见[MallChatWeb](https://github.com/Evansy/MallChatWeb)

### 环境搭建

在项目目录下的`application.yml`修改自己的启动环境`spring.profiles.active` = `test`然后找到同级文件`application-test.properties`，填写自己的环境配置。[星球成员](https://www.yuque.com/snab/mallcaht)提供一套测试环境配置，可直连

### 项目文档

保姆级教学路线，环境搭建、核心功能、基建轮子、接口压测、问题记录、项目亮点一个不落。点击[项目文档](https://www.yuque.com/snab/mallcaht)

更多有趣功能在持续更新中。。。

[![p92Qa2q.png](https://s1.ax1x.com/2023/05/15/p92Qa2q.png)]

## star 趋势图

[![Stargazers over time](https://starchart.cc/zongzibinbin/MallChat.svg)]

## 公众号

微信搜索 **阿斌Java之路** 关注我的原创公众号，后台回复「**抹茶**」即可加入抹茶交流群，一些做过公司万人群聊，高并发的小伙伴都在里面讨论方案。公众号也会经常更新项目相关的文档，等你来撩~~

[![p9211Ag.png](https://s1.ax1x.com/2023/05/15/p9211Ag.png)]

