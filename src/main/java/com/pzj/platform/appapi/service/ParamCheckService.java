/**
 * piaozhijia.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package com.pzj.platform.appapi.service;

import com.pzj.platform.appapi.exception.Exceptions;
import com.pzj.platform.appapi.exception.ParamException;
import com.pzj.platform.appapi.util.CheckUtils;

/**
 * 
 * @author Mark
 * @version $Id: ParamCheckService.java, v 0.1 2016年7月15日 下午5:50:37 pengliqing Exp $
 */
public class ParamCheckService {

    public static void checkParam(Object param, String message) {
        if (CheckUtils.isEmpty(param)) {
            throw new ParamException(Exceptions.PARAMEMPTYERROR.getCode(), message);
        }
    }
}
