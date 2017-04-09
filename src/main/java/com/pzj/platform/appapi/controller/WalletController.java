/**
 * piaozhijia.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package com.pzj.platform.appapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.pzj.customer.entity.Customer;
import com.pzj.framework.entity.QueryResult;
import com.pzj.modules.appapi.entity.JsonEntity;
import com.pzj.platform.appapi.model.SimpleSellChecking;
import com.pzj.platform.appapi.model.SummarySellChecking;
import com.pzj.platform.appapi.service.ParamCheckService;
import com.pzj.platform.appapi.service.WalletService;

/**
 * 
 * @author fanggang
 * @version $Id: WalletController.java, v 0.1 2016年7月26日 下午2:28:22 fanggang Exp $
 */
@Controller
@RequestMapping("wallet")
public class WalletController {

    @Autowired
    private WalletService walletService;

    @RequestMapping("myCheckings")
    @ResponseBody
    public JsonEntity findMyCheckingList(@RequestAttribute JSONObject requestObject, @RequestAttribute Customer customer) {
        //参数完整性校验
        ParamCheckService.checkParam(requestObject.getString("pageNO"), "页码不能为空"); // 当前页码
        ParamCheckService.checkParam(requestObject.getString("pageSize"), "每页大小不能为空"); // 每页记录数
        // status: 1 已创建（财务创建） 2 对账中（财务发送对账单） 3 已对账（分销商确认） 4 退回（财务撤销） 5 取消（分销商拒绝） 6 已确认(财务已审核)
        QueryResult<SimpleSellChecking> simpleSellCheckings = walletService.findCheckings(customer, requestObject.getInteger("status"),
            requestObject.getInteger("pageNO"), requestObject.getInteger("pageSize"));
        return JsonEntity.makeSuccessJsonEntity(simpleSellCheckings);
    }

    @RequestMapping("checkingDetail")
    @ResponseBody
    public JsonEntity findCheckingDetail(@RequestAttribute JSONObject requestObject, @RequestAttribute Customer customer) {
        //参数完整性校验
        ParamCheckService.checkParam(requestObject.getString("id"), "id不能为空");
        SummarySellChecking ssc = walletService.findCheckingDetail(requestObject.getLong("id"), customer);
        return JsonEntity.makeSuccessJsonEntity(ssc);
    }

    @RequestMapping("checkingConfirm")
    @ResponseBody
    public JsonEntity checkingConfirm(@RequestAttribute JSONObject requestObject) {
        //参数完整性校验
        ParamCheckService.checkParam(requestObject.getString("id"), "id不能为空");
        walletService.confirmChecking(requestObject.getLong("id"));
        return JsonEntity.makeSuccessJsonEntity(null);
    }

    @RequestMapping("checkingRefuse")
    @ResponseBody
    public JsonEntity checkingRefuse(@RequestAttribute JSONObject requestObject) {
        //参数完整性校验
        ParamCheckService.checkParam(requestObject.getString("id"), "id不能为空");
        walletService.refuseChecking(requestObject.getLong("id"));
        return JsonEntity.makeSuccessJsonEntity(null);
    }

    @RequestMapping("accountBalance")
    @ResponseBody
    public JsonEntity accountBalance(@RequestAttribute JSONObject requestObject, @RequestAttribute Customer customer) {
        return JsonEntity.makeSuccessJsonEntity(walletService.findAccountBalance(customer));
    }

    @RequestMapping("monthlySales")
    @ResponseBody
    public JsonEntity monthlySales(@RequestAttribute JSONObject requestObject, @RequestAttribute Customer customer) {
        return JsonEntity.makeSuccessJsonEntity(walletService.findMonthlySales(customer));
    }

    @RequestMapping("oftenlyPurchased")
    @ResponseBody
    public JsonEntity oftenlyPurchased(@RequestAttribute JSONObject requestObject, @RequestAttribute Customer customer) {
        //参数完整性校验
        ParamCheckService.checkParam(requestObject.getString("pageNO"), "页码不能为空"); // 当前页码
        ParamCheckService.checkParam(requestObject.getString("pageSize"), "每页大小不能为空"); // 每页记录数
        return JsonEntity.makeSuccessJsonEntity(walletService.findOftenlyPurchased(customer, requestObject.getInteger("pageNO"),
            requestObject.getInteger("pageSize")));
    }

    /**
     *  提现前查询接口
     */
    @RequestMapping("queryWithdrawals")
    @ResponseBody
    public JsonEntity queryWithdrawals(@RequestAttribute JSONObject requestObject, @RequestAttribute Customer customer) {
        return JsonEntity.makeSuccessJsonEntity(walletService.queryWithdrawalsByCustomer(customer));
    }

    /**
     * 提现历史查询接口
     */
    @RequestMapping("queryWithdrawalsHistory")
    @ResponseBody
    public JsonEntity queryWithdrawalsHistory(@RequestAttribute JSONObject requestObject, @RequestAttribute Customer customer) {

        ParamCheckService.checkParam(requestObject.getString("pageNO"), "页码不能为空"); // 当前页码
        ParamCheckService.checkParam(requestObject.getString("pageSize"), "每页大小不能为空"); // 每页记录数
        return JsonEntity.makeSuccessJsonEntity(walletService.queryWithdrawalsHistory(customer, requestObject.getInteger("pageNO"),
            requestObject.getInteger("pageSize")));
    }

    /**
     *  提现接口 
     */
    @RequestMapping("withdrawals")
    @ResponseBody
    public JsonEntity withdrawals(@RequestAttribute JSONObject requestObject, @RequestAttribute Customer customer) {
        ParamCheckService.checkParam(requestObject.getString("withdrawalsPrice"), "页码不能为空"); // 当前页码
        String withdrawalsPrice = requestObject.getString("withdrawalsPrice");
        Integer withdrawalsType = requestObject.getInteger("withdrawalsType");
        Integer isWithdrawalsFirst = requestObject.getInteger("isWithdrawalsFirst");
        return JsonEntity.makeSuccessJsonEntity(walletService.withdrawals(customer, withdrawalsPrice, withdrawalsType, isWithdrawalsFirst));
    }
}
