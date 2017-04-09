/**
 * piaozhijia.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package com.pzj.platform.appapi.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pzj.base.common.global.product.GlobalPorduct;
import com.pzj.product.vo.product.SkuProduct;
import com.pzj.trade.order.entity.MerchResponse;

/**
 * 
 * @author pengliqing
 * @version $Id: OrderMerchModel.java, v 0.1 2016年8月1日 上午10:11:57 pengliqing Exp $
 */
public class OrderMerchModel extends MerchResponse {

    /**
     * 
     */
    private static final long  serialVersionUID = 1L;
    private Boolean            isTicketProduct;                                 //是否票
    private String             productName;                                     //产品名称
    private String             popularName;                                     //备注名称，对应productInfo的name
    /** 参数名称 */
    private String             skuParamsName;
    /** 票品 团散 */
    private String             ticketVarie;
    private String             proCategory;                                     //产品类别\n1：普通票;5：普通联票子票;12：积分票联票子票;13：演艺联票子票;4：积分票;9：房型;10：剧场（演艺票）;11：组合票
    private Boolean            isOneVote;                                       //是否一证一票
    private String             releaseThurl;                                    //缩略图

    private String             theaterArea;                                     // 剧场区域 
    private String             theatercnum;                                     //剧场场次

    private int                discountType;                                    //优惠类型：1. 前返; 2: 后返*/
    private double             discountAmount;                                  //优惠金额

    //Voucher
    /** 凭证有效开始时间 */
    private Date               startTime;
    /** 演艺开始时间 */
    private Date               showStartTime;
    /** 产品线(景区、演艺、住宿、特产、小交通、线路)  参照VoucherCategory 枚举对象*/
    private Integer            voucherCategory;
    /** 核销凭证列表 */
    private List<VoucherModel> vouchers         = new ArrayList<VoucherModel>(); //

    private boolean            confirm;
    /** 是否允许退票 */
    @JsonIgnore
    private Boolean            refund;
    /** 
     * 退换票时间类型 
     *  1不限,2游玩日期当日,3游玩日期前,4游玩日期后,5开演时间前,6演出结束前 
     * @see GlobalPorduct.TicketRuleType
     * */
    @JsonIgnore
    private Integer            timeType;
    /** 时限相关 */
    /** 天 */
    @JsonIgnore
    private Integer            days;
    /** 小时 */
    @JsonIgnore
    private Double             hours;
    /** 分钟 */
    @JsonIgnore
    private Integer            minutes;

    private transient String   areaDesc;                                        //区域描述
    private transient long     supplierId;
    //产品票型
    private String             productTypes;

    private String             useMethod;                                       //使用说明

    private SkuProduct         skuProduct;                                      //产品组信息

    private String             ownerPhone;                                      //店主电话

    private String             ticketsStartTime;                                //检票开始时间

    private String             ticketsEndTime;                                  //检票结束时间

    private String             checkInAddress;                                  //取票地址

    private String             checkInType;                                     //检票类型

    public String getCheckInType() {
        return checkInType;
    }

    public void setCheckInType(String checkInType) {
        this.checkInType = checkInType;
    }

    public String getTicketsStartTime() {
        return ticketsStartTime;
    }

    public void setTicketsStartTime(String ticketsStartTime) {
        this.ticketsStartTime = ticketsStartTime;
    }

    public String getTicketsEndTime() {
        return ticketsEndTime;
    }

    public void setTicketsEndTime(String ticketsEndTime) {
        this.ticketsEndTime = ticketsEndTime;
    }

    public String getCheckInAddress() {
        return checkInAddress;
    }

    public void setCheckInAddress(String checkInAddress) {
        this.checkInAddress = checkInAddress;
    }

    public String getOwnerPhone() {
        return ownerPhone;
    }

    public void setOwnerPhone(String ownerPhone) {
        this.ownerPhone = ownerPhone;
    }

    public SkuProduct getSkuProduct() {
        return skuProduct;
    }

    public void setSkuProduct(SkuProduct skuProduct) {
        this.skuProduct = skuProduct;
    }

    public String getUseMethod() {
        return useMethod;
    }

    public void setUseMethod(String useMethod) {
        this.useMethod = useMethod;
    }

    public Boolean getIsTicketProduct() {
        return isTicketProduct;
    }

    public void setIsTicketProduct(Boolean isTicketProduct) {
        this.isTicketProduct = isTicketProduct;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getPopularName() {
        return popularName;
    }

    public void setPopularName(String popularName) {
        this.popularName = popularName;
    }

    public String getSkuParamsName() {
        return skuParamsName;
    }

    public void setSkuParamsName(String skuParamsName) {
        this.skuParamsName = skuParamsName;
    }

    public String getTicketVarie() {
        return ticketVarie;
    }

    public void setTicketVarie(String ticketVarie) {
        this.ticketVarie = ticketVarie;
    }

    public String getProCategory() {
        return proCategory;
    }

    public void setProCategory(String proCategory) {
        this.proCategory = proCategory;
    }

    public Boolean getIsOneVote() {
        return isOneVote;
    }

    public void setIsOneVote(Boolean isOneVote) {
        this.isOneVote = isOneVote;
    }

    public String getReleaseThurl() {
        return releaseThurl;
    }

    public void setReleaseThurl(String releaseThurl) {
        this.releaseThurl = releaseThurl;
    }

    public String getTheaterArea() {
        return theaterArea;
    }

    public void setTheaterArea(String theaterArea) {
        this.theaterArea = theaterArea;
    }

    public String getTheatercnum() {
        return theatercnum;
    }

    public void setTheatercnum(String theatercnum) {
        this.theatercnum = theatercnum;
    }

    public int getDiscountType() {
        return discountType;
    }

    public void setDiscountType(int discountType) {
        this.discountType = discountType;
    }

    public double getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(double discountAmount) {
        this.discountAmount = discountAmount;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getShowStartTime() {
        return showStartTime;
    }

    public void setShowStartTime(Date showStartTime) {
        this.showStartTime = showStartTime;
    }

    public Integer getVoucherCategory() {
        return voucherCategory;
    }

    public void setVoucherCategory(Integer voucherCategory) {
        this.voucherCategory = voucherCategory;
    }

    public List<VoucherModel> getVouchers() {
        return vouchers;
    }

    public void setVouchers(List<VoucherModel> vouchers) {
        this.vouchers = vouchers;
    }

    public boolean isConfirm() {
        return confirm;
    }

    public void setConfirm(boolean confirm) {
        this.confirm = confirm;
    }

    public Boolean getRefund() {
        return refund;
    }

    public void setRefund(Boolean refund) {
        this.refund = refund;
    }

    public Integer getTimeType() {
        return timeType;
    }

    public void setTimeType(Integer timeType) {
        this.timeType = timeType;
    }

    public Integer getDays() {
        return days;
    }

    public void setDays(Integer days) {
        this.days = days;
    }

    public Double getHours() {
        return hours;
    }

    public void setHours(Double hours) {
        this.hours = hours;
    }

    public Integer getMinutes() {
        return minutes;
    }

    public void setMinutes(Integer minutes) {
        this.minutes = minutes;
    }

    public String getAreaDesc() {
        return areaDesc;
    }

    public void setAreaDesc(String areaDesc) {
        this.areaDesc = areaDesc;
    }

    public long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(long supplierId) {
        this.supplierId = supplierId;
    }

    public String getProductTypes() {
        return productTypes;
    }

    public void setProductTypes(String productTypes) {
        this.productTypes = productTypes;
    }

}
