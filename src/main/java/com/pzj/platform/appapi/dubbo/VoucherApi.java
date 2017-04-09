/**
 * piaozhijia.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package com.pzj.platform.appapi.dubbo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pzj.voucher.common.ExecuteResult;
import com.pzj.voucher.entity.VoucherEntity;

/**
 * 
 * @author Mark
 * @version $Id: VoucherApi.java, v 0.1 2016年8月1日 上午10:46:03 pengliqing Exp $
 */
@Component
public class VoucherApi {

    @Autowired
    private com.pzj.voucher.service.VoucherService voucherService;

    public ExecuteResult<List<VoucherEntity>> queryVoucherByParam(VoucherEntity baseVoucher) {
        return voucherService.queryVoucherByParam(baseVoucher);

    }
}
