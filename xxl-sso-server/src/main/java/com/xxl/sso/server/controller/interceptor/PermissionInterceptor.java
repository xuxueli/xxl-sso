package com.xxl.sso.server.controller.interceptor;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author xuxueli 2015-12-12 18:09:04
 */
public class PermissionInterceptor extends HandlerInterceptorAdapter {


	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		
		if (!(handler instanceof HandlerMethod)) {
			return super.preHandle(request, response, handler);
		}

		// TODO

		return super.preHandle(request, response, handler);
	}
	
}
