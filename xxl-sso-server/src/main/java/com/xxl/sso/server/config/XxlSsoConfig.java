package com.xxl.sso.server.config;

import com.xxl.sso.core.bootstrap.XxlSsoBootstrap;
import com.xxl.sso.core.store.impl.RedisLoginStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author xuxueli 2018-04-03 20:41:07
 */
@Configuration
public class XxlSsoConfig {


    @Value("${xxl.sso.server.address}")
    private String serverAddress;

    @Value("${xxl-sso.token.key}")
    private String tokenKey;

    @Value("${xxl-sso.token.timeout}")
    private long tokenTimeout;

    @Value("${xxl-sso.client.excluded.paths}")
    private String excludedPaths;

    @Value("${xxl-sso.client.store.redis.nodes}")
    private String redisNodes;

    @Value("${xxl-sso.client.store.redis.user}")
    private String redisUser;

    @Value("${xxl-sso.client.store.redis.password}")
    private String redisPassword;

    @Value("${xxl-sso.client.store.redis.keyprefix}")
    private String redisKeyprefix;


    /**
     * 1、配置 LoginStore
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

        return bootstrap;
    }


}
