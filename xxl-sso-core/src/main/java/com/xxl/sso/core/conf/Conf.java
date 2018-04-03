package com.xxl.sso.core.conf;

/**
 * conf
 *
 * @author xuxueli 2018-04-02 19:18:04
 */
public class Conf {

    /**
     * redirect url, for client
     */
    public static final String REDIRECT_URL = "redirect_url";

    /**
     * sso sessionid, between browser and sso-server
     */
    public static final String SSO_SESSIONID = "xxl_sso_sessionid";

    public static final String SSO_USER = "xxl_sso_user";



    /**
     * sso server address
     */
    public static final String SSO_SERVER = "sso_server";

    /**
     * login url
     */
    public static final String SSO_LOGIN = "/login";
    /**
     * logout url
     */
    public static final String SSO_LOGOUT = "/logout";
    /**
     * login check
     */
    public static final String SSO_LOGIN_CHECK = "/loginCheck";



    /**
     * filter logout path
     */
    public static final String SSO_LOGOUT_PATH = "logoutPath";


}
