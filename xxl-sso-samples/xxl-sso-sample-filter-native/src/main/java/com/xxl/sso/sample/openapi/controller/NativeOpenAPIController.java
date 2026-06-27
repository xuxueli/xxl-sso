package com.xxl.sso.sample.openapi.controller;

import com.xxl.sso.core.helper.XxlSsoHelper;
import com.xxl.sso.core.model.LoginInfo;
import com.xxl.sso.sample.openapi.model.AccountInfo;
import com.xxl.sso.sample.openapi.model.LoginRequest;
import com.xxl.sso.sample.openapi.service.AccountService;
import com.xxl.tool.id.UUIDTool;
import com.xxl.tool.response.Response;
import jakarta.servlet.http.HttpServletRequest;
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
@RequestMapping("/native/openapi/")
public class NativeOpenAPIController {

    @Autowired
    private AccountService accountService;


    /**
     * Login
     */
    @RequestMapping("/login")
    @ResponseBody
    public Response<String> login(@RequestBody(required = false) LoginRequest loginRequest) {
        // base valid
        if (loginRequest == null) {
            return Response.ofFail("username or password is invalid.");
        }

        // 1、find user
        Response<AccountInfo> result = accountService.findUser(loginRequest.getUsername(), loginRequest.getPassword());
        if (!result.isSuccess()) {
            return Response.ofFail(result.getMsg());
        }
        AccountInfo accoutInfo = result.getData();

        // 2、build LoginInfo
        LoginInfo loginInfo = new LoginInfo(
                accoutInfo.getUserid(),
                accoutInfo.getUsername(),
                null,
                null,
                accoutInfo.getRoleList(),
                accoutInfo.getPermissionList(),
                -1,
                UUIDTool.getSimpleUUID());

        // 4、login (write store)
        Response<String> loginResult = XxlSsoHelper.login(loginInfo);
        if (!loginResult.isSuccess()) {
            return loginResult;
        }
        String token = loginResult.getData();
        return Response.ofSuccess(token);
    }

    /**
     * Logout
     */
    @RequestMapping("/logout")
    @ResponseBody
    public Response<String> logout(HttpServletRequest request) {
        return XxlSsoHelper.logoutWithHeader(request);
    }

    /**
     * loginCheck
     */
    @RequestMapping("/logincheck")
    @ResponseBody
    public Response<LoginInfo> logincheck(HttpServletRequest request) {
        // login check
        return XxlSsoHelper.loginCheckWithAttr(request);
    }

}
