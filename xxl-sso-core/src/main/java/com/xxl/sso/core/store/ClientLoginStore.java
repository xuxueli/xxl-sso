package com.xxl.sso.core.store;

import com.xxl.sso.core.user.XxlUser;

import java.util.concurrent.ConcurrentHashMap;

/**
 * local login store
 *
 * @author xuxueli 2018-04-02 20:03:11
 */
public class ClientLoginStore {

    private static ConcurrentHashMap<String, XxlUser> loginStore = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, Long> loginStoreTim = new ConcurrentHashMap<>();

    public static XxlUser get(String sessionId) {

        // valid info
        Long timeoutTim = loginStoreTim.get(sessionId);
        if (timeoutTim!=null && timeoutTim > System.currentTimeMillis()) {
            return loginStore.get(sessionId);
        }

        // remove invalid info
        remove(sessionId);
        return null;
    }

    /**
     * TODO, listener offline broadcasd
     *
     * @param sessionId
     */
    public static void remove(String sessionId) {
        loginStore.remove(sessionId);
        loginStoreTim.remove(sessionId);
    }

    public static void put(String sessionId, XxlUser xxlUser) {
        loginStore.put(sessionId, xxlUser);

        long timeoutTim = System.currentTimeMillis() + (5 * 60 * 1000);
        loginStoreTim.put(sessionId, timeoutTim);
    }


}
