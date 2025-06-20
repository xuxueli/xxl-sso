package com.xxl.sso.core.test.token;

import com.xxl.sso.core.model.LoginInfo;
import com.xxl.sso.core.token.TokenHelper;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TokenHelperTest {
    private static final Logger logger = LoggerFactory.getLogger(TokenHelperTest.class);

    @Test
    public void test(){
        LoginInfo loginInfo = new LoginInfo("1", "admin", "v1", -1);
        String token = TokenHelper.generateToken(loginInfo);
        logger.info("token:" + token);

        LoginInfo loginInfo2 = TokenHelper.parseToken(token);
        logger.info("loginInfo:" + loginInfo2);
    }


}
