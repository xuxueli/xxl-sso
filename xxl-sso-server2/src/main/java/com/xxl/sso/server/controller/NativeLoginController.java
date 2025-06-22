package com.xxl.sso.server.controller;

import com.xxl.sso.core.helper.XxlSsoHelper;
import com.xxl.sso.core.model.LoginInfo;
import com.xxl.sso.core.token.TokenHelper;
import com.xxl.sso.server.model.AccountInfo;
import com.xxl.sso.server.model.TokenRequest;
import com.xxl.sso.server.service.AccountService;
import com.xxl.tool.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * login openapi (for native)
 *
 * @author xuxueli 2018-04-08 21:02:54
 */
@Controller
@RequestMapping("/native")
public class NativeLoginController {

    @Autowired
    private AccountService accountService;


    /**
     * Login
     *
     * @param accountInfo
     * @return
     */
    @RequestMapping("/login")
    @ResponseBody
    public Response<String> login(@RequestBody(required = false) AccountInfo accountInfo) {
        // base valid
        if (accountInfo == null) {
            return Response.ofFail("username or password is invalid.");
        }

        // 1、find user
        Response<AccountInfo> result = accountService.findUser(accountInfo.getUsername(), accountInfo.getPassword());
        if (!result.isSuccess()) {
            return Response.ofFail(result.getMsg());
        }
        AccountInfo accoutInfo = result.getData();

        // 2、build LoginInfo
        LoginInfo loginInfo = new LoginInfo();
        loginInfo.setUserId(accoutInfo.getUserid());
        loginInfo.setUserName(accoutInfo.getUsername());
        loginInfo.setVersion("v1");

        // 3、build token
        String token = TokenHelper.generateToken(loginInfo);

        // 4、login (write store)
        XxlSsoHelper.login(token, loginInfo);
        return Response.ofSuccess(token);
    }


    /**
     * Logout
     *
     * @param tokenRequest
     * @return
     */
    @RequestMapping("/logout")
    @ResponseBody
    public Response<String> logout(@RequestBody(required = false) TokenRequest tokenRequest) {
        // base valid
        if (tokenRequest == null) {
            return Response.ofFail("token is invalid.");
        }

        // 1、logout  (remove store)
        XxlSsoHelper.logout(tokenRequest.getToken());
        return Response.ofSuccess();
    }

    /**
     * loginCheck
     *
     * @param tokenRequest
     * @return
     */
    @RequestMapping("/logincheck")
    @ResponseBody
    public Response<LoginInfo> logincheck(@RequestBody(required = false) TokenRequest tokenRequest) {
        // base valid
        if (tokenRequest == null) {
            return Response.ofFail("token is invalid.");
        }

        // 1、logout
        LoginInfo loginInfo = XxlSsoHelper.loginCheck(tokenRequest.getToken());
        if (loginInfo == null) {
            return Response.ofFail("sso not login.");
        }
        return Response.ofSuccess(loginInfo);
    }

}
