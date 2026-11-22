package com.xxl.sso.server.framework.service;

import com.xxl.sso.server.framework.model.AccountInfo;
import com.xxl.tool.response.Response;

/**
 * account service
 *
 * <p>
 * 认证中心账号查询服务，提供账号与密码校验；当前使用内存 Mock 数据。
 * </p>
 *
 * @author xuxueli 2018-03-22 23:51:51
 */
public interface AccountService {

    /**
     * find user by username and password
     *
     * @param username  username, blank is rejected
     * @param password  password, blank is rejected
     * @return          Response#data is matched account; fail when username or password is invalid
     */
    public Response<AccountInfo> findUser(String username, String password);

}
