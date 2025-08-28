package com.xxl.sso.sample.config;

import com.xxl.sso.core.util.JedisUtil;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;

/**
 * @author xuxueli 2018-11-15
 */
@Configuration
public class XxlGatewaySsoConfig implements DisposableBean, ApplicationContextAware {

    @Value("${xxl.sso.redis.address}")
    private String xxlSsoRedisAddress;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        // xxl-sso, redis init
        JedisUtil.init(xxlSsoRedisAddress);
    }

    @Override
    public void destroy() throws Exception {

        // xxl-sso, redis close
        JedisUtil.close();
    }

}
