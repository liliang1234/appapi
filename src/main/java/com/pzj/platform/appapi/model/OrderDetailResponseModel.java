package com.pzj.platform.appapi.model;

import com.pzj.trade.order.entity.OrderDetailResponse;

public class OrderDetailResponseModel extends OrderDetailResponse {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private String            travelName;
    private String            travelDepartName;
    private double            discountAmount2;      //后返金额

    public String getTravelName() {
        return travelName;
    }

    public void setTravelName(String travelName) {
        this.travelName = travelName;
    }

    public String getTravelDepartName() {
        return travelDepartName;
    }

    public void setTravelDepartName(String travelDepartName) {
        this.travelDepartName = travelDepartName;
    }

    public double getDiscountAmount2() {
        return discountAmount2;
    }

    public void setDiscountAmount2(double discountAmount2) {
        this.discountAmount2 = discountAmount2;
    }
}
