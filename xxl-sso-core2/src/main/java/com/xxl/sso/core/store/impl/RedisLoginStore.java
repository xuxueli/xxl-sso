package com.xxl.sso.core.store.impl;

import com.xxl.sso.core.constant.Const;
import com.xxl.sso.core.model.LoginInfo;
import com.xxl.sso.core.store.LoginStore;
import com.xxl.sso.core.token.TokenHelper;
import com.xxl.sso.core.util.JedisTool;
import com.xxl.tool.core.StringTool;

/**
 * @author xuxueli 2018-04-02 20:03:11
 */
public class RedisLoginStore implements LoginStore {

    private JedisTool jedisTool;
    private String storeKeyPrefix;
    public RedisLoginStore(String nodes, String user, String password, String storeKeyPrefix) {
        // valid
        if (StringTool.isBlank(storeKeyPrefix)) {
            storeKeyPrefix = Const.XXL_SSO_USER_STORE_PREFIX;
        }

        // init
        this.jedisTool = new JedisTool(nodes, user, password);
        this.storeKeyPrefix = storeKeyPrefix;
    }


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

        return storeKeyPrefix + tokeyLoginInfo.getUserId();
    }

    @Override
    public void start() {
        jedisTool.start();
    }

    @Override
    public void stop() {
        jedisTool.stop();
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
        long seconds = (loginInfo.getExpireTime() - System.currentTimeMillis()) / 1000;

        // write
        jedisTool.set(storeKey, loginInfo, seconds);
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
        LoginInfo loginInfo = (LoginInfo) jedisTool.get(storeKey);

        // valid expire time
        if (loginInfo!=null && loginInfo.getExpireTime() < System.currentTimeMillis()) {
            jedisTool.del(storeKey);
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
        jedisTool.del(storeKey);
        return true;
    }

}
