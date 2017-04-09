/**
 * piaozhijia.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package com.pzj.platform.appapi.service;

import com.alibaba.fastjson.JSONObject;
import com.pzj.customer.entity.Customer;
import com.pzj.framework.entity.QueryResult;
import com.pzj.platform.appapi.model.PaymentDetailModel;
import com.pzj.trade.order.entity.OrderDetailResponse;
import com.pzj.trade.order.entity.OrderListResponse;

/**
 * 
 * @author Mark
 * @version $Id: OrderService.java, v 0.1 2016年8月1日 上午9:46:59 pengliqing Exp $
 */
public interface OrderService {

    public QueryResult<OrderListResponse> queryOrdersByReseller(JSONObject data, Customer customer);

    public OrderDetailResponse queryOrderDetailByReseller(JSONObject data, String resellerType);

    /**
     * 
     * @param data
     * @param contactMobile
     * @return
     */
    QueryResult<OrderListResponse> queryOrdersByPhoneNum(JSONObject data);

    /**
     * 
     * @param requestObject
     * @return
     */
    public PaymentDetailModel paymentDetails(String orderId);
}
