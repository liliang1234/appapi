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
import com.pzj.modules.appapi.api.order.OrderCreateService;
import com.pzj.modules.appapi.entity.JsonEntity;
import com.pzj.platform.appapi.model.PaymentDetailModel;
import com.pzj.platform.appapi.service.OrderService;
import com.pzj.platform.appapi.service.ParamCheckService;
import com.pzj.trade.order.entity.OrderDetailResponse;
import com.pzj.trade.order.entity.OrderListResponse;

/**
 * 
 * @author Mark
 * @version $Id: OrderController.java, v 0.1 2016年7月4日 上午11:32:23 pengliqing Exp $
 */

@Controller
@RequestMapping("order")
public class OrderController {

    @Autowired
    private OrderService       orderService;
    @Autowired
    private OrderCreateService orderCreateService;

    @RequestMapping("getOrdersByReseller")
    @ResponseBody
    public JsonEntity queryOrdersByReseller(@RequestAttribute JSONObject requestObject, @RequestAttribute Customer customer) {
        //查询订单列表（分销端）
        QueryResult<OrderListResponse> result = orderService.queryOrdersByReseller(requestObject, customer);
        return JsonEntity.makeSuccessJsonEntity(result);
    }

    @RequestMapping("getOrderDetailByReseller")
    @ResponseBody
    public JsonEntity queryOrderDetailByReseller(@RequestAttribute JSONObject requestObject, @RequestAttribute Customer customer) {
        //查询订单详情（分销端）
        OrderDetailResponse result = orderService.queryOrderDetailByReseller(requestObject, customer.getResellerType());
        return JsonEntity.makeSuccessJsonEntity(result);
    }

    @RequestMapping("findOrdersByPhoneNum")
    @ResponseBody
    public JsonEntity findOrdersByPhoneNum(@RequestAttribute JSONObject requestObject) {
        //查询订单列表（游客）
        QueryResult<OrderListResponse> result = orderService.queryOrdersByPhoneNum(requestObject);
        return JsonEntity.makeSuccessJsonEntity(result);
    }

    @RequestMapping("findOrderDetailByPhoneNum")
    @ResponseBody
    public JsonEntity findOrderDetailByPhoneNum(@RequestAttribute JSONObject requestObject) {
        //查询订单详情（游客）
        OrderDetailResponse result = orderService.queryOrderDetailByReseller(requestObject, "");
        return JsonEntity.makeSuccessJsonEntity(result);
    }

    @RequestMapping("saveOrder")
    @ResponseBody
    public JsonEntity saveOrder(@RequestAttribute JSONObject requestObject, @RequestAttribute Customer customer) {
        //生成订单
        JsonEntity json = orderCreateService.saveOrder(requestObject, customer);
        json.setResponseTime(String.valueOf(System.currentTimeMillis()));
        return json;
    }

    /**
     * @api {http} http://{服务器ip地址或域名}/order/checkOrderRelevant 下单页下一步验证
     * @apiName 下单页下一步验证
     * @apiGroup APP1
     * @apiVersion 1.0.0
     * @apiDescription 提供给app前端和微店访问的下单前验证
     * @apiParam {Integer } salesType 销售渠道微店必须传递(7),app可以不传递
     * @apiParam {Timestamp} buyDate 购买时间，如不传递后端自动计算默认时间
     * @apiParam {Long} productInfoId 产品组id
     * @apiParam {Long} productId 产品id，可购买多个产品
     * @apiParam {Integer} productNum 购买的数量，每个产品id对应一个数量
     * @apiParam {Timestamp} showTime 演出时间 可不填
     * @apiParam {String} seat 座位  可不填
     * @apiSuccess {String} String 返回验证结果
     * @throws Exception
     */
    @RequestMapping("checkOrderRelevant")
    @ResponseBody
    public JsonEntity checkOrderRelevant(@RequestAttribute JSONObject requestObject, @RequestAttribute Customer customer) throws Exception {
        //下单前验证
        JsonEntity json = orderCreateService.checkOrderRelevant(requestObject, customer);
        json.setResponseTime(String.valueOf(System.currentTimeMillis()));
        return json;
    }
    @RequestMapping("paymentDetails")
    @ResponseBody
    public JsonEntity paymentDetails(@RequestAttribute JSONObject requestObject, @RequestAttribute Customer customer) throws Exception {
        //下单前验证
        ParamCheckService.checkParam(requestObject.getString("orderId"), "订单号不能为空");
        String orderId = requestObject.getString("orderId");
        PaymentDetailModel paymentDetails = orderService.paymentDetails(orderId);
        return JsonEntity.makeSuccessJsonEntity(paymentDetails);
    }
}
