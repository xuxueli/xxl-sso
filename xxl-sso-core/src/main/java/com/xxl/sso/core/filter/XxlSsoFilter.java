package com.xxl.sso.core.filter;

import com.xxl.sso.core.conf.Conf;
import com.xxl.sso.core.user.XxlUser;
import com.xxl.sso.core.util.SsoLoginHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class XxlSsoFilter extends HttpServlet implements Filter {
    private static Logger logger = LoggerFactory.getLogger(XxlSsoFilter.class);

    private String ssoServer;
    private String logoutPath;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

        ssoServer = filterConfig.getInitParameter(Conf.SSO_SERVER);
        if (ssoServer!=null && ssoServer.trim().length()>0) {
            logoutPath = filterConfig.getInitParameter(Conf.SSO_LOGOUT_PATH);
        }

        logger.info("XxlSsoFilter init.");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String servletPath = ((HttpServletRequest) request).getServletPath();
        String link = req.getRequestURL().toString();

        // logout filter
        if (logoutPath!=null
                && logoutPath.trim().length()>0
                && logoutPath.equals(servletPath)) {

            // remove cookie
            SsoLoginHelper.cookieSessionIdRemove(req, res);

            // redirect logout
            String logoutPageUrl = ssoServer.concat(Conf.SSO_LOGOUT);
            res.sendRedirect(logoutPageUrl);

            return;
        }

        // login filter
        XxlUser xxlUser = null;

        // valid cookie user
        String cookieSessionId = SsoLoginHelper.cookieSessionId(req);
        xxlUser = SsoLoginHelper.loginCheck(cookieSessionId);

        // valid param user, client login
        if (xxlUser == null) {

            // remove old cookie
            SsoLoginHelper.cookieSessionIdRemove(req, res);

            // set new cookie
            String paramSessionId = request.getParameter(Conf.SSO_SESSIONID);
            if (paramSessionId != null) {
                xxlUser = SsoLoginHelper.loginCheck(paramSessionId);
                if (xxlUser != null) {
                    SsoLoginHelper.cookieSessionIdSet(res, paramSessionId);
                }
            }
        }

        // valid login fail
        if (xxlUser == null) {

            // redirect logout

            String loginPageUrl = ssoServer.concat(Conf.SSO_LOGIN)
                    + "?" + Conf.REDIRECT_URL + "=" + link;

            res.sendRedirect(loginPageUrl);
            return;
        }

        // ser sso user
        request.setAttribute(Conf.SSO_USER, xxlUser);


        // already login, allow
        chain.doFilter(request, response);
        return;
    }

}
