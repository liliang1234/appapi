/**
 * piaozhijia.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package com.pzj.platform.appapi.model;

import java.io.Serializable;

/**
 * 产品组头信息
 * @author fanggang
 * @version $Id: SpuProductHeadModel.java, v 0.1 2016年8月4日 下午5:19:17 fanggang Exp $
 */
public class SpuProductHeadModel implements Serializable {

    /**  */
    private static final long serialVersionUID = -3440773520301627210L;
    private Long              id;
    /** 产品组名称 */
    private String            name;
    /** 产品类型 */
    private Integer           productType;
    /** 产品介绍 */
    private String            oneWordFeature;
    /** 产品组图片 */
    private String            releaseThurl;
    /** 最低门市价 */
    private String            minMarketPrice;
    /** 最低建议零售价 */
    private String            minAdvicePrice;

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
     * Getter method for property <tt>productType</tt>.
     * 
     * @return property value of productType
     */
    public Integer getProductType() {
        return productType;
    }

    /**
     * Setter method for property <tt>productType</tt>.
     * 
     * @param productType value to be assigned to property productType
     */
    public void setProductType(Integer productType) {
        this.productType = productType;
    }

    /**
     * Getter method for property <tt>oneWordFeature</tt>.
     * 
     * @return property value of oneWordFeature
     */
    public String getOneWordFeature() {
        return oneWordFeature;
    }

    /**
     * Setter method for property <tt>oneWordFeature</tt>.
     * 
     * @param oneWordFeature value to be assigned to property oneWordFeature
     */
    public void setOneWordFeature(String oneWordFeature) {
        this.oneWordFeature = oneWordFeature;
    }

    /**
     * Getter method for property <tt>releaseThurl</tt>.
     * 
     * @return property value of releaseThurl
     */
    public String getReleaseThurl() {
        return releaseThurl;
    }

    /**
     * Setter method for property <tt>releaseThurl</tt>.
     * 
     * @param releaseThurl value to be assigned to property releaseThurl
     */
    public void setReleaseThurl(String releaseThurl) {
        this.releaseThurl = releaseThurl;
    }

    /**
     * Getter method for property <tt>minMarketPrice</tt>.
     * 
     * @return property value of minMarketPrice
     */
    public String getMinMarketPrice() {
        return minMarketPrice;
    }

    /**
     * Setter method for property <tt>minMarketPrice</tt>.
     * 
     * @param minMarketPrice value to be assigned to property minMarketPrice
     */
    public void setMinMarketPrice(String minMarketPrice) {
        this.minMarketPrice = minMarketPrice;
    }

    /**
     * Getter method for property <tt>minAdvicePrice</tt>.
     * 
     * @return property value of minAdvicePrice
     */
    public String getMinAdvicePrice() {
        return minAdvicePrice;
    }

    /**
     * Setter method for property <tt>minAdvicePrice</tt>.
     * 
     * @param minAdvicePrice value to be assigned to property minAdvicePrice
     */
    public void setMinAdvicePrice(String minAdvicePrice) {
        this.minAdvicePrice = minAdvicePrice;
    }
}
