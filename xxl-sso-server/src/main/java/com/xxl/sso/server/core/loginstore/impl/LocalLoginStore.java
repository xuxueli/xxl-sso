package com.xxl.sso.server.core.loginstore.impl;

import com.xxl.sso.core.user.XxlUser;
import com.xxl.sso.server.core.loginstore.LoginStore;

import java.util.concurrent.ConcurrentHashMap;

/**
 * local login store
 *
 * @author xuxueli 2018-04-02 20:03:11
 */
public class LocalLoginStore extends LoginStore {

    private ConcurrentHashMap<String, XxlUser> loginStore = new ConcurrentHashMap<>();

    @Override
    public XxlUser get(String sessionId) {
        return loginStore.get(sessionId);
    }

    @Override
    public void remove(String sessionId) {
        loginStore.remove(sessionId);
    }

    @Override
    public void put(String sessionId, XxlUser xxlUser) {
        loginStore.put(sessionId, xxlUser);
    }

}
