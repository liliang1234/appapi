/**
 * piaozhijia.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package com.pzj.platform.appapi.entity;

import java.io.Serializable;

/**
 * 
 * @author Mark
 * @version $Id: HomeClassificationConfig.java, v 0.1 2016年7月20日 下午5:24:02 pengliqing Exp $
 */
public class HomeClassificationConfig extends BaseEntity implements Serializable {

    /**  */
    private static final long serialVersionUID = 1L;
    private byte              appType;
    private String            province;
    private String            code;
    private String            icon2x;
    private String            icon3x;
    private String            text;
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getIcon2x() {
        return icon2x;
    }

    public void setIcon2x(String icon2x) {
        this.icon2x = icon2x;
    }

    public String getIcon3x() {
        return icon3x;
    }

    public void setIcon3x(String icon3x) {
        this.icon3x = icon3x;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

}
