/**
 * piaozhijia.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package com.pzj.platform.appapi.exception;

/**
 * 
 * @author Mark
 * @version $Id: RpcRemoteException.java, v 0.1 2016年7月15日 下午3:25:29 pengliqing Exp $
 */
public class RpcRemoteException extends AppApiException {

    /**  */
    private static final long serialVersionUID = 1L;

    public RpcRemoteException(Exceptions exception) {
        super(exception);
    }
}
