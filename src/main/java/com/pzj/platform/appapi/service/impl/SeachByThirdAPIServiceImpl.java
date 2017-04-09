package com.pzj.platform.appapi.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.pzj.appapi.constants.ApiConstants;
import com.pzj.common.redis.RedisUtils;
import com.pzj.common.util.PostUtils;
import com.pzj.modules.appapi.entity.JsonEntity;
import com.pzj.platform.appapi.model.SearchAddressByAPIModel;
import com.pzj.platform.appapi.service.SeachByThirdAPIService;
import com.pzj.platform.appapi.util.PropertyLoader;

/**
 * 
 * SeachByThirdAPIServiceImpl实现
 * 
 */
@Service
public class SeachByThirdAPIServiceImpl implements SeachByThirdAPIService {
    private static final Logger logger = LoggerFactory.getLogger(AppVersionMessageServiceImpl.class);
    @Resource(name = "propertyLoader")
    private PropertyLoader      propertyLoader;
    @Autowired
    private RedisUtils<String>  redisUtils;

    /** 
     * @see com.pzj.platform.appapi.service.SeachByThirdAPIService#searchAddress(com.alibaba.fastjson.JSONObject)
     */
    @Override
    public JsonEntity searchAddress(JSONObject requestObject) {
        logger.info("进入到是第三方搜索方法");
        String city = requestObject.containsKey("city") ? requestObject.getString("city") : "";
        String keywords = requestObject.containsKey("keywords") ? requestObject.getString("keywords") : "";
        logger.info("第三方搜索keywords是：" + keywords);
        Integer pageNum = requestObject.containsKey("pageNum") ? Integer.valueOf(requestObject.getString("pageNum")) : 1;
        Integer pageSize = requestObject.containsKey("pageSize") ? Integer.valueOf(requestObject.getString("pageSize")) : 20;
        String redisResultStr = redisUtils.get(city + keywords + pageNum + pageSize);
        if (redisResultStr != null && !"".equals(redisResultStr)) {
            return JSONObject.parseObject(redisResultStr, JsonEntity.class);
        }
        String result = searchAddress(pageNum, pageSize, city, keywords);
        Map<String, Object> rmap = JSON.parseObject(result);
        Integer totalRecord = 0;
        Integer totalPage = 0;
        String pois = rmap.get("pois").toString();

        if (rmap.get("count") != null && !"".equals(rmap.get("count"))) {
            totalRecord = Integer.valueOf(rmap.get("count").toString());
            totalPage = totalRecord % pageSize == 0 ? totalRecord / pageSize : (totalRecord / pageSize + 1);
        }
        List<SearchAddressByAPIModel> list = JSONObject.parseArray(pois, SearchAddressByAPIModel.class);
        //过滤返回结果的地址中带[]改为""，返回给app
        for (SearchAddressByAPIModel searchAddressByAPIModel : list) {
            if ("[]".equals(searchAddressByAPIModel.getAddress())) {
                searchAddressByAPIModel.setAddress("");
            }
        }
        redisResultStr = JSONObject
            .toJSONString(JsonEntity.makeSuccessJsonEntity("searchKeys", generateResponseBodyMap(list, totalRecord, totalPage, pageNum)));
        if (redisResultStr != null && !"".equals(redisResultStr)) {
            redisUtils.add(city + keywords + pageNum + pageSize, redisResultStr, RedisUtils.REDIS_EXPIRE_TIME);
        }
        return JsonEntity.makeSuccessJsonEntity("searchKeys", generateResponseBodyMap(list, totalRecord, totalPage, pageNum));
    }

    public String searchAddress(Integer pageNum, Integer pageSize, String city, String keywords) {

        String urlPath = propertyLoader.getProperty(ApiConstants.SYSTEM_FILENAME, "bmap.api.url");
        String key = propertyLoader.getProperty(ApiConstants.SYSTEM_FILENAME, "bmap.api.key");
        StringBuilder data = new StringBuilder().append("output=json&extensions=base").append("&keywords=").append(keywords).append("&city=").append(city)
            .append("&offset=").append(pageSize).append("&page=").append(pageNum).append("&key=").append(key);
        logger.info("第三方搜索URL是：" + urlPath);
        logger.info("第三方搜索传入参数是：" + data);
        String result = PostUtils.post(urlPath, data.toString(), "json");
        //        if (result != null && !"".equals(result)) {
        //            redisUtils.add(city + keywords + pageNum + pageSize, result, RedisUtils.REDIS_EXPIRE_TIME);
        //        }
        return result;
    }

    /**
     * list为空时构造一个空list对象
     *
     * @param list
     * @param <T>
     * @return 一个以 RESPONSEBODY_KEY 为key,list为value的map作为responsebody
     */
    private Map<String, Object> generateResponseBodyMap(List<SearchAddressByAPIModel> list, Integer totalRecord, Integer totalPage, Integer pageNum) {
        Map<String, Object> responseBodyMap = new HashMap<>();
        if (CollectionUtils.isEmpty(list)) {
            list = Lists.newArrayList();
        }
        responseBodyMap.put("resultList", list);
        responseBodyMap.put("totalRecord", totalRecord);
        responseBodyMap.put("totalPage", totalPage);
        responseBodyMap.put("totalPage", totalPage);
        responseBodyMap.put("pageNum", pageNum);
        return responseBodyMap;
    }

    public static void main(String[] args) {
        String urlPath = "http://restapi.amap.com/v3/place/text";
        StringBuilder data = new StringBuilder().append("output=json&offset=5&page=2&key=c67e3fe79cabd795cfefff93a1101135&extensions=base");
        data.append("&keywords=").append("北京").append("&city=").append("回龙观");
        String result = PostUtils.post(urlPath, data.toString(), "json");
        Map<String, Object> rmap = JSON.parseObject(result);
        String pois = rmap.get("pois").toString();
        List<SearchAddressByAPIModel> list = JSONObject.parseArray(pois, SearchAddressByAPIModel.class);
        System.out.println(pois);

    }
}