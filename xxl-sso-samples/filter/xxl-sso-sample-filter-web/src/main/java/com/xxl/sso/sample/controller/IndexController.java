package com.xxl.sso.sample.controller;

import com.xxl.sso.core.annotation.XxlSso;
import com.xxl.sso.core.helper.XxlSsoHelper;
import com.xxl.sso.core.model.LoginInfo;
import com.xxl.tool.response.Response;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author xuxueli 2017-08-01 21:39:47
 */
@Controller
public class IndexController {

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
     * 示例：API方式获取登录用户信息（LoginInfo）；API方式校验角色、权限；
     *
     * @return
     */
    @RequestMapping("/test41")
    @ResponseBody
    @XxlSso
    public Response<String> test41(HttpServletRequest request) {

        Response<LoginInfo> loginCheckResult = XxlSsoHelper.loginCheckWithAttr( request);
        Response<String> hasRole01 = XxlSsoHelper.hasRole(loginCheckResult.getData(), "role01");
        Response<String> hasRole02 = XxlSsoHelper.hasRole(loginCheckResult.getData(), "role02");
        Response<String> hasPermission01 = XxlSsoHelper.hasPermission(loginCheckResult.getData(), "user:query");
        Response<String> hasPermission02 = XxlSsoHelper.hasPermission(loginCheckResult.getData(), "user:delete");

        String data = "LoginInfo:" + loginCheckResult.getData() +
                ", hasRole01:" + hasRole01.isSuccess() +
                ", hasRole02:" + hasRole02.isSuccess() +
                ", hasPermission01:" + hasPermission01.isSuccess() +
                ", hasPermission02:" + hasPermission02.isSuccess();

        return Response.ofSuccess(data);
    }

}