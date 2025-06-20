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
    public RedisLoginStore(JedisTool jedisTool, String storeKeyPrefix) {
        // valid
        if (StringTool.isBlank(storeKeyPrefix)) {
            storeKeyPrefix = Const.XXL_SSO_USER_STORE_PREFIX;
        }

        // init
        this.jedisTool = jedisTool;
        this.storeKeyPrefix = storeKeyPrefix;
    }


    /**
     * generate store key
     *
     * @param loginInfo
     * @return
     */
    private String generateStoreKey(LoginInfo loginInfo) {
        return storeKeyPrefix + loginInfo.getUserId();
    }

    @Override
    public boolean set(LoginInfo loginInfo) {
        if (loginInfo == null
                || StringTool.isBlank(loginInfo.getUserId())
                || loginInfo.getExpireTime() < System.currentTimeMillis()) {
            return false;
        }
        long seconds = (loginInfo.getExpireTime() - System.currentTimeMillis()) / 1000;

        // write
        String storeKey = generateStoreKey(loginInfo);
        jedisTool.set(storeKey, loginInfo, seconds);
        return true;
    }

    @Override
    public LoginInfo get(String token) {
        if (token==null) {
            return null;
        }

        // parse store key
        LoginInfo tokenLoginInfo = TokenHelper.parseToken(token);
        if (tokenLoginInfo == null) {
            return null;
        }

        // read
        String storeKey = generateStoreKey(tokenLoginInfo);
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
        if (token==null) {
            return false;
        }

        // parse store key
        LoginInfo tokenLoginInfo = TokenHelper.parseToken(token);
        if (tokenLoginInfo == null) {
            return false;
        }

        // remove
        String storeKey = generateStoreKey(tokenLoginInfo);
        jedisTool.del(storeKey);
        return true;
    }

}
