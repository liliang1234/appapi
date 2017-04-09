package com.pzj.platform.appapi.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.pzj.customer.entity.Customer;
import com.pzj.modules.appapi.api.CustumerService;
import com.pzj.modules.appapi.seat.model.SeatArea;
import com.pzj.platform.appapi.service.AppSeatService;

@Controller
@RequestMapping("screen")
public class ScreenSeatController {
    private static final Logger logger = LoggerFactory.getLogger(ScreenSeatController.class);

    @Autowired
    private AppSeatService        seatService;
    @Autowired
    private CustumerService     custumerService;

    @RequestMapping("product")
    @ResponseBody
    public SeatArea product(@RequestAttribute JSONObject requestObject) {

        String token = requestObject.getString("token");
        Long rid = requestObject.getLong("rid");
        Customer customer = new Customer();
        if (!"".equals(token) && token != null) {
            customer = custumerService.queryCustomer(token);
        } else {
            customer = custumerService.getCustomerById(rid);
        }
        SeatArea seatArea2 = new SeatArea();
        String showStartTime = requestObject.getString("showStartTime");
        String seatArea = requestObject.getString("seatArea");
        Long scenicId = requestObject.getLong("scenicId");
        String sort = requestObject.getString("sort");
        String productId = requestObject.getString("productId");
        if (customer != null) {
            seatArea2 = seatService.getSeatArea(showStartTime, seatArea, scenicId, customer, sort, productId);
        }
        return seatArea2;
    }
}
