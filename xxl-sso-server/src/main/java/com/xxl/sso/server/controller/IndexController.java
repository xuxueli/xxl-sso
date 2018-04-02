package com.xxl.sso.server.controller;

import com.xxl.sso.core.conf.Conf;
import com.xxl.sso.core.exception.XxlSsoException;
import com.xxl.sso.core.user.XxlUser;
import com.xxl.sso.server.core.loginstore.LoginStore;
import com.xxl.sso.server.core.model.UserInfo;
import com.xxl.sso.server.core.result.ReturnT;
import com.xxl.sso.server.dao.UserInfoDao;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private UserInfoDao userInfoDao;

    /**
     * SSO Server 登陆首页
     *
     * @param model
     * @param request
     * @return
     */
    @RequestMapping("/")
    public String index(Model model, HttpServletRequest request) {

        String redirectUrl = request.getParameter(Conf.REDIRECT_URL);
        if (redirectUrl == null) {
            throw new XxlSsoException("来源非法");
        }

        // login store
        String sessionId = request.getSession().getId();
        XxlUser xxlUser = LoginStore.getInstance().get(sessionId);
        if (xxlUser != null) {
            String redirectUrlFinal = redirectUrl + "?" + Conf.SSO_SESSIONID + "=" + sessionId;;
            return "redirect:" + redirectUrlFinal;
        }

        model.addAttribute(Conf.REDIRECT_URL, redirectUrl);
        return "index";
    }

    /**
     * 登陆接口
     *
     * @param request
     * @param username
     * @param password
     * @return
     */
    @RequestMapping("/login")
    @ResponseBody
    public ReturnT<String> login(HttpServletRequest request, String username, String password) {

        // valid
        if (StringUtils.isBlank(username)) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "Please input username.");
        }
        if (StringUtils.isBlank(password)) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "Please input password.");
        }
        UserInfo existUser = userInfoDao.findByUsername(username);
        if (existUser == null) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "username is invalid.");
        }
        if (!existUser.getPassword().equals(password)) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "password is invalid.");
        }

        // login
        XxlUser xxlUser = new XxlUser();
        xxlUser.setUserid(existUser.getId());
        xxlUser.setUsername(existUser.getUsername());

        String sessionId = request.getSession().getId();

        LoginStore.getInstance().put(sessionId, xxlUser);

        return new ReturnT<String>(sessionId);
    }

    /**
     * 注销接口
     *
     * @param request
     * @return
     */
    @RequestMapping("/logout")
    @ResponseBody
    public ReturnT<XxlUser> logout(HttpServletRequest request) {

        // logout
        String sessionId = request.getSession().getId();
        LoginStore.getInstance().remove(sessionId);

        return new ReturnT<XxlUser>(ReturnT.SUCCESS_CODE, null);
    }


}