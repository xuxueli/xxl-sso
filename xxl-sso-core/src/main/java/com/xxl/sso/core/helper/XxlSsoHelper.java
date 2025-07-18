package com.xxl.sso.core.helper;

import com.xxl.sso.core.constant.Const;
import com.xxl.sso.core.model.LoginInfo;
import com.xxl.sso.core.store.LoginStore;
import com.xxl.sso.core.token.TokenHelper;
import com.xxl.tool.core.CollectionTool;
import com.xxl.tool.core.StringTool;
import com.xxl.tool.http.CookieTool;
import com.xxl.tool.id.UUIDTool;
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


    // ---------------------- login ----------------------

    /**
     * login ( store LoginInfo and generate token )
     *
     * @param loginInfo     login data
     * @return              Response#data is token
     */
    public static Response<String> login(LoginInfo loginInfo) {

        // generate token
        Response<String> tokenResponse = TokenHelper.generateToken(loginInfo);
        if (!tokenResponse.isSuccess()) {
            return tokenResponse;
        }

        // update expire-time
        loginInfo.setExpireTime(System.currentTimeMillis() + getInstance().getTokenTimeout());

        // set store-LoginInfo
        Response<String> setResponse = getInstance().getLoginStore().set(loginInfo);
        if (!setResponse.isSuccess()) {
            return setResponse;
        }

        // return
        return Response.ofSuccess(tokenResponse.getData());
    }

    /**
     * login with token (write LoginStore and response-cookie )
     *
     * @param loginInfo
     * @param response
     * @return  Response#data is token
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


    // ---------------------- login-update ----------------------

    /**
     * login update ( update LoginInfo in LoginStore)
     *
     * @param loginInfo
     * @return
     */
    public static Response<String> loginUpdate(LoginInfo loginInfo) {

        // update expire-time
        if (loginInfo != null) {
            loginInfo.setExpireTime(System.currentTimeMillis() + getInstance().getTokenTimeout());
        }

        // update store-LoginInfo
        return getInstance().getLoginStore().update(loginInfo);
    }


    // ---------------------- logout ----------------------

    /**
     * logout with token
     *
     * @param token
     */
    public static Response<String> logout(String token) {

        // parse token
        LoginInfo loginInfoForToken = TokenHelper.parseToken(token);
        if (loginInfoForToken == null) {
            return Response.ofFail("token is invalid");
        }

        // remove store-LoginInfo
        return getInstance().getLoginStore().remove(loginInfoForToken.getUserId());
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


    // ---------------------- loginCheck ----------------------

    /**
     * login check with token
     *
     * @param token
     * @return
     */
    public static Response<LoginInfo> loginCheck(String token) {

        // parse token
        LoginInfo loginInfoForToken = TokenHelper.parseToken(token);
        if (loginInfoForToken == null) {
            return Response.ofFail("token is invalid");
        }

        // get store-LoginInfo
        Response<LoginInfo> loginInfoResponse = getInstance().getLoginStore().get(loginInfoForToken.getUserId());
        if (!loginInfoResponse.isSuccess()) {
            return loginInfoResponse;
        }
        LoginInfo loginInfo = loginInfoResponse.getData();

        // valid version
        if (loginInfo.getVersion()!=null && !loginInfo.getVersion().equals(loginInfoForToken.getVersion())){
            // Non-empty and inconsistent
            return Response.ofFail("token version is invalid");
        }

        return Response.ofSuccess(loginInfo);
    }

    /**
     * login check with request-header
     *
     * @param request
     * @return
     */
    public static Response<LoginInfo> loginCheckWithHeader(HttpServletRequest request) {
        String token = request.getHeader(getInstance().getTokenKey());
        return loginCheck(token);
    }

    /**
     * login check with request-cookie
     *
     * @param request
     * @return
     */
    public static Response<LoginInfo> loginCheckWithCookie(HttpServletRequest request, HttpServletResponse response) {
        // get cookie
        String token = CookieTool.getValue(request, getInstance().getTokenKey());

        // do login check
        Response<LoginInfo> result = loginCheck(token);
        if (!(result!=null && result.isSuccess())) {
            CookieTool.remove(request, response, getInstance().getTokenKey());
        }
        return result;
    }

    /**
     * login check with request-attribute
     *
     * @param request
     * @return
     */
    public static Response<LoginInfo> loginCheckWithAttr(HttpServletRequest request) {
        LoginInfo loginInfo = (LoginInfo) request.getAttribute(Const.XXL_SSO_USER);
        return loginInfo!=null
                ?Response.ofSuccess(loginInfo)
                :Response.ofFail("not login.");
    }


    // ---------------------- for cas ticket ----------------------

    /**
     * create ticket, from token in cookie
     *
     * @param request
     * @return      Response.data is ticket
     */
    public static Response<String> createTicket(HttpServletRequest request) {

        // get cookie
        String token = CookieTool.getValue(request, getInstance().getTokenKey());

        // parse token
        LoginInfo loginInfoForToken = TokenHelper.parseToken(token);
        if (loginInfoForToken == null) {
            return Response.ofFail("not login.");
        }

        // generate ticket
        String ticket = loginInfoForToken.getUserId().concat("_").concat(UUIDTool.getSimpleUUID());

        // valid ticket
        long ticketTimeout = 60 * 1000;
        return getInstance().getLoginStore().createTicket(ticket, token, ticketTimeout);
    }

    /**
     * valid ticket and write cookie, from parameter
     *
     * @param request
     * @return
     */
    public static Response<LoginInfo> validTicket(HttpServletRequest request, HttpServletResponse response) {

        // parse ticket
        String ticket = request.getParameter(Const.XXL_SSO_TICKET);
        if (StringTool.isBlank(ticket)) {
            return Response.ofFail("ticket is null.");
        }

        // valid ticket
        Response<String> validTicketResult = getInstance().getLoginStore().validTicket(ticket);
        if (!validTicketResult.isSuccess()) {
            return Response.ofFail(validTicketResult.getMsg());
        }
        String token = validTicketResult.getData();

        // login check
        Response<LoginInfo> result = loginCheck(token);
        if (result.isSuccess()) {
            // write token - cookie
            CookieTool.set(response, getInstance().getTokenKey(), token, false);
        }

        return loginCheck(token);
    }

    // ---------------------- permission ----------------------

    /**
     * has role valid
     *
     * @param loginInfo
     * @param role
     * @return
     */
    public static Response<String> hasRole(LoginInfo loginInfo, String role) {
        if (StringTool.isBlank(role)) {
            // not limit, default has role.
            return Response.ofSuccess();
        }
        if (CollectionTool.isEmpty(loginInfo.getRoleList())) {
            return Response.ofFail("roleList is null.");
        }

        return loginInfo.getRoleList().contains(role)
                ?Response.ofSuccess()
                :Response.ofFail("has no role.");
    }

    /**
     * has permission valid
     *
     * @param loginInfo
     * @param permission
     * @return
     */
    public static Response<String> hasPermission(LoginInfo loginInfo, String permission) {
        if (StringTool.isBlank(permission)) {
            // not limit, default has permission
            return Response.ofSuccess();
        }
        if (CollectionTool.isEmpty(loginInfo.getPermissionList())) {
            return Response.ofFail("permissionList is null.");
        }

        return loginInfo.getPermissionList().contains(permission)
                ?Response.ofSuccess()
                :Response.ofFail("has no permission.");
    }


}