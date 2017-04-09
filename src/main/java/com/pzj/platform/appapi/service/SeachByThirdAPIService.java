package com.pzj.platform.appapi.service;

import com.alibaba.fastjson.JSONObject;
import com.pzj.modules.appapi.entity.JsonEntity;

/**
 * SeachByThirdAPIService接口
 * 
 * */
public interface SeachByThirdAPIService {
    /**
     * 根据关键字，城市查询第三方地图接口
     * */
    public JsonEntity searchAddress(JSONObject requestObject);
}
