/**
 * piaozhijia.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package com.pzj.platform.appapi.aop;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.pzj.platform.appapi.util.LogUtil;

/**
 * 
 * @author Mark
 * @version $Id: LogMonitor.java, v 0.1 2016年7月1日 上午9:56:57 pengliqing Exp $
 */
@Component("logMonitor")
public class LogMonitor implements AopAdviserHandeler {

    private static final Logger logger = LoggerFactory.getLogger(LogMonitor.class);

    /** 
     * @see com.pzj.platform.appapi.aop.AopAdviserHandeler#beforeAdviceHandler(java.lang.String, java.lang.String, java.lang.Object[])
     */
    @Override
    public void doBefore(String targetClassName, String targetMethodName, Object[] args) {
        if (logger.isDebugEnabled())
            logger.debug(LogUtil.invokeLogFormat(ContextEntry.getMonitor().getTraceId(), targetClassName + "." + targetMethodName, paramsToString(args), true));
    }

    private String paramsToString(Object[] args) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            Object obj = args[i];
            if (obj instanceof Serializable) {
                sb.append("args_" + i + ": " + JSON.toJSONString(obj) + " ");
            }
        }
        return sb.toString();
    }

    private String paramsToString(Object result) {
        if (result == null)
            return null;
        if (result instanceof Serializable) {
            return JSON.toJSONString(result);
        }
        return result.toString();
    }

    /** 
     * @see com.pzj.platform.appapi.aop.AopAdviserHandeler#afterReturnAdviceHandler(java.lang.String, java.lang.String, java.lang.Object[], java.lang.Object)
     */
    @Override
    public void doAfter(String targetClassName, String targetMethodName, Object[] args, Object result, long timeUsed) {
        if (logger.isDebugEnabled())
            logger.debug(
                LogUtil.invokeLogFormat(ContextEntry.getMonitor().getTraceId(), targetClassName + "." + targetMethodName, paramsToString(result), false));
        logger.info(LogUtil.monitorLogFormat(ContextEntry.getMonitor().getTraceId(), targetClassName + "." + targetMethodName, timeUsed));
    }

}
