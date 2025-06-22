package com.xxl.sso.core.helper;

import com.xxl.sso.core.constant.Const;
import com.xxl.sso.core.model.LoginInfo;
import com.xxl.sso.core.store.LoginStore;
import com.xxl.tool.core.StringTool;
import com.xxl.tool.http.CookieTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;

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
    public static void init(LoginStore loginStore, String tokenKey){
        instance = new XxlSsoHelper(loginStore, tokenKey);
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

    public XxlSsoHelper(LoginStore loginStore, String tokenKey) {
        this.loginStore = loginStore;
        if (StringTool.isBlank(tokenKey)) {
            this.tokenKey = Const.XXL_SSO_TOKEN;
        }
    }

    public LoginStore getLoginStore() {
        return loginStore;
    }

    public String getTokenKey() {
        return tokenKey;
    }

    // ---------------------- tool ----------------------

    /**
     * login with token
     *
     * @param token
     * @param loginInfo
     * @return
     */
    public static boolean login(String token, LoginInfo loginInfo) {
        return getInstance().getLoginStore().set(token, loginInfo);
    }

    /**
     * logout with token
     *
     * @param token
     */
    public static void logout(String token) {
        getInstance().getLoginStore().remove(token);
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

    /**
     * login check with request-cookie
     *
     * @param request
     * @return
     */
    public static LoginInfo loginCheckWithCookie(HttpServletRequest request) {
        String token = CookieTool.getValue(request, getInstance().getTokenKey());
        return loginCheck(token);
    }

}