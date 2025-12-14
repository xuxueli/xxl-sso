package com.xxl.sso.sample.openapi.service;

import com.xxl.sso.sample.openapi.model.AccountInfo;
import com.xxl.tool.response.Response;

/**
 * @author xuxueli 2018-03-22 23:51:51
 */
public interface AccountService {

    /**
     * find user
     *
     * @param username
     * @param password
     * @return
     */
    public Response<AccountInfo> findUser(String username, String password);

}
