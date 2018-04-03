package com.xxl.sso.core.util;

import com.xxl.sso.core.conf.Conf;
import com.xxl.sso.core.store.ClientLoginStore;
import com.xxl.sso.core.user.XxlUser;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author xuxueli 2018-04-03
 */
public class ClientLoginHelper {


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
     * @param sessionId
     * @return
     */
    public static XxlUser loginCheck(String  sessionId){
        if (sessionId != null) {
            XxlUser xxlUser = ClientLoginStore.get(sessionId);
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

        ClientLoginStore.put(sessionId, xxlUser);
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
            ClientLoginStore.remove(cookieSessionId);
        }
        CookieUtil.remove(request, response, Conf.SSO_SESSIONID);
    }

}
