package com.xxl.sso.core.test.util;

import com.xxl.sso.core.util.CasUrlTool;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * CasUrlTool test
 *
 * @author xuxueli 2026-11-22
 */
public class CasUrlToolTest {

    private static final String WHITELIST = "http://xxlssoclient1.com:8081/**,"
            + "http://127.0.0.1:8081/**,"
            + "http://*.example.com:8081/app/**,"
            + "https://app.example.com/**";

    private static final CasUrlTool VALIDATOR = new CasUrlTool(WHITELIST);

    @Test
    public void exactOriginTest() {
        Assertions.assertTrue(VALIDATOR.isAllowed("http://xxlssoclient1.com:8081"));
        Assertions.assertTrue(VALIDATOR.isAllowed("http://xxlssoclient1.com:8081/"));
        Assertions.assertTrue(VALIDATOR.isAllowed("http://xxlssoclient1.com:8081/index"));
        Assertions.assertTrue(VALIDATOR.isAllowed("http://127.0.0.1:8081/a/b?k=v"));
    }

    @Test
    public void antPathTest() {
        // 子域名通配
        Assertions.assertTrue(VALIDATOR.isAllowed(
                "http://client1.example.com:8081/app/index"));
        // 路径通配
        Assertions.assertTrue(VALIDATOR.isAllowed(
                "http://client1.example.com:8081/app/sub/index"));
        Assertions.assertFalse(VALIDATOR.isAllowed(
                "http://client1.example.com:8081/other/index"));
        // 默认端口，需与白名单写法一致
        Assertions.assertTrue(VALIDATOR.isAllowed("https://app.example.com/index"));
        Assertions.assertFalse(VALIDATOR.isAllowed("https://app.example.com:443/index"));
    }

    @Test
    public void emptyWhitelistTest() {
        CasUrlTool validator = new CasUrlTool("");
        Assertions.assertTrue(validator.isAllowed("https://evil.com/index"));

        // 跳转地址为空，始终拒绝
        Assertions.assertFalse(validator.isAllowed(null));
        Assertions.assertFalse(validator.isAllowed(""));
    }

    @Test
    public void rejectTest() {
        // 非白名单 host
        Assertions.assertFalse(VALIDATOR.isAllowed("https://evil.com/index"));
        // 后缀伪装
        Assertions.assertFalse(VALIDATOR.isAllowed(
                "http://xxlssoclient1.com.evil.com:8081/index"));
        // 端口不一致
        Assertions.assertFalse(VALIDATOR.isAllowed(
                "https://app.example.com:8443/index"));
    }

    @Test
    public void invalidWhitelistTest() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new CasUrlTool("app.example.com/**"));
    }

    @Test
    public void appendQueryParamTest() {
        Assertions.assertEquals("http://a.com/index?ticket=1",
                CasUrlTool.appendQueryParam("http://a.com/index", "ticket", "1"));
        Assertions.assertEquals("http://a.com/index?k=v&ticket=1",
                CasUrlTool.appendQueryParam("http://a.com/index?k=v", "ticket", "1"));
        Assertions.assertEquals("http://a.com/index?ticket=1",
                CasUrlTool.appendQueryParam("http://a.com/index#fragment", "ticket", "1"));
    }

}
