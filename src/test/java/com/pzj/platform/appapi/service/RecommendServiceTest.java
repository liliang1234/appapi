/**
 * piaozhijia.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package com.pzj.platform.appapi.service;

import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.pzj.common.test.BaseTest;
import com.pzj.platform.appapi.entity.HomeClassificationConfig;
import com.pzj.platform.appapi.entity.HomeSlideConfig;

/**
 * 
 * @author Mark
 * @version $Id: RecommendServiceTest.java, v 0.1 2016年7月22日 下午2:18:43 pengliqing Exp $
 */
public class RecommendServiceTest extends BaseTest {

    private static final Logger logger = LoggerFactory.getLogger(CustomerServiceTest.class);
    @Autowired
    RecommendService            recommendService;

    @Test
    public void getHomeSlide() {
        JSONObject requestObject = new JSONObject();
        try {
            List<HomeSlideConfig> result = recommendService.getHomeSlide(requestObject);
            logger.debug(JSON.toJSONString(result));
        } catch (Exception e) {
            logger.error(JSON.toJSONString(e));
            e.printStackTrace();
        }
    }

    @Test
    public void getHomeClassification() {
        JSONObject requestObject = new JSONObject();
        try {
            List<HomeClassificationConfig> result = recommendService.getHomeClassification(requestObject);
            logger.debug(JSON.toJSONString(result));
        } catch (Exception e) {
            logger.error(JSON.toJSONString(e));
            e.printStackTrace();
        }
    }

    @Test
    public void getHotProduct() {
        //        JSONObject requestObject = new JSONObject();
        //        requestObject.put("currentPage", 1);
        //        requestObject.put("pageSize", 6);
        //        requestObject.put("province", "北京");
        //        
        //        try {
        //            List<HotProductModel> result = recommendService.getHotProduct(requestObject);
        //            logger.debug(JSON.toJSONString(result));
        //        } catch (Exception e) {
        //            logger.error(JSON.toJSONString(e));
        //            e.printStackTrace();
        //        }
    }
}
