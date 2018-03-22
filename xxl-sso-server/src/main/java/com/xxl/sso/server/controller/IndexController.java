package com.xxl.sso.server.controller;

import com.xxl.sso.server.core.model.UserInfo;
import com.xxl.sso.server.service.UserInfoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author xuxueli 2017-08-01 21:39:47
 */
@Controller
public class IndexController {

    @Resource
    private UserInfoService userInfoService;

    @RequestMapping("/")
    public String index(Model model) {

        List<UserInfo> userInfoList = userInfoService.findAll();
        model.addAttribute("userInfoList", userInfoList);

        return "index";
    }

}