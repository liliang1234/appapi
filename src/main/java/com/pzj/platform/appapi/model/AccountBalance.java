/**
 * piaozhijia.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package com.pzj.platform.appapi.model;

import java.io.Serializable;

/**
 * 账户余额
 * 
 * @author fanggang
 * @version $Id: AccountBalance.java, v 0.1 2016年7月29日 下午4:31:43 fanggang Exp $
 */
public class AccountBalance implements Serializable {
    /**  */
    private static final long serialVersionUID = -1405238477671142910L;
    /** 总资产 */
    private Double            total;
    /** 可用余额 */
    private Double            avaliable;
    /** 冻结余额 */
    private Double            freezing;

    public AccountBalance() {
    }

    public AccountBalance(Double total, Double avaliable, Double freezing) {
        super();
        this.total = total;
        this.avaliable = avaliable;
        this.freezing = freezing;
    }

    /**
     * Getter method for property <tt>total</tt>.
     * 
     * @return property value of total
     */
    public Double getTotal() {
        return total;
    }

    /**
     * Setter method for property <tt>total</tt>.
     * 
     * @param total value to be assigned to property total
     */
    public void setTotal(Double total) {
        this.total = total;
    }

    /**
     * Getter method for property <tt>avaliable</tt>.
     * 
     * @return property value of avaliable
     */
    public Double getAvaliable() {
        return avaliable;
    }

    /**
     * Setter method for property <tt>avaliable</tt>.
     * 
     * @param avaliable value to be assigned to property avaliable
     */
    public void setAvaliable(Double avaliable) {
        this.avaliable = avaliable;
    }

    /**
     * Getter method for property <tt>freezing</tt>.
     * 
     * @return property value of freezing
     */
    public Double getFreezing() {
        return freezing;
    }

    /**
     * Setter method for property <tt>freezing</tt>.
     * 
     * @param freezing value to be assigned to property freezing
     */
    public void setFreezing(Double freezing) {
        this.freezing = freezing;
    }
}
