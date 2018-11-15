package com.xxl.sso.core.filter;

import com.xxl.sso.core.conf.Conf;
import com.xxl.sso.core.entity.ReturnT;
import com.xxl.sso.core.login.SsoTokenLoginHelper;
import com.xxl.sso.core.user.XxlSsoUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * app sso filter
 *
 * @author xuxueli 2018-04-08 21:30:54
 */
public class XxlSsoTokenFilter extends HttpServlet implements Filter {
    private static Logger logger = LoggerFactory.getLogger(XxlSsoTokenFilter.class);

    private String ssoServer;
    private String logoutPath;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

        ssoServer = filterConfig.getInitParameter(Conf.SSO_SERVER);
        if (ssoServer!=null && ssoServer.trim().length()>0) {
            logoutPath = filterConfig.getInitParameter(Conf.SSO_LOGOUT_PATH);
        }

        logger.info("XxlSsoTokenFilter init.");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String servletPath = ((HttpServletRequest) request).getServletPath();
        String link = req.getRequestURL().toString();

        XxlSsoUser xxlUser = SsoTokenLoginHelper.loginCheck(req);

        // logout filter
        if (logoutPath!=null
                && logoutPath.trim().length()>0
                && logoutPath.equals(servletPath)) {

            if (xxlUser != null) {
                SsoTokenLoginHelper.logout(req);
            }

            // response
            res.setStatus(HttpServletResponse.SC_OK);
            res.setContentType("application/json;charset=UTF-8");
            res.getWriter().println("{\"code\":"+ReturnT.SUCCESS_CODE+", \"msg\":\"\"}");

            return;
        }

        // login filter
        if (xxlUser == null) {

            // response
            res.setStatus(HttpServletResponse.SC_OK);
            res.setContentType("application/json;charset=UTF-8");
            res.getWriter().println("{\"code\":"+Conf.SSO_LOGIN_FAIL_RESULT.getCode()+", \"msg\":\""+ Conf.SSO_LOGIN_FAIL_RESULT.getMsg() +"\"}");
            return;
        }

        // ser sso user
        request.setAttribute(Conf.SSO_USER, xxlUser);


        // already login, allow
        chain.doFilter(request, response);
        return;
    }


}
