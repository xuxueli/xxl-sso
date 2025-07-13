package com.xxl.sso.sample.config;

import com.xxl.sso.core.bootstrap.XxlSsoBootstrap;
import com.xxl.sso.core.filter.XxlSsoWebFilter;
import com.xxl.sso.core.store.impl.RedisLoginStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author xuxueli 2018-11-15
 */
@Configuration
public class XxlSsoConfig {


    @Value("${xxl-sso.token.key}")
    private String tokenKey;

    @Value("${xxl-sso.token.timeout}")
    private long tokenTimeout;

    @Value("${xxl-sso.store.redis.nodes}")
    private String redisNodes;

    @Value("${xxl-sso.store.redis.user}")
    private String redisUser;

    @Value("${xxl-sso.store.redis.password}")
    private String redisPassword;

    @Value("${xxl-sso.store.redis.keyprefix}")
    private String redisKeyprefix;

    @Value("${xxl-sso.client.excluded.paths}")
    private String excludedPaths;

    @Value("${xxl.sso.client.login.path}")
    private String loginPath;


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


    /**
     * 2、配置 XxlSsoNativeFilter
     *
     * @param bootstrap
     * @return
     */
    @Bean
    public FilterRegistrationBean<XxlSsoWebFilter> xxlSsoFilterRegistration(XxlSsoBootstrap bootstrap) {

        // 2.1、build xxl-sso filter
        XxlSsoWebFilter webFilter = new XxlSsoWebFilter(excludedPaths, loginPath);

        // 2.2、registry filter
        FilterRegistrationBean<XxlSsoWebFilter> registration = new FilterRegistrationBean<>();
        registration.setName("XxlSsoWebFilter");
        registration.setOrder(1);
        registration.addUrlPatterns("/*");
        registration.setFilter(webFilter);

        return registration;
    }


}
