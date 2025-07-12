package com.xxl.sso.core.store;

import com.xxl.sso.core.model.LoginInfo;
import com.xxl.tool.response.Response;

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
     * @param   loginInfo
     * @param   tokenTimeout
     * @return  Response#data is token
     */
    public Response<String> set(LoginInfo loginInfo, long tokenTimeout);

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
    public Response<String> remove(String token);

}
