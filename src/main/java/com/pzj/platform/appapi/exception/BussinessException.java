/**
 * piaozhijia.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package com.pzj.platform.appapi.exception;

/**
 * 
 * @author Mark
 * @version $Id: LoginException.java, v 0.1 2016年7月13日 下午9:50:00 pengliqing Exp $
 */
public class BussinessException extends AppApiException {

    /**  */
    private static final long serialVersionUID = 1L;

    public BussinessException(Exceptions exception) {
        super(exception);
    }
}
