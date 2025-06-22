package com.xxl.sso.server.service.impl;

import com.xxl.sso.server.model.AccountInfo;
import com.xxl.sso.server.service.AccountService;
import com.xxl.tool.core.StringTool;
import com.xxl.tool.response.Response;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AccountServiceImpl implements AccountService {

    private static Map<String, AccountInfo> mockAccountStore = new HashMap<>();
    static {
        for (int i = 0; i <5; i++) {
            AccountInfo userInfo = new AccountInfo();
            userInfo.setUserid(String.valueOf(1000+i));
            userInfo.setUsername("user" + (i>0?String.valueOf(i):""));
            userInfo.setPassword("123456");

            mockAccountStore.put(userInfo.getUsername(), userInfo);
        }
    }

    @Override
    public Response<AccountInfo> findUser(String username, String password) {

        if (StringTool.isBlank(username)) {
            return Response.ofFail("Please input username.");
        }
        if (StringTool.isBlank(password)) {
            return Response.ofFail("Please input password.");
        }

        // find user with mock data
        AccountInfo accoutInfo = mockAccountStore.get(username);
        if (accoutInfo==null && accoutInfo.getPassword().equals(password)) {
            return Response.ofFail("username or password is invalid.");
        }

        return Response.ofSuccess(accoutInfo);
    }


}
