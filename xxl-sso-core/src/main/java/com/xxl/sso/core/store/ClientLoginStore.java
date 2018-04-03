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

    public static XxlUser get(String sessionId) {
        return loginStore.get(sessionId);
    }

    /**
     * TODO, listener offline broadcasd
     *
     * @param sessionId
     */
    public static void remove(String sessionId) {
        loginStore.remove(sessionId);
    }

    public static void put(String sessionId, XxlUser xxlUser) {
        loginStore.put(sessionId, xxlUser);
    }


}
