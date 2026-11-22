package com.xxl.sso.core.constant;

/**
 * const
 *
 * <p>
 * 统一定义 Cookie/Header/RequestAttribute key、CAS 参数、错误码与登录有效期常量，禁止在业务代码中硬编码。
 * </p>
 *
 * @author xuxueli 2018-04-02 19:18:04
 */
public class Const {


    /**
     * login url （relative address）
     */
    public static final String LOGIN_URL = "/login";
    /**
     * logout url （relative address）
     */
    public static final String LOGOUT_URL = "/logout";



    /**
     * sso server address (web + token client)
     */
    public static final String SSO_SERVER = "sso_server";
    /**
     * sso client redirect url   (sso-server redirect 2 client url )
     */
    public static final String CLIENT_REDIRECT_URL = "redirect_url";



    /**
     * xxl-sso token, for cookie-key、header-key
     */
    public static final String XXL_SSO_TOKEN = "xxl-sso-token";
    /**
     * xxl-sso user, for request.attribute - key
     */
    public static final String XXL_SSO_USER = "xxl_sso_user";
    /**
     * xxl-sso login-user store key prefix, like redis-key-prefix
     */
    public static final String XXL_SSO_STORE_PREFIX = "xxl_sso";

    /**
     * xxl-sso ticket, for cas redirect ticket
     */
    public static final String XXL_SSO_TICKET = "xxl_sso_ticket";
    /**
     * cas ticket timeout, seconds, limit 1s - 3min
     */
    public static final long CAS_TICKET_TIMEOUT = 60 * 3;


    /**
     * code for login error
     */
    public static final int CODE_LOGIN_ERROR = 300;

    /**
     * code for login fail
     */
    public static final int CODE_LOGIN_FAIL = 301;

    /**
     * code for permission fail
     */
    public static final int CODE_PERMISSION_FAIL = 302;

    /**
     * code for role fail
     */
    public static final int CODE_ROLE_FAIL = 303;



    /**
     * expire seconds for 1 day
     */
    public static final long EXPIRE_SECOND_1_DAY = 60 * 60 * 24;
    /**
     * expire seconds for 7 day
     */
    public static final long EXPIRE_SECOND_FOR_7_DAY = EXPIRE_SECOND_1_DAY * 7;
    /**
     * expire seconds for 30 day
     */
    public static final long EXPIRE_SECOND_FOR_30_DAY = EXPIRE_SECOND_1_DAY * 30;
    /**
     * expire seconds for 1 year
     */
    public static final long EXPIRE_SECOND_FOR_1_YEAR = EXPIRE_SECOND_1_DAY * 365;
    /**
     * expire seconds for 10 year
     */
    public static final long EXPIRE_SECOND_FOR_10_YEAR = EXPIRE_SECOND_1_DAY * 365 * 10;


}
