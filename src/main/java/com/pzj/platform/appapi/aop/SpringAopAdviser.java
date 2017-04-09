/**
 * piaozhijia.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package com.pzj.platform.appapi.aop;

import java.io.Serializable;

import org.aspectj.lang.ProceedingJoinPoint;

import com.pzj.common.util.DateUtils;

/**
 * 
 * @author Mark
 * @version $Id: SpringAopAdviser.java, v 0.1 2016年7月1日 上午9:29:10 pengliqing Exp $
 */
public final class SpringAopAdviser implements Serializable {

    /**  */
    private static final long  serialVersionUID = 1L;
    private AopAdviserHandeler adviserHandeler;

    public void setAdviserHandeler(AopAdviserHandeler adviserHandeler) {
        this.adviserHandeler = adviserHandeler;
    }

    protected Object doAround(ProceedingJoinPoint point) throws Throwable {
        adviserHandeler.doBefore(point.getSignature().getDeclaringTypeName(), point.getSignature().getName(), point.getArgs());
        long startTimes = DateUtils.getTimes();
        Object returnValue = point.proceed(point.getArgs());
        long endTimes = DateUtils.getTimes();
        adviserHandeler.doAfter(point.getSignature().getDeclaringTypeName(), point.getSignature().getName(), point.getArgs(), returnValue,
            endTimes - startTimes);
        return returnValue;
    }

}
