package com.xxl.sso.server.controller;

import com.xxl.sso.core.user.XxlUser;
import com.xxl.sso.core.util.SsoLoginHelper;
import com.xxl.sso.server.core.model.UserInfo;
import com.xxl.sso.server.core.result.ReturnT;
import com.xxl.sso.server.dao.UserInfoDao;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.UUID;

/**
 * sso server (for app)
 *
 * @author xuxueli 2018-04-08 21:02:54
 */
@Controller
@RequestMapping("/app")
public class AppController {

    @Autowired
    private UserInfoDao userInfoDao;


    /**
     * Login
     *
     * @param username
     * @param password
     * @return
     */
    @RequestMapping("/login")
    @ResponseBody
    public ReturnT<String> login(String username, String password) {

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

        // login success
        XxlUser xxlUser = new XxlUser();
        xxlUser.setUserid(existUser.getId());
        xxlUser.setUsername(existUser.getUsername());

        String sessionId = UUID.randomUUID().toString();

        SsoLoginHelper.login(sessionId, xxlUser);

        // result
        return new ReturnT<String>(sessionId);
    }


    /**
     * Logout
     *
     * @param sessionId
     * @return
     */
    @RequestMapping("/logout")
    @ResponseBody
    public ReturnT<String> logout(String sessionId) {

        // logout
        SsoLoginHelper.logout(sessionId);
        return ReturnT.SUCCESS;
    }

    /**
     * logincheck
     *
     * @param sessionId
     * @return
     */
    @RequestMapping("/logincheck")
    @ResponseBody
    public ReturnT<XxlUser> logincheck(String sessionId) {

        // logout
        XxlUser xxlUser = SsoLoginHelper.loginCheck(sessionId);
        if (xxlUser == null) {
            return new ReturnT<XxlUser>(ReturnT.FAIL_CODE, "sso not login.");
        }
        return new ReturnT<XxlUser>(xxlUser);
    }

}