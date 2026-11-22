package com.xxl.sso.core.helper;

import com.xxl.sso.core.constant.Const;
import com.xxl.sso.core.model.LoginInfo;
import com.xxl.sso.core.store.LoginStore;
import com.xxl.sso.core.token.TokenHelper;
import com.xxl.sso.core.util.CasUrlTool;
import com.xxl.tool.core.CollectionTool;
import com.xxl.tool.core.StringTool;
import com.xxl.tool.http.CookieTool;
import com.xxl.tool.id.UUIDTool;
import com.xxl.tool.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * xxl-sso helper
 *
 * <p>
 * 面向 Servlet 容器（Filter / Spring MVC Interceptor）的登录态门面类，统一提供登录、续期、注销、
 * 登录态校验与 CAS 票据能力；内部单例由 {@link com.xxl.sso.core.bootstrap.XxlSsoBootstrap} 初始化，
 * 禁止业务代码自行 new。
 * </p>
 *
 * @author xuxueli 2018-04-08 21:30:54
 */
public class XxlSsoHelper {
    private static final Logger logger = LoggerFactory.getLogger(XxlSsoHelper.class);

    // ---------------------- instance ----------------------

    private static XxlSsoHelper instance;

    /**
     * init helper without cas whitelist
     *
     * @param loginStore    login store
     * @param tokenKey      token key, fallback to {@link Const#XXL_SSO_TOKEN} when blank
     * @param tokenTimeout  token timeout, by seconds, fallback to {@link Const#EXPIRE_SECOND_FOR_10_YEAR} when not positive
     */
    public static void init(LoginStore loginStore, String tokenKey, long tokenTimeout){
        init(loginStore, tokenKey, tokenTimeout, null);
    }

    /**
     * init helper
     *
     * @param loginStore    login store
     * @param tokenKey      token key, fallback to {@link Const#XXL_SSO_TOKEN} when blank
     * @param tokenTimeout  token timeout, by seconds, fallback to {@link Const#EXPIRE_SECOND_FOR_10_YEAR} when not positive
     * @param casWhitelist  cas redirect whitelist, support AntPathMatcher; blank means no whitelist validation
     */
    public static void init(LoginStore loginStore, String tokenKey, long tokenTimeout, String casWhitelist){
        instance = new XxlSsoHelper(loginStore, tokenKey, tokenTimeout, casWhitelist);
    }

    /**
     * get singleton instance
     *
     * @return helper instance, {@code null} before {@link com.xxl.sso.core.bootstrap.XxlSsoBootstrap#start()}
     */
    public static XxlSsoHelper getInstance(){
        return instance;
    }


    // ---------------------- init ----------------------

    /**
     * login store
     */
    private final LoginStore loginStore;

    /**
     * token key
     */
    private String tokenKey;

    /**
     * token timeout, by seconds
     */
    private long tokenTimeout;

    /**
     * cas redirect url validator
     */
    private final CasUrlTool redirectValidator;

    /**
     * create helper without cas whitelist
     *
     * @param loginStore    login store
     * @param tokenKey      token key, fallback to {@link Const#XXL_SSO_TOKEN} when blank
     * @param tokenTimeout  token timeout, by seconds, fallback to {@link Const#EXPIRE_SECOND_FOR_10_YEAR} when not positive
     */
    public XxlSsoHelper(LoginStore loginStore, String tokenKey, long tokenTimeout) {
        this(loginStore, tokenKey, tokenTimeout, null);
    }

