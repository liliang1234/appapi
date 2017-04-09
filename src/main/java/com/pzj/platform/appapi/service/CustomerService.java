/**
 * piaozhijia.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package com.pzj.platform.appapi.service;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSONObject;
import com.pzj.customer.entity.Customer;
import com.pzj.platform.appapi.entity.ResellerSimpleInfo;

/**
 * 
 * @author Mark
 * @version $Id: CustomerService.java, v 0.1 2016年7月13日 下午8:43:07 pengliqing Exp $
 */
public interface CustomerService {

    public Customer register(JSONObject data) throws Exception;

    public void validateLoginName(String loginName) throws Exception;

    public Map<String, Object> login(String username, String password, String type) throws Exception;

    public Customer getCustomerById(long customerId);

    public Customer queryCustomer(String token);

    public void updateResellerInfo(HttpServletRequest request, JSONObject data, Customer customer) throws Exception;

    public ResellerSimpleInfo findResellerInfo(Customer customer);
}
