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
import com.pzj.platform.appapi.entity.HotProductRecommend;

/**
 * 
 * @author Mark
 * @version $Id: HotProductRecommendMapperTest.java, v 0.1 2016年7月22日 上午10:48:24 pengliqing Exp $
 */
public class HotProductRecommendMapperTest extends BaseTest {

    @Autowired
    HotProductRecommendMapper hotProductRecommendMapper;

    @Test
    public void testQuery() {
        Map<String, Object> bParam = new HashMap<String, Object>();
        List<HotProductRecommend> list = hotProductRecommendMapper.queryByParamMap(bParam);
        Assert.assertTrue(list.size() > 0);
    }
}
