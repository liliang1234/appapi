/**
 * piaozhijia.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package com.pzj.platform.appapi.entity;

import java.io.Serializable;

/**
 * 
 * @author Mark
 * @version $Id: HomeSlideConfig.java, v 0.1 2016年7月20日 下午6:15:25 pengliqing Exp $
 */
public class HomeSlideConfig extends BaseEntity implements Serializable {

    /**  */
    private static final long serialVersionUID = 1L;
    private byte              appType;
    private String            province;
    private String            image;
    private String            text;
    private String            url;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }
}
