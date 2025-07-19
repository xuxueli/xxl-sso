package com.xxl.sso.core.auth.filter;

import com.xxl.sso.core.constant.Const;
import com.xxl.sso.core.exception.XxlSsoException;
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
 * cas filter
 *
 * 使用场景：适用于Web网页场景，解决跨域登录态共享、单点登录问题；
 * 解释说明：
 *      1、登录信息实体：
 *          - token：登录态标识信息，根据登录态信息（LoginInfo）结合算法生成；
 *          - LoginInfo：登录态数据，包括登录 用户信息、权限/角色信息、设置类信息 等；
 *      2、登录信息存储：
 *          - 客户端（Web）：存储在 域名Cookie中；在 CasSercer 域名、业务域名下，登录Cookie进行存储以及数据同步；
 *          - 服务端（后端）：存储在 LoginStore 中，原生提供 LocalLoginStore（本地缓存） 、RedisLoginStore（Redis） 等多种实现可选用，也可以自定义定制实现；
 *      3、登录态识别：
 *          - cookie：客户端域名下存储认证“cookie”（key支持自定义，value为登录token），服务端解析并识别登录态；
 *      4、核心流程：
 *          - 登录流程：
 *              - a、访问 “Client应用（xxl-sso-sample-cas）”，未登录将通过Filter拦截，并主动跳转 “CasServer（xxl-sso-server）” 进入登录页面；
 *              - b、登录成功，服务端构建 LoginInfo 写入 LoginStore，生成登录 token 写入 CasServer 域名 Cookie中；然后，携带登录token 并跳转返回； “Client应用（xxl-sso-sample-cas）”；
 *              - c、跳转返回 “Client应用（xxl-sso-sample-cas）”，校验所携带登录token，写入 Client应用 域名 Cookie 中；
 *          - 注销流程：客户端访问 “CasServer（xxl-sso-server）” 注销登录页面；将跳转 “CasServer（xxl-sso-server）” 进入登录页面；*
 *          - 认证流程：客户端请求，将携带域名下认证 “cookie”，服务端解析token，从 LoginStore 中获取登录态信息；
 *
 * @author xuxueli 2018-04-08 21:30:54
 */
public class XxlSsoCasFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(XxlSsoCasFilter.class);


    /**
     * path matcher
     */
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    /**
     * server address
     */
    private String serverAddress;

    /**
     * login path
     */
    private String loginPath;

    /**
     * excluded paths, like "/excluded/*,/excluded/pathx"
     */
    private String excludedPaths;

    public XxlSsoCasFilter(String serverAddress, String loginPath, String excludedPaths) {
        this.serverAddress = serverAddress;
        this.loginPath = loginPath;
        this.excludedPaths = excludedPaths;

        // valid
        if (StringTool.isBlank(loginPath)) {
            this.loginPath = Const.LOGIN_URL;
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);

        // valid
        if (StringTool.isBlank(serverAddress)) {
            throw new XxlSsoException("xxl-sso serverAddress can not be blank.");
        }

        logger.info("XxlSsoCasFilter init.");
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

        // 3、login check (ticket + cookie)
        Response<LoginInfo> loginCheckResult = XxlSsoHelper.loginCheckWithCookie(request, response);                // check cookie
        if (!(loginCheckResult!=null && loginCheckResult.isSuccess())) {
            loginCheckResult = XxlSsoHelper.validTicket(request, response);         // check ticket
        }

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

                // origin url
                String originUrl = request.getRequestURL().toString();

                // redirect login-path
                String finalLoginPath = serverAddress
                        .concat(loginPath)
                        .concat("?")
                        .concat(Const.CLIENT_REDIRECT_URL)
                        .concat("=")
                        .concat(originUrl);

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
