/**
 * piaozhijia.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package com.pzj.platform.appapi.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.pzj.base.common.global.UserGlobalDict;
import com.pzj.customer.entity.Customer;
import com.pzj.framework.context.Result;
import com.pzj.framework.entity.QueryResult;
import com.pzj.modules.appapi.exception.AppapiParametersException;
import com.pzj.platform.appapi.dubbo.OrderApi;
import com.pzj.platform.appapi.model.OrderDetailResponseModel;
import com.pzj.platform.appapi.model.OrderMerchValueResult;
import com.pzj.platform.appapi.model.OrderModel;
import com.pzj.platform.appapi.model.PaymentDetailModel;
import com.pzj.platform.appapi.service.CustomerService;
import com.pzj.platform.appapi.service.OrderMerchService;
import com.pzj.platform.appapi.service.OrderService;
import com.pzj.platform.appapi.service.ParamCheckService;
import com.pzj.settlement.balance.response.ReceiptOrderDetailResponse;
import com.pzj.settlement.balance.service.ReceiptOrderDetailService;
import com.pzj.trade.order.entity.MerchResponse;
import com.pzj.trade.order.entity.OrderDetailResponse;
import com.pzj.trade.order.entity.OrderListResponse;
import com.pzj.trade.order.model.DeliveryDetailModel;
import com.pzj.trade.order.vo.ResellerOrderListVO;

/**
 * 
 * @author Mark
 * @version $Id: OrderServiceImpl.java, v 0.1 2016年8月1日 上午9:47:50 pengliqing Exp $
 */
@Service("orderServiceApp")
public class OrderServiceImpl implements OrderService {

    private static final Logger       logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    @Autowired
    private OrderApi                  orderApi;
    @Autowired
    private CustomerService           customerService;
    @Autowired
    private OrderMerchService         orderMerchService;
    @Autowired
    private ReceiptOrderDetailService orderDetailService;

    /**
    * 查询订单列表（分销端）. 分销端订单列表的主体为订单.
    * @param data
    * @param customer
    * @return
    */
    @Override
    public QueryResult<OrderListResponse> queryOrdersByReseller(JSONObject data, Customer customer) {
        ResellerOrderListVO resellerOrderListVO = new ResellerOrderListVO();
        //参数完整性校验
        ParamCheckService.checkParam(data.get("currentPage"), "当前页数必填");
        ParamCheckService.checkParam(data.get("pageSize"), "每页记录数必填");

        resellerOrderListVO.setCurrent_page(data.getIntValue("currentPage"));
        resellerOrderListVO.setPage_size(data.getIntValue("pageSize"));
        resellerOrderListVO.setOperator_id(customer.getId());
        if (data.containsKey("category") && StringUtils.isNotEmpty(data.getString("category"))) {
            resellerOrderListVO.setCategory(data.getIntValue("category")); // 订单(商品)类型
        }
        if (data.containsKey("orderState") && StringUtils.isNotEmpty(data.getString("orderState"))) {
            resellerOrderListVO.setOrder_status(data.getIntValue("orderState")); // 订单状态［1: 待付款,10: 已支付,20: 已取消,30: 已退款,40: 已完成,50: 已确认］
        }

        Result<QueryResult<OrderListResponse>> responseResult = orderApi.queryOrdersByReseller(resellerOrderListVO);
        return wrapperOrderListResponse(customer, responseResult);
    }

