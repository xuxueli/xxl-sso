package com.xxl.sso.sample.controller.resolver;

import com.xxl.tool.gson.GsonTool;
import com.xxl.tool.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author xuxueli 2017-08-01 21:51:21
 */
@Component
public class WebHandlerExceptionResolver implements HandlerExceptionResolver {
    private static transient Logger logger = LoggerFactory.getLogger(WebHandlerExceptionResolver.class);

    @Override
    public ModelAndView resolveException(HttpServletRequest request,
                                         HttpServletResponse response,
                                         Object handler,
                                         Exception ex) {

        //logger.error("WebExceptionResolver:{}", ex.getMessage(), ex);
        logger.warn("WebExceptionResolver:{}", ex.getMessage());

        // if json
        boolean isJson = false;
        if (handler instanceof HandlerMethod) {
            HandlerMethod method = (HandlerMethod)handler;
            ResponseBody responseBody = method.getMethodAnnotation(ResponseBody.class);
            if (responseBody != null) {
                isJson = true;
            }
        }



        // error result
        Response<String> errorResult = Response.ofFail(ex.toString().replaceAll("\n", "<br/>"));

        // response
        ModelAndView mv = new ModelAndView();
        if (isJson) {
            try {
                response.setContentType("application/json;charset=utf-8");
                response.getWriter().print(GsonTool.toJson(errorResult));
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
            return mv;
        } else {

            mv.addObject("exceptionMsg", errorResult.getMsg());
            mv.setViewName("common/common.errorpage");
            return mv;
        }

    }

}