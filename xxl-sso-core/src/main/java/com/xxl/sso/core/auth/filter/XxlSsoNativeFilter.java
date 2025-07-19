package com.xxl.sso.core.auth.filter;

import com.xxl.sso.core.constant.Const;
import com.xxl.sso.core.helper.XxlSsoHelper;
import com.xxl.sso.core.model.LoginInfo;
import com.xxl.sso.core.path.impl.AntPathMatcher;
import com.xxl.tool.core.StringTool;
import com.xxl.tool.gson.GsonTool;
import com.xxl.tool.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * native filter
 *
 * 使用场景：适用于无Cookie场景，天然不受限域名，支持全局登录台共享。比如 APP/移动端、小程度端、客户端软件 …… 等；
 * 解释说明：
 *      1、登录信息实体：
 *          - token：登录态标识信息，根据登录态信息（LoginInfo）结合算法生成；
 *          - LoginInfo：登录态数据，包括登录 用户信息、权限/角色信息、设置类信息 等；
 *      2、登录信息存储：
 *          - 客户端（APP/Web）：自定义存储介质，如 localStorage（Web）、Sqlite（APP）；针对登录后获取的 token 进行存储管理；
 *          - 服务端（后端）：存储在 LoginStore 中，原生提供 LocalLoginStore（本地缓存） 、RedisLoginStore（Redis） 等多种实现可选用，也可以自定义定制实现；
 *      3、登录态识别：
 *          - header：客户端请求设置认证 “header”（key支持自定义，value为登录token），服务端解析 header 识别登录态；
 *      4、核心流程：
 *          - 登录流程：客户端请求 “登录openapi接口”进行登录。服务端构建 LoginInfo 并生成登录 token；服务端通过LoginStore存储LoginInfo；客户端获取 “登录token”并存储管理；
 *          - 注销流程：客户端请求 “注销openapi接口”进行注销。服务端解析认证header，从 LoginStore 中移除登录态信息；客户端移除维护的登录token；
 *          - 认证流程：客户端请求设置认证 “header”，服务端解析token，从 LoginStore 中获取登录态信息；
 *
 * @author xuxueli 2018-04-08 21:30:54
 */
public class XxlSsoNativeFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(XxlSsoNativeFilter.class);


    /**
     * path matcher
     */
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    /**
     * excluded paths, like "/excluded/*,/excluded/pathx"
     */
    private final String excludedPaths;

    public XxlSsoNativeFilter(String excludedPaths) {
        this.excludedPaths = excludedPaths;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
        logger.info("XxlSsoNativeFilter init.");
    }


    // ---------------------- tool ----------------------

    /**
     * is match excluded path
     *
     * @param request
     * @return
     */
    private boolean isMatchExcludedPaths(HttpServletRequest request) {
        // get url
        String servletPath = request.getServletPath();

        // filter excluded path
        if (StringTool.isNotBlank(excludedPaths)) {
            for (String excludedPath : excludedPaths.split(",")) {
                // path check
                String uriPattern = excludedPath.trim();
                if (StringTool.isBlank(uriPattern)) {
                    continue;
                }

                // path match
                if (antPathMatcher.match(uriPattern, servletPath)) {
                    // excluded path, pass
                    return true;
                }

            }
        }
        return false;
    }


    // ---------------------- auth filter ----------------------

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        // 1、valid path, is excluded
        if (isMatchExcludedPaths(request)) {
            // excluded path, pass
            chain.doFilter(request, response);
            return;
        }


        // 3、login check
        Response<LoginInfo> loginCheckResult = XxlSsoHelper.loginCheckWithHeader(request);
        // parse login info
        LoginInfo loginInfo = null;
        if (loginCheckResult!=null && loginCheckResult.isSuccess()) {
            loginInfo = loginCheckResult.getData();
        }
        // process login
        if (loginInfo == null) {

            // 4、login fail message
            String loginFailMsg = GsonTool.toJson(Response.of(Const.CODE_LOGIN_FAIL, "not login for path:"+ request.getServletPath()));

            // write response
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().println(loginFailMsg);
            return;
        }

        // 5、write attribute（ loginInfo ）
        request.setAttribute(Const.XXL_SSO_USER, loginInfo);

        // 6、chain pass
        chain.doFilter(request, response);
        return;
    }

}
