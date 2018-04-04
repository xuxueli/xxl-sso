package com.xxl.sso.sample.controller;

import com.xxl.sso.core.conf.Conf;
import com.xxl.sso.core.user.XxlUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * @author xuxueli 2017-08-01 21:39:47
 */
@Controller
public class IndexController {

    @RequestMapping("/")
    public String index(Model model, HttpServletRequest request) {

        XxlUser xxlUser = (XxlUser) request.getAttribute(Conf.SSO_USER);
        model.addAttribute("xxlUser", xxlUser);
        return "index";
    }

}