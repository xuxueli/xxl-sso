package com.xxl.sso.core.bootstrap;

import com.xxl.sso.core.exception.XxlSsoException;
import com.xxl.sso.core.helper.XxlSsoHelper;
import com.xxl.sso.core.store.LoginStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * xxl-sso bootstrap
 *
 * <p>
 * 生命周期入口：装配登录存储与 token 配置，按「Store 启动 → Helper 初始化」顺序完成启动，停止时释放 Store 资源。
 * 仅负责装配与生命周期，不承载登录业务逻辑。
 * </p>
 *
 * <p>
 * 重复调用 {@link #start()} / {@link #stop()} 会重复执行底层 Store 的启动与释放，
 * 使用方应在应用启动、停止回调中各调用一次。
 * </p>
 *
 * @author xuxueli 2018-04-09 11:38:15
 */
public class XxlSsoBootstrap {
    private static final Logger logger = LoggerFactory.getLogger(XxlSsoBootstrap.class);


    // --------------------------------- base conf ---------------------------------

    /**
     * login store
     */
    private LoginStore loginStore;

    /**
     * token key, used as cookie-key and header-key
     */
    private String tokenKey;

    /**
     * token timeout, by seconds
     */
    private long tokenTimeout;

    /**
     * CAS 登录跳转白名单，支持 AntPathMatcher，逗号分隔
     */
    private String casWhitelist;

    /**
     * set login store
     *
     * @param loginStore    login store
     */
    public void setLoginStore(LoginStore loginStore) {
        this.loginStore = loginStore;
    }

    /**
     * set token key
     *
     * @param tokenKey  token key, fallback to {@link com.xxl.sso.core.constant.Const#XXL_SSO_TOKEN} when blank
     */
    public void setTokenKey(String tokenKey) {
        this.tokenKey = tokenKey;
    }

    /**
     * set token timeout
     *
     * @param tokenTimeout  token timeout, by seconds
     */
    public void setTokenTimeout(long tokenTimeout) {
        this.tokenTimeout = tokenTimeout;
    }

    /**
     * set cas redirect whitelist
     *
     * @param casWhitelist  cas redirect whitelist, support AntPathMatcher, comma separated; blank means no validation
     */
    public void setCasWhitelist(String casWhitelist) {
        this.casWhitelist = casWhitelist;
    }

    /**
     * get login store
     *
     * @return login store
     */
    public LoginStore getLoginStore() {
        return loginStore;
    }

    // --------------------------------- start / stop ---------------------------------


    /**
     * start store and init helper
     *
     * @throws XxlSsoException when loginStore is not configured
     */
    public void start() {

        // fail fast with a clear message instead of NPE
        if (loginStore == null) {
            throw new XxlSsoException("xxl-sso loginStore can not be null.");
        }

        // 1、loginStore start
        loginStore.start();

        // 2、helper init
        XxlSsoHelper.init(loginStore, tokenKey, tokenTimeout, casWhitelist);

        logger.info(">>>>>>>>>>> xxl-sso XxlSsoBootstrap started.");
    }

    /**
     * stop store and release resource
     *
     * <p>未配置 LoginStore 时直接返回，便于应用停机流程容错。</p>
     */
    public void stop() {

        // nothing to stop
        if (loginStore == null) {
            return;
        }

        // 1、loginStore stop
        loginStore.stop();

        logger.info(">>>>>>>>>>> xxl-sso XxlSsoBootstrap stopped.");
    }


    // --------------------------------- other ---------------------------------


}
