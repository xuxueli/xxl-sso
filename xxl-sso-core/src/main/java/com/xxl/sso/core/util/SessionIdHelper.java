package com.xxl.sso.core.util;

import com.xxl.sso.core.user.XxlSsoUser;

import java.util.UUID;

/**
 * @author xuxueli 2018-11-15
 */
public class SessionIdHelper {

    @Deprecated
    public static String makeSessionId(){
        String sessionId = UUID.randomUUID().toString();
        return sessionId;
    }


    public enum SessionIdGroup{
        WEB,APP;
    }

    /**
     * make sessionId
     *
     * @param sessionIdGroup    The same group shares the login status, Different groups will not interact
     * @param xxlSsoUser
     * @return  like "WEB#10001#xsadfaslzxlkdalkdf"
     */
    public static String makeSessionId2(SessionIdGroup sessionIdGroup, XxlSsoUser xxlSsoUser){
        String sessionId = sessionIdGroup.name()
                .concat("#").concat(String.valueOf(xxlSsoUser.getUserid()))
                .concat("#").concat(UUID.randomUUID().toString());

        // TODO, redis key (WEB#10001, -1), redis value(user.sessionidlist=WEB#)
        /**
         * TODO
         *      client： sessionid = group#userid#uuid
         *      server： key = group#userid
         *               value= user.uuid (key exist && uuid equal, login success)
         */

        return sessionId;
    }

}
