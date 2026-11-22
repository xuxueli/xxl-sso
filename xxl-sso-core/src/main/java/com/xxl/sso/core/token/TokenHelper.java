package com.xxl.sso.core.token;

import com.xxl.sso.core.model.LoginInfo;
import com.xxl.tool.core.StringTool;
import com.xxl.tool.crypto.Base64Tool;
import com.xxl.tool.json.GsonTool;
import com.xxl.tool.response.Response;

/**
 * token helper
 *
 * <p>
 * 负责 LoginInfo 与 token 的编解码：token 仅携带 {@code userId + signature}，
 * 使用 Gson 序列化后做 Base64Url 编码，不包含角色、权限等完整登录态。
 * </p>
 *
 * <p>
 * 本类只做基本合法性校验；token 过期、签名比对、存储校验由
 * {@link com.xxl.sso.core.helper.XxlSsoHelper} 完成。
 * </p>
 *
 * @author xuxueli 2018-11-15 15:45:08
 */
public class TokenHelper {

    /**
     * generate token from LoginInfo
     *
     * @param loginInfo     login data, must contain userId and signature
     * @return              Response#data is Base64Url token; fail when loginInfo is invalid
     */
    public static Response<String> generateToken(LoginInfo loginInfo){
        // valid loginInfo
        if (loginInfo==null || StringTool.isBlank(loginInfo.getUserId()) || StringTool.isBlank(loginInfo.getSignature())) {
            return Response.ofFail("generateToken fail, invalid loginInfo.");
        }

        // generate token-LoginInfo, only contains: userId + version
        LoginInfo loginInfoForToken = new LoginInfo(loginInfo.getUserId(), loginInfo.getSignature());

        // generate token
        String json = GsonTool.toJson(loginInfoForToken);
        String token = Base64Tool.encodeUrlSafe(json);

        return Response.ofSuccess(token);

    }

    /**
     * parse token to LoginInfo
     *
     * @param token     Base64Url token
     * @return          parsed LoginInfo; {@code null} when token is blank or malformed
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
