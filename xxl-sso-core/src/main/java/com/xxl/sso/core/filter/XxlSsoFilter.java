package com.xxl.sso.core.filter;

import com.xxl.sso.core.conf.Conf;
import com.xxl.sso.core.user.XxlUser;
import com.xxl.sso.core.util.ClientLoginHelper;
import com.xxl.sso.core.util.HttpClientUtil;
import com.xxl.sso.core.util.JacksonUtil;
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

            ClientLoginHelper.logout(req, res);

            String logoutPageUrl = ssoServer.concat(Conf.SSO_LOGOUT);

            res.sendRedirect(logoutPageUrl);
            return;
        }

        // login filter
        String paramSessionId = request.getParameter(Conf.SSO_SESSIONID);
        String cookieSessionId = ClientLoginHelper.cookieSessionId(req);

        String sessionId = paramSessionId;
        if (sessionId == null) {
            sessionId = cookieSessionId;
        }

        XxlUser xxlUser = ClientLoginHelper.loginCheck(sessionId);
        if (xxlUser == null) {

            // login check
            String loginCheckUrl = ssoServer.concat(Conf.SSO_LOGIN_CHECK)
                    + "?sessionId=" + sessionId;

            String resultStr = HttpClientUtil.post(loginCheckUrl, null);
            if (resultStr!=null && resultStr.trim().length()>0) {
                xxlUser = JacksonUtil.readValue(resultStr, XxlUser.class);
            }

            if (xxlUser == null) {

                // login check fail, to login
                String loginPageUrl = ssoServer.concat(Conf.SSO_LOGIN)
                        + "?" + Conf.REDIRECT_URL + "=" + link;

                res.sendRedirect(loginPageUrl);
                return;
            }

            ClientLoginHelper.login(res, sessionId, xxlUser);

        }
        request.setAttribute(Conf.SSO_USER, xxlUser);

        // already login, allow
        chain.doFilter(request, response);
        return;
    }

}
