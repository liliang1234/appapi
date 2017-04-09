/**
 * piaozhijia.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package com.pzj.platform.appapi.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.pzj.address.entity.Address;
import com.pzj.customer.entity.Customer;
import com.pzj.modules.appapi.entity.JsonEntity;
import com.pzj.platform.appapi.service.CommonAddressService;
import com.pzj.platform.appapi.service.ParamCheckService;

/**
 * 
 * @author liupeng
 * @version $Id: AddressController.java, v 0.1 2016年7月26日 下午2:28:22 fanggang Exp $
 */
@Controller
@RequestMapping("address")
public class AddressController {
    @Autowired
    private CommonAddressService commonAddressService;

    /**
     * 创建单个地址
     * 
     * @return
     */
    @RequestMapping("createAddress")
    @ResponseBody
    public JsonEntity createAddress(@RequestAttribute JSONObject requestObject) {
        ParamCheckService.checkParam(requestObject.getString("province"), "省不能为空");
        ParamCheckService.checkParam(requestObject.getString("city"), "市不能为空");
        ParamCheckService.checkParam(requestObject.getString("address"), "详细地址不能为空");
        ParamCheckService.checkParam(requestObject.getLong("supplierId"), "所属供应商ID不能为空");
        ParamCheckService.checkParam(requestObject.getLong("createBy"), "创建人ID不能为空");
        ParamCheckService.checkParam(requestObject.getString("dataSource"), "所属平台不能为空");
        ParamCheckService.checkParam(requestObject.getInteger("type"), "类型不能为空");

        Address address = JSON.parseObject(requestObject.toString(), Address.class);
        return JsonEntity.makeSuccessJsonEntity(commonAddressService.createAddress(address));
    }

    /**
     * 删除地址
     */
    @RequestMapping("deleteAddress")
    @ResponseBody
    public JsonEntity deleteAddress(@RequestAttribute JSONObject requestObject) {
        ParamCheckService.checkParam(requestObject.getString("addressIds"), "地址主键不能为空"); // 当前页码
        String addressIds = requestObject.getString("addressIds");
        String[] aids = addressIds.split(",");
        List<Long> addressIdList = new ArrayList<Long>();
        for (String addressId : aids) {
            addressIdList.add(Long.valueOf(addressId.toString()));
        }
        return JsonEntity.makeSuccessJsonEntity(commonAddressService.deleteAddress(addressIdList));
    }

    /**
     * 编辑完成,保存联系人信息
     */
    @RequestMapping("updateAddress")
    @ResponseBody
    public JsonEntity updateAddress(@RequestAttribute JSONObject requestObject) {

        ParamCheckService.checkParam(requestObject.getLong("id"), "主键不能为空");
        ParamCheckService.checkParam(requestObject.getLong("updateBy"), "更新人ID不能为空");
        Address address = JSON.parseObject(requestObject.toString(), Address.class);
        return JsonEntity.makeSuccessJsonEntity(commonAddressService.updateAddress(address));
    }

    /**
     * 编辑联系人时,查询
     * 
     * @param requestObject
     * @param customer
     * @return
     */
    @RequestMapping("editAddress")
    @ResponseBody
    public JsonEntity editAddress(@RequestAttribute JSONObject requestObject) {
        ParamCheckService.checkParam(requestObject.getLong("addressId"), "主键不能为空");
        Long addressId = requestObject.getLong("addressId");
        return JsonEntity.makeSuccessJsonEntity(commonAddressService.editAddressById(addressId));
    }

    /**
     * 分页查询联系人
     * 
     * @param requestObject
     * @param customer
     * @return
     */
    @RequestMapping("queryAddressPage")
    @ResponseBody
    public JsonEntity queryAddressPage(@RequestAttribute JSONObject requestObject, @RequestAttribute Customer customer) {
        ParamCheckService.checkParam(requestObject.getString("pageNO"), "页码不能为空"); // 当前页码
        ParamCheckService.checkParam(requestObject.getString("pageSize"), "每页大小不能为空"); // 每页记录数
        ParamCheckService.checkParam(requestObject.getInteger("type"), "地址类型不能为空");
        return JsonEntity.makeSuccessJsonEntity(commonAddressService.queryAddressPage(customer, requestObject.getString("pageNO"),
            requestObject.getString("pageSize"), requestObject.getInteger("type")));
    }

    /**
     * 设置默认联系人
     * 
     * @param requestObject
     * @param id
     * @return
     */
    @RequestMapping("asDefault")
    @ResponseBody
    public JsonEntity asDefault(@RequestAttribute JSONObject requestObject) {
        ParamCheckService.checkParam(requestObject.getLong("addressId"), "主键不能为空");
        Long addressId = requestObject.getLong("addressId");
        return JsonEntity.makeSuccessJsonEntity(commonAddressService.asDefault(addressId));
    }
}
