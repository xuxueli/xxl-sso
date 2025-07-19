package com.xxl.sso.core.token;

import com.xxl.sso.core.model.LoginInfo;
import com.xxl.tool.core.StringTool;
import com.xxl.tool.encrypt.Base64Tool;
import com.xxl.tool.gson.GsonTool;
import com.xxl.tool.response.Response;

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
    public static Response<String> generateToken(LoginInfo loginInfo){
        // valid loginInfo
        if (loginInfo==null || StringTool.isBlank(loginInfo.getUserId()) || StringTool.isBlank(loginInfo.getVersion())) {
            return Response.ofFail("generateToken fail, invalid loginInfo.");
        }

        // generate token-LoginInfo, only contains: userId + version
        LoginInfo loginInfoForToken = new LoginInfo(loginInfo.getUserId(), loginInfo.getVersion());

        // generate token
        String json = GsonTool.toJson(loginInfoForToken);
        String token = Base64Tool.encodeUrlSafe(json);

        return Response.ofSuccess(token);

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
