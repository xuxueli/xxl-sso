package com.xxl.sso.core.helper;

import com.xxl.sso.core.constant.Const;
import com.xxl.sso.core.model.LoginInfo;
import com.xxl.sso.core.token.TokenHelper;
import com.xxl.tool.core.StringTool;
import com.xxl.tool.id.UUIDTool;
import com.xxl.tool.response.Response;
import org.springframework.http.HttpCookie;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.time.Duration;

/**
 * xxl-sso reactive helper
 *
 * 提供面向 Spring WebFlux（Reactive）场景的登录态工具方法；与 {@link XxlSsoHelper} 能力一一对应，可完全平替；
 * 共用底层 LoginStore、TokenHelper 等能力，仅将请求/响应模型替换为 ServerWebExchange；
 *
 * @author xuxueli 2026-08-07
 */
public class XxlSsoReactiveHelper {

    /**
     * 默认缓存时间,单位/秒
     */
    private static final int COOKIE_MAX_AGE = Integer.MAX_VALUE;
    /**
     * 保存路径,根路径
     */
    private static final String COOKIE_PATH = "/";


    // ---------------------- login ----------------------

    /**
     * login ( store LoginInfo and generate token )
     *
     * @param loginInfo     login data
     * @return              Response#data is token
     */
    public static Response<String> login(LoginInfo loginInfo) {
        return XxlSsoHelper.login(loginInfo);
    }

    /**
     * login with token (write LoginStore and response-cookie )
     *
     * @param loginInfo     login data
     * @param exchange      exchange
     * @param ifRemember    if remember
     * @return  Response#data is token
     */
    public static Response<String> loginWithCookie(LoginInfo loginInfo, ServerWebExchange exchange, boolean ifRemember) {

        // do login
        Response<String> loginResult = XxlSsoHelper.login(loginInfo);

        // set cookie
        if (loginResult.isSuccess()) {
            String token = loginResult.getData();
            setCookie(exchange.getResponse(), XxlSsoHelper.getInstance().getTokenKey(), token, ifRemember);
        }

        return loginResult;
    }


    // ---------------------- login-update ----------------------

    /**
     * login update ( update LoginInfo in LoginStore )
     *
     * @param loginInfo     login data
     * @return              response
     */
    public static Response<String> loginUpdate(LoginInfo loginInfo) {
        return XxlSsoHelper.loginUpdate(loginInfo);
    }


    // ---------------------- logout ----------------------

    /**
     * logout with token
     *
     * @param token     token
     */
    public static Response<String> logout(String token) {
        return XxlSsoHelper.logout(token);
    }

    /**
     * logout with cookie
     *
     * @param exchange      exchange
     */
    public static Response<String> logoutWithCookie(ServerWebExchange exchange) {

        // get cookie
        String token = getCookieValue(exchange.getRequest(), XxlSsoHelper.getInstance().getTokenKey());
        if (StringTool.isBlank(token)) {
            return Response.ofSuccess();    // not login; no need to logout.
        }

        // do logout
        Response<String> logoutResult = XxlSsoHelper.logout(token);

        // remove cookie
        removeCookie(exchange.getResponse(), XxlSsoHelper.getInstance().getTokenKey());
        return logoutResult;
    }

    /**
     * logout with header
     *
     * @param exchange      exchange
     */
    public static Response<String> logoutWithHeader(ServerWebExchange exchange) {

        // get header
        String token = exchange.getRequest().getHeaders().getFirst(XxlSsoHelper.getInstance().getTokenKey());
        if (StringTool.isBlank(token)) {
            return Response.ofSuccess();    // not login; no need to logout.
        }

        // do logout
        return XxlSsoHelper.logout(token);
    }


    // ---------------------- loginCheck ----------------------

    /**
     * login check with token
     *
     * @param token     token
     * @return          loginInfo
     */
    public static Response<LoginInfo> loginCheck(String token) {
        return XxlSsoHelper.loginCheck(token);
    }

    /**
     * login check with request-header
     *
     * @param exchange      exchange
     * @return              loginInfo
     */
    public static Response<LoginInfo> loginCheckWithHeader(ServerWebExchange exchange) {
        String token = exchange.getRequest().getHeaders().getFirst(XxlSsoHelper.getInstance().getTokenKey());
        return XxlSsoHelper.loginCheck(token);
    }

    /**
     * login check with request-cookie
     *
     * @param exchange      exchange
     * @return              loginInfo
     */
    public static Response<LoginInfo> loginCheckWithCookie(ServerWebExchange exchange) {
        // get cookie
        String token = getCookieValue(exchange.getRequest(), XxlSsoHelper.getInstance().getTokenKey());

        // do login check
        Response<LoginInfo> result = XxlSsoHelper.loginCheck(token);
        if (!(result!=null && result.isSuccess())) {
            removeCookie(exchange.getResponse(), XxlSsoHelper.getInstance().getTokenKey());
        }
        return result;
    }