    /**
     * create helper
     *
     * @param loginStore    login store
     * @param tokenKey      token key, fallback to {@link Const#XXL_SSO_TOKEN} when blank
     * @param tokenTimeout  token timeout, by seconds, fallback to {@link Const#EXPIRE_SECOND_FOR_10_YEAR} when not positive
     * @param casWhitelist  cas redirect whitelist, support AntPathMatcher; blank means no whitelist validation
     */
    public XxlSsoHelper(LoginStore loginStore, String tokenKey, long tokenTimeout, String casWhitelist) {
        this.loginStore = loginStore;
        this.tokenKey = tokenKey;
        this.tokenTimeout = tokenTimeout;
        this.redirectValidator = new CasUrlTool(casWhitelist);

        // valid
        if (StringTool.isBlank(this.tokenKey)) {
            this.tokenKey = Const.XXL_SSO_TOKEN;
        }
        if (this.tokenTimeout <= 0 ) {
            this.tokenTimeout = Const.EXPIRE_SECOND_FOR_10_YEAR;
        }
    }

    /**
     * get login store
     *
     * @return login store
     */
    public LoginStore getLoginStore() {
        return loginStore;
    }

    /**
     * get token key
     *
     * @return token key, used as cookie-key and header-key
     */
    public String getTokenKey() {
        return tokenKey;
    }

    /**
     * get token timeout
     *
     * @return token timeout, by seconds
     */
    public long getTokenTimeout() {
        return tokenTimeout;
    }


    // ---------------------- login ----------------------

    /**
     * login ( store LoginInfo and generate token )
     *
     * <p>
     * 先按 userId + signature 生成 token，再刷新 {@code expireTime} 并写入 LoginStore；
     * 存储失败时返回存储层错误，不会向调用方返回 token。
     * </p>
     *
     * @param loginInfo     login data, must contain userId and signature
     * @return              Response#data is token; fail when loginInfo is invalid or store write fails
     */
    public static Response<String> login(LoginInfo loginInfo) {

        // generate token
        Response<String> tokenResponse = TokenHelper.generateToken(loginInfo);
        if (!tokenResponse.isSuccess()) {
            return tokenResponse;
        }

        // update expire-time ( seconds )
        loginInfo.setExpireTime(System.currentTimeMillis() / 1000 + getInstance().getTokenTimeout());

        // set store-LoginInfo
        Response<String> setResponse = getInstance().getLoginStore().set(loginInfo);
        if (!setResponse.isSuccess()) {
            return setResponse;
        }

        // return
        return Response.ofSuccess(tokenResponse.getData());
    }

    /**
     * login with cookie (write LoginStore and response-cookie )
     *
     * @param loginInfo     login data
     * @param response      http response, used to write token cookie
     * @param ifRemember    true - persistent cookie; false - session cookie
     * @return              Response#data is token
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
     * login update ( update LoginInfo in LoginStore )
     *
     * <p>
     * 刷新 {@code expireTime} 后整体更新存储中的登录态；{@code loginInfo} 为 {@code null} 时直接交由
     * 存储层返回失败，不会抛出异常。
     * </p>
     *
     * @param loginInfo     login data
     * @return              response
     */
    public static Response<String> loginUpdate(LoginInfo loginInfo) {

        // update expire-time ( seconds )
        if (loginInfo != null) {
            loginInfo.setExpireTime(System.currentTimeMillis() / 1000 + getInstance().getTokenTimeout());
        }

        // update store-LoginInfo
        return getInstance().getLoginStore().update(loginInfo);
    }


    // ---------------------- logout ----------------------

    /**
     * logout with token
     *
     * <p>
     * 先校验 token 再按 userId 移除服务端登录态，避免伪造 token 注销他人会话；
     * token 为空时视为未登录，直接返回成功。
     * </p>
     *
     * @param token     token
     * @return          response, fail when token is invalid or login state does not exist
     */
    public static Response<String> logout(String token) {
        if (StringTool.isBlank(token)) {
            return Response.ofSuccess();    // not login; no need to logout.
        }

        // verify token before remove store-LoginInfo; avoid attackers remove other users' loginInfo by random token.
        Response<LoginInfo> loginCheckResult = loginCheck(token);
        if (!loginCheckResult.isSuccess()) {
            return Response.of(loginCheckResult.getCode(), loginCheckResult.getMsg());
        }

        // remove store-LoginInfo
        return getInstance().getLoginStore().remove(loginCheckResult.getData().getUserId());
    }

