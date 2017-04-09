/**
 * piaozhijia.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package com.pzj.platform.appapi.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.pzj.customer.entity.Customer;
import com.pzj.modules.appapi.api.SceneService;
import com.pzj.modules.appapi.entity.CodeHandle;
import com.pzj.modules.appapi.entity.JsonEntity;
import com.pzj.platform.appapi.model.ProvinceModel;
import com.pzj.platform.appapi.model.ScenicModel;
import com.pzj.platform.appapi.model.SkuStockModel;
import com.pzj.platform.appapi.model.SpuListModel;
import com.pzj.platform.appapi.model.SpuProductHeadModel;
import com.pzj.platform.appapi.model.SpuProductModel;
import com.pzj.platform.appapi.service.ParamCheckService;
import com.pzj.platform.appapi.service.ProductService;
import com.pzj.platform.appapi.util.PropertyLoader;

/**
 * 
 * @author Mark
 * @version $Id: ProductController.java, v 0.1 2016年7月25日 上午11:44:10 pengliqing Exp $
 */
@Controller
@RequestMapping("product")
public class ProductController {

    @Autowired
    private ProductService productService;
    @Resource(name = "propertyLoader")
    private PropertyLoader propertyLoader;
    @Autowired
    private SceneService   sceneService;

    /** 通过景区id 获取产品组列表*/
    @RequestMapping("getSpuListByScence")
    @ResponseBody
    public JsonEntity getSpuListByScenceId(@RequestAttribute JSONObject requestObject, @RequestAttribute Customer customer) {
        try {
            ScenicModel result = productService.getSpuListByScenicId(customer.getId(), requestObject.getLong("scenicId"), requestObject.getString("salesType"),
                requestObject.getInteger("pageNo"), requestObject.getInteger("pageSize"));
            return JsonEntity.makeSuccessJsonEntity(result);
        } catch (Exception ex) {
            ex.printStackTrace();
            return JsonEntity.makeExceptionJsonEntity(CodeHandle.SERVERERROR.getCode(), ex.getMessage());
        }
    }

    /** 通过地区 获取通用产品组列表*/
    @RequestMapping("getCommonSpuList")
    @ResponseBody
    public JsonEntity getCommonSpuList(@RequestAttribute JSONObject requestObject, @RequestAttribute Customer customer) throws Exception {
        SpuListModel result = productService.getCommonSpuList(customer.getId(), requestObject.getString("county"), requestObject.getString("city"),
            requestObject.getString("province"), requestObject.getString("proCategory"), requestObject.getString("salesType"),
            requestObject.getInteger("pageNo"), requestObject.getInteger("pageSize"));

        return JsonEntity.makeSuccessJsonEntity(result);
    }

    @RequestMapping("getSpuStock")
    @ResponseBody
    public JsonEntity getSpuStock(@RequestAttribute JSONObject requestObject, @RequestAttribute Customer customer) throws Exception {
        List<SkuStockModel> result = productService.getSpuStock(customer.getId(), requestObject.getLong("id"), requestObject.getString("queryDate"),
            requestObject.getString("salesType"));
        return JsonEntity.makeSuccessJsonEntity(result);
    }

    @RequestMapping("getSpuForOrder")
    @ResponseBody
    public JsonEntity getSpuForOrder(@RequestAttribute JSONObject requestObject, @RequestAttribute Customer customer) throws Exception {
        SpuProductModel result = productService.getSpuForOrder(customer.getId(), requestObject.getLong("id"), requestObject.getIntValue("idType"),
            requestObject.getString("salesType"));
        return JsonEntity.makeSuccessJsonEntity(result);
    }

    @RequestMapping("getCommSpuForOrder")
    @ResponseBody
    public JsonEntity getCommSpuForOrder(@RequestAttribute JSONObject requestObject, @RequestAttribute Customer customer) throws Exception {
        SpuProductModel result = productService.getSpuForOrder(customer.getId(), requestObject.getLong("id"), requestObject.getIntValue("idType"),
            requestObject.getString("salesType"));
        return JsonEntity.makeSuccessJsonEntity(result);
    }

    @RequestMapping("getCity")
    @ResponseBody
    public JsonEntity getCity(@RequestAttribute JSONObject requestObject, @RequestAttribute Customer customer) throws Exception {
        List<ProvinceModel> result = productService.getCity(customer.getId(), requestObject.getString("salesType"));
        Map<String, Object> map = new HashMap<>();
        map.put("citys", result);
        String hotProvinces = propertyLoader.getProperty("system", "hot.province.list", "");
        map.put("hot_province_list", Arrays.asList(hotProvinces.split(",")));
        return JsonEntity.makeSuccessJsonEntity(map);
    }

    @RequestMapping("search")
    @ResponseBody
    public JsonEntity search(@RequestAttribute JSONObject requestObject, @RequestAttribute Customer customer) throws Exception {
        //参数完整性校验
        ParamCheckService.checkParam(requestObject.getString("salesType"), "销售端口salesType不能为空");
        ParamCheckService.checkParam(requestObject.getString("productName"), "搜索关键字productName不能为空");
        List<SpuProductHeadModel> result = productService.search(customer.getId(), requestObject.getString("salesType"), requestObject.getString("productName"),
            requestObject.getString("province"), requestObject.getString("city"), requestObject.getString("county"));
        return JsonEntity.makeSuccessJsonEntity(result);
    }

    @RequestMapping("hotwords")
    @ResponseBody
    public JsonEntity findHotWords(@RequestAttribute JSONObject requestObject, @RequestAttribute Customer customer) throws Exception {
        return JsonEntity.makeSuccessJsonEntity(productService.getHotWords());
    }

    @RequestMapping("queryForAppIndex")
    @ResponseBody
    public JsonEntity queryForAppIndex(@RequestAttribute JSONObject requestObject, @RequestAttribute Customer customer) {
        JsonEntity json = new JsonEntity();
        return sceneService.queryForAppIndex(requestObject, customer, json);
    }
}
