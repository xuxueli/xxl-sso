package com.xxl.sso.sample.weblogin.service;

import com.xxl.sso.sample.weblogin.model.AccountInfo;
import com.xxl.tool.response.Response;

/**
 * account service
 *
 * <p>
 * 提供账号查询与密码校验；示例项目使用内存 Mock 数据，仅用于演示登录流程。
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
