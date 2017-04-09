/**
 * piaozhijia.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package com.pzj.platform.appapi.support;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.servlet.http.HttpServletResponse;

import org.bouncycastle.util.encoders.Base64;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.pzj.common.util.CheckUtils;
import com.pzj.customer.entity.Customer;
import com.pzj.modules.appapi.entity.JsonEntity;
import com.pzj.platform.appapi.exception.BussinessException;
import com.pzj.platform.appapi.exception.Exceptions;
import com.pzj.platform.appapi.exception.ParamException;
import com.pzj.platform.appapi.util.MD5Util;

/**
 * 
 * @author Mark
 * @version $Id: ApiRequestSupport.java, v 0.1 2016年7月5日 上午11:56:17 pengliqing Exp $
 */
public class ApiRequestSupport {

    private final static Gson gson = new Gson();

    /**
     * 请求参数权限校验
     * 
     * @param customer
     * @param data
     * @param sign
     * @param token
     * @throws UnsupportedEncodingException
     * */
    public static final void checkData(String data) throws UnsupportedEncodingException {
        if (CheckUtils.isNull(data) || data.length() == 0) {
            throw new ParamException(Exceptions.PARAMEMPTYERROR);
        }
    }

    /**
     * 请求参数权限校验
     * 
     * @param customer
     * @param data
     * @param sign
     * @param token
     * @throws UnsupportedEncodingException
     * */
    public static final void checkLogin(Customer customer, String data, String sign, String token) throws UnsupportedEncodingException {
        if (customer == null || customer.getAccountState() == null || !customer.getAccountState().equals(1)) {
            throw new BussinessException(Exceptions.TOKENRERROR);
        }
        String sign1 = MD5Util.md5Hex(URLDecoder.decode(data, "UTF-8") + token);
        if (CheckUtils.isNull(sign) || !sign.equals(sign1)) {
            throw new BussinessException(Exceptions.SIGNERROR);
        }
    }

    /**
     * 获取请求参数实体
     * 
     * @param 请求参数字符串
     * @return
     * @throws UnsupportedEncodingException
     * */
    public static final JSONObject getRequestData(String data) throws UnsupportedEncodingException {
        if (CheckUtils.isNull(data)) {
            throw new ParamException(Exceptions.PARAMEMPTYERROR);
        }
        String value = new String(Base64.decode(URLDecoder.decode(data, "UTF-8")));
        return JSON.parseObject(value);
    }

    public static void invokeExceptionWrapper(HttpServletResponse response, String code, String message) throws IOException {
        JsonEntity json = new JsonEntity();
        json.setCode(code);
        json.setMessage(message);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(gson.toJson(json));
    }
}
