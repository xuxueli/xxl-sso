package com.xxl.sso.core.helper;

import com.xxl.sso.core.constant.Const;
import com.xxl.sso.core.model.LoginInfo;
import com.xxl.sso.core.store.LoginStore;
import com.xxl.tool.core.StringTool;
import com.xxl.tool.http.CookieTool;
import com.xxl.tool.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author xuxueli 2018-04-08 21:30:54
 */
public class XxlSsoHelper {
    private static final Logger logger = LoggerFactory.getLogger(XxlSsoHelper.class);

    // ---------------------- instance ----------------------

    private static XxlSsoHelper instance;

    /**
     * init
     * @param loginStore
     */
    public static void init(LoginStore loginStore, String tokenKey, long tokenTimeout){
        instance = new XxlSsoHelper(loginStore, tokenKey, tokenTimeout);
    }

    /**
     * get instance
     *
     * @return
     */
    public static XxlSsoHelper getInstance(){
        return instance;
    }


    // ---------------------- init ----------------------

    /**
     * login store
     */
    private final LoginStore loginStore;
    private String tokenKey;
    private long tokenTimeout;

    public XxlSsoHelper(LoginStore loginStore, String tokenKey, long tokenTimeout) {
        this.loginStore = loginStore;
        this.tokenKey = tokenKey;
        this.tokenTimeout = tokenTimeout;

        // valid
        if (StringTool.isBlank(this.tokenKey)) {
            this.tokenKey = Const.XXL_SSO_TOKEN;
        }
        if (this.tokenTimeout <= 0 ) {
            this.tokenTimeout = Const.EXPIRE_TIME_FOR_10_YEAR;
        }
    }

    public LoginStore getLoginStore() {
        return loginStore;
    }

    public String getTokenKey() {
        return tokenKey;
    }

    public long getTokenTimeout() {
        return tokenTimeout;
    }


    // ---------------------- login tool ----------------------

    /**
     * login with token (only write LoginStore)
     *
     * @param loginInfo
     * @return
     */
    public static Response<String> login(LoginInfo loginInfo) {
        return getInstance().getLoginStore().set(loginInfo, getInstance().getTokenTimeout());
    }

    /**
     * logout with token
     *
     * @param token
     */
    public static Response<String> logout(String token) {
        return getInstance().getLoginStore().remove(token);
    }

    /**
     * login check with token
     *
     * @param token
     * @return
     */
    public static LoginInfo loginCheck(String token) {
        return getInstance().getLoginStore().get(token);
    }

    /**
     * login check with request-attribute
     *
     * @param request
     * @return
     */
    public static LoginInfo loginCheckWithAttr(HttpServletRequest request) {
        return (LoginInfo) request.getAttribute(Const.XXL_SSO_USER);
    }

    /**
     * login check with request-header
     *
     * @param request
     * @return
     */
    public static LoginInfo loginCheckWithHeader(HttpServletRequest request) {
        String token = request.getHeader(getInstance().getTokenKey());
        return loginCheck(token);
    }


    // ---------------------- login with cookie ----------------------

    /**
     * login with token (write LoginStore and response-cookie )
     *
     * @param loginInfo
     * @param response
     * @return
     */
    public static Response<String> loginWithCookie(LoginInfo loginInfo, HttpServletResponse response, boolean ifRemember) {

        // do login
        Response<String> loginResult = login(loginInfo);

        // set cookie
        if (loginResult.isSuccess()) {
            String token = loginResult.getData();
            CookieTool.set(response, getInstance().getTokenKey(), token, ifRemember);
        }

        return loginResult;
    }

    /**
     * logout with cookie
     *
     * @param request
     * @param response
     */
    public static Response<String> logoutWithCookie(HttpServletRequest request, HttpServletResponse response) {

        // get cookie
        String token = CookieTool.getValue(request, getInstance().getTokenKey());
        if (StringTool.isBlank(token)) {
            return Response.ofSuccess();    // not login; no need to logout.
        }

        // do logout
        Response<String> logoutResult = logout(token);

        // remove cookie
        CookieTool.remove(request, response, getInstance().getTokenKey());
        return logoutResult;
    }

    /**
     * login check with request-cookie
     *
     * @param request
     * @return
     */
    public static LoginInfo loginCheckWithCookie(HttpServletRequest request, HttpServletResponse response) {

        // get cookie
        String token = CookieTool.getValue(request, getInstance().getTokenKey());

        // do login check
        LoginInfo loginInfo = loginCheck(token);
        if (loginInfo == null) {
            CookieTool.remove(request, response, getInstance().getTokenKey());
        }
        return loginInfo;
    }

    /**
     * login check with request-cookie or request-parameter
     *
     * @param request
     * @return
     */
    public static LoginInfo loginCheckWithCookieOrParam(HttpServletRequest request, HttpServletResponse response) {

        // get cookie
        String token = CookieTool.getValue(request, getInstance().getTokenKey());

        // do login check
        LoginInfo loginInfo = loginCheck(token);
        if (loginInfo != null) {
            return loginInfo;
        }

        // sync token(cookie) from cas-server
        token = request.getParameter(Const.XXL_SSO_TOKEN);
        loginInfo = loginCheck(token);
        if (loginInfo != null) {

            // set cookie
            CookieTool.set(response, getInstance().getTokenKey(), token, true);     // todo, sync ifRemember-time
            return loginInfo;
        }
        return null;
    }

    /**
     * login check with request-param
     *
     * @param request
     * @return
     */
    public static String getTokenWithCookie(HttpServletRequest request) {
        // get cookie
        return CookieTool.getValue(request, getInstance().getTokenKey());
    }

}