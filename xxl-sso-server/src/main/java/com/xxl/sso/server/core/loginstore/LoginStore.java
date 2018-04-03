package com.xxl.sso.server.core.loginstore;

import com.xxl.sso.core.user.XxlUser;
import com.xxl.sso.server.core.loginstore.impl.RedisLoginStore;

/**
 * login store
 *
 * @author xuxueli 2018-04-02 20:07:46
 */
public abstract class LoginStore {

    private static LoginStore loginStore = new RedisLoginStore();

    public static LoginStore getInstance(){
        return loginStore;
    }


    /**
     * load login user
     *
     * @param sessionId
     * @return
     */
    public abstract XxlUser get(String sessionId);

    /**
     * remove login user
     *
     * @param sessionId
     */
    public abstract void remove(String sessionId);

    /**
     * put login user
     *
     * @param sessionId
     * @param xxlUser
     */
    public abstract void put(String sessionId, XxlUser xxlUser);

}
