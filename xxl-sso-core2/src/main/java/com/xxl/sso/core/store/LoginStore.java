package com.xxl.sso.core.store;

import com.xxl.sso.core.model.LoginInfo;

/**
 * login store
 *
 * @author xuxueli 2018-04-02 20:03:11
 */
public interface LoginStore {

    /**
     * set login info
     *
     * @param loginInfo
     */
    public boolean set(LoginInfo loginInfo);

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
