## 《分布式单点登录框架XXL-SSO》

[![Actions Status](https://github.com/xuxueli/xxl-sso/workflows/Java%20CI/badge.svg)](https://github.com/xuxueli/xxl-sso/actions)
[![Maven Central](https://img.shields.io/maven-central/v/com.xuxueli/xxl-sso-core)](https://central.sonatype.com/artifact/com.xuxueli/xxl-sso-core/)
[![GitHub release](https://img.shields.io/github/release/xuxueli/xxl-sso.svg)](https://github.com/xuxueli/xxl-sso/releases)
[![GitHub stars](https://img.shields.io/github/stars/xuxueli/xxl-sso)](https://github.com/xuxueli/xxl-sso/)
[![License](https://img.shields.io/badge/license-GPLv3-blue.svg)](http://www.gnu.org/licenses/gpl-3.0.html)
[![donate](https://img.shields.io/badge/%24-donate-ff69b4.svg?style=flat-square)](https://www.xuxueli.com/page/donate.html)

[TOCM]

[TOC]

## 一、简介

### 1.1 概述
XXL-SSO 是 单点登录框架，只需要登录一次就可以访问所有相互信任的应用系统。具备 “轻量级、高扩展、渐进式” 的等特性，支持 “登录认证、权限(角色)认证、分布式会话认证、单点登录、Web登录、Native(前后端分离)登录” 等多登录及认证类型，现已开放源代码，开箱即用。

### 1.2 特性

- 1、易用性：支持注解/API多方式接入，一行注解/代码即可实现 登录认证、权限认证、角色认证 等，接入灵活方便；
- 2、轻量级：针对第三方组件、部署环境零依赖，部署及接入低成本、轻量级；
- 3、高扩展：得益于模块化抽象设计，各框架组件可灵活扩展；可选用官方提供组件实现或自定义扩展。
  - 登录态持久化组价（LoginStore）：提供登录态/会话数据持久化能力；官方提供Cache、Redis等组件实现，可选用接入或自定义扩展；
  - 登录认证组件（Auth）：提供系统登录/认证集成能力；官方提供 Filter（Servlet）和Interceptor（Spring）等实现，可选用接入或自定义扩展；
  - 登录用户模型（LoginInfo）：提供统一登录用户模型，且模型支持扩展存储自定义扩展属性；
- 4、渐进式：支持渐进式集成接入使用，从简单到复杂场景，包括：单体Web系统、复杂企业内多系统、互联网多端高并发系统 等，均可接入使用；
- 5、多登录类型：
  - Web登录：适用于常规Web系统，不限制接入系统数量；但是限制相关Web系统部署在相同域名下，登录凭证存储在公共域名下；
  - Native登录：适用于 移动端、小程序、前后端分离系统 等系统，不限制接入系统数量，且无域名限制，支持多端登录；但是登录凭证需要客户端管理维护；
  - CAS单点登录：适用于多Web系统部署域名不一致场景，解决了系统 跨域登录认证 问题；但是需要单独部署CAS认证中心，CAS认证中心提供单点登录基础能力；
- 6、多认证方式：
  - 登录认证：本质为验证用户身份的过程，目的是确认“你是谁”，确保访问者合法可信；
  - 权限认证：在用户身份认证通过后，校验用户是否具备访问特定资源的权限，决定“你能做什么”；认证维度是“权限”；
  - 角色认证：在用户身份认证通过后，校验用户是否具备访问特定资源的权限，决定“你能做什么”；认证维度是“角色”；
- 7、安全性：针对系统框架多个模块落地安全性设计，包括：登录Token安全设计、客户端登录凭证Cookie安全设计、CAS跳转Ticket安全设计 等；
- 8、分布式会话/认证：支持分布式登录以及会话认证，集成分布式系统可共享的 登录态持久化组价（LoginStore），可选用或参考官方RedisLoginStore；
- 9、单点登录/注销：针对CAS单点登录场景，提供单点登录及注销能力；
- 10、高可用/HA：针对CAS单点登录场景，CAS认证中心支持集群部署，并可借助LoginStore实现登录态共享，从而实现系统水平扩展以及高可用；
- 11、跨域登录认证：针对CAS单点登录场景，支持跨域Web应用接入，解决了系统 跨域登录认证 问题；
- 12、多端登录认证：针对多端登录场景，如 Web、移动端、小程序 等多端，提供多端登录及认证能力；
- 13、前后端分离认证：针对前后端分离系统，提供 Native登录 方案，支持前后端分离场景登录认证能力；
- 14、记住密码：支持记住密码功能；记住密码时，支持登录态自动延期；未记住密码时，关闭浏览器则登录态失效；
- 15、登录态自动延期：支持自定义登录态有效期窗口，当登录态有效期窗口过半时自动顺延一个周期；

### 1.3 发展  
于2018年初，我在github上创建XXL-SSO项目仓库并提交第一个commit，随之进行系统结构设计，UI选型，交互设计……

于2018-12-05，XXL-SSO参与"[2018年度最受欢迎中国开源软件](https://www.oschina.net/project/top_cn_2018?sort=1)"评比，在当时已录入的一万多个国产开源项目中角逐，最终排名第55名。

于2019-01-23，XXL-SSO被评选上榜"[2018年度新增开源软件排行榜之国产 TOP 50](https://www.oschina.net/news/103857/2018-osc-new-opensource-software-cn-top50)"评比，排名第8名。

至今，XXL-SSO已接入多家公司的线上产品线，接入场景如电商业务，O2O业务和核心中间件配置动态化等，截止2018-03-15为止，XXL-SSO已接入的公司包括不限于：

    1. 湖南创发科技
    2. 深圳龙华科技有限公司
    3. 摩根国际
    4. 印记云
    5、小太阳CRM
    6、盛歌行科技（深圳）有限公司
    7、苏州安软

> 更多接入的公司，欢迎在 [登记地址](https://github.com/xuxueli/xxl-sso/issues/1 ) 登记，登记仅仅为了产品推广。

欢迎大家的关注和使用，XXL-SSO也将拥抱变化，持续发展。

### 1.4 下载

#### 文档地址   
- [中文文档](https://www.xuxueli.com/xxl-sso/)

#### 源码仓库地址

源码仓库地址 | Release Download
--- | ---
[https://github.com/xuxueli/xxl-sso](https://github.com/xuxueli/xxl-sso) | [Download](https://github.com/xuxueli/xxl-sso/releases)
[https://gitee.com/xuxueli0323/xxl-sso](https://gitee.com/xuxueli0323/xxl-sso) | [Download](https://gitee.com/xuxueli0323/xxl-sso/releases)  
[https://gitcode.com/xuxueli/xxl-sso](https://gitcode.com/xuxueli/xxl-sso) | [Download](https://gitcode.com/xuxueli/xxl-sso/releases)

#### 中央仓库地址

```
<!-- https://mvnrepository.com/artifact/com.xuxueli/xxl-sso-core -->
<dependency>
    <groupId>com.xuxueli</groupId>
    <artifactId>xxl-sso-core</artifactId>
    <version>{最新Release版本}</version>
</dependency>
```

#### 技术交流
- [社区交流](https://www.xuxueli.com/page/community.html)

### 1.5 环境

- Maven：3.9+
- Jdk：8+
- Redis：7.4+


## 二、快速入门

### 2.1、项目编辑    
XXL-SSO 作为单点登录框架，支持业务渐进式集成接入使用。结合系统及业务特征差异，仓库代码提供三种业务中接入示例：

- 1、**Web登录**：适用于常规Web系统，不限制接入系统数量；但是限制相关Web系统部署在相同域名下，登录凭证存储在公共域名下；
- 2、**Native登录**：适用于 移动端、小程序、前后端分离系统 等系统，不限制接入系统数量，且无域名限制，支持多端登录；但是登录凭证需要客户端管理维护；
- 3、**CAS单点登录**：适用于多Web系统部署域名不一致场景，解决了系统 跨域登录认证 问题；但是需要单独部署CAS认证中心，CAS认证中心提供单点登录基础能力；

```
- xxl-sso-core: 客户端 核心依赖, 提供登录态持久化、登录认证及权限认证等能力；
- xxl-sso-server: CAS认证中心，仅 “CAS单点登录” 场景下才会使用；
- xxl-sso-samples：接入示例项目
    - xxl-sso-sample-web: Web登录方式，Interceptor（Spring）接入示例；
    - xxl-sso-sample-native: Native登录，Interceptor（Spring）接入示例；
    - xxl-sso-sample-cas: CAS单点登录，Interceptor（Spring）接入示例；
    - filter：
        - xxl-sso-sample-filter-web: Web登录方式，Filter（Servlet）接入示例；
        - xxl-sso-sample-filter-native: Native登录，Filter（Servlet）接入示例；
        - xxl-sso-sample-filter-cas: CAS单点登录，Filter（Servlet）接入示例；
```

### 2.2、环境配置    
为模拟并体验生产环境多种登录认证方式，需要修改Host文件，以域名方式访问示例项目：

```
### 编辑Host 文件，在文件中添加以下内容：
127.0.0.1 xxlssoserver.com
127.0.0.1 xxlssoclient1.com
127.0.0.1 xxlssoclient2.com
```

### 2.3、接入项目示例（Web登录）

- Web登录：适用于常规Web系统，不限制接入系统数量；但是限制相关Web系统部署在相同域名下，登录凭证存储在公共域名下；
- 项目名称：xxl-sso-sample-web

#### 第一步、引入Maven依赖
参考章节 “1.4 下载”，pom引入 “xxl-sso-core” 相关maven依赖；

#### 第二步、修改配置文件
```
### xxl-sso 登录凭证/token传输key, 用于cookie、header登录凭证传输；
xxl-sso.token.key=xxl_sso_token
### xxl-sso 登录凭证/token超时时间，单位毫秒；
xxl-sso.token.timeout=604800000
### xxl-sso 登录态持久化配置，如下为Redis组件相关配置；
xxl-sso.store.redis.nodes=127.0.0.1:6379
xxl-sso.store.redis.user=
xxl-sso.store.redis.password=
### xxl-sso 登录态存储，Redis key前缀
xxl-sso.store.redis.keyprefix=xxl_sso_user:
### xxl-sso 客户端过滤排除路径，如 "/excluded/xpath"?"/excluded/xpath,/excluded/*"
xxl-sso.client.excluded.paths=/weblogin/*,/static/**
### xxl-sso 客户端登录页路径
xxl.sso.client.login.path=/weblogin/login
```

#### 第三步、修改配置文件

```
/**
 * 1、配置 XxlSsoBootstrap
 */
@Bean(initMethod = "start", destroyMethod = "stop")
public XxlSsoBootstrap xxlSsoBootstrap() {

    XxlSsoBootstrap bootstrap = new XxlSsoBootstrap();
    bootstrap.setLoginStore(new RedisLoginStore(
            redisNodes,
            redisUser,
            redisPassword,
            redisKeyprefix));
    bootstrap.setTokenKey(tokenKey);
    bootstrap.setTokenTimeout(tokenTimeout);
    return bootstrap;
}

/**
 * 2、配置 XxlSso 拦截器
 *
 * @param registry
 */
@Override
public void addInterceptors(InterceptorRegistry registry) {

    // 2.1、build xxl-sso interceptor
    XxlSsoWebInterceptor webInterceptor = new XxlSsoWebInterceptor(excludedPaths, loginPath);

    // 2.2、add interceptor
    registry.addInterceptor(webInterceptor).addPathPatterns("/**");
}
```

#### 第四步、验证

- 启动项目："xxl-sso-sample-web"
- 访问地址：http://xxlssoclient1.com:8083/xxl-sso-sample-web/


### 2.4、接入项目示例（Native登录）

### 2.5、接入项目示例（CAS单点登录）



## 二、快速入门（基于Cookie）
> 基于Cookie，相关概念可参考 "章节 4.6"
### 2.2 部署 "认证中心（SSO Server）"
```
项目名：xxl-sso-server
```
#### 配置说明
配置文件位置：application.properties
```
……
// redis 地址： 如 "{ip}"、"{ip}:{port}"、"{redis/rediss}://xxl-sso:{password}@{ip}:{port:6379}/{db}"；多地址逗号分隔
xxl.sso.redis.address=redis://127.0.0.1:6379

// 登录态有效期窗口，默认24H，当登录态有效期窗口过半时，自动顺延一个周期
xxl.sso.redis.expire.minute=1440
```
### 2.3 部署 "单点登陆Client端接入示例项目"
```
项目名：xxl-sso-web-sample-springboot
```
#### 配置 XxlSsoFilter
参考代码：com.xxl.sso.sample.config.XxlSsoConfig
```
@Bean
public FilterRegistrationBean xxlSsoFilterRegistration() {

    // xxl-sso, redis init
    JedisUtil.init(xxlSsoRedisAddress);

    // xxl-sso, filter init
    FilterRegistrationBean registration = new FilterRegistrationBean();

    registration.setName("XxlSsoWebFilter");
    registration.setOrder(1);
    registration.addUrlPatterns("/*");
    registration.setFilter(new XxlSsoWebFilter());
    registration.addInitParameter(Conf.SSO_SERVER, xxlSsoServer);
    registration.addInitParameter(Conf.SSO_LOGOUT_PATH, xxlSsoLogoutPath);

    return registration;
}
```
#### 配置说明

配置文件位置：application.properties
```
……

### xxl-sso     (CLient端SSO配置)

##### SSO Server认证中心地址（推荐以域名方式配置认证中心，本机可参考章节"2.5"修改host文件配置域名指向）
xxl.sso.server=http://xxlssoserver.com:8080/xxl-sso-server

##### 注销登陆path，值为Client端应用的相对路径
xxl.sso.logout.path=/logout

##### 路径排除Path，允许设置多个，且支持Ant表达式。用于排除SSO客户端不需要过滤的路径
xxl-sso.excluded.paths=

### redis   // redis address, like "{ip}"、"{ip}:{port}"、"{redis/rediss}://xxl-sso:{password}@{ip}:{port:6379}/{db}"；Multiple "," separated
xxl.sso.redis.address=redis://xxl-sso:password@127.0.0.1:6379/0
```

### 2.4 验证

- 修改Host文件：域名方式访问认证中心，模拟跨域与线上真实环境

- 分别运行 "xxl-sso-server" 与 "xxl-sso-web-sample-springboot"


    1、SSO认证中心地址：
    http://xxlssoserver.com:8080/xxl-sso-server
    
    2、Client01应用地址：
    http://xxlssoclient1.com:8081/xxl-sso-web-sample-springboot/
    
    3、Client02应用地址：
    http://xxlssoclient2.com:8081/xxl-sso-web-sample-springboot/


- SSO登录/注销流程验证


    正常情况下，登录流程如下：
    1、访问 "Client01应用地址" ，将会自动 redirect 到 "SSO认证中心地址" 登录界面
    2、成功登录后，将会自动 redirect 返回到 "Client01应用地址"，并切换为已登录状态
    3、此时，访问 "Client02应用地址"，不需登陆将会自动切换为已登录状态
    
    正常情况下，注销流程如下：
    1、访问 "Client01应用地址" 配置的 "注销登陆path"，将会自动 redirect 到 "SSO认证中心地址" 并自动注销登陆状态
    2、此时，访问 "Client02应用地址"，也将会自动注销登陆状态



## 三、快速入门（基于Token）

> 基于Token，相关概念可参考 "章节 4.7"；（在一些无法使用Cookie的场景下，可使用该方式，否则可以忽略本章节）

### 3.1 "认证中心（SSO Server）" 搭建
> 可参考 "章节二" 搭建

"认证中心" 搭建成功后，默认为Token方式登陆提供API接口如下：

- 1、登陆接口：/app/login
    - 参数：POST参数
        - username：账号
        - password：账号
    - 响应：JSON格式
        - code：200 表示成功、其他失败
        - msg：错误提示
        - data: 登陆用户的 sso sessionid

- 2、注销接口：/app/logout
    - 参数：POST参数
        - sessionId：登陆用户的 sso sessionid
    - 响应：JSON格式
        - code：200 表示成功、其他失败
        - msg：错误提示

- 3、登陆状态校验接口：/app/logincheck
    - 参数：POST参数
        - sessionId：登陆用户的 sso sessionid
    - 响应：JSON格式
        - code：200 表示成功、其他失败
        - msg：错误提示
        - data：登陆用户信息
            - userid：用户ID
            - username：用户名

### 3.2 部署 "单点登陆Client端接入示例项目" (Token方式)

```
项目名：xxl-sso-token-sample-springboot
```

> 可参考 "章节 2.4" 部署 "单点登录Client端接入示例项目"，唯一不同点是：将web应用的 "XxlSsoFilter" 更换为app应用 "XxlSsoTokenFilter"

### 3.3 验证  (模拟请求 Token 方式接入SSO的接口)

- 修改Host文件：域名方式访问认证中心，模拟跨域与线上真实环境
```
### 在host文件中添加以下内容0
127.0.0.1 xxlssoserver.com
127.0.0.1 xxlssoclient1.com
127.0.0.1 xxlssoclient2.com
```

- 分别运行 "xxl-sso-server" 与 "xxl-sso-token-sample-springboot"


    1、SSO认证中心地址：
    http://xxlssoserver.com:8080/xxl-sso-server
    
    2、Client01应用地址：
    http://xxlssoclient1.com:8082/xxl-sso-token-sample-springboot/
    
    3、Client02应用地址：
    http://xxlssoclient2.com:8082/xxl-sso-token-sample-springboot/


- SSO登录/注销流程验证
> 可参考测试用例 ：com.xxl.app.sample.test.TokenClientTest


    正常情况下，登录流程如下：
    1、获取用户输入的账号密码后，请求SSO Server的登录接口，获取用户 sso sessionid ；（参考代码：TokenClientTest.loginTest）
    2、登陆成功后，获取到 sso sessionid ，需要主动存储，后续请求时需要设置在 Header参数 中
    3、此时，使用 sso sessionid 访问受保护的 "Client01应用" 和 "Client02应用" 提供的接口，接口均正常返回（参考代码：TokenClientTest.clientApiRequestTest）
    
    正常情况下，注销流程如下：
    1、请求SSO Server的注销接口，注销登陆凭证 sso sessionid ；（参考代码：TokenClientTest.logoutTest）
    2、注销成功后，sso sessionid 将会全局失效
    3、此时，使用 sso sessionid 访问受保护的 "Client01应用" 和 "Client02应用" 提供的接口，接口请求将会被拦截，提示未登录并返回状态码 501（参考代码：TokenClientTest.clientApiRequestTest）


## 四、总体设计

### 4.1 架构图

![输入图片说明](https://www.xuxueli.com/doc/static/xxl-sso/images/img_01.png "在这里输入图片标题")

### 4.2 功能定位

XXL-SSO 是一个分布式单点登录框架。只需要登录一次就可以访问所有相互信任的应用系统。

借助 XXL-SSO，可以快速实现分布式系统单点登录。

### 4.3 核心概念

概念 | 说明
--- | ---
SSO Server | 中央认证服务，支持集群
SSO Client | 接入SSO认证中心的Client应用
SSO SessionId | 登录用户会话ID，SSO 登录成功为用户自动分配
SSO User | 登录用户信息，与 SSO SessionId 相对应

### 4.4 登录流程剖析

- 用户于Client端应用访问受限资源时，将会自动 redirect 到 SSO Server 进入统一登录界面
- 用户登录成功之后将会为用户分配 SSO SessionId 并 redirect 返回来源Client端应用，同时附带分配的 SSO SessionId
- 在Client端的SSO Filter里验证 SSO SessionId 无误，将 SSO SessionId 写入到用户浏览器Client端域名下 cookie 中
- SSO Filter验证 SSO SessionId 通过，受限资源请求放行

### 4.5 注销流程剖析

- 用户与Client端应用请求注销Path时，将会 redirect 到 SSO Server 自动销毁全局 SSO SessionId，实现全局销毁
- 然后，访问接入SSO保护的任意Client端应用时，SSO Filter 均会拦截请求并 redirect 到 SSO Server 的统一登录界面

### 4.6 基于Cookie，相关概念

- 登录凭证存储：登录成功后，用户登录凭证被自动存储在浏览器Cookie中
- Client端校验登录状态：通过校验请求Cookie中的是否包含用户登录凭证判断
- 系统角色模型：
    - SSO Server：认证中心，提供用户登录、注销以及登录状态校验等功能
    - Client应用：受SSO保护的Client端Web应用，为用户浏览器访问提供服务
    - 用户：发起请求的用户，使用浏览器访问

    
### 4.7 基于Token，相关概念

- 登录凭证存储：登录成功后，获取到登录凭证（xxl_sso_sessionid=xxx），需要主动存储，如存储在 localStorage、Sqlite 中
- Client端校验登录状态：通过校验请求 Header参数 中的是否包含用户登录凭证（xxl_sso_sessionid=xxx）判断；因此，发送请求时需要在 Header参数 中设置登陆凭证
- 系统角色模型：
    - SSO Server：认证中心，提供用户登录、注销以及登录状态校验等功能
    - Client应用：受SSO保护的Client端Web应用，为用户请求提供接口服务
    - 用户：发起请求的用户，如使用Android、IOS、桌面客户端等请求访问
        
### 4.8 未登录状态请求处理

基于Cookie，未登录状态请求：
- 页面请求：redirect 到SSO Server登录界面
- JSON请求：返回未登录的JSON格式响应数据
    - 数据格式：
        - code：501 错误码
        - msg：sso not login.
        
基于Token，未登录状态请求：
- 返回未登录的JSON格式响应数据
    - 数据格式：
        - code：501 错误码
        - msg：sso not login.
        

### 4.9 登录态自动延期       
支持自定义登录态有效期窗口，默认24H，当登录态有效期窗口过半时，自动顺延一个周期。

### 4.10 记住密码         
未记住密码时，关闭浏览器则登录态失效；记住密码时，登录态自动延期，在自定义延期时间的基础上，原则上可以无限延期。

### 4.11 路径排除   
自定义路径排除Path，允许设置多个，且支持Ant表达式。用于排除SSO客户端不需要过滤的路径。


## 五、版本更新日志

### 5.1 版本 v0.1.0，新特性[2018-04-04]
- 1、简洁：API直观简洁，可快速上手
- 2、轻量级：环境依赖小，部署与接入成本较低
- 3、单点登录：只需要登录一次就可以访问所有相互信任的应用系统
- 4、分布式：接入SSO认证中心的应用，支持分布式部署
- 5、HA：Server端与Client端，均支持集群部署，提高系统可用性
- 6、实时性：系统登陆、注销状态，全部Server与Client端实时共享
- 7、CS结构：基于CS结构，包括Server"认证中心"与Client"受保护应用"
- 8、跨域：支持跨域应用接入SSO认证中心

### 5.2 版本 v1.1.0 Release Notes [2018-11-06]
- 1、 Redis配置方式增强，支持自定义DB、密码、IP、PORT等等
- 2、 Token接入方式；除了常规Cookie方式外，新增Token接入方式，并提供Sample项目
- 3、 登录态自动延期：支持自定义登录态有效期窗口，默认24H，当登录态有效期窗口过半时，自动顺延一个周期
- 4、 "记住密码" 功能优化：未记住密码时，关闭浏览器则登录态失效；记住密码时，登录态自动延期，在自定义延期时间的基础上，原则上可以无限延期
- 5、 sessionId数据结构优化，进一步提升暴露破解难度
- 6、 认证数据存储结构调整，避免登陆信息存储冗余
- 7、 认证中心用户登录校验改为Mock数据方式，取消对DB强依赖，降低部署难度
- 8、 Client端依赖Core包，slf4j依赖优化，移除log4j强依赖
- 9、 Ajax请求未登录处理逻辑优化，返回JSON格式提示数据
- 10、项目结构梳理，清理冗余依赖，升级多项依赖版本至较近版本
- 11、路径排除：新增自定义属性 "excludedPaths"，允许设置多个，且支持Ant表达式。用于排除SSO客户端不需要过滤的路径


### 5.3 版本 v1.2.0 Release Notes [迭代中]
- 1、升级jedis、springboot等版本依赖版本；
- 2、Client跳转新增一次性Token验证；
- 3、拼写问题修复；



### TODO LIST
- 1、增强用户增强安全性：登陆用户数据中，新增客户端信息如ip、ua等，防止session被窃取；
- 2、认证中心与接入端交互数据加密，临时AccessToken阅后即焚，增强安全性；
- 3、CAS认证中心，支持维护客户端应用；防止跳转非法第三方导致登陆信息泄露；
- 4、集成网关支持；
- 5、支持认证分组，分组内共享登陆状态，分组之间登录态隔离，


## 六、其他

### 6.1 项目贡献
欢迎参与项目贡献！比如提交PR修复一个bug，或者新建 [Issue](https://github.com/xuxueli/xxl-sso/issues/) 讨论新特性或者变更。

### 6.2 用户接入登记
更多接入的公司，欢迎在 [登记地址](https://github.com/xuxueli/xxl-sso/issues/1 ) 登记，登记仅仅为了产品推广。

### 6.3 开源协议和版权
产品开源免费，并且将持续提供免费的社区技术支持。个人或企业内部可自由的接入和使用。

- Licensed under the GNU General Public License (GPL) v3.
- Copyright (c) 2015-present, xuxueli.

---
### 捐赠
无论金额多少都足够表达您这份心意，非常感谢 ：）      [前往捐赠](https://www.xuxueli.com/page/donate.html )