    /**
     * 查询订单列表（分销端）. 分销端订单列表的主体为订单.
     * @param data
     * @param customer
     * @return
     */
    @Override
    public QueryResult<OrderListResponse> queryOrdersByPhoneNum(JSONObject data) {
        ResellerOrderListVO resellerOrderListVO = new ResellerOrderListVO();
        //参数完整性校验
        ParamCheckService.checkParam(data.get("currentPage"), "当前页数必填");
        ParamCheckService.checkParam(data.get("pageSize"), "每页记录数必填");
        ParamCheckService.checkParam(data.get("contactMobile"), "联系人手机号必填");

        resellerOrderListVO.setCurrent_page(data.getIntValue("currentPage"));
        resellerOrderListVO.setPage_size(data.getIntValue("pageSize"));
        resellerOrderListVO.setContact_mobile(data.getString("contactMobile"));
        if (data.containsKey("category") && StringUtils.isNotEmpty(data.getString("category"))) {
            resellerOrderListVO.setCategory(data.getIntValue("category")); // 订单(商品)类型
        }
        if (data.containsKey("orderState") && StringUtils.isNotEmpty(data.getString("orderState"))) {
            resellerOrderListVO.setOrder_status(data.getIntValue("orderState")); // 订单状态［1: 待付款,10: 已支付,20: 已取消,30: 已退款,40: 已完成,50: 已确认］
        }

        Result<QueryResult<OrderListResponse>> responseResult = orderApi.queryOrdersByReseller(resellerOrderListVO);
        return wrapperOrderListResponseByPhone(data.getString("contactMobile"), responseResult);
    }

    /**
     * 组装订单列表返回值对象（分销端）.
     * @param monitorEntity
     * @param responseResult 后台服务接口返回对象
     */
    private QueryResult<OrderListResponse> wrapperOrderListResponse(Customer customer, Result<QueryResult<OrderListResponse>> responseResult) {

        if (!responseResult.isOk() || responseResult.getData() == null || responseResult.getData().getRecords() == null) {
            if (!responseResult.isOk()) {
                logger.error("查询订单列表（分销端）失败, uid: {}, loginName: {}", customer.getId(), customer.getLoginName());
            }
            if (responseResult.getData() == null) {
                QueryResult<OrderListResponse> data = new QueryResult<>(1, 1);
                responseResult.setData(data);
            }
            responseResult.getData().setRecords(new ArrayList<OrderListResponse>());
            return responseResult.getData();
        }

        Map<String, OrderMerchService> process = new HashMap<>();
        process.put("product", orderMerchService);
        process.put("strategy", orderMerchService);
        for (int i = 0; i < responseResult.getData().getRecords().size(); i++) {
            OrderListResponse orderListResponse = responseResult.getData().getRecords().get(i);
            OrderModel order = new OrderModel();
            BeanUtils.copyProperties(orderListResponse, order);

            OrderMerchValueResult orderMerchValueResult = orderMerchService.convert2MerchResponseVo(orderListResponse.getMerchs(), process);
            if (!UserGlobalDict.guideUserType().equals(customer.getResellerType()))
                order.setDiscountAmount2(orderMerchValueResult.getDiscountAmount2());
            order.getMerchs().clear();
            order.getMerchs().addAll(orderMerchValueResult.getMerchResponseVo());
            responseResult.getData().getRecords().set(i, order);
        }

        return responseResult.getData();
    }

    private QueryResult<OrderListResponse> wrapperOrderListResponseByPhone(String phone, Result<QueryResult<OrderListResponse>> responseResult) {

        if (!responseResult.isOk() || responseResult.getData() == null || responseResult.getData().getRecords() == null) {
            if (!responseResult.isOk()) {
                logger.error("查询订单列表（分销端）失败, phone: {}", phone);
            }
            if (responseResult.getData() == null) {
                QueryResult<OrderListResponse> data = new QueryResult<>(1, 1);
                responseResult.setData(data);
            }
            responseResult.getData().setRecords(new ArrayList<OrderListResponse>());
            return responseResult.getData();
        }

        Map<String, OrderMerchService> process = new HashMap<>();
        process.put("product", orderMerchService);
        process.put("strategy", orderMerchService);
        for (int i = 0; i < responseResult.getData().getRecords().size(); i++) {
            OrderListResponse orderListResponse = responseResult.getData().getRecords().get(i);
            OrderModel order = new OrderModel();
            BeanUtils.copyProperties(orderListResponse, order);

            OrderMerchValueResult orderMerchValueResult = orderMerchService.convert2MerchResponseVo(orderListResponse.getMerchs(), process);
            order.getMerchs().clear();
            order.getMerchs().addAll(orderMerchValueResult.getMerchResponseVo());
            responseResult.getData().getRecords().set(i, order);
        }

        return responseResult.getData();
    }

