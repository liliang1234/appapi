/**
 * piaozhijia.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package com.pzj.platform.appapi.entity;

/**
 * 
 * @author Mark
 * @version $Id: HotProductRecommend.java, v 0.1 2016年7月22日 上午10:44:35 pengliqing Exp $
 */
public class HotProductRecommend extends BaseEntity {

    /**  */
    private static final long serialVersionUID = 1L;
    private byte              appType;
    private String            province;
    private long              productId;
    private int               productType;
    private int               seq;

    public byte getAppType() {
        return appType;
    }

    public void setAppType(byte appType) {
        this.appType = appType;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public int getProductType() {
        return productType;
    }

    public void setProductType(int productType) {
        this.productType = productType;
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }
}
