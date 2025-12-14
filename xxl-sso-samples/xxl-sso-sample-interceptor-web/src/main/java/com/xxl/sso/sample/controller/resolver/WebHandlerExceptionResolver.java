package com.xxl.sso.sample.controller.resolver;

import com.xxl.tool.json.GsonTool;
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
    private static Logger logger = LoggerFactory.getLogger(WebHandlerExceptionResolver.class);

    @Override
    public ModelAndView resolveException(HttpServletRequest request,
                                         HttpServletResponse response,
                                         Object handler,
                                         Exception ex) {

        //logger.error("WebExceptionResolver:{}", ex.getMessage(), ex);
        logger.warn("WebExceptionResolver:{}", ex.getMessage());

        // parse isJson
        boolean isJson = false;
        if (handler instanceof HandlerMethod) {
            HandlerMethod method = (HandlerMethod)handler;
            isJson = method.getMethodAnnotation(ResponseBody.class)!=null;
        }

        // process error
        ModelAndView mv = new ModelAndView();
        if (isJson) {
            try {
                // errorMsg
                String errorMsg = GsonTool.toJson(Response.ofFail(ex.toString()));

                // write response
                response.setStatus(HttpServletResponse.SC_OK);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().println(errorMsg);
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
            return mv;
        } else {

            mv.addObject("exceptionMsg", ex.toString());
            mv.setViewName("common/common.errorpage");
            return mv;
        }

    }

}