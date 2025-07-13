package com.xxl.sso.core.auth.interceptor;

import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * cas interceptor
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
public class XxlSsoCasInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

}
