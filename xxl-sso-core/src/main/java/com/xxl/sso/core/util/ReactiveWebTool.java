package com.xxl.sso.core.util;

import org.springframework.web.server.ServerWebExchange;

/**
 * web request tool (reactive)
 *
 * <p>
 * 仅依赖 spring-web 的 Reactive 类型，不引用任何 Servlet 类型；Servlet 场景请使用 {@link WebTool}。
 * </p>
 *
 * @author xuxueli 2026-11-22
 */
public final class ReactiveWebTool {

    private ReactiveWebTool() {
    }

    /**
     * is ajax / json request
     *
     * @param exchange  web exchange
     * @return          {@code true} when request expects json response
     */
    public static boolean isAjax(ServerWebExchange exchange) {
        String accept = exchange.getRequest().getHeaders().getFirst("Accept");
        if (accept != null && accept.contains("json")) {
            return true;
        }

        String requestedWith = exchange.getRequest().getHeaders().getFirst("X-Requested-With");
        if ("XMLHttpRequest".equalsIgnoreCase(requestedWith)) {
            return true;
        }

        String contentType = exchange.getRequest().getHeaders().getFirst("Content-Type");
        return contentType != null && contentType.contains("json");
    }

}
