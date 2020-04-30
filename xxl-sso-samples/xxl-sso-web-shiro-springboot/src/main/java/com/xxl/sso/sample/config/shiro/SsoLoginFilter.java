package com.xxl.sso.sample.config.shiro;

import com.xxl.sso.core.conf.Conf;
import com.xxl.sso.core.login.SsoWebLoginHelper;
import com.xxl.sso.core.path.impl.AntPathMatcher;
import com.xxl.sso.core.user.XxlSsoUser;
import com.xxl.sso.core.util.CookieUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.web.servlet.AdviceFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * shiro sso filter
 */
public class SsoLoginFilter extends AdviceFilter {
    private static Logger logger = LoggerFactory.getLogger(SsoLoginFilter.class);

    private static final AntPathMatcher antPathMatcher = new AntPathMatcher();

    private String ssoServer;
    private String logoutPath;
    private String logoutUrl;

    public SsoLoginFilter(String ssoServer, String logoutPath, String excludedPaths, String logoutUrl) {
        this.ssoServer = ssoServer;
        this.logoutPath = logoutPath;
        this.logoutUrl = logoutUrl;
        logger.info("XxlSsoWebFilter init.");
    }

    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        // make url
        String servletPath = req.getServletPath();

        // valid login user, cookie + redirect
        XxlSsoUser xxlUser = SsoWebLoginHelper.loginCheck(req, res);

        // valid login fail
        if (xxlUser == null) {
            String header = req.getHeader("content-type");
            boolean isJson = header != null && header.contains("json");
            if (isJson) {
                // json msg
                res.setContentType("application/json;charset=utf-8");
                res.getWriter().println("{\"code\":" + Conf.SSO_LOGIN_FAIL_RESULT.getCode() + ", \"msg\":\"" + Conf.SSO_LOGIN_FAIL_RESULT.getMsg() + "\"}");
            } else {
                // total link
                String link = req.getRequestURL().toString();

                // redirect logout
                String loginPageUrl = ssoServer.concat(Conf.SSO_LOGIN)
                        + "?" + Conf.REDIRECT_URL + "=" + link;

                res.sendRedirect(loginPageUrl);
            }
            return false;
        }else {
            // ser sso user
            request.setAttribute(Conf.SSO_USER, xxlUser);
            req.getSession().setAttribute(Conf.SSO_USER, xxlUser);
            if (!SecurityUtils.getSubject().isAuthenticated()){
                //没有登录
                String cookieSessionId = CookieUtil.getValueWithReq(req, Conf.SSO_SESSIONID);
//                String cookieSessionId = CookieUtil.getValue(req, Conf.SSO_SESSIONID);
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
