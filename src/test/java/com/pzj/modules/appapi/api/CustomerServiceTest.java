/**
 * piaozhijia.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package com.pzj.modules.appapi.api;

import javax.annotation.Resource;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONObject;
import com.pzj.common.test.BaseTest;
import com.pzj.modules.appapi.entity.JsonEntity;
import com.pzj.platform.appapi.util.PropertyLoader;

/**
 * 
 * @author pengliqing
 * @version $Id: CustomerServiceTest.java, v 0.1 2016年6月22日 下午7:33:40 pengliqing Exp $
 */
public class CustomerServiceTest extends BaseTest {

    @Autowired
    private CustumerService custumerService;
    @Resource(name = "propertyLoader")
    private PropertyLoader  propertyLoader;

    @Test
    public void testSMSSendService() throws Exception {
        System.out.println(propertyLoader.getProperty("system", "send.SMS.fh.login"));
        JSONObject data = new JSONObject();
        JsonEntity json = new JsonEntity();
        data.put("tel", "");
        data.put("type", 0);
        data.put("loginName", "");
        custumerService.sendCodeWithMobile(data, json);
    }
}
