package com.xxl.sso.core.test.util;

import com.xxl.sso.core.util.WebTool;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link WebTool} test
 *
 * @author xuxueli 2026-11-22
 */
public class WebToolTest {

    @Test
    public void isAjaxTest() {
        assertTrue(WebTool.isAjax(request("application/json", null, null)));
        assertTrue(WebTool.isAjax(request("*/*", "XMLHttpRequest", null)));
        assertTrue(WebTool.isAjax(request(null, null, "application/json;charset=UTF-8")));

        assertFalse(WebTool.isAjax(request("text/html", null, null)));
        assertFalse(WebTool.isAjax(request(null, null, null)));
    }

    /**
     * build a mock request with the given headers
     */
    private HttpServletRequest request(String accept, String requestedWith, String contentType) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", accept);
        headers.put("X-Requested-With", requestedWith);
        headers.put("Content-Type", contentType);

        return (HttpServletRequest) Proxy.newProxyInstance(
                WebToolTest.class.getClassLoader(),
                new Class<?>[]{HttpServletRequest.class},
                (proxy, method, args) -> "getHeader".equals(method.getName()) ? headers.get(args[0]) : null);
    }

}
