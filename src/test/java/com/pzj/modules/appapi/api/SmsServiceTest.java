/**
 * piaozhijia.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package com.pzj.modules.appapi.api;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pzj.common.test.BaseTest;

/**
 * 
 * @author pengliqing
 * @version $Id: SmsServiceTest.java, v 0.1 2016年6月16日 下午1:23:27 pengliqing Exp $
 */
public class SmsServiceTest extends BaseTest {

    @Autowired
    private com.pzj.message.sms.service.SmsSendService sMSSendService;

    @Test
    public void testSMSSendService() throws Exception {
        sMSSendService.sendSMS("18655406838", "much more money");
    }

}
