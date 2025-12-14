package com.xxl.sso.core.auth.filter;

import com.xxl.sso.core.constant.Const;
import com.xxl.sso.core.helper.XxlSsoHelper;
import com.xxl.sso.core.model.LoginInfo;
import com.xxl.sso.core.path.impl.AntPathMatcher;
import com.xxl.tool.core.StringTool;
import com.xxl.tool.json.GsonTool;
import com.xxl.tool.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * web filter
 *
 * 使用场景：适用于Web网页场景，要求访问系统部署在统一域名下；
 * 解释说明：
 *      1、登录信息实体：
 *          - token：登录态标识信息，根据登录态信息（LoginInfo）结合算法生成；
 *          - LoginInfo：登录态数据，包括登录 用户信息、权限/角色信息、设置类信息 等；
 *      2、登录信息存储：
 *          - 客户端（Web）：存储在 统一域名Cookie中；
 *          - 服务端（后端）：存储在 LoginStore 中，原生提供 LocalLoginStore（本地缓存） 、RedisLoginStore（Redis） 等多种实现可选用，也可以自定义定制实现；
 *      3、登录态识别：
 *          - cookie：统一域名下存储认证“cookie”（key支持自定义，value为登录token），服务端解析并识别登录态；
 *      4、核心流程：
 *          - 登录流程：
 *              - a、访问系统应用（xxl-sso-sample-web），未登录将通过Filter拦截，并主动跳转 “/login” 路径进入登录页面；
 *              - b、登录成功，服务端构建 LoginInfo 写入 LoginStore；生成登录 token 写入 统一域名 Cookie中；
 *              - c、跳转返回来源页面，Filter拦截器校验 统一域名 Cookie 中 token ，校验通过放行；
 *          - 注销流程：客户端访问 “/logout” 路径，进行注销登录，并跳转登录页面；
 *          - 认证流程：客户端请求，将携带域名下认证 “cookie”，服务端解析token，从 LoginStore 中获取登录态信息；
 *
 * @author xuxueli 2018-04-08 21:30:54
 */
public class XxlSsoWebFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(XxlSsoWebFilter.class);


    /**
     * path matcher
     */
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    /**
     * excluded paths, like "/excluded/*,/excluded/pathx"
     */
    private final String excludedPaths;

    /**
     * login path
     */
    private String loginPath;


    public XxlSsoWebFilter(String excludedPaths, String loginPath) {
        this.excludedPaths = excludedPaths;
        this.loginPath = loginPath;

        // valid
        if (StringTool.isBlank(loginPath)) {
            this.loginPath = Const.LOGIN_URL;
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
        logger.info("XxlSsoWebFilter init.");
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

        // 2、vlid permission, not support


        // 3、login check
        Response<LoginInfo> loginCheckResult = XxlSsoHelper.loginCheckWithCookie(request, response);

        // parse login info
        LoginInfo loginInfo = null;
        if (loginCheckResult!=null && loginCheckResult.isSuccess()) {
            loginInfo = loginCheckResult.getData();
        }

        // process login
        if (loginInfo == null) {

            // 4、login fail message
            String loginFailMsg = GsonTool.toJson(Response.of(Const.CODE_LOGIN_FAIL, "not login for path:"+ request.getServletPath()));

            // isJson
            String header = request.getHeader("content-type");
            boolean isJson=  header!=null && header.contains("json");
            if (isJson) {

                // write response
                response.setStatus(HttpServletResponse.SC_OK);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().println(loginFailMsg);
                return;
            } else {

                // redirect 2 login
                String finalLoginPath = request.getContextPath().concat(loginPath);
                response.sendRedirect(finalLoginPath);
                return;
            }

        }

        // 5、write attribute（ loginInfo ）
        request.setAttribute(Const.XXL_SSO_USER, loginInfo);

        // 6、chain pass
        chain.doFilter(request, response);
        return;
    }

}
