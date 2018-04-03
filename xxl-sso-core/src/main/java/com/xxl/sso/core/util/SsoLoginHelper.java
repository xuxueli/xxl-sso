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
     * load cookie sessionid
     *
     * @param request
     * @return
     */
    public static String cookieSessionId(HttpServletRequest request) {
        String cookieSessionId = CookieUtil.getValue(request, Conf.SSO_SESSIONID);
        return cookieSessionId;
    }

    public static void cookieSessionIdSet(HttpServletResponse response, String sessionId) {
        if (sessionId!=null && sessionId.trim().length()>0) {
            CookieUtil.set(response, Conf.SSO_SESSIONID, sessionId, false);
        }
    }
    public static void cookieSessionIdRemove(HttpServletRequest request, HttpServletResponse response) {
        CookieUtil.remove(request, response, Conf.SSO_SESSIONID);
    }


    /**
     * login check
     *
     * @param request
     * @return
     */
    public static XxlUser loginCheck(HttpServletRequest request){
        String cookieSessionId = cookieSessionId(request);
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
     * client login
     *
     * @param response
     * @param sessionId
     */
    public static void login(HttpServletResponse response,
                             String sessionId,
                             XxlUser xxlUser) {

        SsoLoginStore.put(sessionId, xxlUser);
        CookieUtil.set(response, Conf.SSO_SESSIONID, sessionId, false);
    }


    /**
     * client logout
     *
     * @param request
     * @param response
     */
    public static void logout(HttpServletRequest request,
                              HttpServletResponse response) {

        String cookieSessionId = cookieSessionId(request);

        if (cookieSessionId != null) {
            SsoLoginStore.remove(cookieSessionId);
        }
        CookieUtil.remove(request, response, Conf.SSO_SESSIONID);
    }

}
