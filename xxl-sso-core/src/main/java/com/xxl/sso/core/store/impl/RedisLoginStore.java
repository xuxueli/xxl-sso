package com.xxl.sso.core.store.impl;

import com.xxl.sso.core.constant.Const;
import com.xxl.sso.core.model.LoginInfo;
import com.xxl.sso.core.store.LoginStore;
import com.xxl.sso.core.util.JedisTool;
import com.xxl.tool.core.StringTool;
import com.xxl.tool.response.Response;

/**
 * redis login store
 *
 * <p>
 * 基于 {@link JedisTool} 的 Redis 实现，登录态 key 形如 {@code {prefix}:{userId}}，
 * 票据 key 形如 {@code {prefix}:ticket:{ticket}}；依赖 Redis TTL 完成过期控制。
 * </p>
 *
 * <p>
 * 并发安全由 Jedis 客户端与连接池保证；{@link #start()} / {@link #stop()} 负责连接资源的创建与释放。
 * </p>
 *
 * @author xuxueli 2018-04-02 20:03:11
 */
public class RedisLoginStore implements LoginStore {

    /**
     * redis client
     */
    private final JedisTool jedisTool;

    /**
     * store key prefix, endswith ":"
     */
    private final String storeKeyPrefix;

    /**
     * create redis login store
     *
     * @param nodes             redis nodes, like "127.0.0.1:6379" or "host1:port1,host2:port2"
     * @param user              redis user, nullable
     * @param password          redis password, nullable
     * @param storeKeyPrefix    store key prefix, fallback to {@link Const#XXL_SSO_STORE_PREFIX} when blank
     */
    public RedisLoginStore(String nodes, String user, String password, String storeKeyPrefix) {
        // init
        this.jedisTool = new JedisTool(nodes, user, password);
        this.storeKeyPrefix = (StringTool.isNotBlank(storeKeyPrefix)
                ? storeKeyPrefix.trim()
                : Const.XXL_SSO_STORE_PREFIX)
                + ":";
    }


    /**
     * parse store key from userId
     *
     * <pre>
     *     key: "xxl_sso_user:" + {user001}
     *     value: loginInfo
     * </pre>
     *
     * @param userId    user id
     * @return          store key
     */
    private String parseStoreKey(String userId){
        return storeKeyPrefix + userId;
    }

    /**
     * parse store key from ticket
     *
     * <pre>
     *     key: "xxl_sso_user:" + "ticket:" + {ticket}
     *     value: token
     * </pre>
     *
     * @param ticket    ticket
     * @return          ticket store key; {@code null} when ticket is null
     */
    private String parseTicketStoreKey(String ticket){
        if (ticket == null) {
            return null;
        }

        return storeKeyPrefix + "ticket:" + ticket;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void start() {
        jedisTool.start();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop() {
        jedisTool.stop();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Response<String> set(LoginInfo loginInfo) {

        // valid loginInfo
        if (loginInfo == null
                || StringTool.isBlank(loginInfo.getUserId())
                || StringTool.isBlank(loginInfo.getSignature())) {
            return Response.ofFail("loginInfo invalid.");
        }

        // valid expire-time ( seconds )
        long nowSecond = System.currentTimeMillis() / 1000;
        if (loginInfo.getExpireTime() < nowSecond) {
            return Response.ofFail("expireTime invalid.");
        }

        // generate redis timeout (seconds), at least 1s to avoid invalid zero TTL
        long seconds = Math.max(1, loginInfo.getExpireTime() - nowSecond);

        // generate storeKey
        String storeKey = parseStoreKey(loginInfo.getUserId());

        // write
        jedisTool.set(storeKey, loginInfo, seconds);
        return Response.ofSuccess();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Response<String> update(LoginInfo loginInfo) {

        // valid loginInfo
        if (loginInfo == null || StringTool.isBlank(loginInfo.getUserId())) {
            return Response.ofFail("loginInfo invalid.");
        }

        // generate storeKey
        String storeKey = parseStoreKey(loginInfo.getUserId());

        // valid expire-time ( seconds )
        long nowSecond = System.currentTimeMillis() / 1000;
        if (loginInfo.getExpireTime() < nowSecond) {
            return Response.ofFail("expireTime invalid.");
        }

        // read
        LoginInfo loginInfoStore = (LoginInfo) jedisTool.get(storeKey);
        if (loginInfoStore == null) {
            return Response.ofFail("loginInfo not exists.");
        }

        // update LoginInfo
        loginInfoStore.setUserName(loginInfo.getUserName());
        loginInfoStore.setRealName(loginInfo.getRealName());
        loginInfoStore.setExtraInfo(loginInfo.getExtraInfo());
        loginInfoStore.setRoleList(loginInfo.getRoleList());
        loginInfoStore.setPermissionList(loginInfo.getPermissionList());
        loginInfoStore.setExpireTime(loginInfo.getExpireTime());

        // generate redis timeout (seconds), at least 1s to avoid invalid zero TTL
        long seconds = Math.max(1, loginInfo.getExpireTime() - nowSecond);

        // write
        jedisTool.set(storeKey, loginInfoStore, seconds);
        return Response.ofSuccess();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Response<String> remove(String userId) {

        // valid userId
        if (StringTool.isBlank(userId)) {
            return Response.ofFail("userId invalid.");
        }

        // generate storeKey
        String storeKey = parseStoreKey(userId);

        // remove
        jedisTool.del(storeKey);
        return Response.ofSuccess();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Response<LoginInfo> get(String userId) {

        // valid userId
        if (StringTool.isBlank(userId)) {
            return Response.ofFail("userId invalid.");
        }

        // generate storeKey
        String storeKey = parseStoreKey(userId);

        // read
        LoginInfo loginInfo = (LoginInfo) jedisTool.get(storeKey);
        if (loginInfo == null) {
            return Response.ofFail("loginInfo not exists.");
        }

        // valid expire time ( seconds )
        if (loginInfo.getExpireTime() < System.currentTimeMillis() / 1000) {
            jedisTool.del(storeKey);
            return Response.ofFail("loginInfo is timeout");
        }

        return Response.ofSuccess(loginInfo);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Response<String> createTicket(String ticket, String token, long ticketTimeout) {

        // valid param
        if (StringTool.isBlank(ticket)) {
            return Response.ofFail("ticket is invalid");
        }
        if (!(ticketTimeout>=1 && ticketTimeout<=60 * 3)) {
            return Response.ofFail("ticket timeout is invalid");
        }

        // build ticket
        String storeKey = parseTicketStoreKey(ticket);

        // set
        jedisTool.set(storeKey, token, ticketTimeout);
        return Response.ofSuccess(ticket);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Response<String> validTicket(String ticket) {
        // valid ticket
        if (StringTool.isBlank(ticket)) {
            return Response.ofFail("ticket is invalid");
        }

        // parse storeKey
        String storeKey = parseTicketStoreKey(ticket);

        // valid ticket
        String token = (String) jedisTool.get(storeKey);
        if (StringTool.isBlank(token)) {
            return Response.ofFail("ticket not found.");
        }

        // remove after verify-success;
        // consume once, avoid ticket replay
        jedisTool.del(storeKey);

        return Response.ofSuccess(token);
    }

}
