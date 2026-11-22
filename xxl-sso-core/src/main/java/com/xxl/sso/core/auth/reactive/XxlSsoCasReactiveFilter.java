package com.xxl.sso.core.auth.reactive;

import com.xxl.sso.core.constant.Const;
import com.xxl.sso.core.exception.XxlSsoException;
import com.xxl.sso.core.helper.XxlSsoReactiveHelper;
import com.xxl.sso.core.model.LoginInfo;
import com.xxl.sso.core.path.impl.AntPathMatcher;
import com.xxl.sso.core.util.CasUrlTool;
import com.xxl.sso.core.util.ReactiveWebTool;
import com.xxl.tool.core.StringTool;
import com.xxl.tool.json.GsonTool;
import com.xxl.tool.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.buffer.DataBuffer;
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
 * cas reactive filter
 *
 * Spring WebFlux（Reactive） 场景下的 {@link com.xxl.sso.core.auth.filter.XxlSsoCasFilter} 实现，适用于Web网页场景，解决跨域登录态共享、单点登录问题；
 * 使用方式：声明为 Spring Bean（{@link WebFilter}）即可生效，如 "@Bean public XxlSsoCasReactiveFilter ..."；
 *
 * @author xuxueli 2026-08-07
 */
public class XxlSsoCasReactiveFilter implements WebFilter {
    private static final Logger logger = LoggerFactory.getLogger(XxlSsoCasReactiveFilter.class);


    /**
     * path matcher
     */
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    /**
     * server address
     */
    private String serverAddress;

    /**
     * login path
     */
    private String loginPath;

    /**
     * excluded paths, like "/excluded/*,/excluded/pathx"
     */
    private String excludedPaths;

    /**
     * create cas reactive filter
     *
     * @param serverAddress cas server address, required
     * @param loginPath     cas login path, fallback to {@link Const#LOGIN_URL} when blank
     * @param excludedPaths excluded paths, ant pattern, comma separated; nullable
     */
    public XxlSsoCasReactiveFilter(String serverAddress, String loginPath, String excludedPaths) {
        this.serverAddress = serverAddress;
        this.loginPath = loginPath;
        this.excludedPaths = excludedPaths;

        // valid
        if (StringTool.isBlank(loginPath)) {
            this.loginPath = Const.LOGIN_URL;
        }

        // valid
        if (StringTool.isBlank(serverAddress)) {
            throw new XxlSsoException("xxl-sso serverAddress can not be blank.");
        }

        logger.info("XxlSsoCasReactiveFilter init.");
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
     * auth filter: pass excluded path, then validate cookie token or cas ticket, redirect to cas login on fail
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

        // 3、login check (ticket + cookie)
        Response<LoginInfo> loginCheckResult = XxlSsoReactiveHelper.loginCheckWithCookie(exchange);                // check cookie
        if (!(loginCheckResult!=null && loginCheckResult.isSuccess())) {
            loginCheckResult = XxlSsoReactiveHelper.casValidTicket(exchange);         // check ticket
        }

        // parse login info
        LoginInfo loginInfo = null;
        if (loginCheckResult!=null && loginCheckResult.isSuccess()) {
            loginInfo = loginCheckResult.getData();
        }

        // process login
        if (loginInfo == null) {

            // 4、login fail message
            String loginFailMsg = GsonTool.toJson(Response.of(Const.CODE_LOGIN_FAIL, "not login for path:"+ exchange.getRequest().getPath().pathWithinApplication().value()));

            // is ajax / json request
            if (ReactiveWebTool.isAjax(exchange)) {

                // write response
                return writeJson(exchange, loginFailMsg);
            } else {

                // origin url
                String originUrl = exchange.getRequest().getURI().toString();

                // redirect login-path (encoded redirect_url, drop original query)
                String finalLoginPath = CasUrlTool.buildLoginUrl(serverAddress, loginPath, originUrl);

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
