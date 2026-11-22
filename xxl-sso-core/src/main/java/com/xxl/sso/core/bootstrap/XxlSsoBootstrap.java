package com.xxl.sso.core.bootstrap;

import com.xxl.sso.core.helper.XxlSsoHelper;
import com.xxl.sso.core.store.LoginStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author xuxueli 2018-04-09 11:38:15
 */
public class XxlSsoBootstrap {
    private static final Logger logger = LoggerFactory.getLogger(XxlSsoBootstrap.class);


    // --------------------------------- base conf ---------------------------------

    private LoginStore loginStore;
    private String tokenKey;
    private long tokenTimeout;

    /**
     * CAS 登录跳转白名单，支持 AntPathMatcher，逗号分隔
     */
    private String casWhitelist;

    public void setLoginStore(LoginStore loginStore) {
        this.loginStore = loginStore;
    }

    public void setTokenKey(String tokenKey) {
        this.tokenKey = tokenKey;
    }

    public void setTokenTimeout(long tokenTimeout) {
        this.tokenTimeout = tokenTimeout;
    }

    public void setCasWhitelist(String casWhitelist) {
        this.casWhitelist = casWhitelist;
    }

    public LoginStore getLoginStore() {
        return loginStore;
    }

    // --------------------------------- start / stop ---------------------------------


    public void start() {

        // 1、loginStore start
        loginStore.start();

        // 2、helper init
        XxlSsoHelper.init(loginStore, tokenKey, tokenTimeout, casWhitelist);

        logger.info(">>>>>>>>>>> xxl-sso XxlSsoBootstrap started.");
    }

    public void stop() {

        // 1、loginStore stop
        loginStore.stop();

        logger.info(">>>>>>>>>>> xxl-sso XxlSsoBootstrap stopped.");
    }


    // --------------------------------- other ---------------------------------


}
