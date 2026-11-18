package com.xxl.sso.core.test.helper;

import com.xxl.sso.core.constant.Const;
import com.xxl.sso.core.helper.XxlSsoHelper;
import com.xxl.sso.core.model.LoginInfo;
import com.xxl.sso.core.store.impl.LocalLoginStore;
import com.xxl.sso.core.token.TokenHelper;
import com.xxl.tool.id.UUIDTool;
import com.xxl.tool.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class XxlSsoHelperLogoutTest {

    private String validToken;

    @BeforeEach
    void setUp() {
        XxlSsoHelper.init(new LocalLoginStore(), Const.XXL_SSO_TOKEN, Const.EXPIRE_TIME_FOR_7_DAY);
        LoginInfo loginInfo = new LoginInfo("1001", UUIDTool.getSimpleUUID());
        Response<String> loginResponse = XxlSsoHelper.login(loginInfo);
        assertTrue(loginResponse.isSuccess());
        validToken = loginResponse.getData();
    }

    @Test
    void logoutRejectsForgedTokenWithoutRemovingActiveSession() {
        String forgedWrongSig = TokenHelper.generateToken(new LoginInfo("1001", "forged-sig")).getData();
        Response<String> forgedResp = XxlSsoHelper.logout(forgedWrongSig);
        assertFalse(forgedResp.isSuccess());

        // Missing signature in payload
        String forgedNoSig = java.util.Base64.getUrlEncoder().withoutPadding()
                .encodeToString("{\"userId\":\"1001\"}".getBytes(java.nio.charset.StandardCharsets.UTF_8));
        Response<String> noSigResp = XxlSsoHelper.logout(forgedNoSig);
        assertFalse(noSigResp.isSuccess());

        // Active session must remain valid
        assertTrue(XxlSsoHelper.loginCheck(validToken).isSuccess());
    }

    @Test
    void logoutWithValidTokenRemovesSession() {
        Response<String> logoutResp = XxlSsoHelper.logout(validToken);
        assertTrue(logoutResp.isSuccess());
        assertFalse(XxlSsoHelper.loginCheck(validToken).isSuccess());
    }

    @Test
    void logoutWithBlankOrMalformedTokenDoesNotAffectActiveSession() {
        assertTrue(XxlSsoHelper.logout("").isSuccess());
        assertFalse(XxlSsoHelper.logout("not-a-valid-token").isSuccess());
        assertTrue(XxlSsoHelper.loginCheck(validToken).isSuccess());
    }
}
