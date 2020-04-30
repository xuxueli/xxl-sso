package com.xxl.sso.sample.config.shiro;

import com.xxl.sso.core.conf.Conf;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 工具方法
 *
 * @author KisChang
 * @date 2020-04-30
 */
public class ShiroSsoUtils {

    /**
     * 登出，并跳转sso server
     * @param ssoServer
     * @param req
     * @param res
     * @throws IOException
     */
    public static void issueFailLogin(String ssoServer, HttpServletRequest req, HttpServletResponse res) throws IOException {
        Subject subject = SecurityUtils.getSubject();
        if (subject.isAuthenticated()){
            subject.logout();
        }

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
    }

}
