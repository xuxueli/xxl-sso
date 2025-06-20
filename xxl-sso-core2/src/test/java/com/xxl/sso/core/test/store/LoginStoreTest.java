package com.xxl.sso.core.test.store;

import com.xxl.sso.core.model.LoginInfo;
import com.xxl.sso.core.store.LoginStore;
import com.xxl.sso.core.store.impl.LocalLoginStore;
import com.xxl.sso.core.store.impl.RedisLoginStore;
import com.xxl.sso.core.token.TokenHelper;
import com.xxl.sso.core.util.JedisTool;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginStoreTest {
    private static Logger logger = LoggerFactory.getLogger(LoginStoreTest.class);

    @Test
    public void test() {
        LoginStore loginStore = new LocalLoginStore();

        LoginInfo loginInfo = new LoginInfo("666", "zhagnsan", "v1");
        String token = TokenHelper.generateToken(loginInfo);
        logger.info("token:{}", token);

        loginStore.set(loginInfo);
        logger.info("store loginInfo:{}", loginStore.get(token));

        loginStore.remove( token);
        logger.info("store loginInfo2:{}", loginStore.get(token));
    }

    @Test
    public void test2() {
        // start redis
        String nodes = "127.0.0.1:6379";
        JedisTool jedisTool = new JedisTool(nodes, null, null);
        jedisTool.start();

        String prefix = "xxl-sso:";

        // store
        LoginStore loginStore = new RedisLoginStore(jedisTool, prefix);

        LoginInfo loginInfo = new LoginInfo("666", "zhagnsan", "v1");
        String token = TokenHelper.generateToken(loginInfo);
        logger.info("token:{}", token);

        loginStore.set(loginInfo);
        logger.info("store loginInfo:{}", loginStore.get(token));

        loginStore.remove( token);
        logger.info("store loginInfo2:{}", loginStore.get(token));


        // stop redis
        jedisTool.stop();
    }

}
