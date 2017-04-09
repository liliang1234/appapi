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
import com.pzj.platform.appapi.entity.HotWords;

/**
 * 
 * 
 * @author fanggang
 * @version $Id: HotWordsMapperTest.java, v 0.1 2016年8月12日 下午3:23:39 fanggang Exp $
 */
public class HotWordsMapperTest extends BaseTest {

    @Autowired
    HotWordsMapper hotWordsMapper;

    @Test
    public void testQuery() {
        Map<String, Object> bParam = new HashMap<String, Object>();
        List<HotWords> list = hotWordsMapper.queryByParamMap(bParam);
        Assert.assertTrue(list.size() > 0);
    }
}
