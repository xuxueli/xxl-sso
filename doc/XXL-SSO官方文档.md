## 《分布式单点登录框架XXL-SSO》

[![License](https://img.shields.io/badge/license-GPLv3-blue.svg)](http://www.gnu.org/licenses/gpl-3.0.html)
[![donate](https://img.shields.io/badge/%24-donate-ff69b4.svg?style=flat-square)](http://www.xuxueli.com/page/donate.html)

## 一、简介

### 1.1 概述
XXL-SSO 是一个分布式单点登录框架。只需要登录一次就可以访问所有相互信任的应用系统。
其核心设计目标是开发迅速、学习简单、轻量级、易扩展。现已开放源代码，开箱即用。

### 1.2 特性
- 1、简洁：API直观简洁，可快速上手；
- 2、轻量级：环境依赖小，部署与接入成本较低；
- 3、单点登录：只需要登录一次就可以访问所有相互信任的应用系统。
- 4、分布式：接入SSO认证中心的应用，支持分布式部署；
- 5、HA：Server端与Client端，均支持集群部署，提高系统可用性；
- 6、实时性：系统登陆、注销状态，全部Server与Client端实时共享；
- 7、CS结构：基于CS结构，包括Server"认证中心"与Client"受保护应用"；
- 8、跨域：支持跨域应用接入SSO认证中心；


### 1.3 下载

#### 文档地址

- [中文文档](http://www.xuxueli.com/xxl-sso/)

#### 源码仓库地址

源码仓库地址 | Release Download
--- | ---
[https://github.com/xuxueli/xxl-sso](https://github.com/xuxueli/xxl-sso) | [Download](https://github.com/xuxueli/xxl-sso/releases)
  

#### 技术交流
- [社区交流](http://www.xuxueli.com/page/community.html)

### 1.4 环境
- JDK：1.7+
- Redis：4.0+
- Mysql：5.6+


## 二、快速入门

### 2.1：系统数据库初始化

建表SQL位置：
```
/xxl-sso/doc/db/xxl-sso-mysql.sql
```

### 2.2：源码编译

```
- xxl-sso-server：中央认证服务，支持集群；
- xxl-sso-core：Client端依赖；
- xxl-sso-samples：单点登陆Client端接入示例项目；
    - xxl-sso-sample-springboot：springboot版本
```

### 2.3 部署 "认证中心（SSO Server）"

```
项目名：xxl-sso-server
```

#### 配置说明

配置文件位置：application.properties
```
### web
server.port=8080
server.context-path=/xxl-sso-server

### freemarker
spring.freemarker.request-context-attribute=request
spring.freemarker.cache=false

### resource (default: /**  + classpath:/static/ )
spring.mvc.static-path-pattern=/static/**
spring.resources.static-locations=classpath:/static/

### jdbc    （登陆用户信息，账号密码等，可根据业务灵活调整定制；）
spring.datasource.url=jdbc:mysql://127.0.0.1:3306/xxl-sso
spring.datasource.username=root
spring.datasource.password=root_pwd
spring.datasource.driver-class-name=com.mysql.jdbc.Driver
spring.datasource.max-idle=10
spring.datasource.max-wait=10000
spring.datasource.min-idle=5
spring.datasource.initial-size=5

### mybatis
mybatis.mapper-locations=classpath:/mybatis-mapper/*Mapper.xml
mybatis.type-aliases-package=com.xxl.sso.server.model

### redis   （sessionid 分布式存储共享Redis）
redis.address=127.0.0.1:6379

```

### 2.4 部署 "单点登陆Client端接入示例项目"

```
项目名：xxl-sso-sample-springboot
```

#### maven依赖

```
<dependency>
    <groupId>com.xuxueli</groupId>
    <artifactId>xxl-sso-core</artifactId>
    <version>${最新稳定版}</version>
</dependency>
```

#### 配置 XxlSsoFilter

参考代码：com.xxl.sso.sample.config.XxlSsoConfig
```
@Bean
public FilterRegistrationBean xxlSsoFilterRegistration() {

    // redis init
    JedisUtil.init(xxlSsoRedisAddress);

    // filter
    FilterRegistrationBean registration = new FilterRegistrationBean();

    registration.setName("XxlSsoFilter");
    registration.setOrder(1);
    registration.addUrlPatterns("/*");
    registration.setFilter(new XxlSsoFilter());
    registration.addInitParameter(Conf.SSO_SERVER, xxlSsoServer);
    registration.addInitParameter(Conf.SSO_LOGOUT_PATH, xxlSsoLogoutPath);

    return registration;
}
```


#### 配置说明

配置文件位置：application.properties
```
### web
server.port=8081
server.context-path=/xxl-sso-sample-springboot

### freemarker
spring.freemarker.request-context-attribute=request
spring.freemarker.cache=false

### resource (default: /**  + classpath:/static/ )
spring.mvc.static-path-pattern=/static/**
spring.resources.static-locations=classpath:/static/

### xxl-sso (CLient端SSO配置)
##### SSO Server认证中心地址（推荐以域名方式配置认证中心，本机可参考章节"2.5"修改host文件配置域名指向）
xxl.sso.server=http://xxlssoserver.com:8080/xxl-sso-server
##### 注销登陆path，值为Client端应用的相对路径
xxl.sso.logout.path=/logout
##### （sessionid 分布式存储共享Redis）
xxl.sso.redis.address=127.0.0.1:6379
```

### 2.5 验证

- 环境准备：启动Redis、初始化Mysql表数据；

- 修改Host文件：域名方式访问认证中心，模拟跨域与线上真实环境
```
### 在host文件中添加以下内容0
127.0.0.1 xxlssoserver.com
127.0.0.1 xxlssoclient1.com
127.0.0.1 xxlssoclient2.com
```

- 分别运行 "xxl-sso-server" 与 "xxl-sso-sample-springboot"
```
### SSO认证中心地址
http://xxlssoserver.com:8080/xxl-sso-server

### Client01应用地址
http://xxlssoclient1.com:8081/xxl-sso-sample-springboot/

### Client02应用地址
http://xxlssoclient2.com:8081/xxl-sso-sample-springboot/
```

- SSO登录/注销流程验证
```
正常情况下，登录流程如下：
1、访问 "Client01应用地址" ，将会自动 redirect 到 "SSO认证中心地址" 登录界面；
2、成功登录后，将会自动 redirect 返回到 "Client01应用地址"，并切换为已登录状态；
3、此时，访问 "Client02应用地址"，不需登陆将会自动切换为已登录状态；

正常情况下，注销流程如下：
1、访问 "Client01应用地址" 配置的 "注销登陆path"，将会自动 redirect 到 "SSO认证中心地址" 并自动注销登陆状态；
2、此时，访问 "Client02应用地址"，也将会自动注销登陆状态；
```


## 三、总体设计

### 3.1 架构图

![输入图片说明](https://raw.githubusercontent.com/xuxueli/xxl-sso/master/doc/images/img_01.png "在这里输入图片标题")

### 3.2 功能定位

XXL-SSO 是一个分布式单点登录框架。只需要登录一次就可以访问所有相互信任的应用系统。

借助 XXL-SSO，可以快速实现分布式系统单点登录。

### 3.3 核心概念

概念 | 说明
--- | ---
SSO Server | 中央认证服务，支持集群；
SSO Client | 接入SSO认证中心的Client应用；
SSO SessionId | 登录用户会话ID，SSO 登录成功为用户自动分配；
SSO User | 登录用户信息，与 SSO SessionId 相对应；

### 3.4 登录流程剖析

- 用户于Client端应用访问受限资源时，将会自动 redirect 到 SSO Server 进入统一登录界面。
- 用户登录成功之后将会为用户分配 SSO SessionId 并 redirect 返回来源Client端应用，同时附带分配的 SSO SessionId。
- 在Client端的SSO Filter里验证 SSO SessionId 无误，将 SSO SessionId 写入到用户浏览器Client端域名下 cookie 中。
- SSO Filter验证 SSO SessionId 通过，受限资源请求放行；



## 四、版本更新日志

### 版本 V0.0.1，新特性[2018-04]
- 1、简洁：API直观简洁，可快速上手；
- 2、轻量级：环境依赖小，部署与接入成本较低；
- 3、单点登录：只需要登录一次就可以访问所有相互信任的应用系统。
- 4、分布式：接入SSO认证中心的应用，支持分布式部署；
- 5、HA：Server端与Client端，均支持集群部署，提高系统可用性；
- 6、实时性：系统登陆、注销状态，全部Server与Client端实时共享；
- 7、CS结构：基于CS结构，包括Server"认证中心"与Client"受保护应用"；
- 8、跨域：支持跨域应用接入SSO认证中心；


### TODO LIST
- 1、认证中心与接入端交互数据加密，增强安全性；

## 五、其他

### 5.1 项目贡献
欢迎参与项目贡献！比如提交PR修复一个bug，或者新建 [Issue](https://github.com/xuxueli/xxl-sso/issues/) 讨论新特性或者变更。

### 5.2 用户接入登记
更多接入的公司，欢迎在 [登记地址](https://github.com/xuxueli/xxl-sso/issues/1 ) 登记，登记仅仅为了产品推广。

### 5.3 开源协议和版权
产品开源免费，并且将持续提供免费的社区技术支持。个人或企业内部可自由的接入和使用。

- Licensed under the GNU General Public License (GPL) v3.
- Copyright (c) 2015-present, xuxueli.

---
### 捐赠
无论金额多少都足够表达您这份心意，非常感谢 ：）      [前往捐赠](http://www.xuxueli.com/page/donate.html )
