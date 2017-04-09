/**
 * piaozhijia.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package com.pzj.platform.appapi.service.impl;

/**
 * 
 * @author fanggang
 * @version $Id: ProductIdType.java, v 0.1 2016年8月24日 下午3:56:07 fanggang Exp $
 */
public enum ProductIdType {
    SKU_ID(1, "产品ID"),
    SPU_ID(2, "产品组ID");
    private int    code;
    private String value;

    private ProductIdType(int code, String value) {
        this.code = code;
        this.value = value;
    }

    public int getCode() {
        return code;
    }

    public String getValue() {
        return value;
    }
}