    /**
     * login check with request-attribute
     *
     * @param exchange      exchange
     * @return              loginInfo
     */
    public static Response<LoginInfo> loginCheckWithAttr(ServerWebExchange exchange) {
        LoginInfo loginInfo = (LoginInfo) exchange.getAttributes().get(Const.XXL_SSO_USER);
        return loginInfo!=null
                ?Response.ofSuccess(loginInfo)
                :Response.ofFail("not login.");
    }


    // ---------------------- for cas ----------------------

    /**
     * create cas ticket, from token in cookie
     *
     * @param exchange      exchange
     * @return              Response.data is ticket
     */
    public static Response<String> casCreateTicket(ServerWebExchange exchange) {

        // get cookie
        String token = getCookieValue(exchange.getRequest(), XxlSsoHelper.getInstance().getTokenKey());

        // parse token
        LoginInfo loginInfoForToken = TokenHelper.parseToken(token);
        if (loginInfoForToken == null) {
            return Response.ofFail("create ticket fail, not login.");
        }

        // generate ticket
        String ticket = loginInfoForToken.getUserId().concat("_").concat(UUIDTool.getSimpleUUID());

        // valid ticket
        long ticketTimeout = 60;
        return XxlSsoHelper.getInstance().getLoginStore().createTicket(ticket, token, ticketTimeout);
    }

    /**
     * valid cas ticket and write cookie, from parameter
     *
     * @param exchange      exchange
     * @return              loginInfo
     */
    public static Response<LoginInfo> casValidTicket(ServerWebExchange exchange) {

        // parse ticket
        String ticket = exchange.getRequest().getQueryParams().getFirst(Const.XXL_SSO_TICKET);
        if (StringTool.isBlank(ticket)) {
            return Response.ofFail("ticket is null.");
        }

        // valid ticket
        Response<String> validTicketResult = XxlSsoHelper.getInstance().getLoginStore().validTicket(ticket);
        if (!validTicketResult.isSuccess()) {
            return Response.ofFail(validTicketResult.getMsg());
        }
        String token = validTicketResult.getData();

        // login check
        Response<LoginInfo> result = XxlSsoHelper.loginCheck(token);
        if (result.isSuccess()) {
            // write token - cookie
            setCookie(exchange.getResponse(), XxlSsoHelper.getInstance().getTokenKey(), token, false);
        }

        return result;
    }


    // ---------------------- permission ----------------------

    /**
     * has role valid
     *
     * @param loginInfo     loginInfo
     * @param role          role
     * @return              response
     */
    public static Response<String> hasRole(LoginInfo loginInfo, String role) {
        return XxlSsoHelper.hasRole(loginInfo, role);
    }

    /**
     * has permission valid
     *
     * @param loginInfo     loginInfo
     * @param permission    permission
     * @return              response
     */
    public static Response<String> hasPermission(LoginInfo loginInfo, String permission) {
        return XxlSsoHelper.hasPermission(loginInfo, permission);
    }


    // ---------------------- cookie tool ----------------------
    /**
     * add cookie
     *
     * @param response      response
     * @param key           cookie key
     * @param value         cookie value
     * @param ifRemember    true - 永不过期，false - 浏览器退出则销毁；
     */
    private static void setCookie(ServerHttpResponse response, String key, String value, boolean ifRemember) {
        int maxAge = ifRemember?COOKIE_MAX_AGE:-1;
        ResponseCookie cookie = ResponseCookie.from(key, encode(value))
                .path(COOKIE_PATH)
                .httpOnly(true)
                .maxAge(Duration.ofSeconds(maxAge))
                .build();
        response.addCookie(cookie);
    }

    /**
     * remove cookie
     *
     * @param response      response
     * @param key           cookie key
     */
    private static void removeCookie(ServerHttpResponse response, String key) {
        ResponseCookie cookie = ResponseCookie.from(key, "")
                .path(COOKIE_PATH)
                .maxAge(Duration.ZERO)
                .build();
        response.addCookie(cookie);
    }

    /**
     * get cookie value
     *
     * @param request       request
     * @param key           cookie key
     * @return              cookie value
     */
    private static String getCookieValue(ServerHttpRequest request, String key) {
        HttpCookie cookie = request.getCookies().getFirst(key);
        if (cookie == null) {
            return null;
        }

        // decode value
        String value = cookie.getValue();
        try {
            value = URLDecoder.decode(value, "utf-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        return value;
    }

    /**
     * encode value
     *
     * @param value         value
     * @return              encoded value
     */
    private static String encode(String value) {
        try {
            return URLEncoder.encode(value, "utf-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

}
