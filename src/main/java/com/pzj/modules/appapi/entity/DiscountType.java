/**
 * piaozhijia.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package com.pzj.modules.appapi.entity;

/**
 * 
 * @author Mark
 */
public enum DiscountType {

    DISCOUNT_TYPE_BEFORE(1, "前反"), DISCOUNT_TYPE_AFTER(2, "后反");
    private int code;
    private String value;

    private DiscountType(int code, String value) {
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
