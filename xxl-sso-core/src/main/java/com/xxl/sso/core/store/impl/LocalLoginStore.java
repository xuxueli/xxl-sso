package com.xxl.sso.core.store.impl;

import com.xxl.sso.core.model.LoginInfo;
import com.xxl.sso.core.store.LoginStore;
import com.xxl.tool.core.StringTool;
import com.xxl.tool.response.Response;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author xuxueli 2018-04-02 20:03:11
 */
public class LocalLoginStore implements LoginStore {

    private final ConcurrentMap<String, LoginInfo> loginStore = new ConcurrentHashMap<>();

    /**
     * parse store key from token
     *
     * @param userId
     * @return
     */
    private String parseStoreKey(String userId){
        return userId;
    }

    @Override
    public Response<String> set(LoginInfo loginInfo) {

        // valid loginInfo
        if (loginInfo == null
                || StringTool.isBlank(loginInfo.getUserId())
                || StringTool.isBlank(loginInfo.getVersion())) {
            return Response.ofFail("loginInfo invalid.");
        }

        // valid expire-time
        if (loginInfo.getExpireTime() < System.currentTimeMillis()) {
            return Response.ofFail("expireTime invalid.");
        }

        // generate storeKey
        String storeKey = parseStoreKey(loginInfo.getUserId());

        // write
        loginStore.put(storeKey, loginInfo);
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
        LoginInfo loginInfoStore = loginStore.get(storeKey);
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

        // write
        loginStore.put(storeKey, loginInfoStore);
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
        LoginInfo loginInfo = loginStore.get(storeKey);
        if (loginInfo == null) {
            return Response.ofFail("loginInfo not exists.");
        }

        // valid expire time
        if (loginInfo.getExpireTime() < System.currentTimeMillis()) {
            loginStore.remove(storeKey);
            return Response.ofFail("loginInfo is timeout");
        }

        return Response.ofSuccess(loginInfo);
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
        loginStore.remove(storeKey);
        return Response.ofSuccess();
    }

}
