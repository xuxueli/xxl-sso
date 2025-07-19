package com.xxl.sso.core.store.impl;

import com.xxl.sso.core.constant.Const;
import com.xxl.sso.core.model.LoginInfo;
import com.xxl.sso.core.store.LoginStore;
import com.xxl.sso.core.token.TokenHelper;
import com.xxl.sso.core.util.JedisTool;
import com.xxl.tool.core.StringTool;
import com.xxl.tool.id.UUIDTool;
import com.xxl.tool.response.Response;

/**
 * @author xuxueli 2018-04-02 20:03:11
 */
public class RedisLoginStore implements LoginStore {

    private JedisTool jedisTool;
    private String storeKeyPrefix;
    public RedisLoginStore(String nodes, String user, String password, String storeKeyPrefix) {
        // valid
        if (StringTool.isBlank(storeKeyPrefix)) {
            storeKeyPrefix = Const.XXL_SSO_STORE_PREFIX;
        }

        // init
        this.jedisTool = new JedisTool(nodes, user, password);
        this.storeKeyPrefix = storeKeyPrefix;
    }


    /**
     * parse store key from token
     *
     * <pre>
     *     key: "xxl_sso_user:" + {user001}
     *     value: loginInfo
     * </pre>>
     *
     * @param tokenLoginInfo
     * @return
     */
    private String parseStoreKey(LoginInfo tokenLoginInfo){
        if (tokenLoginInfo == null) {
            return null;
        }

        return storeKeyPrefix + tokenLoginInfo.getUserId();
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
                || StringTool.isBlank(loginInfo.getUserName())) {
            return Response.ofFail("loginInfo invalid.");
        }

        // valid expire-time
        if (loginInfo.getExpireTime() < System.currentTimeMillis()) {
            return Response.ofFail("expireTime invalid.");
        }

        // generate token
        String token = TokenHelper.generateToken(loginInfo);

        // generate redis timeout (seconds)
        long seconds = (loginInfo.getExpireTime() - System.currentTimeMillis()) / 1000;

        // parse storeKey
        String storeKey = parseStoreKey(TokenHelper.parseToken(token));
        if (StringTool.isBlank(storeKey)) {
            return Response.ofFail("token invalid.");
        }

        // write
        jedisTool.set(storeKey, loginInfo, seconds);
        return Response.ofSuccess(token);
    }

    @Override
    public Response<String> update(LoginInfo loginInfo) {

        // parse storeKey
        String storeKey = parseStoreKey(loginInfo);
        if (StringTool.isBlank(storeKey)) {
            return Response.ofFail("loginInfo is invalid");
        }

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

        // generate redis timeout (seconds)
        long seconds = (loginInfo.getExpireTime() - System.currentTimeMillis()) / 1000;

        // write
        jedisTool.set(storeKey, loginInfoStore, seconds);
        return Response.ofSuccess();
    }

    @Override
    public Response<LoginInfo> get(String token) {

        // parse storeKey
        LoginInfo tokenLoginInfo = TokenHelper.parseToken(token);
        String storeKey = parseStoreKey(tokenLoginInfo);
        if (StringTool.isBlank(storeKey)) {
            return Response.ofFail("token is invalid");
        }
        String version = tokenLoginInfo.getVersion();

        // read
        LoginInfo loginInfo = (LoginInfo) jedisTool.get(storeKey);
        if (loginInfo == null) {
            return Response.ofFail("token is invalid2");
        }

        // valid expire time
        if (loginInfo.getExpireTime() < System.currentTimeMillis()) {
            jedisTool.del(storeKey);
            return Response.ofFail("token is timeout");
        }
        // valid version if inconsistent
        if (loginInfo.getVersion()!=null && !loginInfo.getVersion().equals(version)){
            // Non-empty and inconsistent
            return Response.ofFail("token version is invalid");
        }

        return Response.ofSuccess(loginInfo);
    }

    @Override
    public Response<String> remove(String token) {
        // parse storeKey
        String storeKey = parseStoreKey(TokenHelper.parseToken(token));
        if (StringTool.isBlank(storeKey)) {
            return Response.ofFail("token is invalid");
        }

        // remove
        jedisTool.del(storeKey);
        return Response.ofSuccess();
    }

    @Override
    public Response<String> createTicket(String token, long ticketTimeout) {

        // valid param
        LoginInfo tokenLoginInfo = TokenHelper.parseToken(token);
        if (tokenLoginInfo == null) {
            return Response.ofFail("token is invalid");
        }
        if (!(ticketTimeout>=1000 && ticketTimeout<=1000 * 60 * 3)) {
            return Response.ofFail("ticketTimeout is invalid");
        }

        // build ticket
        String ticket = tokenLoginInfo.getUserId() + UUIDTool.getSimpleUUID();
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
        if (token == null) {
            return Response.ofFail("ticket not found.");
        }

        return Response.ofSuccess(token);
    }

}
