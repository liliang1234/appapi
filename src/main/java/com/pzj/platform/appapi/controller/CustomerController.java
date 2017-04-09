/**
 * piaozhijia.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package com.pzj.platform.appapi.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.pzj.customer.entity.Customer;
import com.pzj.modules.appapi.entity.JsonEntity;
import com.pzj.platform.appapi.service.CustomerService;

/**
 * 用户相关接口
 * 
 * @author Mark
 * @version $Id: CustomerController.java, v 0.1 2016年7月13日 下午8:33:40 pengliqing Exp $
 */
@Controller
@RequestMapping("user")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @RequestMapping("register")
    @ResponseBody
    public JsonEntity register(@RequestAttribute JSONObject requestObject) throws Exception {
        Customer result = customerService.register(requestObject);
        return JsonEntity.makeSuccessJsonEntity(result);
    }

    @RequestMapping("checkUserName")
    @ResponseBody
    public JsonEntity checkUserName(@RequestAttribute JSONObject requestObject) throws Exception {
        customerService.validateLoginName(requestObject.getString("username"));
        return JsonEntity.makeSuccessJsonEntity(null);
    }

    @RequestMapping("login")
    @ResponseBody
    public JsonEntity login(@RequestAttribute JSONObject requestObject) throws Exception {
        Map<String, Object> result = customerService.login(requestObject.getString("username"), requestObject.getString("password"),
            requestObject.getString("type"));
        return JsonEntity.makeSuccessJsonEntity(result);
    }

    /**
     * 完善用户资料（包括上传资质图片）
     */
    @RequestMapping("updateCustomerInfo")
    @ResponseBody
    public JsonEntity updateCustomerInfo(HttpServletRequest request, @RequestAttribute JSONObject requestObject,
                                         @RequestAttribute Customer customer) throws Exception {
        customerService.updateResellerInfo(request, requestObject, customer);
        return JsonEntity.makeSuccessJsonEntity(customer);
    }

    @RequestMapping("info")
    @ResponseBody
    public JsonEntity findUserInfo(@RequestAttribute JSONObject requestObject, @RequestAttribute Customer customer) throws Exception {
        return JsonEntity.makeSuccessJsonEntity(customerService.findResellerInfo(customer));
    }
}
