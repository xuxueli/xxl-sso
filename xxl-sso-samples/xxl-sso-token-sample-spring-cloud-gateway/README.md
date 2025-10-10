# 基于 Spring-Cloud-Gateway 的 SSO Client Demo

* 基于 Spring-Cloud-Gateway 2.0.0.M9 (Spring Boot 2.0.0.RELEASE)
* 在网关层实现鉴权，登录状态有效的请求才会被路由到对应的服务
* 由于 Spring Boot 版本问题，本Demo的 pom.xml 不直接继承原来的 sample pom

注意事项：
* ~~在`DispatcherHandler`中，`RequestMappingHandlerMapping`的优先级高于`RoutePredicateHandlerMapping`，因此请求路径与Controller的RequestMapping一致时，`RequestMappingHandlerMapping`优先处理请求~~
* 基于 GlobalFilter / GatewayFilter 实现的过滤器只能覆盖路由请求，直接在项目中编写的 @RequestMapping Controller无法覆盖
* 基于 `org.springframework.web.server.WebFilter` 实现的过滤逻辑能够覆盖所有请求