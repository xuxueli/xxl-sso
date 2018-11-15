package com.xxl.sso.core.login;

import com.xxl.sso.core.conf.Conf;
import com.xxl.sso.core.store.SsoLoginStore;
import com.xxl.sso.core.user.XxlSsoUser;
import com.xxl.sso.core.util.CookieUtil;
import com.xxl.sso.core.util.SsoSessionIdHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author xuxueli 2018-04-03
 */
public class SsoWebLoginHelper {

    /**
     * client login
     *
     * @param response
     * @param sessionId
     * @param xxlUser
     */
    public static void login(HttpServletResponse response,
                             String sessionId,
                             XxlSsoUser xxlUser) {

        String storeKey = SsoSessionIdHelper.parseStoreKey(sessionId);
        if (storeKey == null) {
            return;
        }

        SsoLoginStore.put(storeKey, xxlUser);
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

        String cookieSessionId = CookieUtil.getValue(request, Conf.SSO_SESSIONID);
        if (cookieSessionId==null) {
            return;
        }

        String storeKey = SsoSessionIdHelper.parseStoreKey(cookieSessionId);
        if (storeKey != null) {
            SsoLoginStore.remove(storeKey);
        }

        CookieUtil.remove(request, response, Conf.SSO_SESSIONID);
    }



    /**
     * login check
     *
     * @param request
     * @param response
     * @return
     */
    public static XxlSsoUser loginCheck(HttpServletRequest request, HttpServletResponse response){

        String cookieSessionId = CookieUtil.getValue(request, Conf.SSO_SESSIONID);

        // cookie user
        XxlSsoUser xxlUser = SsoTokenLoginHelper.loginCheck(cookieSessionId);
        if (xxlUser != null) {
            return xxlUser;
        }

        // redirect user

        // remove old cookie
        SsoWebLoginHelper.removeSessionIdByCookie(request, response);

        // set new cookie
        String paramSessionId = request.getParameter(Conf.SSO_SESSIONID);
        xxlUser = SsoTokenLoginHelper.loginCheck(paramSessionId);
        if (xxlUser != null) {
            CookieUtil.set(response, Conf.SSO_SESSIONID, paramSessionId, false);
            return xxlUser;
        }

        return null;
    }


    /**
     * client logout, cookie only
     *
     * @param request
     * @param response
     */
    public static void removeSessionIdByCookie(HttpServletRequest request, HttpServletResponse response) {
        CookieUtil.remove(request, response, Conf.SSO_SESSIONID);
    }

    /**
     * get sessionid by cookie
     *
     * @param request
     * @return
     */
    public static String getSessionIdByCookie(HttpServletRequest request) {
        String cookieSessionId = CookieUtil.getValue(request, Conf.SSO_SESSIONID);
        return cookieSessionId;
    }


}
