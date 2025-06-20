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
     * generate store key
     *
     * @param loginInfo
     * @return
     */
    private String generateStoreKey(LoginInfo loginInfo) {
        return loginInfo.getUserId();
    }

    @Override
    public boolean set(LoginInfo loginInfo) {
        if (loginInfo == null
                || StringTool.isBlank(loginInfo.getUserId())
                || loginInfo.getExpireTime() < System.currentTimeMillis()) {
            return false;
        }

        // write
        String storeKey = generateStoreKey(loginInfo);
        loginStore.put(storeKey, loginInfo);
        return true;
    }

    @Override
    public LoginInfo get(String token) {
        if (token==null) {
            return null;
        }

        // parse store key
        LoginInfo tokeyLoginInfo = TokenHelper.parseToken(token);
        if (tokeyLoginInfo == null) {
            return null;
        }

        // read
        String storeKey = generateStoreKey(tokeyLoginInfo);
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
        if (token==null) {
            return false;
        }

        // parse store key
        LoginInfo tokeyLoginInfo = TokenHelper.parseToken(token);
        if (tokeyLoginInfo == null) {
            return false;
        }

        // remove
        String storeKey = generateStoreKey(tokeyLoginInfo);
        loginStore.remove(storeKey);
        return true;
    }

}
