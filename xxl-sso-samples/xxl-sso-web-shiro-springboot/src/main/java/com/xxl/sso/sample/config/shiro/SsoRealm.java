package com.xxl.sso.sample.config.shiro;

import com.xxl.sso.core.login.SsoTokenLoginHelper;
import com.xxl.sso.core.user.XxlSsoUser;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.Principal;

/**
 * Shiro Realm
 * 取消了shiro的校验功能（由SSO实现）
 * 仅负责加载权限信息
 *
 * @author KisChang
 * @date 2019-05-20
 */
public class SsoRealm extends AuthorizingRealm {

    private static final Logger logger = LoggerFactory.getLogger(SsoRealm.class);

    public SsoRealm() {
    }

    public SsoRealm(CacheManager cacheManager) {
        super(cacheManager);
    }

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof SsoToken;//表示此Realm只支持OAuth2Token类型
    }

    @Override
    public boolean isPermitted(PrincipalCollection principals, String permission) {
        return super.isPermitted(principals, permission);
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        //执行身份校验
        SsoToken ssoToken = (SsoToken) token;
        XxlSsoUser xxlUser = SsoTokenLoginHelper.loginCheck(ssoToken.getSessionId());
        if (xxlUser == null) {
            //跳转到登录
            throw new AccountException("请重新登录！");
        }else {
            return new SimpleAuthenticationInfo(xxlUser.getUsername(), xxlUser, getName());
        }
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        String username;
        if (principals.getPrimaryPrincipal() instanceof Principal){
            Principal principal = (Principal) principals.getPrimaryPrincipal();
            username = principal.getName();
        }else {
            username = (String) principals.getPrimaryPrincipal();
        }
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        //获取权限信息
        //do something...
        return authorizationInfo;
    }


    //覆盖Shiro的token校验
    @Override
    protected void assertCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) throws AuthenticationException{
        //此处之前是用于检测token的密码是否正确的，覆盖后不再校验
    }

}