package com.xxl.sso.core.store;

import com.xxl.sso.core.model.LoginInfo;

/**
 * login store
 *
 * @author xuxueli 2018-04-02 20:03:11
 */
public interface LoginStore {

    /**
     * start
     */
    public void start();

    /**
     * stop
     */
    public void stop();

    /**
     * set login info
     *
     * @param loginInfo
     */
    public boolean set(String token, LoginInfo loginInfo);

    /**
     * get login info
     *
     * @param token
     * @return
     */
    public LoginInfo get(String token);

    /**
     * remove login info
     *
     * @param token
     * @return
     */
    public boolean remove(String token);

}
