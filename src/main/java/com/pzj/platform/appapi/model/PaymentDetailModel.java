/**
 * piaozhijia.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package com.pzj.platform.appapi.model;

import java.io.Serializable;

/**
 * 
 * @author cc
 * @version $Id: PaymentDetailModel.java, v 0.1 2016年11月1日 下午3:51:27 cc Exp $
 */
public class PaymentDetailModel implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private double orderAmount;    //订单总额

    private double weShopRebatePay; //微店返利支付金额

    private double appRebatePay;    //app返利支付金额

    private double otherPay;        //第三方支付金额

    private Integer otherPayType;    //第三方支付类型  1支付宝支付 2 微信支付 

    public double getOrderAmount() {
        return orderAmount;
    }

    public void setOrderAmount(double orderAmount) {
        this.orderAmount = orderAmount;
    }

    public double getWeShopRebatePay() {
        return weShopRebatePay;
    }

    public void setWeShopRebatePay(double weShopRebatePay) {
        this.weShopRebatePay = weShopRebatePay;
    }

    public double getAppRebatePay() {
        return appRebatePay;
    }

    public void setAppRebatePay(double appRebatePay) {
        this.appRebatePay = appRebatePay;
    }

    public double getOtherPay() {
        return otherPay;
    }

    public void setOtherPay(double otherPay) {
        this.otherPay = otherPay;
    }

    public Integer getOtherPayType() {
        return otherPayType;
    }

    public void setOtherPayType(Integer otherPayType) {
        this.otherPayType = otherPayType;
    }

}
