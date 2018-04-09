package com.xxl.sso.server.controller;

import com.xxl.sso.core.conf.Conf;
import com.xxl.sso.core.exception.XxlSsoException;
import com.xxl.sso.core.user.XxlUser;
import com.xxl.sso.core.util.SsoLoginHelper;
import com.xxl.sso.server.core.model.UserInfo;
import com.xxl.sso.server.dao.UserInfoDao;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

/**
 * sso server (for web)
 *
 * @author xuxueli 2017-08-01 21:39:47
 */
@Controller
public class IndexController {

    @Autowired
    private UserInfoDao userInfoDao;

    @RequestMapping("/")
    public String index(Model model, HttpServletRequest request) {

        // login check
        XxlUser xxlUser = SsoLoginHelper.loginCheck(request);

        if (xxlUser == null) {
            return "redirect:/login";
        } else {
            model.addAttribute("xxlUser", xxlUser);
            return "index";
        }
    }

    /**
     * Login page
     *
     * @param model
     * @param request
     * @return
     */
    @RequestMapping(Conf.SSO_LOGIN)
    public String login(Model model, HttpServletRequest request) {

        // login check
        XxlUser xxlUser = SsoLoginHelper.loginCheck(request);

        if (xxlUser != null) {

            // success redirect
            String redirectUrl = request.getParameter(Conf.REDIRECT_URL);
            if (StringUtils.isNotBlank(redirectUrl)) {

                String sessionId = SsoLoginHelper.getSessionIdByCookie(request);
                String redirectUrlFinal = redirectUrl + "?" + Conf.SSO_SESSIONID + "=" + sessionId;;

                return "redirect:" + redirectUrlFinal;
            } else {
                return "redirect:/";
            }
        }

        model.addAttribute("errorMsg", request.getParameter("errorMsg"));
        model.addAttribute(Conf.REDIRECT_URL, request.getParameter(Conf.REDIRECT_URL));
        return "login";
    }

    /**
     * Login
     *
     * @param request
     * @param redirectAttributes
     * @param username
     * @param password
     * @return
     */
    @RequestMapping("/doLogin")
    public String doLogin(HttpServletRequest request,
                        HttpServletResponse response,
                        RedirectAttributes redirectAttributes,
                        String username,
                        String password) {

        String errorMsg = null;
        // valid
        UserInfo existUser = null;
        try {
            if (StringUtils.isBlank(username)) {
                throw new XxlSsoException("Please input username.");
            }
            if (StringUtils.isBlank(password)) {
                throw new XxlSsoException("Please input password.");
            }
            existUser = userInfoDao.findByUsername(username);
            if (existUser == null) {
                throw new XxlSsoException("username is invalid.");
            }
            if (!existUser.getPassword().equals(password)) {
                throw new XxlSsoException("password is invalid.");
            }
        } catch (XxlSsoException e) {
            errorMsg = e.getMessage();
        }

        if (StringUtils.isNotBlank(errorMsg)) {
            redirectAttributes.addAttribute("errorMsg", errorMsg);

            redirectAttributes.addAttribute(Conf.REDIRECT_URL, request.getParameter(Conf.REDIRECT_URL));
            return "redirect:/login";
        }

        // login success
        XxlUser xxlUser = new XxlUser();
        xxlUser.setUserid(existUser.getId());
        xxlUser.setUsername(existUser.getUsername());

        String sessionId = UUID.randomUUID().toString();

        SsoLoginHelper.login(response, sessionId, xxlUser);

        // success redirect
        String redirectUrl = request.getParameter(Conf.REDIRECT_URL);
        if (StringUtils.isNotBlank(redirectUrl)) {
            String redirectUrlFinal = redirectUrl + "?" + Conf.SSO_SESSIONID + "=" + sessionId;
            return "redirect:" + redirectUrlFinal;
        } else {
            return "redirect:/";
        }
    }

    /**
     * Logout
     *
     * @param request
     * @param redirectAttributes
     * @return
     */
    @RequestMapping(Conf.SSO_LOGOUT)
    public String logout(HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {

        // logout
        SsoLoginHelper.logout(request, response);

        redirectAttributes.addAttribute(Conf.REDIRECT_URL, request.getParameter(Conf.REDIRECT_URL));
        return "redirect:/login";
    }


}