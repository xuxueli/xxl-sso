package com.xxl.sso.core.store;

import com.xxl.sso.core.exception.XxlSsoException;
import com.xxl.sso.core.model.LoginInfo;
import com.xxl.tool.response.Response;

/**
 * login store
 *
 * <p>
 * 登录态持久化 SPI，负责 LoginInfo 的写入、更新、删除与读取，并可选支持 CAS 票据。
 * 实现需明确序列化方式、过期语义、并发安全与 {@link #start()} / {@link #stop()} 资源释放。
 * </p>
 *
 * <p>
 * 时间单位统一为秒：{@link LoginInfo#getExpireTime()} 为 epoch seconds，票据 {@code ticketTimeout} 为秒。
 * 存储实现必须按秒进行过期判断与 TTL 计算。
 * </p>
 *
 * @author xuxueli 2018-04-02 20:03:11
 */
public interface LoginStore {


    // ---------------------- for init ----------------------

    /**
     * start store, default do nothing
     */
    default void start() {
        // default do nothing
    }

    /**
     * stop store and release resources, default do nothing
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
     * <p>
     * 仅更新已存在的登录态；实现不得覆盖 {@code userId} 与 {@code signature}。
     * </p>
     *
     * @param loginInfo     loginInfo
     * @return  response, fail when loginInfo is invalid or not exists
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
     * @return Response.data is LoginInfo; fail when userId is blank, not exists or expired
     */
    Response<LoginInfo> get(String userId);


    // ---------------------- for cas ----------------------

    /**
     * create ticket of token
     *
     * @param ticket        ticket value
     * @param token         the token of ticket
     * @param ticketTimeout for second, limit 1s - 3min
     * @return Response.data is ticket
     */
    default Response<String> createTicket(String ticket, String token, long ticketTimeout) {
        throw new XxlSsoException("default not support.");
    }

    /**
     * valid ticket of token
     *
     * <p>
     * 票据一次性使用，校验成功后应立即失效，避免重放。
     * </p>
     *
     * @param ticket cas ticket
     * @return Response.data is token; fail when ticket is blank, not exists or expired
     */
    default Response<String> validTicket(String ticket) {
        throw new XxlSsoException("default not support.");
    }

}
