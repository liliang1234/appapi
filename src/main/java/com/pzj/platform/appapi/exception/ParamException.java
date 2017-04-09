/**
 * piaozhijia.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package com.pzj.platform.appapi.exception;

/**
 * 
 * @author Mark
 * @version $Id: ParamException.java, v 0.1 2016年7月13日 下午9:51:32 pengliqing Exp $
 */
public class ParamException extends AppApiException {

    /**  */
    private static final long serialVersionUID = 1L;

    public ParamException(Exceptions exception) {
        super(exception);
    }

    public ParamException(String code, String message) {
        super(code, message);
    }
}
