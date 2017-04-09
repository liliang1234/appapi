/**
 * piaozhijia.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package com.pzj.platform.appapi.model;

import java.io.Serializable;
import java.util.List;

/**
 * 
 * @author mark
 * @version $Id: OrderMerchValueResult.java, v 0.1 2016年6月8日 下午4:43:52 mark Exp $
 */
public class OrderMerchValueResult implements Serializable {

    /**  */
    private static final long     serialVersionUID = 1L;
    private double                discountAmount2;
    private List<OrderMerchModel> merchResponseVo;

    public double getDiscountAmount2() {
        return discountAmount2;
    }

    public void setDiscountAmount2(double discountAmount2) {
        this.discountAmount2 = discountAmount2;
    }

    public List<OrderMerchModel> getMerchResponseVo() {
        return merchResponseVo;
    }

    public void setMerchResponseVo(List<OrderMerchModel> merchResponseVo) {
        this.merchResponseVo = merchResponseVo;
    }
}
