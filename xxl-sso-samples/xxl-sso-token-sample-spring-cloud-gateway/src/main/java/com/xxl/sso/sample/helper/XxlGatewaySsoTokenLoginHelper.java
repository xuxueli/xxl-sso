package com.xxl.sso.sample.helper;

import com.xxl.sso.core.conf.Conf;
import com.xxl.sso.core.login.SsoTokenLoginHelper;
import com.xxl.sso.core.user.XxlSsoUser;
import org.springframework.http.server.reactive.ServerHttpRequest;

/**
 * @author Wu Weijie
 */
public class XxlGatewaySsoTokenLoginHelper {

    /**
     * client login
     *
     * @param request
     * @return
     */
    public static XxlSsoUser loginCheck(ServerHttpRequest request) {
        return SsoTokenLoginHelper.loginCheck(ReactiveHttpHelper.getHeader(request, Conf.SSO_SESSIONID));
    }

    /**
     * client logout
     *
     * @param request
     */
    public static void logout(ServerHttpRequest request) {
        SsoTokenLoginHelper.logout(ReactiveHttpHelper.getHeader(request, Conf.SSO_SESSIONID));
    }

}
