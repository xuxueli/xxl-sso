package com.xxl.sso.sample.config;

import com.xxl.sso.core.auth.interceptor.XxlSsoCasInterceptor;
import com.xxl.sso.core.auth.interceptor.XxlSsoWebInterceptor;
import com.xxl.sso.core.bootstrap.XxlSsoBootstrap;
import com.xxl.sso.core.store.impl.RedisLoginStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author xuxueli 2018-11-15
 */
@Configuration
public class XxlSsoConfig implements WebMvcConfigurer {


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

    @Value("${xxl.sso.server.address}")
    private String serverAddress;

    @Value("${xxl.sso.server.login.path}")
    private String loginPath;

    @Value("${xxl-sso.client.excluded.paths}")
    private String excludedPaths;


    /**
     * 1、配置 XxlSsoBootstrap
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
     * 2、配置 XxlSso 拦截器
     *
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        // 2.1、build xxl-sso interceptor
        XxlSsoCasInterceptor casInterceptor = new XxlSsoCasInterceptor(serverAddress, loginPath, excludedPaths);

        // 2.2、add interceptor
        registry.addInterceptor(casInterceptor).addPathPatterns("/**");
    }

}
