//package com.xxl.sso.core.login;
//
//import com.xxl.sso.core.constant.Const;
//import com.xxl.sso.core.store.SsoLoginStore;
//import com.xxl.sso.core.user.XxlSsoUser;
//import com.xxl.sso.core.util.CookieUtil;
//import com.xxl.sso.core.store.SsoSessionIdHelper;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
///**
// * @author xuxueli 2018-04-03
// */
//public class SsoWebLoginHelper {
//
//    /**
//     * client login
//     *
//     * @param response
//     * @param sessionId
//     * @param ifRemember    true: cookie not expire, false: expire when browser close （server cookie）
//     * @param xxlUser
//     */
//    public static void login(HttpServletResponse response,
//                             String sessionId,
//                             XxlSsoUser xxlUser,
//                             boolean ifRemember) {
//
//        String storeKey = SsoSessionIdHelper.parseStoreKey(sessionId);
//        if (storeKey == null) {
//            throw new RuntimeException("parseStoreKey Fail, sessionId:" + sessionId);
//        }
//
//        SsoLoginStore.put(storeKey, xxlUser);
//        CookieUtil.set(response, Const.SSO_TOKEN, sessionId, ifRemember);
//    }
//
//    /**
//     * client logout
//     *
//     * @param request
//     * @param response
//     */
//    public static void logout(HttpServletRequest request,
//                              HttpServletResponse response) {
//
//        String cookieSessionId = CookieUtil.getValue(request, Const.SSO_TOKEN);
//        if (cookieSessionId==null) {
//            return;
//        }
//
//        String storeKey = SsoSessionIdHelper.parseStoreKey(cookieSessionId);
//        if (storeKey != null) {
//            SsoLoginStore.remove(storeKey);
//        }
//
//        CookieUtil.remove(request, response, Const.SSO_TOKEN);
//    }
//
//
//
//    /**
//     * login check
//     *
//     * @param request
//     * @param response
//     * @return
//     */
//    public static XxlSsoUser loginCheck(HttpServletRequest request, HttpServletResponse response){
//
//        String cookieSessionId = CookieUtil.getValue(request, Const.SSO_TOKEN);
//
//        // cookie user
//        XxlSsoUser xxlUser = SsoTokenLoginHelper.loginCheck(cookieSessionId);
//        if (xxlUser != null) {
//            return xxlUser;
//        }
//
//        // redirect user
//
//        // remove old cookie
//        SsoWebLoginHelper.removeSessionIdByCookie(request, response);
//
//        // set new cookie
//        String paramSessionId = request.getParameter(Const.SSO_TOKEN);
//        xxlUser = SsoTokenLoginHelper.loginCheck(paramSessionId);
//        if (xxlUser != null) {
//            CookieUtil.set(response, Const.SSO_TOKEN, paramSessionId, false);    // expire when browser close （client cookie）
//            return xxlUser;
//        }
//
//        return null;
//    }
//
//
//    /**
//     * client logout, cookie only
//     *
//     * @param request
//     * @param response
//     */
//    public static void removeSessionIdByCookie(HttpServletRequest request, HttpServletResponse response) {
//        CookieUtil.remove(request, response, Const.SSO_TOKEN);
//    }
//
//    /**
//     * get sessionid by cookie
//     *
//     * @param request
//     * @return
//     */
//    public static String getSessionIdByCookie(HttpServletRequest request) {
//        String cookieSessionId = CookieUtil.getValue(request, Const.SSO_TOKEN);
//        return cookieSessionId;
//    }
//
//
//}
