/**
 * piaozhijia.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package com.pzj.platform.appapi.model;

import com.pzj.trade.order.entity.OrderListResponse;

/**
 * 
 * @author Mark
 * @version $Id: OrderModel.java, v 0.1 2016年8月1日 上午10:07:03 pengliqing Exp $
 */
public class OrderModel extends OrderListResponse {

    /**  */
    private static final long serialVersionUID = 1L;

    private double            discountAmount2;      //后返金额

    public double getDiscountAmount2() {
        return discountAmount2;
    }

    public void setDiscountAmount2(double discountAmount2) {
        this.discountAmount2 = discountAmount2;
    }
}
