package com.xxl.sso.core.test.store;

import com.xxl.sso.core.constant.Const;
import com.xxl.sso.core.model.LoginInfo;
import com.xxl.sso.core.store.LoginStore;
import com.xxl.sso.core.store.impl.LocalLoginStore;
import com.xxl.sso.core.store.impl.RedisLoginStore;
import com.xxl.sso.core.token.TokenHelper;
import com.xxl.tool.response.Response;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginStoreTest {
    private static Logger logger = LoggerFactory.getLogger(LoginStoreTest.class);

    @Test
    public void test() {
        // store
        LoginStore loginStore = new LocalLoginStore();

        // init login info
        LoginInfo loginInfo = new LoginInfo("666", "zhagnsan", "v1", System.currentTimeMillis() + Const.EXPIRE_TIME_FOR_7_DAY);

        // set
        Response<String> ret = loginStore.set(loginInfo, Const.EXPIRE_TIME_FOR_7_DAY);
        String token = ret.getData();
        logger.info("store loginInfo:{}", loginStore.get(token));

        // remove
        loginStore.remove( token);
        logger.info("store loginInfo2:{}", loginStore.get(token));
    }

    @Test
    public void test2() {
        // store param
        String nodes = "127.0.0.1:6379";
        String prefix = "xxl-sso:";

        // store start
        LoginStore loginStore = new RedisLoginStore(nodes, null, null, prefix);
        loginStore.start();

        // init login info
        LoginInfo loginInfo = new LoginInfo("666", "zhagnsan", "v1", System.currentTimeMillis() + Const.EXPIRE_TIME_FOR_7_DAY);

        // set
        Response<String> ret = loginStore.set(loginInfo, Const.EXPIRE_TIME_FOR_7_DAY);
        String token = ret.getData();
        logger.info("store loginInfo:{}", loginStore.get(token));

        // remove
        loginStore.remove( token);
        logger.info("store loginInfo2:{}", loginStore.get(token));

        // store stop
        loginStore.stop();
    }


    @Test
    public void test3() {
        // store param
        String nodes = "127.0.0.1:6379";
        String prefix = "xxl-sso:";

        // store start
        LoginStore loginStore = new RedisLoginStore(nodes, null, null, prefix);
        loginStore.start();

        // init login info
        LoginInfo loginInfo = new LoginInfo("666", "zhagnsan", "v1", System.currentTimeMillis() + Const.EXPIRE_TIME_FOR_7_DAY);
        String token = TokenHelper.generateToken(loginInfo);

        // init login info
        Response<String> ret = loginStore.createTicket(token, 30*1000);
        String ticket = ret.getData();
        logger.info("store ticket:{}", ticket);

        // remove
        Response<String> ret2 = loginStore.validTicket( ticket);
        String token2 = ret2.getData();
        logger.info("store token2:{}", token2);

        // store stop
        loginStore.stop();
    }

}
