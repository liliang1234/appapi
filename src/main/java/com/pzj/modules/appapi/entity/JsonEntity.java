package com.pzj.modules.appapi.entity;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.pzj.platform.appapi.exception.Exceptions;

public class JsonEntity implements Serializable {

    /**  */
    private static final long serialVersionUID = 1L;
    // 响应流水号
    private String            responseReq      = "";
    // 响应时间
    private String            responseTime     = "";
    private String            message          = "";
    private String            code             = "";
    private String            accountStr       = "";
    private Object            responseBody     = null;

    public static JsonEntity makeSuccessJsonEntity(Object responseBody) {
        JsonEntity json = new JsonEntity();
        json.setCode(Exceptions.SUCCESS.getCode());
        json.setMessage(Exceptions.SUCCESS.getMessage());
        json.setResponseBody(responseBody);
        json.setResponseTime(String.valueOf(System.currentTimeMillis()));
        return json;
    }

    public static JsonEntity makeSuccessJsonEntity(String key, Object responseBody) {
        JsonEntity json = new JsonEntity();
        json.setCode(Exceptions.SUCCESS.getCode());
        json.setMessage(Exceptions.SUCCESS.getMessage());
        json.setResponseBody(responseBody);
        Map<String, Object> map = new HashMap<>();
        map.put(key, responseBody);
        json.setResponseTime(String.valueOf(System.currentTimeMillis()));
        return json;
    }

    public static JsonEntity makeExceptionJsonEntity(String code, String message) {
        JsonEntity json = new JsonEntity();
        json.setCode(code);
        json.setMessage(message);
        json.setResponseTime(String.valueOf(System.currentTimeMillis()));
        return json;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getAccountStr() {
        return accountStr;
    }

    public void setAccountStr(String accountStr) {
        this.accountStr = accountStr;
    }

    public Object getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(Object responseBody) {
        this.responseBody = responseBody;
    }

    public String getResponseReq() {
        return responseReq;
    }

    public void setResponseReq(String responseReq) {
        this.responseReq = responseReq;
    }

    public String getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(String responseTime) {
        this.responseTime = responseTime;
    }

    @Override
    public String toString() {
        return "JsonEntity [responseReq=" + responseReq + ", responseTime=" + responseTime + ", message=" + message + ", code=" + code + ", accountStr="
               + accountStr + ", responseBody=" + responseBody + "]";
    }

}
