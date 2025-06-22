package com.xxl.sso.core.store.impl;

import com.xxl.sso.core.model.LoginInfo;
import com.xxl.sso.core.store.LoginStore;
import com.xxl.sso.core.token.TokenHelper;
import com.xxl.tool.core.StringTool;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author xuxueli 2018-04-02 20:03:11
 */
public class LocalLoginStore implements LoginStore {

    private final ConcurrentMap<String, LoginInfo> loginStore = new ConcurrentHashMap<>();

    /**
     * parse store key from token
     * @param token
     * @return
     */
    private String parseStoreKey(String token){
        // valid
        if (token == null) {
            return null;
        }

        // parse store key
        LoginInfo tokeyLoginInfo = TokenHelper.parseToken(token);
        if (tokeyLoginInfo == null) {
            return null;
        }

        return tokeyLoginInfo.getUserId();
    }

    @Override
    public void start() {
    }

    @Override
    public void stop() {
    }

    @Override
    public boolean set(String token, LoginInfo loginInfo) {

        // parse storeKey
        String storeKey = parseStoreKey(token);
        if (StringTool.isBlank(storeKey)) {
            return false;
        }

        // valid loginInfo
        if (loginInfo == null
                || StringTool.isBlank(loginInfo.getUserId())
                || loginInfo.getExpireTime() < System.currentTimeMillis()) {
            return false;
        }

        // write
        loginStore.put(storeKey, loginInfo);
        return true;
    }

    @Override
    public LoginInfo get(String token) {
        // parse storeKey
        String storeKey = parseStoreKey(token);
        if (StringTool.isBlank(storeKey)) {
            return null;
        }

        // read
        LoginInfo loginInfo = loginStore.get(storeKey);

        // valid expire time
        if (loginInfo!=null && loginInfo.getExpireTime() < System.currentTimeMillis()) {
            loginStore.remove(storeKey);
            return null;
        }
        return loginInfo;
    }

    @Override
    public boolean remove(String token) {
        // parse storeKey
        String storeKey = parseStoreKey(token);
        if (StringTool.isBlank(storeKey)) {
            return false;
        }

        // remove
        loginStore.remove(storeKey);
        return true;
    }

}
