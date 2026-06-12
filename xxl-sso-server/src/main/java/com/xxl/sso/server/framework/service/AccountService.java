package com.xxl.sso.server.framework.service;

import com.xxl.sso.server.framework.model.AccountInfo;
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
