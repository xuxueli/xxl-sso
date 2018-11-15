package com.xxl.sso.server.service;

import com.xxl.sso.server.core.model.UserInfo;
import com.xxl.sso.server.core.result.ReturnT;

public interface UserService {

    public ReturnT<UserInfo> findUser(String username, String password);

}
