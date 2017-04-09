/**
 * piaozhijia.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package com.pzj.platform.appapi.service;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.pzj.common.test.BaseTest;
import com.pzj.customer.entity.Customer;

/**
 * 
 * @author Mark
 * @version $Id: CustomerServiceTest.java, v 0.1 2016年7月15日 上午10:40:58 pengliqing Exp $
 */
public class CustomerServiceTest extends BaseTest {

    private static final Logger logger = LoggerFactory.getLogger(CustomerServiceTest.class);
    @Autowired
    CustomerService             customerService;

    @Test
    public void updateResellerInfo() {
        JSONObject jsonObject = new JSONObject();
        Customer customer = new Customer();
        try {
            customerService.updateResellerInfo(null, jsonObject, customer);
            logger.debug(JSON.toJSONString(customer));
        } catch (Exception e) {
            logger.error(JSON.toJSONString(e));
            e.printStackTrace();
        }
    }
}
