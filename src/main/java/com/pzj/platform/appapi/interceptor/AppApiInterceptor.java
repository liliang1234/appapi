/**
 * piaozhijia.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package com.pzj.platform.appapi.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.alibaba.fastjson.JSONObject;
import com.pzj.platform.appapi.aop.ContextEntry;
import com.pzj.platform.appapi.exception.AppApiException;
import com.pzj.platform.appapi.exception.Exceptions;
import com.pzj.platform.appapi.support.ApiRequestSupport;
import com.pzj.platform.appapi.util.LogUtil;

/**
 * 
 * @author Mark
 * @version $Id: AppApiInterceptor.java, v 0.1 2016年7月4日 下午6:30:45 pengliqing Exp $
 */
public class AppApiInterceptor extends HandlerInterceptorAdapter {

    private final String        OLD_HANDLER_BAPI = "BApiController";
    private final String        OLD_HANDLER_SEAT = "SeatController";
    private static final Logger logger           = LoggerFactory.getLogger(AppApiInterceptor.class);

    /** 
    * preHandle方法是进行处理器拦截用的，该方法将在Controller处理之前进行调用，SpringMVC中的Interceptor拦截器是链式的，可以同时存在 
    * 多个Interceptor，然后SpringMVC会根据声明的前后顺序一个接一个的执行，而且所有的Interceptor中的preHandle方法都会在 
    * Controller方法调用之前调用。SpringMVC的这种Interceptor链式结构也是可以进行中断的，这种中断方式是令preHandle的返 
    * 回值为false，当preHandle的返回值为false的时候整个请求就结束了。 
    */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        response.setHeader("Access-Control-Allow-Origin", "*");
        if (handler instanceof HandlerMethod) {
            HandlerMethod hm = (HandlerMethod) handler;
            if (OLD_HANDLER_BAPI.equals(hm.getBean().getClass().getSimpleName()) || OLD_HANDLER_SEAT.equals(hm.getBean().getClass().getSimpleName())) {//兼容旧版，不拦截旧版接口
                return true;
            }
            try {
                String data = request.getParameter("data");
                ApiRequestSupport.checkData(data);
                JSONObject requestObject = ApiRequestSupport.getRequestData(data);
                logger.debug(LogUtil.logFormat(ContextEntry.getMonitor().getTraceId(), "http request by rest param: " + requestObject.toJSONString()));
                request.setAttribute("requestObject", requestObject);
            } catch (AppApiException e) {
                logger.error("请求数据预处理出现AppApi异常", e);
                ApiRequestSupport.invokeExceptionWrapper(response, e.getCode(), e.getMessage());
                return false;
            } catch (Exception e) {
                logger.error("请求数据预处理错误", e);
                ApiRequestSupport.invokeExceptionWrapper(response, Exceptions.SERVERERROR.getCode(),
                    Exceptions.SERVERERROR.getMessage() + "[" + e.getMessage() + "]");
                return false;
            }
        }
        return true;
    }

    /** 
    * 这个方法只会在当前这个Interceptor的preHandle方法返回值为true的时候才会执行。postHandle是进行处理器拦截用的，它的执行时间是在处理器进行处理之 
    * 后，也就是在Controller的方法调用之后执行，但是它会在DispatcherServlet进行视图的渲染之前执行，也就是说在这个方法中你可以对ModelAndView进行操 
    * 作。这个方法的链式结构跟正常访问的方向是相反的，也就是说先声明的Interceptor拦截器该方法反而会后调用。 
    */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        // TODO Auto-generated method stub 
        ContextEntry.removeMonitor();//清除监控路由
    }

    /** 
    * 该方法也是需要当前对应的Interceptor的preHandle方法的返回值为true时才会执行。该方法将在整个请求完成之后，也就是DispatcherServlet渲染了视图执行， 
    * 这个方法的主要作用是用于清理资源的，当然这个方法也只能在当前这个Interceptor的preHandle方法的返回值为true时才会执行。 
    */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // TODO Auto-generated method stub  
    }

}
