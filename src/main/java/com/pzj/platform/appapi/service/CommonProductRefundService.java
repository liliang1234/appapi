/**
 * piaozhijia.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package com.pzj.platform.appapi.service;

import java.util.Date;

import com.pzj.base.entity.product.TicketRule;

/**
 * 
 * @author pengliqing
 * @version $Id: CommonProductRefundService.java, v 0.1 2016年8月1日 上午10:41:17 pengliqing Exp $
 */
public interface CommonProductRefundService {

    public boolean isRefund(Integer timeType, Integer days, Double hours, Integer minutes, Date startTime);

    public TicketRule findActiveRefundRule(Long productId);
}
