package com.xxl.sso.core.store;

import com.xxl.sso.core.exception.XxlSsoException;
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
    default void start() {
        // default do nothing
    }

    /**
     * stop
     */
    default void stop() {
        // default do nothing
    }


    // ---------------------- for token ----------------------

    /**
     * set LoginInfo
     *
     * @param   loginInfo   will be stored with key-userId
     * @return  response
     */
    Response<String> set(LoginInfo loginInfo);

    /**
     * update LoginInfo
     *
     * @param loginInfo     loginInfo
     * @return  response
     */
    Response<String> update(LoginInfo loginInfo);

    /**
     * remove LoginInfo
     *
     * @param userId    the userId of LoginInfo
     * @return  response
     */
    Response<String> remove(String userId);

    /**
     * get LoginInfo
     *
     * @param userId     the userId of LoginInfo
     * @return Response.data is LoginInfo
     */
    Response<LoginInfo> get(String userId);


    // ---------------------- for cas ----------------------

    /**
     * create ticket of token
     *
     * @param userId        the userId of ticket
     * @param token         the token of ticket
     * @param ticketTimeout for millisecond, limit 1s - 3min
     * @return Response.data is ticket
     */
    default Response<String> createTicket(String userId, String token, long ticketTimeout) {
        throw new XxlSsoException("default not support.");
    }

    /**
     * valid ticket of token
     *
     * @param ticket cas ticket
     * @return Response.data is token
     */
    default Response<String> validTicket(String ticket) {
        throw new XxlSsoException("default not support.");
    }

}
