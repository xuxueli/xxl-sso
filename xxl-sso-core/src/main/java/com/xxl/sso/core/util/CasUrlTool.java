package com.xxl.sso.core.util;

import com.xxl.sso.core.constant.Const;
import com.xxl.sso.core.path.impl.AntPathMatcher;
import com.xxl.tool.core.CollectionTool;
import com.xxl.tool.core.StringTool;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * CAS url tool
 *
 * <p>
 * 统一处理 CAS 跳转相关的地址能力：客户端到登录页的登录地址构建（{@link #buildLoginUrl}），
 * 以及登录成功后服务端到客户端的回跳地址白名单校验（{@link #isAllowed}）。
 * </p>
 *
 * <pre>
 *  http://127.0.0.1:8081/**             匹配该来源下全部路径
 *  http://localhost:8081/app/**         仅匹配 /app 下路径
 *  http://*.example.com:8081/**         匹配 example.com 的任意子域名
 *  https://app.example.com/**           匹配默认端口 443
 * </pre>
 *
 * <p>
 * 白名单为空表示不校验，仅校验跳转地址非空。
 * </p>
 *
 * @author xuxueli 2026-11-22
 */
public class CasUrlTool {

    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    /**
     * 白名单表达式
     */
    private final List<String> patternList;

    /**
     * create cas url tool
     *
     * @param casWhitelist whitelist expressions, comma separated, must start with http:// or https://;
     *                     blank means no whitelist validation
     * @throws IllegalArgumentException when any expression is not a valid http(s) url pattern
     */
    public CasUrlTool(String casWhitelist) {
        // 路径为空，表示不校验
        if (StringTool.isBlank(casWhitelist)) {
            this.patternList = Collections.emptyList();
            return;
        }

        // 解析白名单路径
        List<String> patterns = new ArrayList<>();
        for (String item : casWhitelist.split(",")) {
            if (StringTool.isBlank(item)) {
                continue;
            }
            String pattern = item.trim();
            if (!pattern.startsWith("http://") && !pattern.startsWith("https://")) {
                throw new IllegalArgumentException("Invalid cas redirect whitelist: " + item);
            }
            patterns.add(pattern);
        }
        if (patterns.isEmpty()) {
            throw new IllegalArgumentException("Invalid cas redirect whitelist: " + casWhitelist);
        }
        this.patternList = patterns;
    }

    // ---------------------- cas server: redirectUrl (server -> client) ----------------------

    /**
     * 校验跳转地址
     *
     * @param redirectUrl 待校验的跳转地址
     * @return 允许跳转返回 {@code true}
     */
    public boolean isAllowed(String redirectUrl) {
        // 跳转地址为空，直接拒绝
        if (StringTool.isBlank(redirectUrl)) {
            return false;
        }
        String url = redirectUrl.trim();

        // 白名单为空表示不校验
        if (CollectionTool.isEmpty(patternList)) {
            return true;
        }

        // 匹配时忽略 query / fragment
        int queryIndex = url.indexOf('?');
        if (queryIndex >= 0) {
            url = url.substring(0, queryIndex);
        }
        int fragmentIndex = url.indexOf('#');
        if (fragmentIndex >= 0) {
            url = url.substring(0, fragmentIndex);
        }

        // 匹配
        for (String pattern : patternList) {
            if (PATH_MATCHER.match(pattern, url)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 追加 query 参数
     *
     * <p>
     * 自动根据 URL 是否已有 query 选择 {@code ?} 或 {@code &}，并丢弃 fragment。
     * </p>
     *
     * @param url   原地址
     * @param key   参数名
     * @param value 参数值
     * @return 追加参数后的地址；{@code url} 为空时原样返回
     */
    public static String appendQueryParam(String url, String key, String value) {
        if (StringTool.isBlank(url)) {
            return url;
        }

        int fragmentIndex = url.indexOf('#');
        if (fragmentIndex >= 0) {
            url = url.substring(0, fragmentIndex);
        }

        return url + (url.indexOf('?') >= 0 ? "&" : "?") + key + "=" + value;
    }

    // ---------------------- cas server: loginUrl (client -> server) ----------------------

    /**
     * 构建 CAS 登录地址
     *
     * <p>
     * 丢弃原始地址的 query（避免回跳时票据参数累积导致重定向循环），并对 {@code redirect_url}
     * 做 URL 编码，防止原始地址中的 {@code &}、{@code =} 等字符截断参数。
     * </p>
     *
     * @param serverAddress CAS 服务端地址
     * @param loginPath     CAS 登录路径
     * @param originUrl     原始请求地址
     * @return              追加编码后 {@code redirect_url} 的登录地址
     */
    public static String buildLoginUrl(String serverAddress, String loginPath, String originUrl) {
        String redirectUrl = originUrl;
        int queryIndex = redirectUrl.indexOf('?');
        if (queryIndex > -1) {
            redirectUrl = redirectUrl.substring(0, queryIndex);
        }

        return serverAddress
                .concat(loginPath)
                .concat("?")
                .concat(Const.CLIENT_REDIRECT_URL)
                .concat("=")
                .concat(URLEncoder.encode(redirectUrl, StandardCharsets.UTF_8));
    }



}
