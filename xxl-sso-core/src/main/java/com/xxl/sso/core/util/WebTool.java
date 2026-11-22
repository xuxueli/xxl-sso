package com.xxl.sso.core.util;

import jakarta.servlet.http.HttpServletRequest;

/**
 * web request tool (servlet)
 *
 * <p>
 * 仅依赖 jakarta.servlet，不引用任何 Reactive 类型；Reactive 场景请使用 {@link ReactiveWebTool}。
 * </p>
 *
 * @author xuxueli 2026-11-22
 */
public final class WebTool {

    private WebTool() {
    }

    /**
     * is ajax / json request
     *
     * @param request   http request
     * @return          {@code true} when request expects json response
     */
    public static boolean isAjax(HttpServletRequest request) {
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("json")) {
            return true;
        }

        String requestedWith = request.getHeader("X-Requested-With");
        if ("XMLHttpRequest".equalsIgnoreCase(requestedWith)) {
            return true;
        }

        String contentType = request.getHeader("Content-Type");
        return contentType != null && contentType.contains("json");
    }

}
