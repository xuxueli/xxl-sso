package com.xxl.sso.server.core.loginstore.impl;

import com.xxl.sso.core.conf.Conf;
import com.xxl.sso.core.user.XxlUser;
import com.xxl.sso.core.util.JedisUtil;
import com.xxl.sso.server.core.loginstore.LoginStore;

/**
 * @author xuxueli 2018-04-03 21:05:13
 */
public class RedisLoginStore extends LoginStore {

    @Override
    public XxlUser get(String sessionId) {
        String redisKey = redisKey(sessionId);
        Object objectValue = JedisUtil.getObjectValue(redisKey);
        if (objectValue != null) {
            XxlUser xxlUser = (XxlUser) objectValue;
            return xxlUser;
        }
        return null;
    }

    @Override
    public void remove(String sessionId) {
        String redisKey = redisKey(sessionId);
        JedisUtil.del(redisKey);
    }

    @Override
    public void put(String sessionId, XxlUser xxlUser) {
        String redisKey = redisKey(sessionId);
        JedisUtil.setObjectValue(redisKey, xxlUser);
    }


    private static String redisKey(String sessionId){
        return Conf.SSO_SESSIONID.concat("#").concat(sessionId);
    }

}
