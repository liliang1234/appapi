/**
 * piaozhijia.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package com.pzj.platform.appapi.model;

import java.io.Serializable;

/**
 * 
 * @author Mark
 * @version $Id: HotProductModel.java, v 0.1 2016年7月22日 上午11:06:20 pengliqing Exp $
 */
public class HotProductModel implements Serializable {

    /**  */
    private static final long serialVersionUID = 1L;
    private long              productId;
    private String            popularName;          //产品组名称
    private String            releaseThurl;         //缩略图
    private String            reeaseInfo;           //产品介绍
    private int               seq;
    private String            minAdvicePrice;
    private String            minMarketPrice;
    private Integer           proCategory;          //产品类型 包车 住宿 演艺  门票  等

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public String getPopularName() {
        return popularName;
    }

    public void setPopularName(String popularName) {
        this.popularName = popularName;
    }

    public String getReleaseThurl() {
        return releaseThurl;
    }

    public void setReleaseThurl(String releaseThurl) {
        this.releaseThurl = releaseThurl;
    }

    public String getReeaseInfo() {
        return reeaseInfo;
    }

    public void setReeaseInfo(String reeaseInfo) {
        this.reeaseInfo = reeaseInfo;
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public String getMinAdvicePrice() {
        return minAdvicePrice;
    }

    public void setMinAdvicePrice(String minAdvicePrice) {
        this.minAdvicePrice = minAdvicePrice;
    }

    public String getMinMarketPrice() {
        return minMarketPrice;
    }

    public void setMinMarketPrice(String minMarketPrice) {
        this.minMarketPrice = minMarketPrice;
    }

    /**
     * Getter method for property <tt>proCategory</tt>.
     * 
     * @return property value of proCategory
     */
    public Integer getProCategory() {
        return proCategory;
    }

    /**
     * Setter method for property <tt>proCategory</tt>.
     * 
     * @param proCategory value to be assigned to property proCategory
     */
    public void setProCategory(Integer proCategory) {
        this.proCategory = proCategory;
    }

}
