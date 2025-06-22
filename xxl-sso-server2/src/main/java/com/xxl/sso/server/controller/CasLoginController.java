package com.xxl.sso.server.controller;

import com.xxl.sso.core.constant.Const;
import com.xxl.sso.core.helper.XxlSsoHelper;
import com.xxl.sso.core.model.LoginInfo;
import com.xxl.sso.core.token.TokenHelper;
import com.xxl.sso.server.model.AccountInfo;
import com.xxl.sso.server.service.AccountService;
import com.xxl.tool.core.StringTool;
import com.xxl.tool.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
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
        LoginInfo loginInfo = XxlSsoHelper.loginCheckWithCookie(request);

        if (loginInfo == null) {
            return "redirect:/login";
        } else {
            model.addAttribute("loginInfo", loginInfo);
            return "index";
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
    public String login(Model model, HttpServletRequest request) {

        // param
        String redirectUrl = request.getParameter(Const.CLIENT_REDIRECT_URL);

        // login check
        LoginInfo loginInfo = XxlSsoHelper.loginCheckWithCookie(request);
        if (loginInfo != null) {

            // redirect back
            if (StringTool.isNotBlank(redirectUrl)) {

                String token = XxlSsoHelper.getTokenWithCookie(request);
                String redirectUrlFinal = redirectUrl.trim() + "?" + Const.XXL_SSO_TOKEN + "=" + token;;
                return "redirect:" + redirectUrlFinal;
            } else {
                return "redirect:/";
            }
        }

        model.addAttribute("errorMsg", request.getParameter("errorMsg"));
        model.addAttribute(Const.CLIENT_REDIRECT_URL, redirectUrl);
        return "login";
    }

    /**
     * Login
     *
     * @param request
     * @param redirectAttributes
     * @param username
     * @param password
     * @return
     */
    @RequestMapping("/doLogin")
    public String doLogin(HttpServletRequest request,
                        HttpServletResponse response,
                        RedirectAttributes redirectAttributes,
                        String username,
                        String password,
                        String ifRemember) {

        // process param
        boolean ifRem = "on".equals(ifRemember);

        // 1、find user
        Response<AccountInfo> accountResult = userService.findUser(username, password);
        if (!accountResult.isSuccess()) {

            // login fail
            redirectAttributes.addAttribute("errorMsg", accountResult.getMsg());
            redirectAttributes.addAttribute(Const.CLIENT_REDIRECT_URL, request.getParameter(Const.CLIENT_REDIRECT_URL));
            return "redirect:/login";
        }
        AccountInfo accoutInfo = accountResult.getData();

        // 2、build LoginInfo
        LoginInfo loginInfo = new LoginInfo();
        loginInfo.setUserId(accoutInfo.getUserid());
        loginInfo.setUserName(accoutInfo.getUsername());
        loginInfo.setVersion("v1");

        // 3、build token
        String token = TokenHelper.generateToken(loginInfo);

        // 4、login (write store + cookie)
        Response<String> loginResult = XxlSsoHelper.loginWithCookie(token, loginInfo, response, ifRem);
        if (!loginResult.isSuccess()) {

            // login fail
            redirectAttributes.addAttribute("errorMsg", loginResult.getMsg());
            redirectAttributes.addAttribute(Const.CLIENT_REDIRECT_URL, request.getParameter(Const.CLIENT_REDIRECT_URL));
            return "redirect:/login";
        }


        // 5、redirect back
        String redirectUrl = request.getParameter(Const.CLIENT_REDIRECT_URL);
        if (StringTool.isNotBlank(redirectUrl)) {
            String redirectUrlFinal = redirectUrl.trim() + "?" + Const.XXL_SSO_TOKEN + "=" + token;
            return "redirect:" + redirectUrlFinal;
        } else {
            return "redirect:/";
        }

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
