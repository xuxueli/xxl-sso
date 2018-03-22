package com.xxl.sso.server.core.config;

import com.xxl.sso.server.core.result.ReturnT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 统一异常处理（Controller切面方式实现）
 *
 *      1、@ControllerAdvice：扫描所有Controller；
 *      2、@ControllerAdvice(annotations=RestController.class)：扫描指定注解类型的Controller；
 *      3、@ControllerAdvice(basePackages={"com.aaa","com.bbb"})：扫描指定package下的Controller
 *
 * @author xuxueli 2017-08-01 21:51:21
 */
@ControllerAdvice
public class WebExceptionHandler {
    private static Logger logger = LoggerFactory.getLogger(WebExceptionHandler.class);

    /**
     * 异常处理类
     *
     * 异常注解：
     *      1、@ExceptionHandler(RuntimeException.class)：指定类型异常；
     *      2、@ExceptionHandler：所有类型异常；
     *
     * 异常返回：
     *      1、String：跳转到某个view；
     *      2、ModelAndView：跳转到某个view；
     *      3、model类 + @ResponseBody：返回JSON；
     *
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @return
     */
    @ExceptionHandler
    @ResponseBody //在返回自定义相应类的情况下必须有，这是@ControllerAdvice注解的规定
    public Object exceptionHandler(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        logger.error(ex.getMessage(),ex);

        String header = request.getHeader("content-type");
        boolean isJson=  header != null && header.contains("json");
        if (isJson) {
            return new ReturnT(ReturnT.FAIL_CODE, ex.getMessage());
        } else {
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("common/common.error");
            modelAndView.addObject("exception", ex);
            return modelAndView;
        }
    }

}