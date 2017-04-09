/**
 * piaozhijia.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package com.pzj.platform.appapi.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pzj.common.test.BaseTest;
import com.pzj.platform.appapi.entity.HomeClassificationConfig;

/**
 * 
 * @version $Id: HomeClassificationConfigMapperTest.java, v 0.1 2016年7月20日 下午5:46:46 pengliqing Exp $
 */
public class HomeClassificationConfigMapperTest extends BaseTest {

    @Autowired
    HomeClassificationConfigMapper homeClassificationConfigMapper;

    @Test
    public void testQuery() {
        Map<String, Object> bParam = new HashMap<String, Object>();
        List<HomeClassificationConfig> list = homeClassificationConfigMapper.queryByParamMap(bParam);
        Assert.assertTrue(list.size() > 0);
    }
}