    /**
     * 查询订单详情（分销端）.
     *
     * @param data
     * @param resellerType
     * @param jsonEntity 值对象
     * @return
     */
    @Override
    public OrderDetailResponse queryOrderDetailByReseller(JSONObject data, String resellerType) throws AppapiParametersException {
        //参数完整性校验
        ParamCheckService.checkParam(data.get("orderId"), "订单ID不能为空");
        Result<OrderDetailResponse> responseResult = orderApi.queryOrderDetail(data.getString("orderId"));

        if (!responseResult.isOk()) {
            logger.error("查询订单详情（分销端）失败, orderId: {}", data.get("orderId"));
            return null;
        }

        Result<? extends OrderDetailResponse> wrapResult = wrapperOrderDetailResponse(resellerType, responseResult);
        if (wrapResult.getData().getDelivery_info() == null) {
            DeliveryDetailModel delivery_info = new DeliveryDetailModel();
            wrapResult.getData().setDelivery_info(delivery_info);
        }
        return wrapResult.getData();
    }

    /**
    * 组装订单详情返回值对象（分销端）.
    *
    * @param responseResult 后台服务接口返回对象
    */
    private Result<? extends OrderDetailResponse> wrapperOrderDetailResponse(String resellerType, Result<OrderDetailResponse> responseResult) {
        if (responseResult.getData() == null)
            return responseResult;

        Result<OrderDetailResponseModel> vo = new Result<>();
        OrderDetailResponseModel orderDetailVo = new OrderDetailResponseModel();
        vo.setData(orderDetailVo);

        BeanUtils.copyProperties(responseResult.getData(), orderDetailVo);

        if (orderDetailVo.getTravel() != 0)
            orderDetailVo.setTravelName(getCustomerName(orderDetailVo.getTravel()));//组装旅行社名称
        if (orderDetailVo.getTravel_depart_id() != 0)
            orderDetailVo.setTravelDepartName(getCustomerName(orderDetailVo.getTravel_depart_id()));//组装旅行社部门名称
        Customer customer = customerService.getCustomerById(orderDetailVo.getReseller_id());
        if (responseResult.getData().getMerchs() != null) {
            Map<String, OrderMerchService> process = new HashMap<>();//由于代码结构不是面向接口编程，泛型定义为具体实现类
            process.put("product", orderMerchService);
            process.put("strategy", orderMerchService);
            process.put("voucher", orderMerchService);
            process.put("refundFlow", orderMerchService);
            OrderMerchValueResult orderMerchValueResult = orderMerchService.convert2MerchResponseVo(responseResult.getData().getMerchs(), process);
            if (orderMerchValueResult.getMerchResponseVo().get(0) != null) {
                orderMerchValueResult.getMerchResponseVo().get(0).setOwnerPhone(customer.getCorporaterMobile());
            }
            if (!UserGlobalDict.guideUserType().equals(resellerType))
                orderDetailVo.setDiscountAmount2(orderMerchValueResult.getDiscountAmount2());
            orderDetailVo.setMerchs(new ArrayList<MerchResponse>());
            orderDetailVo.getMerchs().addAll(orderMerchValueResult.getMerchResponseVo());
        }
        return vo;
    }

    private String getCustomerName(long customerId) {
        Customer customer = customerService.getCustomerById(customerId);
        return customer != null ? customer.getName() : null;
    }

    /** 
     * @see com.pzj.platform.appapi.service.OrderService#paymentDetails(com.alibaba.fastjson.JSONObject)
     */
    @Override
    public PaymentDetailModel paymentDetails(String orderId) {
        PaymentDetailModel detailModel = new PaymentDetailModel();
        Result<ReceiptOrderDetailResponse> orderDetail = orderDetailService.queryReceiveBillsOrderDetail(orderId);
        ReceiptOrderDetailResponse data = orderDetail.getData();
        detailModel.setOrderAmount(data.getOrderAmount());
        detailModel.setAppRebatePay(data.getAppRebatePay());
        detailModel.setWeShopRebatePay(data.getWeShopRebatePay());
        detailModel.setOtherPay(data.getOtherPay());
        detailModel.setOtherPayType(data.getOtherPayType());
        return detailModel;
    }
}
