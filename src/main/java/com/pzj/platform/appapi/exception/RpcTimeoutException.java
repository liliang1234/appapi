/**
 * piaozhijia.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package com.pzj.platform.appapi.exception;

/**
 * 
 * @author pengliqing
 * @version $Id: RpcTimeoutException.java, v 0.1 2016年7月15日 下午3:25:14 pengliqing Exp $
 */
public class RpcTimeoutException extends AppApiException {

    /**  */
    private static final long serialVersionUID = 1L;

    public RpcTimeoutException(Exceptions exception) {
        super(exception);
    }
}
