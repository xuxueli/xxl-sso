package com.xxl.sso.core.token;

import com.xxl.sso.core.exception.XxlSsoException;
import com.xxl.sso.core.model.LoginInfo;
import com.xxl.tool.core.StringTool;
import com.xxl.tool.encrypt.Base64Tool;
import com.xxl.tool.gson.GsonTool;

/**
 * token helper
 *
 * @author xuxueli 2018-11-15 15:45:08
 */
public class TokenHelper {

    /**
     * generate token from LoginInfo
     *
     * @param loginInfo
     * @return
     */
    public static String generateToken(LoginInfo loginInfo){
        try {
            // valid loginInfo
            if (loginInfo==null || StringTool.isBlank(loginInfo.getUserId()) || StringTool.isBlank(loginInfo.getUserName())) {
                throw new XxlSsoException("TokenHelper.generateToken fail, invalid loginInfo.");
            }
            LoginInfo loginInfoForToken = new LoginInfo(loginInfo.getUserId(), loginInfo.getUserName(), loginInfo.getVersion(), loginInfo.getExpireTime());

            // generate token
            String json = GsonTool.toJson(loginInfoForToken);
            return Base64Tool.encodeUrlSafe(json);
        } catch (Exception e) {
            throw new XxlSsoException(e);
        }
    }

    /**
     * parse token 2 LoginInfo
     *
     * @param token
     * @return
     */
    public static LoginInfo parseToken(String token){
        try {
            // valid token
            if (StringTool.isBlank( token)) {
                return null;
            }

            // parse token
            String json = Base64Tool.decodeUrlSafe(token);
            return GsonTool.fromJson(json, LoginInfo.class);
        } catch (Exception e) {
            return null;
        }
    }

}
