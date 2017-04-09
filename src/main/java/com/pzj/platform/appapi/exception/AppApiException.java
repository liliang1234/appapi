/**
 * piaozhijia.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package com.pzj.platform.appapi.exception;

/**
 * 
 * @author Mark
 * @version $Id: AppApiException.java, v 0.1 2016年7月13日 下午9:49:16 pengliqing Exp $
 */
public class AppApiException extends RuntimeException {

    /**  */
    private static final long serialVersionUID = 1L;
    protected String          code;

    public AppApiException() {
        super();
    }

    public AppApiException(String message) {
        super(message);
        this.code = Exceptions.SERVERERROR.getCode();
    }

    public AppApiException(Exceptions exception) {
        super(exception.getMessage());
        this.code = exception.getCode();
    }

    public AppApiException(String code, String message) {
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
