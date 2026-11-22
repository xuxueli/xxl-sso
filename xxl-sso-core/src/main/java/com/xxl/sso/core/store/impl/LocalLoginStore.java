package com.xxl.sso.core.store.impl;

import com.xxl.sso.core.model.LoginInfo;
import com.xxl.sso.core.store.LoginStore;
import com.xxl.tool.cache.CacheTool;
import com.xxl.tool.cache.iface.Cache;
import com.xxl.tool.core.StringTool;
import com.xxl.tool.response.Response;

/**
 * local login store
 *
 * <p>
 * 使用 LRU 策略限制容量，容量满时淘汰最久未使用的登录态，避免本地缓存无界增长。不支持 CAS 票据（调用票据方法会抛
 * {@link com.xxl.sso.core.exception.XxlSsoException}）。
 * </p>
 *
 * <p>
 * 适用于单机场景；多实例部署时登录态不共享，应改用 Redis 实现。
 * 并发安全由 {@link Cache} 实现保证。
 * </p>
 *
 * @author xuxueli 2018-04-02 20:03:11
 */
public class LocalLoginStore implements LoginStore {

    /**
     * default cache capacity
     */
    public static final int DEFAULT_CAPACITY = 10000;

    /**
     * userId -> LoginInfo, LRU with capacity limit
     */
    private final Cache<String, LoginInfo> loginStore;

    /**
     * create local login store with {@link #DEFAULT_CAPACITY}
     */
    public LocalLoginStore() {
        this(DEFAULT_CAPACITY);
    }

    /**
     * create local login store
     *
     * @param capacity cache capacity, must be positive
     * @throws IllegalArgumentException when capacity is not positive
     */
    public LocalLoginStore(int capacity) {
        this.loginStore = CacheTool.<String, LoginInfo>newLRUCache(capacity).build();
    }

    /**
     * parse store key from userId
     *
     * @param userId    user id
     * @return          store key
     */
    private String parseStoreKey(String userId){
        return userId;
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
        loginStore.remove(storeKey);
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

}
