package com.xxl.sso.core.store;

import com.xxl.sso.core.user.XxlSsoUser;

/**
 * make client sessionId
 *
 *      client: cookie = [userid#version]
 *      server: redis
 *                  key = [userid]
 *                  value = user (user.version, valid this)
 *
 * //   group         The same group shares the login status, Different groups will not interact
 *
 * @author xuxueli 2018-11-15 15:45:08
 */

public class SsoSessionIdHelper {


    /**
     * make client sessionId
     *
     * @param xxlSsoUser
     * @return
     */
    public static String makeSessionId(XxlSsoUser xxlSsoUser){
        String sessionId = xxlSsoUser.getUserid().concat("_").concat(xxlSsoUser.getVersion());
        return sessionId;
    }

    /**
     * parse storeKey from sessionId
     *
     * @param sessionId
     * @return
     */
    public static String parseStoreKey(String sessionId) {
        if (sessionId!=null && sessionId.indexOf("_")>-1) {
            String[] sessionIdArr = sessionId.split("_");
            if (sessionIdArr.length==2
                    && sessionIdArr[0]!=null
                    && sessionIdArr[0].trim().length()>0) {
                String userId = sessionIdArr[0].trim();
                return userId;
            }
        }
        return null;
    }

    /**
     * parse version from sessionId
     *
     * @param sessionId
     * @return
     */
    public static String parseVersion(String sessionId) {
        if (sessionId!=null && sessionId.indexOf("_")>-1) {
            String[] sessionIdArr = sessionId.split("_");
            if (sessionIdArr.length==2
                    && sessionIdArr[1]!=null
                    && sessionIdArr[1].trim().length()>0) {
                String version = sessionIdArr[1].trim();
                return version;
            }
        }
        return null;
    }

}
