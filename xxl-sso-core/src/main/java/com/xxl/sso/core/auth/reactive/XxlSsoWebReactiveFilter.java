package com.xxl.sso.core.auth.reactive;

import com.xxl.sso.core.constant.Const;
import com.xxl.sso.core.helper.XxlSsoReactiveHelper;
import com.xxl.sso.core.model.LoginInfo;
import com.xxl.sso.core.path.impl.AntPathMatcher;
import com.xxl.tool.core.StringTool;
import com.xxl.tool.json.GsonTool;
import com.xxl.tool.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.nio.charset.StandardCharsets;

/**
 * web reactive filter
 *
 * Spring WebFlux（Reactive） 场景下的 {@link com.xxl.sso.core.auth.filter.XxlSsoWebFilter} 实现，适用于Web网页场景，要求访问系统部署在统一域名下；
 * 使用方式：声明为 Spring Bean（{@link WebFilter}）即可生效，如 "@Bean public XxlSsoWebReactiveFilter ..."；
 *
 * @author xuxueli 2026-08-07
 */
public class XxlSsoWebReactiveFilter implements WebFilter {
    private static final Logger logger = LoggerFactory.getLogger(XxlSsoWebReactiveFilter.class);


    /**
     * path matcher
     */
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    /**
     * excluded paths, like "/excluded/*,/excluded/pathx"
     */
    private final String excludedPaths;

    /**
     * login path
     */
    private String loginPath;


    /**
     * create web reactive filter
     *
     * @param excludedPaths excluded paths, ant pattern, comma separated; nullable
     * @param loginPath     login path, fallback to {@link Const#LOGIN_URL} when blank
     */
    public XxlSsoWebReactiveFilter(String excludedPaths, String loginPath) {
        this.excludedPaths = excludedPaths;
        this.loginPath = loginPath;

        // valid
        if (StringTool.isBlank(loginPath)) {
            this.loginPath = Const.LOGIN_URL;
        }

        logger.info("XxlSsoWebReactiveFilter init.");
    }


    // ---------------------- tool ----------------------

    /**
     * is match excluded path
     *
     * @param exchange  web exchange
     * @return          {@code true} when current request path is excluded
     */
    private boolean isMatchExcludedPaths(ServerWebExchange exchange) {
        // get url
        String servletPath = exchange.getRequest().getPath().pathWithinApplication().value();

        // filter excluded path
        if (StringTool.isNotBlank(excludedPaths)) {
            for (String excludedPath : excludedPaths.split(",")) {
                // path check
                String uriPattern = excludedPath.trim();
                if (StringTool.isBlank(uriPattern)) {
                    continue;
                }

                // path match
                if (antPathMatcher.match(uriPattern, servletPath)) {
                    // excluded path, pass
                    return true;
                }

            }
        }
        return false;
    }

    /**
     * write json response
     *
     * @param exchange  web exchange
     * @param body      json body
     * @return          completion signal
     */
    private Mono<Void> writeJson(ServerWebExchange exchange, String body) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.OK);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        DataBuffer buffer = response.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }


    // ---------------------- auth filter ----------------------

    /**
     * auth filter: pass excluded path, then validate cookie token, redirect or write json on login fail
     *
     * @param exchange  web exchange
     * @param chain     filter chain
     * @return          completion signal
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        // 1、valid path, is excluded
        if (isMatchExcludedPaths(exchange)) {
            // excluded path, pass
            return chain.filter(exchange);
        }

        // 2、vlid permission, not support

        // 3、login check
        Response<LoginInfo> loginCheckResult = XxlSsoReactiveHelper.loginCheckWithCookie(exchange);

        // parse login info
        LoginInfo loginInfo = null;
        if (loginCheckResult!=null && loginCheckResult.isSuccess()) {
            loginInfo = loginCheckResult.getData();
        }

        // process login
        if (loginInfo == null) {

            // 4、login fail message
            String loginFailMsg = GsonTool.toJson(Response.of(Const.CODE_LOGIN_FAIL, "not login for path:"+ exchange.getRequest().getPath().pathWithinApplication().value()));

            // isJson
            String header = exchange.getRequest().getHeaders().getFirst(HttpHeaders.CONTENT_TYPE);
            boolean isJson = header!=null && header.contains("json");
            if (isJson) {

                // write response
                return writeJson(exchange, loginFailMsg);
            } else {

                // redirect 2 login
                String finalLoginPath = exchange.getRequest().getPath().contextPath().value().concat(loginPath);
                ServerHttpResponse response = exchange.getResponse();
                response.setStatusCode(HttpStatus.FOUND);
                response.getHeaders().setLocation(URI.create(finalLoginPath));
                return response.setComplete();
            }

        }

        // 5、write attribute（ loginInfo ）
        exchange.getAttributes().put(Const.XXL_SSO_USER, loginInfo);

        // 6、chain pass
        return chain.filter(exchange);
    }

}
