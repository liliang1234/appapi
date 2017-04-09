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
import com.pzj.contacts.entity.Contacts;
import com.pzj.customer.entity.Customer;
import com.pzj.modules.appapi.entity.JsonEntity;
import com.pzj.platform.appapi.service.ContactService;
import com.pzj.platform.appapi.service.ParamCheckService;

/**
 * 
 * @author liupeng
 * @version $Id: WalletController.java, v 0.1 2016年7月26日 下午2:28:22 fanggang Exp $
 */
@Controller
@RequestMapping("contact")
public class ContactsController {
    @Autowired
    private ContactService contactService;

    /**
     * 创建联系人接口
     * 
     * @return
     */
    @RequestMapping("createContact")
    @ResponseBody
    public JsonEntity createContact(@RequestAttribute JSONObject requestObject) {
        ParamCheckService.checkParam(requestObject.getLong("createBy"), "创建人ID不能为空");
        ParamCheckService.checkParam(requestObject.getString("idNumber"), "身份证不能为空");
        ParamCheckService.checkParam(requestObject.getString("name"), "名字不能为空");
        ParamCheckService.checkParam(requestObject.getString("supplierId"), "所属供应商ID不能为空");
        ParamCheckService.checkParam(requestObject.getString("phoneNumber"), "联系人电话不能为空");
        ParamCheckService.checkParam(requestObject.getString("dataSource"), "所属平台不能为空");

        Contacts contacts = JSON.parseObject(requestObject.toString(), Contacts.class);
        return JsonEntity.makeSuccessJsonEntity(contactService.createContact(contacts));
    }

    /**
     * 删除联系人
     */
    @RequestMapping("deleteContact")
    @ResponseBody
    public JsonEntity deleteContact(@RequestAttribute JSONObject requestObject) {
        ParamCheckService.checkParam(requestObject.getString("contactIds"), "联系人主键不能为空"); // 当前页码
        String contactIds = requestObject.getString("contactIds");
        String[] cids = contactIds.split(",");
        List<Long> contactIdList = new ArrayList<Long>();
        for (String contactId : cids) {
            contactIdList.add(Long.valueOf(contactId));
        }
        return JsonEntity.makeSuccessJsonEntity(contactService.deleteContact(contactIdList));
    }

    /**
     * 编辑完成,保存联系人信息
     */
    @RequestMapping("updateContact")
    @ResponseBody
    public JsonEntity updateContact(@RequestAttribute JSONObject requestObject) {

        ParamCheckService.checkParam(requestObject.getLong("id"), "主键ID不能为空");

        Contacts contacts = JSON.parseObject(requestObject.toString(), Contacts.class);
        return JsonEntity.makeSuccessJsonEntity(contactService.updateContact(contacts));
    }

    /**
     * 编辑联系人时,查询
     * 
     * @param requestObject
     * @param customer
     * @return
     */
    @RequestMapping("editContacts")
    @ResponseBody
    public JsonEntity editContacts(@RequestAttribute JSONObject requestObject) {
        ParamCheckService.checkParam(requestObject.getLong("contactId"), "主键不能为空");
        Long contactId = requestObject.getLong("contactId");
        return JsonEntity.makeSuccessJsonEntity(contactService.editContactsById(contactId));
    }

    /**
     * 分页查询联系人
     * 
     * @param requestObject
     * @param customer
     * @return
     */
    @RequestMapping("queryContactsPage")
    @ResponseBody
    public JsonEntity queryContactsPage(@RequestAttribute JSONObject requestObject, @RequestAttribute Customer customer) {
        ParamCheckService.checkParam(requestObject.getString("pageNO"), "页码不能为空"); // 当前页码
        ParamCheckService.checkParam(requestObject.getString("pageSize"), "每页大小不能为空"); // 每页记录数
        return JsonEntity.makeSuccessJsonEntity(contactService.queryContactsPage(customer, requestObject.getString("pageNO"),
            requestObject.getString("pageSize")));
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
        ParamCheckService.checkParam(requestObject.getLong("contactId"), "主键不能为空");
        Long contactId = requestObject.getLong("contactId");
        return JsonEntity.makeSuccessJsonEntity(contactService.asDefault(contactId));
    }
}
