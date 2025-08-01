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
     * set LoginInfo
     *
     * @param   loginInfo   will be stored with key-userId
     * @return
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
     * @param userId     the userId of LoginInfo
     * @return
     */
    public Response<LoginInfo> get(String userId);

    /**
     * remove LoginInfo
     *
     * @param userId    the userId of LoginInfo
     * @return
     */
    public Response<String> remove(String userId);


    // ---------------------- for cas ----------------------

    /**
     * create ticket of token
     *
     * @param   userId          the userId of ticket
     * @param   token           the token of ticket
     * @param   ticketTimeout   for millisecond, limit 1s - 3min
     * @return  Response.data is ticket
     */
    public Response<String> createTicket(String userId, String token, long ticketTimeout);

    /**
     * valid ticket of token
     *
     * @param   ticket          cas ticket
     * @return  Response.data is token
     */
    public Response<String> validTicket(String ticket);

}
