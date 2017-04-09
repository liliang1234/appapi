/**
 * piaozhijia.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package com.pzj.platform.appapi.model;

import java.io.Serializable;

/**
 * 账单列表对象(简略)
 * @author fanggang
 * @version $Id: SimpleSellChecking.java, v 0.1 2016年7月26日 下午5:45:53 fanggang Exp $
 */
public class SimpleSellChecking implements Serializable {

    /**  */
    private static final long serialVersionUID = -7329937344025418231L;
    /** 唯一标识 */
    private Long              id;
    /** 对账单编号 */
    private String            checkingNum;
    /** 创建时间 */
    private Long              createTime;
    /** 创建时间 */
    private String            createDate;
    /** status: 1 已创建（财务创建） 2 对账中（财务发送对账单） 3 已对账（分销商确认） 4 退回（财务撤销） 5 取消（分销商拒绝） 6 已确认(财务已审核)*/
    private Integer           status;

    public SimpleSellChecking() {
    }

    public SimpleSellChecking(Long id, String checkingNum, Long createTime, String createDate, Integer status) {
        super();
        this.id = id;
        this.checkingNum = checkingNum;
        this.createTime = createTime;
        this.createDate = createDate;
        this.status = status;
    }

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
     * Getter method for property <tt>checkingNum</tt>.
     * 
     * @return property value of checkingNum
     */
    public String getCheckingNum() {
        return checkingNum;
    }

    /**
     * Setter method for property <tt>checkingNum</tt>.
     * 
     * @param checkingNum value to be assigned to property checkingNum
     */
    public void setCheckingNum(String checkingNum) {
        this.checkingNum = checkingNum;
    }

    /**
     * Getter method for property <tt>createTime</tt>.
     * 
     * @return property value of createTime
     */
    public Long getCreateTime() {
        return createTime;
    }

    /**
     * Setter method for property <tt>createTime</tt>.
     * 
     * @param createTime value to be assigned to property createTime
     */
    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

}
