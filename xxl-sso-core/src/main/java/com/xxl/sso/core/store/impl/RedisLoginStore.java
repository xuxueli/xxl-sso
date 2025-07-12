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
     *
     * @param tokenLoginInfo
     * @return
     */
    private String parseStoreKey(LoginInfo tokenLoginInfo){
        if (tokenLoginInfo == null) {
            return null;
        }

        return storeKeyPrefix + tokenLoginInfo.getUserId();
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
    public Response<String> set(LoginInfo loginInfo, long tokenTimeout) {

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

        // generate token
        String token = TokenHelper.generateToken(loginInfo);

        // parse storeKey
        String storeKey = parseStoreKey(TokenHelper.parseToken(token));
        if (StringTool.isBlank(storeKey)) {
            return Response.ofFail("token invalid.");
        }

        // write
        jedisTool.set(storeKey, loginInfo, seconds);
        return Response.ofSuccess(token);
    }

    @Override
    public LoginInfo get(String token) {

        // parse storeKey
        LoginInfo tokenLoginInfo = TokenHelper.parseToken(token);
        String storeKey = parseStoreKey(tokenLoginInfo);
        if (StringTool.isBlank(storeKey)) {
            return null;
        }
        String version = tokenLoginInfo.getVersion();

        // read
        LoginInfo loginInfo = (LoginInfo) jedisTool.get(storeKey);

        // valid
        if (loginInfo != null) {
            // valid expire time
            if (loginInfo.getExpireTime() < System.currentTimeMillis()) {
                jedisTool.del(storeKey);
                return null;
            }
            // valid version if inconsistent
            if (loginInfo.getVersion()!=null && !loginInfo.getVersion().equals(version)){
                return null;    // Non-empty and inconsistent, intercept, intercept it
            }
        }

        return loginInfo;
    }

    @Override
    public Response<String> remove(String token) {
        // parse storeKey
        String storeKey = parseStoreKey(TokenHelper.parseToken(token));
        if (StringTool.isBlank(storeKey)) {
            return Response.ofFail("token is invalid");
        }

        // remove
        jedisTool.del(storeKey);
        return Response.ofSuccess();
    }

}
