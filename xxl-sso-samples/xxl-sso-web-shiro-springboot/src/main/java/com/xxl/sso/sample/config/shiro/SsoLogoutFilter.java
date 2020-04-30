package com.xxl.sso.sample.config.shiro;

import com.xxl.sso.core.conf.Conf;
import com.xxl.sso.core.login.SsoWebLoginHelper;
import org.apache.shiro.session.SessionException;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.LogoutFilter;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

/**
 * 登出功能
 *
 * @author KisChang
 * @date 2019-05-20
 */
public class SsoLogoutFilter extends LogoutFilter {

    private static final Logger logger = LoggerFactory.getLogger(SsoLogoutFilter.class);

    private String ssoServer;

    public SsoLogoutFilter() {
    }

    public SsoLogoutFilter(String ssoServer) {
        this.ssoServer = ssoServer;
    }

    @Override
    public boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        Subject subject = getSubject(request, response);

        System.out.println("SsoLogoutFilter ");
        // Check if POST only logout is enabled
        if (isPostOnlyLogout()) {

            // check if the current request's method is a POST, if not redirect
            if (!WebUtils.toHttp(request).getMethod().toUpperCase(Locale.ENGLISH).equals("POST")) {
                return onLogoutRequestNotAPost(request, response);
            }
        }

        //try/catch added for SHIRO-298:
        try {
            subject.logout();
        } catch (SessionException ignored) {
        }

        // remove cookie
        SsoWebLoginHelper.removeSessionIdByCookie(req, res);

        // redirect logout
        String logoutPageUrl = ssoServer.concat(Conf.SSO_LOGOUT);
        res.sendRedirect(logoutPageUrl);

        return false;
    }

    public void setSsoServer(String ssoServer) {
        this.ssoServer = ssoServer;
    }
}
