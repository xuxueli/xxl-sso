package com.xxl.sso.sample.helper;


import org.springframework.http.server.reactive.ServerHttpRequest;

import java.util.Collections;

/**
 * @author Wu Weijie
 */
public class ReactiveHttpHelper {

    /**
     * 获取请求头
     *
     * @param request
     * @param headerName
     * @return
     */
    public static String getHeader(ServerHttpRequest request, String headerName) {
        return request.getHeaders().getOrDefault(headerName, Collections.emptyList()).stream().reduce((a, b) -> a + b).orElse(null);
    }
}
