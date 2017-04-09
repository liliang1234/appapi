/**
 * piaozhijia.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package com.pzj.platform.appapi.interceptor;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.pzj.common.util.CheckUtils;
import com.pzj.customer.entity.Customer;
import com.pzj.platform.appapi.exception.AppApiException;
import com.pzj.platform.appapi.exception.BussinessException;
import com.pzj.platform.appapi.exception.Exceptions;
import com.pzj.platform.appapi.service.CustomerService;
import com.pzj.platform.appapi.support.ApiRequestSupport;
import com.pzj.platform.appapi.support.HttpSupport;

/**
 * 
 * @author Mark
 * @version $Id: LoginUserInterceptor.java, v 0.1 2016年7月5日 上午11:47:16 pengliqing Exp $
 */
public class LoginUserInterceptor extends HandlerInterceptorAdapter {

    private static final Logger logger = LoggerFactory.getLogger(LoginUserInterceptor.class);
    private CustomerService     customerService;

    public void setCustomerService(CustomerService customerService) {
        this.customerService = customerService;
    }

    /** 
    * preHandle方法是进行处理器拦截用的，顾名思义，该方法将在Controller处理之前进行调用，SpringMVC中的Interceptor拦截器是链式的，可以同时存在 
    * 多个Interceptor，然后SpringMVC会根据声明的前后顺序一个接一个的执行，而且所有的Interceptor中的preHandle方法都会在 
    * Controller方法调用之前调用。SpringMVC的这种Interceptor链式结构也是可以进行中断的，这种中断方式是令preHandle的返 
    * 回值为false，当preHandle的返回值为false的时候整个请求就结束了。 
    */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            try {
                HashMap<String, String> header = HttpSupport.getHeader(request);
                String data = request.getParameter("data");
                String token = header.get("token");
                String resellerId = header.get("rid");
                if (token == null && resellerId == null) {
                    throw new BussinessException(Exceptions.TOKENRERROR);
                }

                Customer customer = null;
                if (CheckUtils.isNotNull(token)) {
                    customer = customerService.queryCustomer(token.trim());
                    // 请求参数权限校验(验证签名)
                    ApiRequestSupport.checkLogin(customer, data, String.valueOf(request.getParameter("sign")), token);
                } else if (CheckUtils.isNotNull(resellerId)) {
                    customer = customerService.getCustomerById(Long.parseLong(resellerId));
                    ApiRequestSupport.checkLogin(customer, data, String.valueOf(request.getParameter("sign")), resellerId);
                }
                request.setAttribute("customer", customer);
            } catch (AppApiException e) {
                logger.error(e.getMessage(), e);
                ApiRequestSupport.invokeExceptionWrapper(response, e.getCode(), e.getMessage());
                return false;
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                ApiRequestSupport.invokeExceptionWrapper(response, Exceptions.SERVERERROR.getCode(), Exceptions.SERVERERROR.getMessage());
                return false;
            }
        }
        return true;
    }

}
