package com.xxl.sso.core.store;

import com.xxl.sso.core.model.LoginInfo;
import com.xxl.tool.response.Response;

/**
 * login store
 *
 * @author xuxueli 2018-04-02 20:03:11
 */
public interface LoginStore {


    // ---------------------- for init ----------------------

    /**
     * start
     */
    public void start();

    /**
     * stop
     */
    public void stop();


    // ---------------------- for token ----------------------

    /**
     * set LoginInfo, and generate token
     *
     * @param   loginInfo
     * @return  Response#data is token
     */
    public Response<String> set(LoginInfo loginInfo);

    /**
     * update LoginInfo
     *
     * @param loginInfo
     * @return
     */
    public Response<String> update(LoginInfo loginInfo);

    /**
     * get LoginInfo
     *
     * @param token
     * @return
     */
    public Response<LoginInfo> get(String token);

    /**
     * remove LoginInfo
     *
     * @param token
     * @return
     */
    public Response<String> remove(String token);


    // ---------------------- for cas ----------------------

    /**
     * create ticket of token
     *
     * @param   token           token
     * @param   ticketTimeout   for millisecond, limit 1s - 3min
     * @return  Response.data is ticket
     */
    public Response<String> createTicket(String token, long ticketTimeout);

    /**
     * valid ticket of token
     *
     * @param   ticket          cas ticket
     * @return  Response.data is token
     */
    public Response<String> validTicket(String ticket);

}
