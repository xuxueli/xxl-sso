package com.xxl.sso.server.framework.controller;

import com.xxl.sso.core.constant.Const;
import com.xxl.sso.core.helper.XxlSsoHelper;
import com.xxl.sso.core.model.LoginInfo;
import com.xxl.sso.server.framework.model.AccountInfo;
import com.xxl.sso.server.framework.service.AccountService;
import com.xxl.tool.core.StringTool;
import com.xxl.tool.id.UUIDTool;
import com.xxl.tool.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * CAS login controller
 *
 * <p>
 * 认证中心登录入口：展示登录页、处理账号登录、已登录用户回跳 CAS Client，以及注销。
 * 回跳地址仅允许白名单命中的客户端地址；直接访问认证中心时跳转首页，不签发 ticket。
 * </p>
 *
 * @author xuxueli 2017-08-01 21:39:47
 */
@Controller
public class CasLoginController {
    private static Logger logger = LoggerFactory.getLogger(CasLoginController.class);

    /**
     * account service, mock account data in sample
     */
    @Autowired
    private AccountService accountService;

    /**
     * home page
     *
     * @param model     model
     * @param request   request
     * @param response  response, used to clear invalid cookie
     * @return index view when logged in; otherwise redirect to login page
     */
    @RequestMapping("/")
    public String index(Model model, HttpServletRequest request, HttpServletResponse response) {

        // login check
        Response<LoginInfo> loginCheckResult = XxlSsoHelper.loginCheckWithCookie(request, response);

        if (loginCheckResult!=null && loginCheckResult.isSuccess()) {
            model.addAttribute("loginInfo", loginCheckResult.getData());
            return "framework/index";
        } else {
            return "redirect:/login";
        }
    }

    /**
     * login page
     *
     * <p>
     * 已登录时直接回跳：来自 CAS Client 且回跳地址命中白名单则签发 ticket；
     * 直接访问认证中心则返回首页。未登录时展示登录页，并保留 redirect_url 供登录成功后回跳。
     * </p>
     *
     * @param model     model
     * @param request   request
     * @param response  response, used to clear invalid cookie
     * @return view name or redirect address
     */
    @RequestMapping(Const.LOGIN_URL)
    public String login(Model model, HttpServletRequest request, HttpServletResponse response) {

        // 1、param
        String redirectUrl = request.getParameter(Const.CLIENT_REDIRECT_URL);

        // 2、login check
        Response<LoginInfo> loginCheckResult = XxlSsoHelper.loginCheckWithCookie(request, response);
        if (loginCheckResult!=null && loginCheckResult.isSuccess()) {

            // 2.1、login success; direct access without cas-client redirect, go home
            if (StringTool.isBlank(redirectUrl)) {
                return "redirect:/";
            }

            // 2.2、login success, redirect back
            Response<String> redirectResult = XxlSsoHelper.casGenerateRedirectUrl(request, redirectUrl);
            if (redirectResult.isSuccess() && StringTool.isNotBlank(redirectResult.getData())) {
                return "redirect:" + redirectResult.getData();
            }

            // 2.3、redirect url invalid or empty, go home with error tip
            String homeUrl = "/";
            if (!redirectResult.isSuccess() && StringTool.isNotBlank(redirectResult.getMsg())) {
                homeUrl += "?errorMsg=" + redirectResult.getMsg();
            }
            return "redirect:" + homeUrl;
        } else {

            // 2.4、login fail, go to login-page
            model.addAttribute(Const.CLIENT_REDIRECT_URL, redirectUrl);

            return "framework/login";
        }
    }


    /**
     * do login
     *
     * <p>
     * 校验账号密码后写入登录态与 Cookie；来自 CAS Client 时校验回跳地址并签发 ticket，
     * 直接登录认证中心时返回首页地址。
     * </p>
     *
     * @param request       request
     * @param response      response, used to write token cookie
     * @param username      username
     * @param password      password
     * @param ifRemember    "on" means persistent cookie; otherwise session cookie
     * @param redirectUrl   cas client redirect url, nullable
     * @return              Response#data is redirect address; fail when account or redirect url is invalid
     */
    @RequestMapping("/doLogin")
    @ResponseBody
    public Response<String> doLogin(HttpServletRequest request,
                                    HttpServletResponse response,
                                    @RequestParam("username") String username,
                                    @RequestParam("password") String password,
                                    @RequestParam(value = "ifRemember", required = false) String ifRemember,
                                    @RequestParam(value = "redirectUrl", required = false) String redirectUrl) {

        // process param
        boolean ifRem = "on".equals(ifRemember);

        // 1、find user
        Response<AccountInfo> accountResult = accountService.findUser(username, password);
        if (!accountResult.isSuccess()) {
            return Response.ofFail(accountResult.getMsg());
        }
        AccountInfo accountInfo = accountResult.getData();

        // 2、build LoginInfo
        LoginInfo loginInfo = new LoginInfo(
                accountInfo.getUserid(),
                accountInfo.getUsername(),
                null,
                null,
                accountInfo.getRoleList(),
                accountInfo.getPermissionList(),
                -1,
                UUIDTool.getSimpleUUID());

        // 3、login (write store + cookie)
        Response<String> loginResult = XxlSsoHelper.loginWithCookie(loginInfo, response, ifRem);
        if (!loginResult.isSuccess()) {
            return Response.ofFail(loginResult.getMsg());
        }

        // 4、login success
        String redirectUrlFinal = "";
        if (StringTool.isBlank(redirectUrl)) {
            // 4.1、direct access without cas-client redirect, go home
            redirectUrlFinal = request.getContextPath() + "/";
        } else {
            // 4.2、login success, redirect back
            Response<String> redirectResult = XxlSsoHelper.casGenerateRedirectUrl(request, redirectUrl);
            if (!redirectResult.isSuccess()) {
                // 4.2.1、redirect url invalid or empty, go home with error log
                logger.warn("cas redirect url invalid, userId:{}, redirectUrl:{}, msg:{}", loginInfo.getUserId(), redirectUrl, redirectResult.getMsg());
            } else {
                // 4.2.2、redirect url valid, redirect back
                redirectUrlFinal = redirectResult.getData();
                if (StringTool.isBlank(redirectUrlFinal)) {
                    redirectUrlFinal = request.getContextPath() + "/";
                }
            }

        }
        return Response.ofSuccess(redirectUrlFinal);
    }

    /**
     * logout
     *
     * <p>
     * 清理服务端登录态与客户端 Cookie；来自 CAS Client 时保留 redirect_url，注销后重新进入登录流程。
     * </p>
     *
     * @param request              request
     * @param response             response, used to remove token cookie
     * @param redirectAttributes   redirect attributes, used to keep cas client redirect url
     * @return                     redirect to login page
     */
    @RequestMapping(Const.LOGOUT_URL)
    public String logout(HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {

        // logout
        XxlSsoHelper.logoutWithCookie(request, response);

        // keep cas-client redirect url, so login page can redirect back after re-login
        String redirectUrl = request.getParameter(Const.CLIENT_REDIRECT_URL);
        if (StringTool.isNotBlank(redirectUrl)) {
            redirectAttributes.addAttribute(Const.CLIENT_REDIRECT_URL, redirectUrl);
        }
        return "redirect:/login";
    }

}
