package com.xxl.sso.sample.weblogin.service.impl;

import com.xxl.sso.sample.weblogin.model.AccountInfo;
import com.xxl.sso.sample.weblogin.service.AccountService;
import com.xxl.tool.core.StringTool;
import com.xxl.tool.response.Response;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * account service impl
 *
 * <p>
 * 内存 Mock 实现：用户名 user、user1 ~ user4，密码 123456，角色 role01，权限 user:query / user:add；
 * 仅用于示例演示，生产环境应替换为真实账号数据源。
 * </p>
 *
 * @author xuxueli 2018-03-22 23:51:51
 */
@Service
public class AccountServiceImpl implements AccountService {

    /**
     * mock account store: username -> account
     */
    private static final Map<String, AccountInfo> MOCK_ACCOUNT_STORE = new HashMap<>();

    static {
        for (int i = 0; i < 5; i++) {
            AccountInfo accountInfo = new AccountInfo();
            accountInfo.setUserid(String.valueOf(1000 + i));
            accountInfo.setUsername("user" + (i > 0 ? String.valueOf(i) : ""));
            accountInfo.setPassword("123456");
            accountInfo.setRoleList(Arrays.asList("role01"));
            accountInfo.setPermissionList(Arrays.asList("user:query", "user:add"));

            MOCK_ACCOUNT_STORE.put(accountInfo.getUsername(), accountInfo);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Response<AccountInfo> findUser(String username, String password) {

        if (StringTool.isBlank(username)) {
            return Response.ofFail("Please input username.");
        }
        if (StringTool.isBlank(password)) {
            return Response.ofFail("Please input password.");
        }

        // find user with mock data
        AccountInfo accountInfo = MOCK_ACCOUNT_STORE.get(username);
        if (!(accountInfo != null && accountInfo.getPassword().equals(password))) {
            return Response.ofFail("username or password is invalid.");
        }

        return Response.ofSuccess(accountInfo);
    }

}
