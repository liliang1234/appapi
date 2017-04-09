/**
 * piaozhijia.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package com.pzj.platform.appapi.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * 库存实体
 * @author daixuewei
 * @version $Id: SpuStockModel.java, v 0.1 2016年8月16日 15:10:28 daixuewei Exp $
 */
@JsonInclude(Include.NON_NULL)
public class SkuStockModel implements Serializable {
    /**  */
    private static final long serialVersionUID = 1L;
    /**
     * 库存主键id
     */
    private Long              id;
    /** 
     * 库存规则主键id 
     */
    private Long              ruleId;
    /** 
     * 库存总数 
     */
    private Integer           totalNum;
    /** 
     * 已用库存数量 
     */
    private Integer           usedNum;
    /** 
     * 剩余库存数 
     */
    private Integer           leftNum;

    /**
     * Getter method for property <tt>id</tt>.
     * 
     * @return property value of id
     */
    public Long getId() {
        return id;
    }

    /**
     * Setter method for property <tt>id</tt>.
     * 
     * @param id value to be assigned to property id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Getter method for property <tt>totalNum</tt>.
     * 
     * @return property value of totalNum
     */
    public Integer getTotalNum() {
        return totalNum;
    }

    /**
     * Setter method for property <tt>totalNum</tt>.
     * 
     * @param totalNum value to be assigned to property totalNum
     */
    public void setTotalNum(Integer totalNum) {
        this.totalNum = totalNum;
    }

    /**
     * Getter method for property <tt>usedNum</tt>.
     * 
     * @return property value of usedNum
     */
    public Integer getUsedNum() {
        return usedNum;
    }

    /**
     * Setter method for property <tt>usedNum</tt>.
     * 
     * @param usedNum value to be assigned to property usedNum
     */
    public void setUsedNum(Integer usedNum) {
        this.usedNum = usedNum;
    }

    /**
     * Getter method for property <tt>leftNum</tt>.
     * 
     * @return property value of leftNum
     */
    public Integer getLeftNum() {
        return leftNum;
    }

    /**
     * Setter method for property <tt>leftNum</tt>.
     * 
     * @param leftNum value to be assigned to property leftNum
     */
    public void setLeftNum(Integer leftNum) {
        this.leftNum = leftNum;
    }

    /**
     * Getter method for property <tt>ruleId</tt>.
     * 
     * @return property value of ruleId
     */
    public Long getRuleId() {
        return ruleId;
    }

    /**
     * Setter method for property <tt>ruleId</tt>.
     * 
     * @param ruleId value to be assigned to property ruleId
     */
    public void setRuleId(Long ruleId) {
        this.ruleId = ruleId;
    }
}
