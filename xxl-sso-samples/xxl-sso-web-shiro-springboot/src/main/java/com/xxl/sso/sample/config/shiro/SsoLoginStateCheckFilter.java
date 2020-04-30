package com.xxl.sso.sample.config.shiro;

import com.xxl.sso.core.login.SsoWebLoginHelper;
import com.xxl.sso.core.user.XxlSsoUser;
import org.apache.shiro.web.servlet.AdviceFilter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * shiro中的第二个默认过滤器，用于检测是否已登出sso
 *
 * @author KisChang
 * @date 2020-04-30
 */
public class SsoLoginStateCheckFilter extends AdviceFilter {

    private String ssoServer;

    public SsoLoginStateCheckFilter(String ssoServer) {
        this.ssoServer = ssoServer;
    }

    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        XxlSsoUser xxlUser = SsoWebLoginHelper.loginCheck(req, res);
        if (xxlUser == null){
            ShiroSsoUtils.issueFailLogin(ssoServer, req, res);
            return false;
        }else {
            return true;
        }
    }

}
