package com.xxl.sso.sample.filter;

import com.xxl.sso.core.conf.Conf;
import com.xxl.sso.core.entity.ReturnT;
import com.xxl.sso.core.exception.XxlSsoException;
import com.xxl.sso.core.path.impl.AntPathMatcher;
import com.xxl.sso.core.user.XxlSsoUser;
import com.xxl.sso.sample.helper.XxlGatewaySsoTokenLoginHelper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;

import static com.xxl.sso.core.conf.Conf.SSO_USER;

/**
 * 基于 GlobalFilter 的过滤器只能过滤 Spring-Cloud-Gateway 的路由配置
 * 基于 WebFilter 能够覆盖 RequestMapping 和 Gateway
 *
 * @author Wu Weijie
 * @see XxlSsoTokenWebFilter
 */
@Configuration
@Order(-1)
public class XxlSsoTokenWebFilter implements WebFilter {

    public static final String NOT_LOGIN_MESSAGE = "{\"code\":" + Conf.SSO_LOGIN_FAIL_RESULT.getCode() + ", \"msg\":\"" + Conf.SSO_LOGIN_FAIL_RESULT.getMsg() + "\"}";
    public static final String ERROR_MESSAGE_TEMPLATE = "'{'\"code\":\"500\", \"msg\":\"{0}\"}";
    public static final String LOGOUT_SUCCESS_MESSAGE = "{\"code\":" + ReturnT.SUCCESS_CODE + ", \"msg\":\"\"}";

    private static final AntPathMatcher ANT_PATH_MATCHER = new AntPathMatcher();
    @Value("${xxl-sso.excluded.paths}")
    private String excludedPaths;
    @Value("${xxl.sso.server}")
    private String ssoServer;
    @Value("${xxl.sso.logout.path}")
    private String logoutPath;
    @Value("${server.api-prefix}")
    private String apiPrefix;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        /*
        获取请求路径并去掉统一前缀
        eg:
        api-prefix=/api
        requestPath=/api/user/demo
        处理后：/user/demo
        统一的前缀可以理解为 server.servlet.context-path
        但 spring cloud gateway 中暂时没有前缀配置方法，因此自定义一个路径前缀参数
         */
        String requestPath = request.getPath().value().substring(apiPrefix.length());

        if (excludedPaths != null && !excludedPaths.trim().isEmpty()) {
            // if path in excludePaths
            for (String excludePath : excludedPaths.split(",")) {
                String uriPattern = excludePath.trim();

                if (ANT_PATH_MATCHER.match(uriPattern, requestPath)) {
                    // pass
                    return chain.filter(exchange);
                }
            }
        }


        // logout filter
        if (logoutPath != null
                && logoutPath.trim().length() > 0
                && logoutPath.equals(requestPath)) {

            // logout
            XxlGatewaySsoTokenLoginHelper.logout(request);

            // response
            response.setStatusCode(HttpStatus.OK);
            response.getHeaders().add("Content-Type", "application/json;charset=utf-8");
            return response.writeWith(
                    Flux.just(response.bufferFactory().wrap(LOGOUT_SUCCESS_MESSAGE.getBytes(StandardCharsets.UTF_8))));
        }

        // login filter
        try {
            XxlSsoUser xxlSsoUser = XxlGatewaySsoTokenLoginHelper.loginCheck(request);
            if (xxlSsoUser == null) {
                response.setStatusCode(HttpStatus.OK);
                response.getHeaders().add("Content-Type", "application/json;charset=utf-8");
                return response.writeWith(
                        Flux.just(response.bufferFactory().wrap(NOT_LOGIN_MESSAGE.getBytes(StandardCharsets.UTF_8))));
            }

            // 用户登录状态有效，滤过
            exchange.getAttributes().put(SSO_USER, xxlSsoUser);
            return chain.filter(exchange);

        } catch (XxlSsoException e) {
            response.setStatusCode(HttpStatus.OK);
            response.getHeaders().add("Content-Type", "application/json;charset=utf-8");
            // return error
            return response.writeWith(
                    Flux.just(response.bufferFactory().wrap(MessageFormat.format(ERROR_MESSAGE_TEMPLATE, e.getMessage()).getBytes(StandardCharsets.UTF_8))));
        }
    }
}
