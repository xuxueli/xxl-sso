package com.xxl.sso.server.framework.config;

import com.xxl.sso.core.bootstrap.XxlSsoBootstrap;
import com.xxl.sso.core.store.impl.RedisLoginStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * xxl-sso server config
 *
 * <p>
 * 认证中心装配入口：根据 application.properties 构建 RedisLoginStore 与 XxlSsoBootstrap，
 * 并注入 token、CAS 回跳白名单等配置。
 * </p>
 *
 * @author xuxueli 2018-04-03 20:41:07
 */
@Configuration
public class XxlSsoConfig {


    /**
     * token key, used as cookie-key and header-key
     */
    @Value("${xxl-sso.token.key}")
    private String tokenKey;

    /**
     * token timeout, seconds
     */
    @Value("${xxl-sso.token.timeout}")
    private long tokenTimeout;

    /**
     * redis nodes, like "127.0.0.1:6379"
     */
    @Value("${xxl-sso.store.redis.nodes}")
    private String redisNodes;

    /**
     * redis user, nullable
     */
    @Value("${xxl-sso.store.redis.user}")
    private String redisUser;

    /**
     * redis password, nullable
     */
    @Value("${xxl-sso.store.redis.password}")
    private String redisPassword;

    /**
     * redis store key prefix
     */
    @Value("${xxl-sso.store.redis.keyprefix}")
    private String redisKeyprefix;

    /**
     * cas redirect whitelist, comma separated, support AntPathMatcher; blank means no validation
     */
    @Value("${xxl-sso.cas.redirect-whitelist:}")
    private String casWhitelist;


    /**
     * build xxl-sso bootstrap
     *
     * @return xxl-sso bootstrap
     */
    @Bean(initMethod = "start", destroyMethod = "stop")
    public XxlSsoBootstrap xxlSsoBootstrap() {
        XxlSsoBootstrap bootstrap = new XxlSsoBootstrap();
        bootstrap.setLoginStore(new RedisLoginStore(
                redisNodes,
                redisUser,
                redisPassword,
                redisKeyprefix));
        bootstrap.setTokenKey(tokenKey);
        bootstrap.setTokenTimeout(tokenTimeout);
        bootstrap.setCasWhitelist(casWhitelist);

        return bootstrap;
    }


}
