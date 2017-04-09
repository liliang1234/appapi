/**
 * piaozhijia.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package com.pzj.platform.appapi.service;

import com.pzj.voucher.entity.VoucherEntity;

/**
 * 
 * @author pengliqing
 * @version $Id: VoucherService.java, v 0.1 2016年8月1日 上午10:45:35 pengliqing Exp $
 */
public interface VoucherService {

    public VoucherEntity getVoucherById(Long voucherId);
}
