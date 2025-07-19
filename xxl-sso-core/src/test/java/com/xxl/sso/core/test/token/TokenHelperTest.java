package com.xxl.sso.core.test.token;

import com.xxl.sso.core.model.LoginInfo;
import com.xxl.sso.core.token.TokenHelper;
import com.xxl.tool.id.UUIDTool;
import com.xxl.tool.response.Response;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TokenHelperTest {
    private static final Logger logger = LoggerFactory.getLogger(TokenHelperTest.class);

    @Test
    public void test(){
        LoginInfo loginInfo = new LoginInfo("1", UUIDTool.getSimpleUUID());
        Response<String> tokenResponse = TokenHelper.generateToken(loginInfo);
        String token = tokenResponse.getData();
        logger.info("token:" + token);

        LoginInfo loginInfo2 = TokenHelper.parseToken(token);
        logger.info("loginInfo:" + loginInfo2);
    }


}
