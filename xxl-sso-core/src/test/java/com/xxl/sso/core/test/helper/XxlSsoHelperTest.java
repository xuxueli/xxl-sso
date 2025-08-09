package com.xxl.sso.core.test.helper;

import com.xxl.sso.core.bootstrap.XxlSsoBootstrap;
import com.xxl.sso.core.constant.Const;
import com.xxl.sso.core.helper.XxlSsoHelper;
import com.xxl.sso.core.model.LoginInfo;
import com.xxl.sso.core.store.impl.RedisLoginStore;
import com.xxl.tool.id.UUIDTool;
import com.xxl.tool.response.Response;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class XxlSsoHelperTest {
    private static Logger logger = LoggerFactory.getLogger(XxlSsoHelperTest.class);

    private XxlSsoBootstrap bootstrap;
    private void start(){
        bootstrap = new XxlSsoBootstrap();
        bootstrap.setLoginStore(new RedisLoginStore(
                "127.0.0.1:6379",
                "",
                "",
                "xxl_sso_user"));
        bootstrap.setTokenKey("xxl_sso_token");
        bootstrap.setTokenTimeout(604800000);

        bootstrap.start();
    }

    private void stop(){
        bootstrap.stop();
    }

    @Test
    public void test() {
        // do start
        start();


        // build LoginInfo
        LoginInfo loginInfo = new LoginInfo(
                "666",
                "zhagnsan",
                null,
                null,
                Arrays.asList("role1", "role2"),
                Arrays.asList("permission1", "permission2"),
                System.currentTimeMillis() + Const.EXPIRE_TIME_FOR_7_DAY,
                UUIDTool.getSimpleUUID());

        // login
        Response<String> loginResponse = XxlSsoHelper.login(loginInfo);
        String token = loginResponse.getData();
        logger.info("Login Response = " + loginResponse);

        // login check1
        logger.info("Login Check Response(after login) = " + XxlSsoHelper.loginCheck(token));

        // login update
        loginInfo.setUserName("lisi");
        loginInfo.setRoleList(Arrays.asList("role3", "role4"));
        loginInfo.setPermissionList(Arrays.asList("permission3", "permission4"));
        loginInfo.setExpireTime(System.currentTimeMillis() + Const.EXPIRE_TIME_FOR_7_DAY);

        Response<String> loginUpdateResponse = XxlSsoHelper.loginUpdate(loginInfo);
        logger.info("Login Update Response = " + loginUpdateResponse);

        // login check2
        logger.info("Login Check Response(after loginUpdate) = " + XxlSsoHelper.loginCheck(token));

        // has permission
        logger.info("Has Permission Response (permission1) = " + XxlSsoHelper.hasPermission(loginInfo, "permission1"));
        logger.info("Has Permission Response (permission4) = " + XxlSsoHelper.hasPermission(loginInfo, "permission4"));

        // has role
        logger.info("Has Role Response(role1) = " + XxlSsoHelper.hasRole(loginInfo, "role1"));
        logger.info("Has Role Response(role4) = " + XxlSsoHelper.hasRole(loginInfo, "role4"));

        // logout
        Response<String> logoutResponse = XxlSsoHelper.logout(token);
        logger.info("Logout Response = " + logoutResponse);

        // login check3
        logger.info("Login Check Response(after logout) = " + XxlSsoHelper.loginCheck(token));

        // do stop
        start();
    }


}

