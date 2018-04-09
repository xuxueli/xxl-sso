package com.xxl.sso.core.util;

import com.xxl.sso.core.conf.Conf;
import com.xxl.sso.core.store.SsoLoginStore;
import com.xxl.sso.core.user.XxlUser;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author xuxueli 2018-04-03
 */
public class SsoLoginHelper {


    /**
     * get sessionid by cookie (web)
     *
     * @param request
     * @return
     */
    public static String getSessionIdByCookie(HttpServletRequest request) {
        String cookieSessionId = CookieUtil.getValue(request, Conf.SSO_SESSIONID);
        return cookieSessionId;
    }

    /**
     * set sessionid in cookie (web)
     *
     * @param response
     * @param sessionId
     */
    public static void setSessionIdInCookie(HttpServletResponse response, String sessionId) {
        if (sessionId!=null && sessionId.trim().length()>0) {
            CookieUtil.set(response, Conf.SSO_SESSIONID, sessionId, false);
        }
    }

    /**
     * remove sessionId in cookie (web)
     *
     * @param request
     * @param response
     */
    public static void removeSessionIdInCookie(HttpServletRequest request, HttpServletResponse response) {
        CookieUtil.remove(request, response, Conf.SSO_SESSIONID);
    }

    /**
     * load cookie sessionid (app)
     *
     * @param request
     * @return
     */
    public static String cookieSessionIdGetByHeader(HttpServletRequest request) {
        String cookieSessionId = request.getHeader(Conf.SSO_SESSIONID);
        return cookieSessionId;
    }

    /**
     * login check
     *
     * @param request
     * @return
     */
    public static XxlUser loginCheck(HttpServletRequest request){
        String cookieSessionId = getSessionIdByCookie(request);
        if (cookieSessionId!=null && cookieSessionId.trim().length()>0) {
            return loginCheck(cookieSessionId);
        }
        return null;
    }

    /**
     * login check
     *
     * @param sessionId
     * @return
     */
    public static XxlUser loginCheck(String  sessionId){
        if (sessionId!=null && sessionId.trim().length()>0) {
            XxlUser xxlUser = SsoLoginStore.get(sessionId);
            if (xxlUser != null) {
                return xxlUser;
            }
        }
        return null;
    }

    /**
     * client login (web)
     *
     * @param response
     * @param sessionId
     * @param xxlUser
     */
    public static void login(HttpServletResponse response,
                             String sessionId,
                             XxlUser xxlUser) {

        SsoLoginStore.put(sessionId, xxlUser);
        CookieUtil.set(response, Conf.SSO_SESSIONID, sessionId, false);
    }

    /**
     * client login (app)
     *
     * @param sessionId
     * @param xxlUser
     */
    public static void login(String sessionId,
                             XxlUser xxlUser) {
        SsoLoginStore.put(sessionId, xxlUser);
    }


    /**
     * client logout (web)
     *
     * @param request
     * @param response
     */
    public static void logout(HttpServletRequest request,
                              HttpServletResponse response) {

        String cookieSessionId = getSessionIdByCookie(request);

        if (cookieSessionId != null) {
            SsoLoginStore.remove(cookieSessionId);
        }
        CookieUtil.remove(request, response, Conf.SSO_SESSIONID);
    }

    /**
     * client logout (app)
     *
     * @param sessionId
     */
    public static void logout(String sessionId) {
        SsoLoginStore.remove(sessionId);
    }

}
