package com.xxl.sso.sample.config;

import com.xxl.sso.core.util.JedisUtil;
import com.xxl.sso.sample.config.shiro.SsoLoginFilter;
import com.xxl.sso.sample.config.shiro.SsoLoginStateCheckFilter;
import com.xxl.sso.sample.config.shiro.SsoLogoutFilter;
import com.xxl.sso.sample.config.shiro.SsoRealm;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.DelegatingFilterProxy;

import javax.servlet.Filter;
import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class XxlSsoConfig implements DisposableBean, InitializingBean {


    @Value("${xxl.sso.server}")
    private String xxlSsoServer;

    @Value("${xxl.sso.logout.path}")
    private String xxlSsoLogoutPath;

    @Value("${xxl-sso.excluded.paths}")
    private String xxlSsoExcludedPaths;

    @Value("${xxl.sso.redis.address}")
    private String xxlSsoRedisAddress;

    @Bean
    public FilterRegistrationBean filterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new DelegatingFilterProxy("shiroFilter"));
        registration.addUrlPatterns("/*");
        registration.addInitParameter("targetFilterLifecycle", "true");
        registration.addInitParameter("staticSecurityManagerEnabled", "true");
        registration.setName("shiroFilter");
        registration.setEnabled(true);
        return registration;
    }

    public SsoLoginFilter loginFilter(){
        return new SsoLoginFilter(xxlSsoServer);
    }
    public SsoLoginStateCheckFilter loginSsoCheckFilter(){
        return new SsoLoginStateCheckFilter(xxlSsoServer);
    }
    public SsoLogoutFilter logoutFilter(){
        return new SsoLogoutFilter(xxlSsoServer);
    }
    @Bean
    public ShiroFilterFactoryBean shiroFilter(SecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);

        //自定义登录和登出功能
        Map<String, Filter> filters = new LinkedHashMap<>();
        filters.put("loginSsoFilter", loginFilter());
        filters.put("logoutSsoFilter", logoutFilter());
        filters.put("loginSsoCheckFilter", loginSsoCheckFilter());
        shiroFilterFactoryBean.setFilters(filters);

        //拦截器配置
        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<String, String>();
        // 配置不会被拦截的链接 顺序判断
        filterChainDefinitionMap.put("/static/**", "anon");
        //配置登录、退出 过滤器,具体覆盖shiro实现
        filterChainDefinitionMap.put(xxlSsoLogoutPath, "logoutSsoFilter");
        filterChainDefinitionMap.put("/login", "loginSsoFilter");
        //<!-- 过滤链定义，从上向下顺序执行，一般将/**放在最为下边 -->:这是一个坑呢，一不小心代码就不好使了;
        //<!-- authc:所有url都必须认证通过才可以访问; anon:所有url都都可以匿名访问-->
        filterChainDefinitionMap.put("/**", "authc,loginSsoCheckFilter");
        // 设置为login，触发302后，转到loginSsoFilter处理登录流程
        shiroFilterFactoryBean.setLoginUrl("/login");
        // 登录成功后要跳转的链接
        shiroFilterFactoryBean.setSuccessUrl("/");
        //未授权界面;
        shiroFilterFactoryBean.setUnauthorizedUrl("/403");
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        return shiroFilterFactoryBean;
    }

    @Bean
    public Realm myShiroRealm() {
        return new SsoRealm();
    }


    @Bean
    public SecurityManager securityManager(Realm realm) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(realm);
        SecurityUtils.setSecurityManager(securityManager);
        return securityManager;
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        // xxl-sso, redis init
        JedisUtil.init(xxlSsoRedisAddress);
    }

    @Override
    public void destroy() throws Exception {
        // xxl-sso, redis close
        JedisUtil.close();
    }
}
