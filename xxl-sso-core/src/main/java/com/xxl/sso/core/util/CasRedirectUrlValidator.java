package com.xxl.sso.core.util;

import com.xxl.sso.core.path.impl.AntPathMatcher;
import com.xxl.tool.core.CollectionTool;
import com.xxl.tool.core.StringTool;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * CAS redirect url validator
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
public class CasRedirectUrlValidator {

    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    /**
     * 白名单表达式
     */
    private final List<String> patternList;

    public CasRedirectUrlValidator(String casWhitelist) {
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

}