    /**
     * logout with cookie
     *
     * @param request       request
     * @param response      response
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
     * logout with header
     *
     * @param request       request
     */
    public static Response<String> logoutWithHeader(HttpServletRequest request) {

        // get header
        String token = request.getHeader(getInstance().getTokenKey());
        if (StringTool.isBlank(token)) {
            return Response.ofSuccess();    // not login; no need to logout.
        }

        // do logout
        return logout(token);
    }


    // ---------------------- loginCheck ----------------------

    /**
     * login check with token
     *
     * <p>
     * 解析 token 后从 LoginStore 读取登录态，并校验 token 与存储中的 signature 是否一致；
     * token 非法、登录态不存在或 signature 不匹配时均返回失败。
     * </p>
     *
     * @param token     token
     * @return          loginInfo; fail when token is invalid or login state does not exist
     */
    public static Response<LoginInfo> loginCheck(String token) {

        // parse token
        LoginInfo loginInfoForToken = TokenHelper.parseToken(token);
        if (loginInfoForToken == null || StringTool.isBlank(loginInfoForToken.getSignature())) {
            return Response.ofFail("not login, token is invalid");
        }

        // get store-LoginInfo
        Response<LoginInfo> loginInfoResponse = getInstance().getLoginStore().get(loginInfoForToken.getUserId());
        if (!loginInfoResponse.isSuccess()) {
            return loginInfoResponse;
        }
        LoginInfo loginInfo = loginInfoResponse.getData();
        // store impl may return success with null data
        if (loginInfo == null) {
            return Response.ofFail("not login, loginInfo not exists");
        }

        // valid version
        if (!loginInfoForToken.getSignature().equals(loginInfo.getSignature())){
            // Non-empty and inconsistent
            return Response.ofFail("not login, token signature is invalid");
        }

        return Response.ofSuccess(loginInfo);
    }

    /**
     * login check with request-header
     *
     * @param request       request
     * @return              loginInfo
     */
    public static Response<LoginInfo> loginCheckWithHeader(HttpServletRequest request) {
        String token = request.getHeader(getInstance().getTokenKey());
        return loginCheck(token);
    }

    /**
     * login check with request-cookie
     *
     * <p>
     * 校验失败时同步清理无效 Cookie，避免后续请求继续携带过期 token。
     * </p>
     *
     * @param request       request
     * @param response      response, used to remove invalid cookie
     * @return              loginInfo
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
     * @param request       request
     * @return              loginInfo
     */
    public static Response<LoginInfo> loginCheckWithAttr(HttpServletRequest request) {
        LoginInfo loginInfo = (LoginInfo) request.getAttribute(Const.XXL_SSO_USER);
        return loginInfo!=null
                ?Response.ofSuccess(loginInfo)
                :Response.ofFail("not login.");
    }


    // ---------------------- for cas ----------------------

    /**
     * generate cas login url
     *
     * <p>
     * 与 {@link #casGenerateRedirectUrl(HttpServletRequest, String)} 配套使用：
     *      客户端跳转 CAS 登录页时，由本方法拼接登录地址；
     *      登录成功后由 {@code casGenerateRedirectUrl} 校验回跳地址并追加票据。
     *      地址处理细节统一由 {@link CasUrlTool} 实现。
     * </p>
     *
     * @param serverAddress cas server address
     * @param loginPath     cas login path
     * @param originUrl     original request url
     * @return              cas login url with encoded redirect_url
     */
    public static String casGenerateLoginUrl(String serverAddress, String loginPath, String originUrl) {
        return CasUrlTool.buildLoginUrl(serverAddress, loginPath, originUrl);
    }

