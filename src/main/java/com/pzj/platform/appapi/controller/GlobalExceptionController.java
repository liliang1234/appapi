/**
 * piaozhijia.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package com.pzj.platform.appapi.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pzj.modules.appapi.entity.JsonEntity;
import com.pzj.platform.appapi.exception.AppApiException;
import com.pzj.platform.appapi.exception.Exceptions;

/**
 * 
 * @author Mark
 * @version $Id: GlobalExceptionController.java, v 0.1 2016年7月19日 上午9:59:44 pengliqing Exp $
 */

@ControllerAdvice
public class GlobalExceptionController {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionController.class);

    @ExceptionHandler(AppApiException.class)
    @ResponseBody
    public JsonEntity handleAPIException(HttpServletRequest request, AppApiException ex) {
        logger.error(ex.getMessage(), ex);
        return JsonEntity.makeExceptionJsonEntity(ex.getCode(), ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public JsonEntity handleSystemException(HttpServletRequest request, Exception ex) {
        logger.error(ex.getMessage(), ex);
        return JsonEntity.makeExceptionJsonEntity(Exceptions.SERVERERROR.getCode(), Exceptions.SERVERERROR.getMessage());
    }

}
