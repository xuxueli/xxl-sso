package com.xxl.sso.server.controller;

import com.xxl.sso.core.constant.Const;
import com.xxl.sso.core.helper.XxlSsoHelper;
import com.xxl.sso.core.model.LoginInfo;
import com.xxl.sso.server.model.AccountInfo;
import com.xxl.sso.server.service.AccountService;
import com.xxl.tool.core.StringTool;
import com.xxl.tool.id.UUIDTool;
import com.xxl.tool.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * login page (for cas)
 *
 * @author xuxueli 2017-08-01 21:39:47
 */
@Controller
public class CasLoginController {

    @Autowired
    private AccountService userService;

    @RequestMapping("/")
    public String index(Model model, HttpServletRequest request, HttpServletResponse response) {

        // login check
        Response<LoginInfo> loginCheckResult = XxlSsoHelper.loginCheckWithCookie(request, response);

        if (loginCheckResult!=null && loginCheckResult.isSuccess()) {
            model.addAttribute("loginInfo", loginCheckResult.getData());
            return "index";
        } else {
            return "redirect:/login";
        }
    }

    /**
     * Login page
     *
     * @param model
     * @param request
     * @return
     */
    @RequestMapping(Const.LOGIN_URL)
    public String login(Model model, HttpServletRequest request, HttpServletResponse response) {

        // 1、param
        String redirectUrl = request.getParameter(Const.CLIENT_REDIRECT_URL);

        // 2、login check
        Response<LoginInfo> loginCheckResult = XxlSsoHelper.loginCheckWithCookie(request, response);
        if (loginCheckResult!=null && loginCheckResult.isSuccess()) {

            // 2.1、login success, redirect back
            String redirectUrlFinal = genereteRedirectUrl(request, redirectUrl);
            if (StringTool.isBlank(redirectUrlFinal)) {
                redirectUrlFinal = "/";
            }

            return "redirect:" + redirectUrlFinal;
        } else {

            // 2.2、login fail, go to login-page
            model.addAttribute(Const.CLIENT_REDIRECT_URL, redirectUrl);

            return "login";
        }
    }

    /**
     * generate redirect url, back 2 client
     *
     * @param request
     * @param redirectUrl
     * @return
     */
    private String genereteRedirectUrl(HttpServletRequest request, String redirectUrl){

        // valid
        if (StringTool.isBlank(redirectUrl)) {
            return "";
        }

        // parse ticket
        Response<String> createTicketResult = XxlSsoHelper.createTicket(request);
        if (!createTicketResult.isSuccess()) {
            return "";
        }
        String ticket = createTicketResult.getData();

        // build redirect url
        return redirectUrl.trim() + "?" + Const.XXL_SSO_TICKET + "=" + ticket;
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
        String redirectUrlFinal = genereteRedirectUrl(request, redirectUrl);
        if (StringTool.isBlank(redirectUrlFinal)) {
            redirectUrlFinal = request.getContextPath() + "/";
        }
        return Response.ofSuccess(redirectUrlFinal);
    }

    /**
     * Logout
     *
     * @param request
     * @param redirectAttributes
     * @return
     */
    @RequestMapping(Const.LOGOUT_URL)
    public String logout(HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {

        // logout
        XxlSsoHelper.logoutWithCookie(request, response);

        // redirect 2 login
        redirectAttributes.addAttribute(Const.CLIENT_REDIRECT_URL, request.getParameter(Const.CLIENT_REDIRECT_URL));
        return "redirect:/login";
    }

}
