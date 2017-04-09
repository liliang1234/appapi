package com.pzj.modules.appapi.api.dto;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Administrator on 2016-6-8.
 */
public class AccountDetailFlowDto implements Serializable {
    private Date   createTime;          // 创建时间
    private String remarks;             // 账户备注(App页面上的摘要)
    private Double resellerAcountMoney; // 分销商账户金额
    private String orderId;             // 关联业务订单id
    private Double accountBalanceMoney; // 余额
    private String supplierName;        // 交易对象
    private String desc;                // App页面上的备注

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Double getResellerAcountMoney() {
        return resellerAcountMoney;
    }

    public void setResellerAcountMoney(Double resellerAcountMoney) {
        this.resellerAcountMoney = resellerAcountMoney;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Double getAccountBalanceMoney() {
        return accountBalanceMoney;
    }

    public void setAccountBalanceMoney(Double accountBalanceMoney) {
        this.accountBalanceMoney = accountBalanceMoney;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
