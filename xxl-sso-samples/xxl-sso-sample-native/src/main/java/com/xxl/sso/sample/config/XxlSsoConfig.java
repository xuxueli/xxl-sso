package com.xxl.sso.sample.config;

import com.xxl.sso.core.filter.XxlSsoNativeFilter;
import com.xxl.sso.core.helper.XxlSsoHelper;
import com.xxl.sso.core.store.LoginStore;
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


    @Value("${xxl.sso.server.address}")
    private String serverAddress;

    @Value("${xxl-sso.token.key}")
    private String tokenKey;

    @Value("${xxl-sso.token.timeout}")
    private long tokenTimeout;

    @Value("${xxl-sso.client.excluded.paths}")
    private String excludedPaths;

    @Value("${xxl-sso.client.store.type}")
    private String storeType;

    @Value("${xxl-sso.client.store.redis.nodes}")
    private String storeNodes;

    @Value("${xxl-sso.client.store.redis.user}")
    private String storeUser;

    @Value("${xxl-sso.client.store.redis.password}")
    private String storePassword;

    @Value("${xxl-sso.client.store.redis.keyprefix}")
    private String storeKeyprefix;


    /**
     * 1、配置 LoginStore
     */
    private LoginStore loginStore;

    @Bean(initMethod = "start", destroyMethod = "stop")
    public LoginStore loginStore(){
        return new RedisLoginStore(storeNodes, storeUser, storePassword, storeKeyprefix);
    }

    /**
     * 2、配置 XxlSsoNativeFilter
     *
     * @param loginStore
     * @return
     */
    @Bean
    public FilterRegistrationBean xxlSsoFilterRegistration(LoginStore loginStore) {

        // filter init
        XxlSsoNativeFilter xxlSsoNativeFilter = new XxlSsoNativeFilter(excludedPaths, loginStore, tokenKey, tokenTimeout);

        // filter registry
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setName("xxlSsoNativeFilter");
        registration.setOrder(1);
        registration.addUrlPatterns("/*");
        registration.setFilter(xxlSsoNativeFilter);

        return registration;
    }



}
