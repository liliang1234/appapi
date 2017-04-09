/**
 * piaozhijia.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package com.pzj.platform.appapi.service;

import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.pzj.customer.entity.Customer;
import com.pzj.platform.appapi.entity.HomeClassificationConfig;
import com.pzj.platform.appapi.entity.HomeSlideConfig;
import com.pzj.platform.appapi.model.HotProductModel;

/**
 * 
 * @author Mark
 * @version $Id: AppHomeService.java, v 0.1 2016年7月22日 上午11:03:51 pengliqing Exp $
 */
public interface RecommendService {

    public List<HomeSlideConfig> getHomeSlide(JSONObject requestObject);

    public List<HomeClassificationConfig> getHomeClassification(JSONObject requestObject);

    public List<HotProductModel> getHotProduct(JSONObject requestObject, Customer customer);
}
