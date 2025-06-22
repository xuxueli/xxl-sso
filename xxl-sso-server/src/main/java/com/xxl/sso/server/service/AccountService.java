package com.xxl.sso.server.service;

import com.xxl.sso.server.model.AccountInfo;
import com.xxl.tool.response.Response;

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
