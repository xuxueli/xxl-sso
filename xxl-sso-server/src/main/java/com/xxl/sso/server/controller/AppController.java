package com.xxl.sso.server.controller;

import com.xxl.sso.core.user.XxlSsoUser;
import com.xxl.sso.core.util.SessionIdHelper;
import com.xxl.sso.core.util.SsoLoginHelper;
import com.xxl.sso.server.core.model.UserInfo;
import com.xxl.sso.server.core.result.ReturnT;
import com.xxl.sso.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * sso server (for app)
 *
 * @author xuxueli 2018-04-08 21:02:54
 */
@Controller
@RequestMapping("/app")
public class AppController {

    @Autowired
    private UserService userService;


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

        // valid login
        ReturnT<UserInfo> result = userService.findUser(username, password);
        if (result.getCode() != ReturnT.SUCCESS_CODE) {
            return new ReturnT<String>(result.getCode(), result.getMsg());
        }

        // make xxl-sso user
        XxlSsoUser xxlUser = new XxlSsoUser();
        xxlUser.setUserid(result.getData().getUserid());
        xxlUser.setUsername(result.getData().getUsername());

        // make session id
        String sessionId = SessionIdHelper.makeSessionId(SessionIdHelper.SessionIdGroup.WEB, xxlUser);

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
    public ReturnT<XxlSsoUser> logincheck(String sessionId) {

        // logout
        XxlSsoUser xxlUser = SsoLoginHelper.loginCheck(sessionId);
        if (xxlUser == null) {
            return new ReturnT<XxlSsoUser>(ReturnT.FAIL_CODE, "sso not login.");
        }
        return new ReturnT<XxlSsoUser>(xxlUser);
    }

}