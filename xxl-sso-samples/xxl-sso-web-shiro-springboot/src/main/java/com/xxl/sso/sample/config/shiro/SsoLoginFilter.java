package com.xxl.sso.sample.config.shiro;

import com.xxl.sso.core.conf.Conf;
import com.xxl.sso.core.login.SsoWebLoginHelper;
import com.xxl.sso.core.user.XxlSsoUser;
import com.xxl.sso.core.util.CookieUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.web.servlet.AdviceFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * shiro sso filter
 *
 * @author KisChang
 * @date 2020-04-30
 */
public class SsoLoginFilter extends AdviceFilter {
    private static Logger logger = LoggerFactory.getLogger(SsoLoginFilter.class);

    private String ssoServer;

    public SsoLoginFilter(String ssoServer) {
        this.ssoServer = ssoServer;
    }

    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        // make url
//        String servletPath = req.getServletPath();
//        System.out.println("SsoLoginFilter >>>" + servletPath);

        // valid login user, cookie + redirect
        XxlSsoUser xxlUser = SsoWebLoginHelper.loginCheck(req, res);

        // valid login fail
        if (xxlUser == null) {
            ShiroSsoUtils.issueFailLogin(ssoServer, req, res);
            return false;
        }else {
            // ser sso user
            request.setAttribute(Conf.SSO_USER, xxlUser);
            req.getSession().setAttribute(Conf.SSO_USER, xxlUser);
            if (!SecurityUtils.getSubject().isAuthenticated()){
                //没有登录
                String cookieSessionId = CookieUtil.getValueWithReq(req, Conf.SSO_SESSIONID);
                if (cookieSessionId != null){
                    SsoWebLoginHelper.login(res, cookieSessionId, xxlUser, false);
                    try {
                        // shiro auto login
                        SsoToken ssoToken = new SsoToken(cookieSessionId);
                        SecurityUtils.getSubject().login(ssoToken);
                        //跳转到成功页
                        res.sendRedirect("/");
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                return false;
            }else {
                // already login, allow
                return true;
            }
        }
    }

}
