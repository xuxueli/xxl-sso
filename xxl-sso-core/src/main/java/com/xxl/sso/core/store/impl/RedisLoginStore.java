package com.xxl.sso.core.store.impl;

import com.xxl.sso.core.constant.Const;
import com.xxl.sso.core.model.LoginInfo;
import com.xxl.sso.core.store.LoginStore;
import com.xxl.sso.core.token.TokenHelper;
import com.xxl.sso.core.util.JedisTool;
import com.xxl.tool.core.StringTool;
import com.xxl.tool.response.Response;

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
    public Response<String> set(String token, LoginInfo loginInfo, long tokenTimeout) {

        // parse storeKey
        String storeKey = parseStoreKey(token);
        if (StringTool.isBlank(storeKey)) {
            return Response.ofFail("token invalid.");
        }

        // valid loginInfo
        if (loginInfo == null
                || StringTool.isBlank(loginInfo.getUserId())
                || StringTool.isBlank(loginInfo.getUserName())) {
            return Response.ofFail("loginInfo invalid.");
        }

        // process expire time
        long expireTime = System.currentTimeMillis() + tokenTimeout;
        if (expireTime < System.currentTimeMillis()) {
            return Response.ofFail("expireTime invalid.");
        }
        loginInfo.setExpireTime(expireTime);

        // redis timeout (seconds)
        long seconds = (loginInfo.getExpireTime() - System.currentTimeMillis()) / 1000;

        // write
        jedisTool.set(storeKey, loginInfo, seconds);
        return Response.ofSuccess(token);
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
    public Response<String> remove(String token) {
        // parse storeKey
        String storeKey = parseStoreKey(token);
        if (StringTool.isBlank(storeKey)) {
            return Response.ofFail("token is invalid");
        }

        // remove
        jedisTool.del(storeKey);
        return Response.ofSuccess();
    }

}
