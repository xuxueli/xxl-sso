package com.xxl.sso.sample.config.shiro;

import com.xxl.sso.core.conf.Conf;
import com.xxl.sso.core.login.SsoWebLoginHelper;
import com.xxl.sso.core.user.XxlSsoUser;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.servlet.AdviceFilter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author KisChang
 * @date 2020-04-30
 */
public class SsoShiroLoginStateCheckFilter extends AdviceFilter {

    private String ssoServer;

    public SsoShiroLoginStateCheckFilter(String ssoServer) {
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
