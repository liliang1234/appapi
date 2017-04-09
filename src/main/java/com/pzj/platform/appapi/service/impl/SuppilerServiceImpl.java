/**
 * piaozhijia.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package com.pzj.platform.appapi.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pzj.platform.appapi.aop.ContextEntry;
import com.pzj.platform.appapi.service.SuppilerService;
import com.pzj.platform.appapi.util.LogUtil;

/**
 * 
 * @author Mark
 * @version $Id: SuppilerService.java, v 0.1 2016年6月24日 下午4:18:17 pengliqing Exp $
 */
@Service
public class SuppilerServiceImpl implements SuppilerService {

    private static final Logger                      logger = LoggerFactory.getLogger(SuppilerServiceImpl.class);
    @Autowired
    private com.pzj.base.service.sale.IOrdersService iOrdersService;

    /**
    * 通过供应商查询是否一证一票
    * 
    * @param supplierId
    * @return
    */
    @Override
    public Boolean getSuppilerIdIsUnique(Long supplierId) {
        logger.info(LogUtil.logFormat(ContextEntry.getMonitor().getTraceId(), "request=" + supplierId));
        Integer flag = iOrdersService.getSuppilerIdIsUnique(supplierId);//flag为空是非一证一票
        return (flag == null || flag.intValue() == 1) ? false : true;
    }
}
