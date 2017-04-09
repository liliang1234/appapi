package com.pzj.platform.appapi.service;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.pzj.common.test.BaseTest;
import com.pzj.customer.entity.Customer;
import com.pzj.framework.entity.QueryResult;
import com.pzj.trade.order.entity.OrderDetailResponse;
import com.pzj.trade.order.entity.OrderListResponse;

public class OrderServiceTest extends BaseTest {

    @Autowired
    OrderService      orderService;
    @Autowired
    OrderMerchService orderMerchService;

    @Test
    public void testQueryOrdersByReseller() throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("currentPage", 1);
        jsonObject.put("pageSize", 10);
        Customer customer = new Customer();
        customer.setId(2216619736567063l);
        //customerId = 2216619736567063
        QueryResult<OrderListResponse> result = orderService.queryOrdersByReseller(jsonObject, customer);
        System.out.println("result --> " + JSON.toJSONString(result));
        System.out.println(1);
    }

    @Test
    public void testQueryOrderDetailByReseller() throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("orderId", "MF1000003440");
        OrderDetailResponse result = orderService.queryOrderDetailByReseller(jsonObject, null);
        System.out.println(JSON.toJSONString(result));
        System.out.println(1);
    }

}