    /**
     * generate cas redirect url, valid redirect url and append ticket
     *
     * <p>
     * 白名单为空时不校验；配置白名单后，地址不合法或未命中时返回失败，不会签发任何票据。
     * </p>
     *
     * @param request       request
     * @param redirectUrl   client redirect url
     * @return              Response.data is redirect url
     */
    public static Response<String> casGenerateRedirectUrl(HttpServletRequest request, String redirectUrl) {

        // valid redirect url
        if (!getInstance().redirectValidator.isAllowed(redirectUrl)) {
            return Response.ofFail("cas redirect url is invalid.");
        }

        // create ticket
        Response<String> createTicketResult = casCreateTicket(request);
        if (!createTicketResult.isSuccess()) {
            return createTicketResult;
        }

        // build redirect url
        String redirectUrlFinal = CasUrlTool.appendQueryParam
                (redirectUrl.trim(),
                        Const.XXL_SSO_TICKET,
                        createTicketResult.getData());

        // build redirect url
        return Response.ofSuccess(redirectUrlFinal);
    }

    /**
     * create cas ticket, from token in cookie
     *
     * <p>
     * ticket 有效期固定 180 秒；LoginStore 需实现 {@link LoginStore#createTicket(String, String, long)}，
     * 否则返回不支持错误。
     * </p>
     *
     * @param request   request
     * @return          Response.data is ticket
     */
    public static Response<String> casCreateTicket(HttpServletRequest request) {

        // get cookie
        String token = CookieTool.getValue(request, getInstance().getTokenKey());

        // parse token
        LoginInfo loginInfoForToken = TokenHelper.parseToken(token);
        if (loginInfoForToken == null) {
            return Response.ofFail("create ticket fail, not login.");
        }

        // generate ticket
        String ticket = loginInfoForToken.getUserId().concat("_").concat(UUIDTool.getSimpleUUID());

        // valid ticket
        long ticketTimeout = Const.CAS_TICKET_TIMEOUT;
        return getInstance().getLoginStore().createTicket(ticket, token, ticketTimeout);
    }

    /**
     * valid cas ticket and write cookie, from parameter
     *
     * <p>
     * 票据一次性使用，校验成功后立即从 LoginStore 中删除；登录态校验通过后写入 token Cookie。
     * </p>
     *
     * @param request       request
     * @param response      response
     * @return              loginInfo; fail when ticket is blank, invalid or expired
     */
    public static Response<LoginInfo> casValidTicket(HttpServletRequest request, HttpServletResponse response) {

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

        return result;
    }

    // ---------------------- permission ----------------------

    /**
     * has role valid
     *
     * <p>
     * {@code role} 为空表示不做角色限制，直接返回成功。
     * </p>
     *
     * @param loginInfo     loginInfo
     * @param role          required role
     * @return              response
     */
    public static Response<String> hasRole(LoginInfo loginInfo, String role) {
        if (StringTool.isBlank(role)) {
            // not limit, default has role.
            return Response.ofSuccess();
        }
        if (loginInfo == null) {
            return Response.ofFail("not login.");
        }
        if (CollectionTool.isEmpty(loginInfo.getRoleList())) {
            return Response.ofFail("has no role[2]");
        }

        return loginInfo.getRoleList().contains(role)
                ?Response.ofSuccess()
                :Response.ofFail("has no role");
    }

    /**
     * has permission valid
     *
     * <p>
     * {@code permission} 为空表示不做权限限制，直接返回成功。
     * </p>
     *
     * @param loginInfo     loginInfo
     * @param permission    required permission
     * @return              response
     */
    public static Response<String> hasPermission(LoginInfo loginInfo, String permission) {
        if (StringTool.isBlank(permission)) {
            // not limit, default has permission
            return Response.ofSuccess();
        }
        if (loginInfo == null) {
            return Response.ofFail("not login.");
        }
        if (CollectionTool.isEmpty(loginInfo.getPermissionList())) {
            return Response.ofFail("has no permission[2]");
        }

        return loginInfo.getPermissionList().contains(permission)
                ?Response.ofSuccess()
                :Response.ofFail("has no permission");
    }


}