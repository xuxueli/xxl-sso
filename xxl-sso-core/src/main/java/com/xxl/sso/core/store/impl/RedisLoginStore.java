package com.xxl.sso.core.store.impl;

import com.xxl.sso.core.constant.Const;
import com.xxl.sso.core.model.LoginInfo;
import com.xxl.sso.core.store.LoginStore;
import com.xxl.sso.core.util.JedisTool;
import com.xxl.tool.core.StringTool;
import com.xxl.tool.response.Response;

/**
 * @author xuxueli 2018-04-02 20:03:11
 */
public class RedisLoginStore implements LoginStore {

    private JedisTool jedisTool;
    private String storeKeyPrefix;
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
     * </pre>>
     *
     * @param userId
     * @return
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
     * </pre>>
     *
     * @param ticket
     * @return
     */
    private String parseTicketStoreKey(String ticket){
        if (ticket == null) {
            return null;
        }

        return storeKeyPrefix + "ticket:" + ticket;
    }

    @Override
    public void start() {
        jedisTool.start();
    }

    @Override
    public void stop() {
        jedisTool.stop();
    }

    @Override
    public Response<String> set(LoginInfo loginInfo) {

        // valid loginInfo
        if (loginInfo == null
                || StringTool.isBlank(loginInfo.getUserId())
                || StringTool.isBlank(loginInfo.getSignature())) {
            return Response.ofFail("loginInfo invalid.");
        }

        // valid expire-time
        if (loginInfo.getExpireTime() < System.currentTimeMillis()) {
            return Response.ofFail("expireTime invalid.");
        }

        // generate redis timeout (seconds)
        long seconds = (loginInfo.getExpireTime() - System.currentTimeMillis()) / 1000;

        // generate storeKey
        String storeKey = parseStoreKey(loginInfo.getUserId());

        // write
        jedisTool.set(storeKey, loginInfo, seconds);
        return Response.ofSuccess();
    }

    @Override
    public Response<String> update(LoginInfo loginInfo) {

        // valid loginInfo
        if (loginInfo == null || StringTool.isBlank(loginInfo.getUserId())) {
            return Response.ofFail("loginInfo invalid.");
        }

        // generate storeKey
        String storeKey = parseStoreKey(loginInfo.getUserId());

        // valid expire-time
        if (loginInfo.getExpireTime() < System.currentTimeMillis()) {
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

        // generate redis timeout (seconds)
        long seconds = (loginInfo.getExpireTime() - System.currentTimeMillis()) / 1000;

        // write
        jedisTool.set(storeKey, loginInfoStore, seconds);
        return Response.ofSuccess();
    }

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

        // valid expire time
        if (loginInfo.getExpireTime() < System.currentTimeMillis()) {
            jedisTool.del(storeKey);
            return Response.ofFail("loginInfo is timeout");
        }

        return Response.ofSuccess(loginInfo);
    }

    @Override
    public Response<String> createTicket(String ticket, String token, long ticketTimeout) {

        // valid param
        if (StringTool.isBlank(ticket)) {
            return Response.ofFail("ticket is invalid");
        }
        if (!(ticketTimeout>=1000 && ticketTimeout<=1000 * 60 * 3)) {
            return Response.ofFail("ticketTimeout is invalid");
        }

        // build ticket
        String storeKey = parseTicketStoreKey(ticket);

        // set
        jedisTool.set(storeKey, token, ticketTimeout);
        return Response.ofSuccess(ticket);
    }

    @Override
    public Response<String> validTicket(String ticket) {
        // parse storeKey
        String storeKey = parseTicketStoreKey(ticket);

        // get
        String token = (String) jedisTool.get(storeKey);
        if (StringTool.isBlank(token)) {
            return Response.ofFail("ticket not found.");
        }

        return Response.ofSuccess(token);
    }

}
