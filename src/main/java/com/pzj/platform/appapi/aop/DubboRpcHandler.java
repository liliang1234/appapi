/**
 * piaozhijia.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package com.pzj.platform.appapi.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.dubbo.remoting.TimeoutException;
import com.alibaba.dubbo.rpc.RpcException;
import com.pzj.common.util.DateUtils;
import com.pzj.platform.appapi.exception.Exceptions;
import com.pzj.platform.appapi.exception.RpcRemoteException;
import com.pzj.platform.appapi.exception.RpcTimeoutException;
import com.pzj.platform.appapi.util.LogUtil;

/**
 * 
 * @author Mark
 * @version $Id: DubboRpcHandler.java, v 0.1 2016年7月15日 下午3:14:39 pengliqing Exp $
 */
public class DubboRpcHandler {

    private static final Logger logger = LoggerFactory.getLogger(DubboRpcHandler.class);
    private AopAdviserHandeler  adviserHandeler;

    public void setAdviserHandeler(AopAdviserHandeler adviserHandeler) {
        this.adviserHandeler = adviserHandeler;
    }

    protected Object doAround(ProceedingJoinPoint point) throws Throwable {
        adviserHandeler.doBefore(point.getSignature().getDeclaringTypeName(), point.getSignature().getName(), point.getArgs());
        long startTimes = DateUtils.getTimes();
        try {
            Object returnValue = point.proceed(point.getArgs());
            long endTimes = DateUtils.getTimes();
            adviserHandeler.doAfter(point.getSignature().getDeclaringTypeName(), point.getSignature().getName(), point.getArgs(), returnValue,
                endTimes - startTimes);
            return returnValue;
        } catch (TimeoutException te) {
            logger.error(LogUtil.logFormat(ContextEntry.getMonitor().getTraceId(), te.getMessage()));
            throw new RpcTimeoutException(Exceptions.TIMEOUTEERROR);
        } catch (RpcException te) {
            logger.error(LogUtil.logFormat(ContextEntry.getMonitor().getTraceId(), te.getMessage()));
            throw new RpcRemoteException(Exceptions.SERVICEERROR);
        }
    }
}
