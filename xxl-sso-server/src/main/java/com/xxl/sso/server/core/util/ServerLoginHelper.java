package com.xxl.sso.server.core.util;

import com.xxl.sso.core.conf.Conf;
import com.xxl.sso.core.user.XxlUser;
import com.xxl.sso.core.util.CookieUtil;
import com.xxl.sso.server.core.loginstore.LoginStore;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

/**
 * @author xuxueli 2018-04-03
 */
public class ServerLoginHelper {

    /**
     * load cookie sessionid
     *
     * @param request
     * @return
     */
    public static String cookieSessionId(HttpServletRequest request) {
        String cookieSessionId = CookieUtil.getValue(request, Conf.SSO_SESSIONID);
        return cookieSessionId;
    }

    /**
     * login check
     *
     * @param request
     * @return
     */
    public static XxlUser loginCheck(HttpServletRequest request){
        String cookieSessionId = cookieSessionId(request);
        return loginCheck(cookieSessionId);
    }

    /**
     * login check
     *
     * @param sessionId
     * @return
     */
    public static XxlUser loginCheck(String  sessionId){
        if (StringUtils.isNotBlank(sessionId)) {
            XxlUser xxlUser = LoginStore.getInstance().get(sessionId);
            if (xxlUser != null) {
                return xxlUser;
            }
        }
        return null;
    }

    /**
     * login
     *
     * @param request
     * @param xxlUser
     * @return
     */
    public static String login(HttpServletRequest request, HttpServletResponse response, XxlUser xxlUser){
        String sessionId = UUID.randomUUID().toString();

        CookieUtil.set(response, Conf.SSO_SESSIONID, sessionId, false);
        LoginStore.getInstance().put(sessionId, xxlUser);

        return sessionId;
    }

    /**
     * logout
     *
     * @param request
     * @param response
     * @return
     */
    public static boolean logout(HttpServletRequest request, HttpServletResponse response){
        String cookieSessionId = cookieSessionId(request);

        if (StringUtils.isNotBlank(cookieSessionId)) {
            LoginStore.getInstance().remove(cookieSessionId);
        }

        CookieUtil.remove(request, response, Conf.SSO_SESSIONID);

        return true;
    }

}
