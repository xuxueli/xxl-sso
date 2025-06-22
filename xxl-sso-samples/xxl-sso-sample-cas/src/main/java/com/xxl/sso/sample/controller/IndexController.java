package com.xxl.sso.sample.controller;

import com.xxl.sso.core.helper.XxlSsoHelper;
import com.xxl.sso.core.model.LoginInfo;
import com.xxl.tool.response.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * @author xuxueli 2017-08-01 21:39:47
 */
@Controller
public class IndexController {


    @Value("${xxl.sso.server.address}")
    private String serverAddress;
    @Value("${xxl.sso.server.logout.path}")
    private String logoutPath;

    /**
     * cas-server logout path
     */
    private String getLogoutPath(){
        return serverAddress + logoutPath;
    }

    @RequestMapping("/")
    public String index(Model model, HttpServletRequest request) {

        LoginInfo loginInfo = XxlSsoHelper.loginCheckWithAttr(request);
        model.addAttribute("loginInfo", loginInfo);
        model.addAttribute("logoutPath", getLogoutPath());
        return "index";
    }

    @RequestMapping("/json")
    @ResponseBody
    public Response<LoginInfo> json(HttpServletRequest request) {
        LoginInfo loginInfo = XxlSsoHelper.loginCheckWithAttr(request);
        return Response.ofSuccess(loginInfo);
    }

}