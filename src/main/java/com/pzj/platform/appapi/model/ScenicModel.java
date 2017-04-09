/**
 * piaozhijia.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package com.pzj.platform.appapi.model;

import java.io.Serializable;
import java.util.List;

/**
 * 景区信息，包括景区下的产品组信息。
 * 
 * @author Mark
 * @version $Id: ScenicModel.java, v 0.1 2016年8月2日 下午5:48:29 pengliqing Exp $
 */
public class ScenicModel implements Serializable {

    /**  */
    private static final long serialVersionUID = 1L;

    Long                      scenicId;

    String                    sceneName;
    /**
     * 省
     */
    private String            province;
    /**
     * 市
     */
    private String            city;
    /**
     * 县
     */
    private String            county;

    /**轮播图*/
    private String            img_url;

    /**供应商介绍*/
    private String            info;

    /** 最低门市价 */
    private String            minMarketPrice;
    /** 最低建议零售价 */
    private String            minAdvicePrice;
    /**产品组列表*/
    List<SpuProductModel>     spuLists;

    private Integer           pageNo;

    private Integer           pageSize;

    private long              totalCount;

    /**
     * Getter method for property <tt>sceneName</tt>.
     * 
     * @return property value of sceneName
     */
    public String getSceneName() {
        return sceneName;
    }

    /**
     * Setter method for property <tt>sceneName</tt>.
     * 
     * @param sceneName value to be assigned to property sceneName
     */
    public void setSceneName(String sceneName) {
        this.sceneName = sceneName;
    }

    /**
     * Getter method for property <tt>city</tt>.
     * 
     * @return property value of city
     */
    public String getCity() {
        return city;
    }

    /**
     * Setter method for property <tt>city</tt>.
     * 
     * @param city value to be assigned to property city
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * Getter method for property <tt>county</tt>.
     * 
     * @return property value of county
     */
    public String getCounty() {
        return county;
    }

    /**
     * Setter method for property <tt>county</tt>.
     * 
     * @param county value to be assigned to property county
     */
    public void setCounty(String county) {
        this.county = county;
    }

    /**
     * Getter method for property <tt>province</tt>.
     * 
     * @return property value of province
     */
    public String getProvince() {
        return province;
    }

    /**
     * Setter method for property <tt>province</tt>.
     * 
     * @param province value to be assigned to property province
     */
    public void setProvince(String province) {
        this.province = province;
    }

    /**
     * Getter method for property <tt>img_url</tt>.
     * 
     * @return property value of img_url
     */
    public String getImg_url() {
        return img_url;
    }

    /**
     * Setter method for property <tt>img_url</tt>.
     * 
     * @param img_url value to be assigned to property img_url
     */
    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    /**
     * Getter method for property <tt>scenicId</tt>.
     * 
     * @return property value of scenicId
     */
    public Long getScenicId() {
        return scenicId;
    }

    /**
     * Setter method for property <tt>scenicId</tt>.
     * 
     * @param scenicId value to be assigned to property scenicId
     */
    public void setScenicId(Long scenicId) {
        this.scenicId = scenicId;
    }

    /**
     * Getter method for property <tt>info</tt>.
     * 
     * @return property value of info
     */
    public String getInfo() {
        return info;
    }

    /**
     * Setter method for property <tt>info</tt>.
     * 
     * @param info value to be assigned to property info
     */
    public void setInfo(String info) {
        this.info = info;
    }

    /**
     * Setter method for property <tt>spuLists</tt>.
     * 
     * @param spuLists value to be assigned to property spuLists
     */
    public void setSpuLists(List<SpuProductModel> spuLists) {
        this.spuLists = spuLists;
    }

    /**
     * Getter method for property <tt>spuLists</tt>.
     * 
     * @return property value of spuLists
     */
    public List<SpuProductModel> getSpuLists() {
        return spuLists;
    }

    /**
     * Getter method for property <tt>pageNo</tt>.
     * 
     * @return property value of pageNo
     */
    public Integer getPageNo() {
        return pageNo;
    }

    /**
     * Setter method for property <tt>pageNo</tt>.
     * 
     * @param pageNo value to be assigned to property pageNo
     */
    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }

    /**
     * Getter method for property <tt>pageSize</tt>.
     * 
     * @return property value of pageSize
     */
    public Integer getPageSize() {
        return pageSize;
    }

    /**
     * Setter method for property <tt>pageSize</tt>.
     * 
     * @param pageSize value to be assigned to property pageSize
     */
    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    /**
     * Getter method for property <tt>totalCount</tt>.
     * 
     * @return property value of totalCount
     */
    public long getTotalCount() {
        return totalCount;
    }

    /**
     * Setter method for property <tt>totalCount</tt>.
     * 
     * @param totalCount value to be assigned to property totalCount
     */
    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
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
}
