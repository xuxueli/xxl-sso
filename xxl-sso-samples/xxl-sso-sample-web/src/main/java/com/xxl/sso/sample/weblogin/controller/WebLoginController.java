package com.xxl.sso.sample.weblogin.controller;

import com.xxl.sso.core.constant.Const;
import com.xxl.sso.core.helper.XxlSsoHelper;
import com.xxl.sso.core.model.LoginInfo;
import com.xxl.sso.sample.weblogin.model.AccountInfo;
import com.xxl.sso.sample.weblogin.service.AccountService;
import com.xxl.tool.id.UUIDTool;
import com.xxl.tool.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * login page (for web)
 *
 * @author xuxueli 2017-08-01 21:39:47
 */
@Controller
@RequestMapping("/weblogin")
public class WebLoginController {

    @Autowired
    private AccountService userService;

    /**
     * Login page
     *
     * @param request
     * @return
     */
    @RequestMapping(Const.LOGIN_URL)
    public String login(HttpServletRequest request, HttpServletResponse response) {

        // login check
        Response<LoginInfo> loginCheckResult = XxlSsoHelper.loginCheckWithCookie(request, response);
        if (loginCheckResult.isSuccess()) {
            return "redirect:/";
        }

        return "login";
    }

    /**
     * Do login
     *
     * @param request
     * @param username
     * @param password
     * @return
     */
    @RequestMapping("/doLogin")
    @ResponseBody
    public Response<String> doLogin(HttpServletRequest request,
                                    HttpServletResponse response,
                                    String username,
                                    String password,
                                    String ifRemember,
                                    String redirectUrl) {

        // process param
        boolean ifRem = "on".equals(ifRemember);

        // 1、find user
        Response<AccountInfo> accountResult = userService.findUser(username, password);
        if (!accountResult.isSuccess()) {
            return Response.ofFail(accountResult.getMsg());
        }
        AccountInfo accoutInfo = accountResult.getData();

        // 2、build LoginInfo
        LoginInfo loginInfo = new LoginInfo();
        loginInfo.setUserId(accoutInfo.getUserid());
        loginInfo.setUserName(accoutInfo.getUsername());
        loginInfo.setVersion(UUIDTool.getSimpleUUID());

        // 4、login (write store + cookie)
        Response<String> loginResult = XxlSsoHelper.loginWithCookie(loginInfo, response, ifRem);
        if (!loginResult.isSuccess()) {
            return Response.ofFail(accountResult.getMsg());
        }

        // 5、redirect back
        return Response.ofSuccess();
    }

    /**
     * Logout
     *
     * @param request
     * @param redirectAttributes
     * @return
     */
    @RequestMapping(Const.LOGOUT_URL)
    @ResponseBody
    public Response<String> logout(HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
        return XxlSsoHelper.logoutWithCookie(request, response);
    }

}
