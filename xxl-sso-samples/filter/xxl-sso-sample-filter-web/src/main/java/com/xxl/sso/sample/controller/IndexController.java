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
     * 示例：必须登录
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/test1")
    @ResponseBody
    @XxlSso
    public Response<String> test1(HttpServletRequest request, HttpServletResponse response) {
        return Response.ofSuccess("login success");
    }

    /**
     * 示例：不要求登录，不进行任何拦截
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/test2")
    @ResponseBody
    @XxlSso(permission = "user:query")
    public Response<String> test2(HttpServletRequest request, HttpServletResponse response) {
        return Response.ofSuccess("had permission[user:query]");
    }

    /**
     * 示例：限制必须拥有指定权限
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/test3")
    @ResponseBody
    @XxlSso(permission = "user:delete")
    public Response<String> test3(HttpServletRequest request, HttpServletResponse response) {
        return Response.ofSuccess("had permission[user:delete]");
    }

    /**
     * 示例：限制必须拥有指定权限
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/test4")
    @ResponseBody
    @XxlSso(login = false)
    public Response<String> test4(HttpServletRequest request, HttpServletResponse response) {
        return Response.ofSuccess("allow anything.");
    }

    /**
     * 示例：限制必须拥有指定角色
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/test5")
    @ResponseBody
    @XxlSso(role = "role01")
    public Response<String> test5(HttpServletRequest request, HttpServletResponse response) {
        return Response.ofSuccess("had role[role01]");
    }

    /**
     * 示例：限制必须拥有指定角色
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/test6")
    @ResponseBody
    @XxlSso(role = "role02")
    public Response<String> test6(HttpServletRequest request, HttpServletResponse response) {
        return Response.ofSuccess("had role[role02]");
    }

}