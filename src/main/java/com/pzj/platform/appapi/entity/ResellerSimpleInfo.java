/**
 * piaozhijia.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package com.pzj.platform.appapi.entity;

import java.io.Serializable;

/**
 * 
 * @author fanggang
 * @version $Id: UserSimpleInfo.java, v 0.1 2016年8月30日 下午4:59:44 fanggang Exp $
 */
public class ResellerSimpleInfo implements Serializable {

    private static final long serialVersionUID = -7261730137829214072L;

    private Long              resellerId;                              // 分销商ID

    private String            resellerType;                            // 分销商类型［2:分销商－旅行社，3:分销商－旅行社部门，4:分销商－导游，5:分销商－商户］

    private String            resellerIdentifyType;                    // 分销商身份类型［p:个人，q:企业］

    private String            name;                                    // 微店名称：分销商公司名称或个人姓名

    private String            idCard;                                  // 分销商身份证号

    private String            mobile;                                  // 分销商手机号

    /**
     * Getter method for property <tt>resellerId</tt>.
     * 
     * @return property value of resellerId
     */
    public Long getResellerId() {
        return resellerId;
    }

    /**
     * Setter method for property <tt>resellerId</tt>.
     * 
     * @param resellerId value to be assigned to property resellerId
     */
    public void setResellerId(Long resellerId) {
        this.resellerId = resellerId;
    }

    /**
     * Getter method for property <tt>resellerIdentifyType</tt>.
     * 
     * @return property value of resellerIdentifyType
     */
    public String getResellerIdentifyType() {
        return resellerIdentifyType;
    }

    /**
     * Setter method for property <tt>resellerIdentifyType</tt>.
     * 
     * @param resellerIdentifyType value to be assigned to property resellerIdentifyType
     */
    public void setResellerIdentifyType(String resellerIdentifyType) {
        this.resellerIdentifyType = resellerIdentifyType;
    }

    /**
     * Getter method for property <tt>resellerType</tt>.
     * 
     * @return property value of resellerType
     */
    public String getResellerType() {
        return resellerType;
    }

    /**
     * Setter method for property <tt>resellerType</tt>.
     * 
     * @param resellerType value to be assigned to property resellerType
     */
    public void setResellerType(String resellerType) {
        this.resellerType = resellerType;
    }

    /**
     * Getter method for property <tt>name</tt>.
     * 
     * @return property value of name
     */
    public String getName() {
        return name;
    }

    /**
     * Setter method for property <tt>name</tt>.
     * 
     * @param name value to be assigned to property name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter method for property <tt>idCard</tt>.
     * 
     * @return property value of idCard
     */
    public String getIdCard() {
        return idCard;
    }

    /**
     * Setter method for property <tt>idCard</tt>.
     * 
     * @param idCard value to be assigned to property idCard
     */
    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    /**
     * Getter method for property <tt>mobile</tt>.
     * 
     * @return property value of mobile
     */
    public String getMobile() {
        return mobile;
    }

    /**
     * Setter method for property <tt>mobile</tt>.
     * 
     * @param mobile value to be assigned to property mobile
     */
    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

}
