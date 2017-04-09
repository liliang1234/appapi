/**
 * piaozhijia.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package com.pzj.platform.appapi.exception;

/**
 * 
 * @author Mark
 * @version $Id: UnExpectResultException.java, v 0.1 2016年7月15日 下午6:00:06 pengliqing Exp $
 */
public class UnExpectResultException extends AppApiException {

    /**  */
    private static final long serialVersionUID = 1L;

    public UnExpectResultException(Exceptions exception) {
        super(exception);
    }

    public UnExpectResultException(String code, String message) {
        super(code, message);
    }
}
