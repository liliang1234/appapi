/**
 * piaozhijia.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package com.pzj.platform.appapi.aop;

/**
 * 
 * @author Mark
 * @version $Id: AopAdviser.java, v 0.1 2016年7月1日 上午10:27:46 pengliqing Exp $
 */
public interface AopAdviserHandeler {

    public void doBefore(String targetClassName, String targetMethodName, Object[] args);

    public void doAfter(String targetClassName, String targetMethodName, Object[] args, Object result, long timeUsed);
}
