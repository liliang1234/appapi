/**
 * piaozhijia.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package com.pzj.platform.appapi.service;

import java.util.List;
import java.util.Map;

import com.pzj.platform.appapi.model.OrderMerchModel;
import com.pzj.platform.appapi.model.OrderMerchValueResult;
import com.pzj.trade.order.entity.MerchResponse;
import com.pzj.voucher.entity.VoucherEntity;

/**
 * 
 * @author Mark
 * @version $Id: OrderMerchService.java, v 0.1 2016年8月1日 上午9:47:30 pengliqing Exp $
 */
public interface OrderMerchService {

    public void wrapperMerchProduct(OrderMerchModel orderMerchModel);

    public void wrapperMerchStrategy(OrderMerchValueResult orderMerchValueResult, MerchResponse merch, OrderMerchModel orderMerchModel);

    public void wrapperMerchVoucher(MerchResponse merch, OrderMerchModel orderMerchModel, Map<Long, VoucherEntity> voucherMap);

    public void wrapperRefundFlowResponse(MerchResponse merch, OrderMerchModel orderMerchModel, Map<Long, VoucherEntity> voucherMap);

    public OrderMerchValueResult convert2MerchResponseVo(List<MerchResponse> merchs, Map<String, OrderMerchService> wrapperProcesses);

}
