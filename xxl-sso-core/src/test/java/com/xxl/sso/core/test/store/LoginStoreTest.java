package com.xxl.sso.core.test.store;

import com.xxl.sso.core.constant.Const;
import com.xxl.sso.core.model.LoginInfo;
import com.xxl.sso.core.store.LoginStore;
import com.xxl.sso.core.store.impl.LocalLoginStore;
import com.xxl.sso.core.store.impl.RedisLoginStore;
import com.xxl.sso.core.token.TokenHelper;
import com.xxl.tool.core.MapTool;
import com.xxl.tool.id.UUIDTool;
import com.xxl.tool.response.Response;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LoginStoreTest {
    private static Logger logger = LoggerFactory.getLogger(LoginStoreTest.class);

    @Test
    public void test() {
        // store
        LoginStore loginStore = new LocalLoginStore();

        // init LoginInfo
        LoginInfo loginInfo = new LoginInfo(
                "666",
                "zhagnsan",
                null,
                null,
                Arrays.asList("role1", "role2"),
                Arrays.asList("permission1", "permission2"),
                System.currentTimeMillis() / 1000 + Const.EXPIRE_SECOND_FOR_7_DAY,
                UUIDTool.getSimpleUUID());

        // set
        Response<String> ret = loginStore.set(loginInfo);
        String token = ret.getData();
        logger.info("store loginInfo:{}", loginStore.get(loginInfo.getUserId()));

        // update
        loginInfo.setUserName("zhagnsan22");
        loginInfo.setExtraInfo(MapTool.newMap("k1", "v1"));
        loginInfo.setRoleList(Arrays.asList("role1", "role2"));
        loginInfo.setPermissionList(Arrays.asList("permission1", "permission2"));
        loginInfo.setExpireTime(System.currentTimeMillis() / 1000 + Const.EXPIRE_SECOND_FOR_7_DAY);
        loginStore.update(loginInfo);
        logger.info("store loginInfo (after update):{}", loginStore.get(loginInfo.getUserId()));

        // remove
        loginStore.remove( loginInfo.getUserId());
        logger.info("store loginInfo2 (after remove):{}", loginStore.get(loginInfo.getUserId()));
    }

    @Test
    public void test2() {
        // store param
        String nodes = "127.0.0.1:6379";
        String prefix = "xxl-sso:";

        // store start
        LoginStore loginStore = new RedisLoginStore(nodes, null, null, prefix);
        loginStore.start();

        // init LoginInfo
        LoginInfo loginInfo = new LoginInfo(
                "666",
                "zhagnsan",
                null,
                null,
                Arrays.asList("role1", "role2"),
                Arrays.asList("permission1", "permission2"),
                System.currentTimeMillis() / 1000 + Const.EXPIRE_SECOND_FOR_7_DAY,
                UUIDTool.getSimpleUUID());

        // set
        Response<String> ret = loginStore.set(loginInfo);
        String token = ret.getData();
        logger.info("store loginInfo:{}", loginStore.get(loginInfo.getUserId()));

        // update
        loginInfo.setUserName("zhagnsan22");
        loginInfo.setExtraInfo(MapTool.newMap("k1", "v1"));
        loginInfo.setRoleList(Arrays.asList("role1", "role2"));
        loginInfo.setPermissionList(Arrays.asList("permission1", "permission2"));
        loginInfo.setExpireTime(System.currentTimeMillis() / 1000 + Const.EXPIRE_SECOND_FOR_7_DAY);
        loginStore.update(loginInfo);
        logger.info("store loginInfo(after update):{}", loginStore.get(loginInfo.getUserId()));

        // remove
        loginStore.remove( loginInfo.getUserId());
        logger.info("store loginInfo2(after remove):{}", loginStore.get(loginInfo.getUserId()));

        // store stop
        loginStore.stop();
    }


    @Test
    public void localStoreCapacityTest() {
        // capacity = 2, LRU evicts the oldest entry
        LocalLoginStore loginStore = new LocalLoginStore(2);
        for (int i = 0; i < 3; i++) {
            LoginInfo loginInfo = new LoginInfo("cap-" + i, UUIDTool.getSimpleUUID());
            loginInfo.setExpireTime(System.currentTimeMillis() / 1000 + Const.EXPIRE_SECOND_FOR_7_DAY);
            assertTrue(loginStore.set(loginInfo).isSuccess());
        }

        assertFalse(loginStore.get("cap-0").isSuccess());
        assertTrue(loginStore.get("cap-1").isSuccess());
        assertTrue(loginStore.get("cap-2").isSuccess());
    }

    @Test
    public void redisShortExpireTimeTest() {
        // expire-time is seconds; short remaining timeout must fallback to at least 1s TTL
        RedisLoginStore loginStore = new RedisLoginStore("127.0.0.1:6379", null, null, "xxl-sso:short:");
        loginStore.start();
        try {
            LoginInfo loginInfo = new LoginInfo("short-" + UUIDTool.getSimpleUUID(), UUIDTool.getSimpleUUID());
            loginInfo.setExpireTime(System.currentTimeMillis() / 1000 + 1);

            assertTrue(loginStore.set(loginInfo).isSuccess());
        } finally {
            loginStore.stop();
        }
    }

    @Test
    public void testTicket() {
        // store param
        String nodes = "127.0.0.1:6379";
        String prefix = "xxl-sso:";

        // store start
        LoginStore loginStore = new RedisLoginStore(nodes, null, null, prefix);
        loginStore.start();

        // init LoginInfo
        LoginInfo loginInfo = new LoginInfo("1", UUIDTool.getSimpleUUID());

        // generate token
        Response<String> tokenResponse = TokenHelper.generateToken(loginInfo);
        String token = tokenResponse.getData();

        // generate ticket
        String ticket = loginInfo.getUserId().concat("_").concat(UUIDTool.getSimpleUUID());

        // createTicket
        Response<String> ret = loginStore.createTicket(ticket, token, 30);
        logger.info("store ticket:{}", ticket);

        // validTicket
        Response<String> ret2 = loginStore.validTicket( ticket);
        String token2 = ret2.getData();
        logger.info("store token2:{}", token2);

        // store stop
        loginStore.stop();
    }

}
