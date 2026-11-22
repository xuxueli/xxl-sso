package com.xxl.sso.core.test.util;

import com.xxl.sso.core.util.ReactiveWebTool;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;

import java.lang.reflect.Proxy;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link ReactiveWebTool} test
 *
 * @author xuxueli 2026-11-22
 */
public class ReactiveWebToolTest {

    @Test
    public void isAjaxTest() {
        assertTrue(ReactiveWebTool.isAjax(exchange("application/json", null, null)));
        assertTrue(ReactiveWebTool.isAjax(exchange("*/*", "XMLHttpRequest", null)));
        assertTrue(ReactiveWebTool.isAjax(exchange(null, null, "application/json;charset=UTF-8")));

        assertFalse(ReactiveWebTool.isAjax(exchange("text/html", null, null)));
        assertFalse(ReactiveWebTool.isAjax(exchange(null, null, null)));
    }

    /**
     * build a mock exchange with the given headers
     */
    private ServerWebExchange exchange(String accept, String requestedWith, String contentType) {
        HttpHeaders headers = new HttpHeaders();
        if (accept != null) {
            headers.set("Accept", accept);
        }
        if (requestedWith != null) {
            headers.set("X-Requested-With", requestedWith);
        }
        if (contentType != null) {
            headers.set("Content-Type", contentType);
        }

        ServerHttpRequest request = (ServerHttpRequest) Proxy.newProxyInstance(
                ReactiveWebToolTest.class.getClassLoader(),
                new Class<?>[]{ServerHttpRequest.class},
                (proxy, method, args) -> "getHeaders".equals(method.getName()) ? headers : null);

        return (ServerWebExchange) Proxy.newProxyInstance(
                ReactiveWebToolTest.class.getClassLoader(),
                new Class<?>[]{ServerWebExchange.class},
                (proxy, method, args) -> "getRequest".equals(method.getName()) ? request : null);
    }

}
