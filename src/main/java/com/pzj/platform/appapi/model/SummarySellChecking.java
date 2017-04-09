/**
 * piaozhijia.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package com.pzj.platform.appapi.model;

import java.io.Serializable;

/**
 * 账单明细(概要)
 * @author fanggang
 * @version $Id: SellChecking.java, v 0.1 2016年7月27日 上午11:01:05 fanggang Exp $
 */
public class SummarySellChecking extends SimpleSellChecking implements Serializable {

    /**  */
    private static final long serialVersionUID = -7329937344025418231L;
    /** 账期 */
    private String            accountingMonth;
    /** 本期应收 */
    private Double            settleAmount;
    /** 本期已收 */
    private Double            settleReceived;
    /** 本期欠款 */
    private Double            defaulters;
    /** 状态 1 已创建（财务创建） 2 对账中（财务发送对账单） 3 已对账（分销商确认） 4 退回（财务撤销） 5 取消（分销商拒绝） 6 已确认(财务已审核) */
    private Integer           status;
    /** 可用金额 */
    private Double            balanceOfAvailable;
    /** 冻结金额 */
    private Double            balanceOfFrozen;

    /**
     * Getter method for property <tt>accountingMonth</tt>.
     * 
     * @return property value of accountingMonth
     */
    public String getAccountingMonth() {
        return accountingMonth;
    }

    /**
     * Setter method for property <tt>accountingMonth</tt>.
     * 
     * @param accountingMonth value to be assigned to property accountingMonth
     */
    public void setAccountingMonth(String accountingMonth) {
        this.accountingMonth = accountingMonth;
    }

    /**
     * Getter method for property <tt>settleAmount</tt>.
     * 
     * @return property value of settleAmount
     */
    public Double getSettleAmount() {
        return settleAmount;
    }

    /**
     * Setter method for property <tt>settleAmount</tt>.
     * 
     * @param settleAmount value to be assigned to property settleAmount
     */
    public void setSettleAmount(Double settleAmount) {
        this.settleAmount = settleAmount;
    }

    /**
     * Getter method for property <tt>settleReceived</tt>.
     * 
     * @return property value of settleReceived
     */
    public Double getSettleReceived() {
        return settleReceived;
    }

    /**
     * Setter method for property <tt>settleReceived</tt>.
     * 
     * @param settleReceived value to be assigned to property settleReceived
     */
    public void setSettleReceived(Double settleReceived) {
        this.settleReceived = settleReceived;
    }

    /**
     * Getter method for property <tt>defaulters</tt>.
     * 
     * @return property value of defaulters
     */
    public Double getDefaulters() {
        return defaulters;
    }

    /**
     * Setter method for property <tt>defaulters</tt>.
     * 
     * @param defaulters value to be assigned to property defaulters
     */
    public void setDefaulters(Double defaulters) {
        this.defaulters = defaulters;
    }

    /**
     * Getter method for property <tt>status</tt>.
     * 
     * @return property value of status
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * Setter method for property <tt>status</tt>.
     * 
     * @param status value to be assigned to property status
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    public Double getBalanceOfAvailable() {
        return balanceOfAvailable;
    }

    public void setBalanceOfAvailable(Double balanceOfAvailable) {
        this.balanceOfAvailable = balanceOfAvailable;
    }

    public Double getBalanceOfFrozen() {
        return balanceOfFrozen;
    }

    public void setBalanceOfFrozen(Double balanceOfFrozen) {
        this.balanceOfFrozen = balanceOfFrozen;
    }

}
