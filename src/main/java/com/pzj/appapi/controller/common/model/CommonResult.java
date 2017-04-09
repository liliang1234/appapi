package com.pzj.appapi.controller.common.model;

import java.io.Serializable;

/**
 * @author hesimin
 * @date 2016/10/26
 */
public class CommonResult implements Serializable {
    private boolean success;
    private String  message;
    private Object  data;

    public boolean isSuccess() {
        return success;
    }

    public CommonResult setSuccess(boolean success) {
        this.success = success;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public CommonResult setMessage(String message) {
        this.message = message;
        return this;
    }

    public Object getData() {
        return data;
    }

    public CommonResult setData(Object data) {
        this.data = data;
        return this;
    }
}
