<p align="center">
    <img src="https://www.xuxueli.com/doc/static/xxl-job/images/xxl-logo.jpg" width="150">
    <h3 align="center">XXL-SSO</h3>
    <p align="center">
        XXL-SSO, A Distributed Single-Sign-On Framework.
        <br>
        <a href="https://www.xuxueli.com/xxl-sso/"><strong>-- Home Page --</strong></a>
        <br>
        <br>
        <a href="https://github.com/xuxueli/xxl-sso/actions">
            <img src="https://github.com/xuxueli/xxl-sso/workflows/Java%20CI/badge.svg" >
        </a>
        <a href="https://central.sonatype.com/artifact/com.xuxueli/xxl-sso-core/">
            <img src="https://img.shields.io/maven-central/v/com.xuxueli/xxl-sso-core" >
        </a>
        <a href="https://github.com/xuxueli/xxl-sso/releases">
            <img src="https://img.shields.io/github/release/xuxueli/xxl-sso.svg" >
        </a>
        <a href="https://github.com/xuxueli/xxl-sso/">
            <img src="https://img.shields.io/github/stars/xuxueli/xxl-sso" >
        </a>
        <a href="http://www.gnu.org/licenses/gpl-3.0.html">
            <img src="https://img.shields.io/badge/license-GPLv3-blue.svg" >
        </a>
        <a href="https://www.xuxueli.com/page/donate.html">
            <img src="https://img.shields.io/badge/%24-donate-ff69b4.svg?style=flat-square" >
        </a>
    </p>    
</p>


## Introduction

XXL-SSO is a single sign-on framework that allows you to access all trusted applications with just one login. 
It has "lightweight, highly scalable, progressive" and other features, supports "login authentication, permissions (role) authentication, distributed session authentication, single sign-on, Web login, Native(front-end separation) login" and other multi-login and authentication types.
Now, it's already open source code, real "out-of-the-box".

XXL-SSO 是一个 单点登录框架，只需要登录一次就可以访问所有相互信任的应用系统。具备 “轻量级、高扩展、渐进式” 的等特性，支持 “登录认证、权限认证、角色认证、分布式会话认证、单点登录、Web常规登录、前后端分离” 等多登录及认证类型，现已开放源代码，开箱即用。


## Documentation
- [中文文档](https://www.xuxueli.com/xxl-sso/)

## Communication    
- [社区交流](https://www.xuxueli.com/page/community.html)

## Features

- 1、易用性：支持注解/API多方式接入，一行注解/代码即可实现 登录认证、权限认证、角色认证 等，接入灵活方便；
- 2、轻量级：针对第三方组件、部署环境零依赖，部署及接入低成本、轻量级；
- 3、高扩展：得益于模块化抽象设计，各框架组件可灵活扩展；可选用官方提供组件实现或自定义扩展。
  - 登录态持久化组件（LoginStore）：提供登录态/会话数据持久化能力；官方提供Cache、Redis等组件实现，可选用接入或自定义扩展；
  - 登录认证组件（Auth）：提供系统登录/认证集成能力；官方提供 Filter（Servlet）和Interceptor（Spring）等实现，可选用接入或自定义扩展；
  - 登录用户模型（LoginInfo）：提供统一登录用户模型，且模型支持扩展存储自定义扩展属性；
- 4、渐进式：支持渐进式集成接入使用，从简单到复杂场景，包括：单体系统(Web常规登录)、复杂企业内多系统(CAS单点登录)、互联网多端&高并发系统(Native登录) 等，均可接入使用；
- 5、多登录类型：
  - Web常规登录：适用于常规“单体系统”场景；限制相关Web系统部署在相同域名下，登录凭证存储在公共域名下；
  - Native登录：适用于“移动端、小程序、前后端分离、客户端”等系统场景；适用于无Cookie场景，天然不受限域名。支持多端登录、以及登录态共享，但是登录凭证需要客户端管理维护；
  - CAS单点登录：适用于“多系统跨域、企业多系统统一登录”等系统场景；解决了系统 跨域登录认证、统一登录认证 问题；但是需要单独部署CAS认证中心、提供单点登录相关基础能力；
- 6、多认证方式：
  - 登录认证：本质为验证用户身份的过程，目的是确认“你是谁”，确保访问者合法可信；
  - 权限认证：在用户身份认证通过后，校验用户是否具备访问特定资源的权限，决定“你能做什么”；认证维度是“权限”；
  - 角色认证：在用户身份认证通过后，校验用户是否具备访问特定资源的权限，决定“你能做什么”；认证维度是“角色”；
- 7、分布式会话/认证：支持分布式登录以及会话认证，集成分布式系统可共享的 登录态持久化组件（LoginStore），可选用或参考官方RedisLoginStore；
- 8、安全性：针对系统框架多个模块落地安全性设计，包括：登录Token安全设计、客户端登录凭证Cookie安全设计、CAS跳转Ticket安全设计 等；
- 9、单点登录：针对CAS单点登录场景，提供单点登录及注销能力；
- 10、跨域登录认证：针对CAS单点登录场景，支持跨域Web应用接入，解决了系统 跨域登录认证 问题；
- 11、高可用/HA：针对CAS单点登录场景，CAS认证中心支持集群部署，并可借助LoginStore实现登录态共享，从而实现系统水平扩展以及高可用；
- 12、多端登录认证：针对多端登录场景，如 Web、移动端、小程序 等多端，提供多端登录及认证能力；
- 13、前后端分离：针对前后端分离系统，提供 Native登录 方案，支持前后端分离场景登录认证能力；
- 14、记住密码：支持记住密码功能；记住密码时，支持登录态自动延期；未记住密码时，关闭浏览器则登录态失效；
- 15、登录态自动延期：支持自定义登录态有效期窗口，当登录态有效期窗口过半时自动顺延一个周期；


## Development
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
    8、杭州博物文化

> 更多接入的公司，欢迎在 [登记地址](https://github.com/xuxueli/xxl-sso/issues/1 ) 登记，登记仅仅为了产品推广。

欢迎大家的关注和使用，XXL-SSO也将拥抱变化，持续发展。


## Contributing
Contributions are welcome! Open a pull request to fix a bug, or open an [Issue](https://github.com/xuxueli/xxl-sso/issues/) to discuss a new feature or change.

欢迎参与项目贡献！比如提交PR修复一个bug，或者新建 [Issue](https://github.com/xuxueli/xxl-sso/issues/) 讨论新特性或者变更。


## Copyright and License
This product is open source and free, and will continue to provide free community technical support. Individual or enterprise users are free to access and use.

- Licensed under the GNU General Public License (GPL) v3.
- Copyright (c) 2015-present, xuxueli.

产品开源免费，并且将持续提供免费的社区技术支持。个人或企业内部可自由的接入和使用。


## Donate
No matter how much the amount is enough to express your thought, thank you very much ：）     [To donate](https://www.xuxueli.com/page/donate.html )

无论金额多少都足够表达您这份心意，非常感谢 ：）      [前往捐赠](https://www.xuxueli.com/page/donate.html )
