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
     * 示例：不添加注解，限制登录
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/test11")
    @ResponseBody
    public Response<String> test11(HttpServletRequest request, HttpServletResponse response) {
        Response<LoginInfo> loginCheckResult = XxlSsoHelper.loginCheckWithAttr( request);
        return Response.ofSuccess("login success, LoginInfo:" + loginCheckResult.getData().getUserName());
    }

    /**
     * 示例：默认注解（@XxlSso），限制登录
     *
     * @return
     */
    @RequestMapping("/test12")
    @ResponseBody
    @XxlSso
    public Response<String> test12() {
        return Response.ofSuccess("login success");
    }

    /**
     * 示例：注解login属性定制“@XxlSso(login = false)”，不限制登录
     *
     * @return
     */
    @RequestMapping("/test13")
    @ResponseBody
    @XxlSso(login = false)
    public Response<String> test13() {
        return Response.ofSuccess("not check login.");
    }

    /**
     * 示例：注解permission属性定制“@XxlSso(permission = "user:query")”，限制拥有指定权限
     *
     * @return
     */
    @RequestMapping("/test21")
    @ResponseBody
    @XxlSso(permission = "user:query")
    public Response<String> test21() {
        return Response.ofSuccess("has permission[user:query]");
    }

    /**
     * 示例：注解permission属性定制“@XxlSso(permission = "user:delete")”，限制拥有指定权限
     *
     * @return
     */
    @RequestMapping("/test22")
    @ResponseBody
    @XxlSso(permission = "user:delete")
    public Response<String> test22() {
        return Response.ofSuccess("has permission[user:delete]");
    }

    /**
     * 示例：注解role属性定制“@XxlSso(role = "role01")”，限制拥有指定角色
     *
     * @return
     */
    @RequestMapping("/test31")
    @ResponseBody
    @XxlSso(role = "role01")
    public Response<String> test31() {
        return Response.ofSuccess("has role[role01]");
    }

    /**
     * 示例：注解role属性定制“@XxlSso(role = "role02")”，限制拥有指定角色
     *
     * @return
     */
    @RequestMapping("/test32")
    @ResponseBody
    @XxlSso(role = "role02")
    public Response<String> test32() {
        return Response.ofSuccess("has role[role02]");
    }

}