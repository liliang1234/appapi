/**
 * piaozhijia.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package com.pzj.platform.appapi.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.pzj.address.entity.Address;
import com.pzj.contacts.entity.Contacts;
import com.pzj.product.vo.product.SkuFilled;
import com.pzj.product.vo.voParam.resultParam.SkuSaledCalendarResult;

/**
 * 产品组实体
 * @author pengliqing
 * @version $Id: SpuProductModel.java, v 0.1 2016年7月27日 下午5:03:49 pengliqing Exp $
 */
public class SpuProductModel extends SpuProductHeadModel implements Serializable {

    /**  */
    private static final long            serialVersionUID = 4892626270016364988L;

    private List<SkuProductModel>        skuProducts;
    private SkuCloseTimeSlotModel        closeTimes;
    //产品详情
    private String                       photoinfoId;
    /**
     * 游玩时间的开始时间的方式
     */
    private Integer                      playtimeMode;
    /**
     * 游玩时间的数量
     */
    private Integer                      playtimeValue;
    /**
     * 游玩时间的单位
     */
    private Integer                      playtimeUnit;
    /** 入园方式*/
    private String                       checkInType;
    /**产品可用开始日期 */
    private Date                         startDate;
    /** 产品可用结束日期*/
    private Date                         endDate;
    /**一证一票标识 */
    private Integer                      isUnique;

    private List<String>                 slideImageList;

    /**
     * 提前预订时间限制
     */
    private Integer                      isLimitAdvanceDue;
    /**
     * 日期前天数可预订
     */
    private Integer                      advanceDueDays;
    /**
     * 日期前时刻-（时）可预订
     */
    private Integer                      advanceDueHour;
    /**
     * 日期前时刻-（分）可预订
     */
    private Integer                      advanceDueMinute;
    /**
     * 延迟消费时间限制
     */
    private Integer                      isLimitDelayConsume;

    /**
     * 领票类型
     * <br/>
     * 请参考常量{@link GlobalDict.GainType}
     */
    private Integer                      gainType;

    /**
     * 领票人数限制，如果为-1表示不限制
     */
    private Integer                      gainPeopleNum;

    /**
     * 同取票人领票时间范围单位
     */
    private Integer                      gainPeopleTimeLimitUnit;

    /**
     * 同取票人领票时间范围数量
     */
    private Integer                      gainPeopleTimeLimitValue;

    /**
     * 同取票人领票时间范围值，如果为-1，则不限制
     */
    private Integer                      gainPeopleTimePurchaseNum;

    /**
     * 同订单最小起定量
     */
    private Integer                      minPurchaseNumInOrder;

    /** 下单是否填写游玩时间*/
    private Integer                      isNeedPlaytime;

    /** 不填写游玩时间下单计算时间类型*/
    private Integer                      noPlaytimeOrdertimeType;

    /** 游玩时间/下单后时间数值*/
    private Integer                      ordertimeValue;

    /** 游玩时间/下单后时间单位*/
    private Integer                      ordertimeUnit;

    /** 产品详情*/
    private String                       productInfoDetail;

    /** 一句话特色*/
    private String                       oneWordFeature;

    /** 预订须知*/
    private String                       orderInfo;

    /** 费用说明*/
    private String                       expenseInfo;

    /** 销售技巧*/
    private String                       salesmanship;

    /** 重要条款*/
    private String                       importantClause;

    /** 注意事项 */
    private String                       attentions;

    /** 使用方法*/
    private String                       useMethod;

    /**
     * 退款日期类型
     */
    private Integer                      refundDateType;
    /**
     * 退款日期前天数
     */
    private Integer                      prerefundDays;
    /**
     * 退款日期前当天时刻-(时）
     */
    private Integer                      prerefundHour;
    /**
     * 退款日期前当天时刻-（分）
     */
    private Integer                      prerefundMinute;
    /**
     * 退款日期前给分销商退款金额类型
     */
    private Integer                      prerefundDistributorFeetype;
    /**
     * 退款日期前给分销商退款金额数值
     */
    private String                       prerefundDistributorFeevalue;
    /**
     * 退款日期后天数
     */
    private Integer                      prorefundDays;
    /**
     * 退款日期后当天时刻-(时）
     */
    private Integer                      prorefundHour;
    /**
     * 退款日期后当天时刻-（分）
     */
    private Integer                      prorefundMinute;
    /**
     * 退款日期后给分销商退款金额类型
     */
    private Integer                      prorefundDistributorFeetype;
    /**
     * 退款日期后给分销商退款金额数值
     */
    private String                       prorefundDistributorFeevalue;

    /** 退款日期前是否可退
     * SkuProductGlobal.IS_NEED_REFUND  可退款
     * SkuProductGlobal.IS_NOT_NEED_REFUND  不可退款
     * 
     * */
    private Boolean                      isNeedPrerefund;

    /** 退款日期前退款数量类型
     * SkuProductGlobal.REFUND_QUANTITY_TYPE_ALL 整单退
     * SkuProductGlobal.REFUND_QUANTITY_TYPE_PART 部分退
     * */
    private Integer                      prerefundQuantityType;

    /** 退款日期后是否可退
     * SkuProductGlobal.IS_NEED_REFUND  可退款
     * SkuProductGlobal.IS_NOT_NEED_REFUND  不可退款
     * 
     * */
    private Boolean                      isNeedProrefund;

    /** 退款日期后退款数量类型
     * SkuProductGlobal.REFUND_QUANTITY_TYPE_ALL 整单退
     * SkuProductGlobal.REFUND_QUANTITY_TYPE_PART 部分退
     * */
    private Integer                      prorefundQuantityType;

    /**入园地址*/
    private String                       checkInAddress;
    /**入园开始时间*/
    private String                       startTime;
    /**入园结束时间*/
    private String                       endTime;
    /**产品介绍*/
    private String                       reeaseInfo;

    /** 产品销售开始日期*/
    private Date                         saleStartDate;

    /** 产品销售结束日期*/
    private Date                         saleEndDate;
    /** 默认收货、上车、下车地址*/
    private List<Address>                addressList;
    /** 默认联系人信息*/
    private Contacts                     contacts;
    /** 填单项信息*/
    private List<SkuFilled>              skuFilledList;
    /**是否为有效日期集合信息*/
    private List<SkuSaledCalendarResult> skuSaledCalendarList;

    /***备注默认文案*/
    private String                       defaultRemarks;

    /** 检票时间段:开始时间 */
    private String                       checkStartTime;

    /** 检票时间段:结束时间 */
    private String                       checkEndTime;
    /** 产品组所在城市*/
    private String                       city;

    /** 通用产品特色所用图片     */
    private String                       productInfoImg;
    /** 通用产品特色文字  */
    private String                       productInfoMsg;

    /**产品组所在 省*/
    private String                       province;

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public Contacts getContacts() {
        return contacts;
    }

    public void setContacts(Contacts contacts) {
        this.contacts = contacts;
    }

    public String getProductInfoImg() {
        return productInfoImg;
    }

    public void setProductInfoImg(String productInfoImg) {
        this.productInfoImg = productInfoImg;
    }

    public String getProductInfoMsg() {
        return productInfoMsg;
    }

    public void setProductInfoMsg(String productInfoMsg) {
        this.productInfoMsg = productInfoMsg;
    }

    public String getCheckStartTime() {
        return checkStartTime;
    }

    public void setCheckStartTime(String checkStartTime) {
        this.checkStartTime = checkStartTime;
    }

    public String getCheckEndTime() {
        return checkEndTime;
    }

    public void setCheckEndTime(String checkEndTime) {
        this.checkEndTime = checkEndTime;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public List<SkuFilled> getSkuFilledList() {
        return skuFilledList;
    }

    public void setSkuFilledList(List<SkuFilled> skuFilledList) {
        this.skuFilledList = skuFilledList;
    }

    public List<SkuSaledCalendarResult> getSkuSaledCalendarList() {
        return skuSaledCalendarList;
    }

    public void setSkuSaledCalendarList(List<SkuSaledCalendarResult> skuSaledCalendarList) {
        this.skuSaledCalendarList = skuSaledCalendarList;
    }

    public String getDefaultRemarks() {
        return defaultRemarks;
    }

    public void setDefaultRemarks(String defaultRemarks) {
        this.defaultRemarks = defaultRemarks;
    }

    public List<Address> getAddressList() {
        return addressList;
    }

    public void setAddressList(List<Address> addressList) {
        this.addressList = addressList;
    }

    public List<SkuProductModel> getSkuProducts() {
        return skuProducts;
    }

    public void setSkuProducts(List<SkuProductModel> skuProducts) {
        this.skuProducts = skuProducts;
    }

    public SkuCloseTimeSlotModel getCloseTimes() {
        return closeTimes;
    }

    public void setCloseTimes(SkuCloseTimeSlotModel closeTimes) {
        this.closeTimes = closeTimes;
    }

    public Integer getPlaytimeMode() {
        return playtimeMode;
    }

    public void setPlaytimeMode(Integer playtimeMode) {
        this.playtimeMode = playtimeMode;
    }

    public Integer getPlaytimeValue() {
        return playtimeValue;
    }

    public void setPlaytimeValue(Integer playtimeValue) {
        this.playtimeValue = playtimeValue;
    }

    public Integer getPlaytimeUnit() {
        return playtimeUnit;
    }

    public void setPlaytimeUnit(Integer playtimeUnit) {
        this.playtimeUnit = playtimeUnit;
    }

    public String getCheckInType() {
        return checkInType;
    }

    public void setCheckInType(String checkInType) {
        this.checkInType = checkInType;
    }

    public Integer getOrdertimeValue() {
        return ordertimeValue;
    }

    public void setOrdertimeValue(Integer ordertimeValue) {
        this.ordertimeValue = ordertimeValue;
    }

    public Integer getOrdertimeUnit() {
        return ordertimeUnit;
    }

    public void setOrdertimeUnit(Integer ordertimeUnit) {
        this.ordertimeUnit = ordertimeUnit;
    }

    public String getOrderInfo() {
        return orderInfo;
    }

    public void setOrderInfo(String orderInfo) {
        this.orderInfo = orderInfo;
    }

    public String getExpenseInfo() {
        return expenseInfo;
    }

    public void setExpenseInfo(String expenseInfo) {
        this.expenseInfo = expenseInfo;
    }

    public String getSalesmanship() {
        return salesmanship;
    }

    public void setSalesmanship(String salesmanship) {
        this.salesmanship = salesmanship;
    }

    public String getImportantClause() {
        return importantClause;
    }

    public void setImportantClause(String importantClause) {
        this.importantClause = importantClause;
    }

    public String getAttentions() {
        return attentions;
    }

    public void setAttentions(String attentions) {
        this.attentions = attentions;
    }

    public String getUseMethod() {
        return useMethod;
    }

    public void setUseMethod(String useMethod) {
        this.useMethod = useMethod;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    /**
     * Getter method for property <tt>isUnique</tt>.
     * 
     * @return property value of isUnique
     */
    public Integer getIsUnique() {
        return isUnique;
    }

    /**
     * Setter method for property <tt>isUnique</tt>.
     * 
     * @param isUnique value to be assigned to property isUnique
     */
    public void setIsUnique(Integer isUnique) {
        this.isUnique = isUnique;
    }

    /**
     * Getter method for property <tt>gainType</tt>.
     * 
     * @return property value of gainType
     */
    public Integer getGainType() {
        return gainType;
    }

    /**
     * Setter method for property <tt>gainType</tt>.
     * 
     * @param gainType value to be assigned to property gainType
     */
    public void setGainType(Integer gainType) {
        this.gainType = gainType;
    }

    public List<String> getSlideImageList() {
        return slideImageList;
    }

    public void setSlideImageList(List<String> slideImageList) {
        this.slideImageList = slideImageList;
    }

    public Integer getGainPeopleNum() {
        return gainPeopleNum;
    }

    public void setGainPeopleNum(Integer gainPeopleNum) {
        this.gainPeopleNum = gainPeopleNum;
    }

    public Integer getGainPeopleTimeLimitUnit() {
        return gainPeopleTimeLimitUnit;
    }

    public void setGainPeopleTimeLimitUnit(Integer gainPeopleTimeLimitUnit) {
        this.gainPeopleTimeLimitUnit = gainPeopleTimeLimitUnit;
    }

    public Integer getGainPeopleTimeLimitValue() {
        return gainPeopleTimeLimitValue;
    }

    public void setGainPeopleTimeLimitValue(Integer gainPeopleTimeLimitValue) {
        this.gainPeopleTimeLimitValue = gainPeopleTimeLimitValue;
    }

    public Integer getGainPeopleTimePurchaseNum() {
        return gainPeopleTimePurchaseNum;
    }

    public void setGainPeopleTimePurchaseNum(Integer gainPeopleTimePurchaseNum) {
        this.gainPeopleTimePurchaseNum = gainPeopleTimePurchaseNum;
    }

    public Integer getMinPurchaseNumInOrder() {
        return minPurchaseNumInOrder;
    }

    public void setMinPurchaseNumInOrder(Integer minPurchaseNumInOrder) {
        this.minPurchaseNumInOrder = minPurchaseNumInOrder;
    }

    public Integer getIsNeedPlaytime() {
        return isNeedPlaytime;
    }

    public void setIsNeedPlaytime(Integer isNeedPlaytime) {
        this.isNeedPlaytime = isNeedPlaytime;
    }

    public Integer getNoPlaytimeOrdertimeType() {
        return noPlaytimeOrdertimeType;
    }

    public void setNoPlaytimeOrdertimeType(Integer noPlaytimeOrdertimeType) {
        this.noPlaytimeOrdertimeType = noPlaytimeOrdertimeType;
    }

    public String getProductInfoDetail() {
        return productInfoDetail;
    }

    public void setProductInfoDetail(String productInfoDetail) {
        this.productInfoDetail = productInfoDetail;
    }

    @Override
    public String getOneWordFeature() {
        return oneWordFeature;
    }

    @Override
    public void setOneWordFeature(String oneWordFeature) {
        this.oneWordFeature = oneWordFeature;
    }

    public Integer getIsLimitAdvanceDue() {
        return isLimitAdvanceDue;
    }

    public void setIsLimitAdvanceDue(Integer isLimitAdvanceDue) {
        this.isLimitAdvanceDue = isLimitAdvanceDue;
    }

    public Integer getAdvanceDueDays() {
        return advanceDueDays;
    }

    public void setAdvanceDueDays(Integer advanceDueDays) {
        this.advanceDueDays = advanceDueDays;
    }

    public Integer getAdvanceDueHour() {
        return advanceDueHour;
    }

    public void setAdvanceDueHour(Integer advanceDueHour) {
        this.advanceDueHour = advanceDueHour;
    }

    public Integer getAdvanceDueMinute() {
        return advanceDueMinute;
    }

    public void setAdvanceDueMinute(Integer advanceDueMinute) {
        this.advanceDueMinute = advanceDueMinute;
    }

    public Integer getIsLimitDelayConsume() {
        return isLimitDelayConsume;
    }

    public void setIsLimitDelayConsume(Integer isLimitDelayConsume) {
        this.isLimitDelayConsume = isLimitDelayConsume;
    }

    /**
     * Getter method for property <tt>refundDateType</tt>.
     * 
     * @return property value of refundDateType
     */
    public Integer getRefundDateType() {
        return refundDateType;
    }

    /**
     * Setter method for property <tt>refundDateType</tt>.
     * 
     * @param refundDateType value to be assigned to property refundDateType
     */
    public void setRefundDateType(Integer refundDateType) {
        this.refundDateType = refundDateType;
    }

    /**
     * Getter method for property <tt>prerefundDays</tt>.
     * 
     * @return property value of prerefundDays
     */
    public Integer getPrerefundDays() {
        return prerefundDays;
    }

    /**
     * Setter method for property <tt>prerefundDays</tt>.
     * 
     * @param prerefundDays value to be assigned to property prerefundDays
     */
    public void setPrerefundDays(Integer prerefundDays) {
        this.prerefundDays = prerefundDays;
    }

    /**
     * Getter method for property <tt>prerefundHour</tt>.
     * 
     * @return property value of prerefundHour
     */
    public Integer getPrerefundHour() {
        return prerefundHour;
    }

    /**
     * Setter method for property <tt>prerefundHour</tt>.
     * 
     * @param prerefundHour value to be assigned to property prerefundHour
     */
    public void setPrerefundHour(Integer prerefundHour) {
        this.prerefundHour = prerefundHour;
    }

    /**
     * Getter method for property <tt>prerefundMinute</tt>.
     * 
     * @return property value of prerefundMinute
     */
    public Integer getPrerefundMinute() {
        return prerefundMinute;
    }

    /**
     * Setter method for property <tt>prerefundMinute</tt>.
     * 
     * @param prerefundMinute value to be assigned to property prerefundMinute
     */
    public void setPrerefundMinute(Integer prerefundMinute) {
        this.prerefundMinute = prerefundMinute;
    }

    /**
     * Getter method for property <tt>prerefundDistributorFeetype</tt>.
     * 
     * @return property value of prerefundDistributorFeetype
     */
    public Integer getPrerefundDistributorFeetype() {
        return prerefundDistributorFeetype;
    }

    /**
     * Setter method for property <tt>prerefundDistributorFeetype</tt>.
     * 
     * @param prerefundDistributorFeetype value to be assigned to property prerefundDistributorFeetype
     */
    public void setPrerefundDistributorFeetype(Integer prerefundDistributorFeetype) {
        this.prerefundDistributorFeetype = prerefundDistributorFeetype;
    }

    /**
     * Getter method for property <tt>prerefundDistributorFeevalue</tt>.
     * 
     * @return property value of prerefundDistributorFeevalue
     */
    public String getPrerefundDistributorFeevalue() {
        return prerefundDistributorFeevalue;
    }

    /**
     * Setter method for property <tt>prerefundDistributorFeevalue</tt>.
     * 
     * @param prerefundDistributorFeevalue value to be assigned to property prerefundDistributorFeevalue
     */
    public void setPrerefundDistributorFeevalue(String prerefundDistributorFeevalue) {
        this.prerefundDistributorFeevalue = prerefundDistributorFeevalue;
    }

    /**
     * Getter method for property <tt>prorefundDays</tt>.
     * 
     * @return property value of prorefundDays
     */
    public Integer getProrefundDays() {
        return prorefundDays;
    }

    /**
     * Setter method for property <tt>prorefundDays</tt>.
     * 
     * @param prorefundDays value to be assigned to property prorefundDays
     */
    public void setProrefundDays(Integer prorefundDays) {
        this.prorefundDays = prorefundDays;
    }

    /**
     * Getter method for property <tt>prorefundHour</tt>.
     * 
     * @return property value of prorefundHour
     */
    public Integer getProrefundHour() {
        return prorefundHour;
    }

    /**
     * Setter method for property <tt>prorefundHour</tt>.
     * 
     * @param prorefundHour value to be assigned to property prorefundHour
     */
    public void setProrefundHour(Integer prorefundHour) {
        this.prorefundHour = prorefundHour;
    }

    /**
     * Getter method for property <tt>prorefundMinute</tt>.
     * 
     * @return property value of prorefundMinute
     */
    public Integer getProrefundMinute() {
        return prorefundMinute;
    }

    /**
     * Setter method for property <tt>prorefundMinute</tt>.
     * 
     * @param prorefundMinute value to be assigned to property prorefundMinute
     */
    public void setProrefundMinute(Integer prorefundMinute) {
        this.prorefundMinute = prorefundMinute;
    }

    /**
     * Getter method for property <tt>prorefundDistributorFeetype</tt>.
     * 
     * @return property value of prorefundDistributorFeetype
     */
    public Integer getProrefundDistributorFeetype() {
        return prorefundDistributorFeetype;
    }

    /**
     * Setter method for property <tt>prorefundDistributorFeetype</tt>.
     * 
     * @param prorefundDistributorFeetype value to be assigned to property prorefundDistributorFeetype
     */
    public void setProrefundDistributorFeetype(Integer prorefundDistributorFeetype) {
        this.prorefundDistributorFeetype = prorefundDistributorFeetype;
    }

    /**
     * Getter method for property <tt>prorefundDistributorFeevalue</tt>.
     * 
     * @return property value of prorefundDistributorFeevalue
     */
    public String getProrefundDistributorFeevalue() {
        return prorefundDistributorFeevalue;
    }

    /**
     * Setter method for property <tt>prorefundDistributorFeevalue</tt>.
     * 
     * @param prorefundDistributorFeevalue value to be assigned to property prorefundDistributorFeevalue
     */
    public void setProrefundDistributorFeevalue(String prorefundDistributorFeevalue) {
        this.prorefundDistributorFeevalue = prorefundDistributorFeevalue;
    }

    /**
     * Getter method for property <tt>checkInAddress</tt>.
     * 
     * @return property value of checkInAddress
     */
    public String getCheckInAddress() {
        return checkInAddress;
    }

    /**
     * Setter method for property <tt>checkInAddress</tt>.
     * 
     * @param checkInAddress value to be assigned to property checkInAddress
     */
    public void setCheckInAddress(String checkInAddress) {
        this.checkInAddress = checkInAddress;
    }

    /**
     * Getter method for property <tt>startTime</tt>.
     * 
     * @return property value of startTime
     */
    public String getStartTime() {
        return startTime;
    }

    /**
     * Setter method for property <tt>startTime</tt>.
     * 
     * @param startTime value to be assigned to property startTime
     */
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    /**
     * Getter method for property <tt>endTime</tt>.
     * 
     * @return property value of endTime
     */
    public String getEndTime() {
        return endTime;
    }

    /**
     * Setter method for property <tt>endTime</tt>.
     * 
     * @param endTime value to be assigned to property endTime
     */
    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    /**
     * Getter method for property <tt>photoinfoId</tt>.
     * 
     * @return property value of photoinfoId
     */
    public String getPhotoinfoId() {
        return photoinfoId;
    }

    /**
     * Setter method for property <tt>photoinfoId</tt>.
     * 
     * @param photoinfoId value to be assigned to property photoinfoId
     */
    public void setPhotoinfoId(String photoinfoId) {
        this.photoinfoId = photoinfoId;
    }

    /**
     * Getter method for property <tt>reeaseInfo</tt>.
     * 
     * @return property value of reeaseInfo
     */
    public String getReeaseInfo() {
        return reeaseInfo;
    }

    /**
     * Setter method for property <tt>reeaseInfo</tt>.
     * 
     * @param reeaseInfo value to be assigned to property reeaseInfo
     */
    public void setReeaseInfo(String reeaseInfo) {
        this.reeaseInfo = reeaseInfo;
    }

    /**
     * Getter method for property <tt>isNeedPrerefund</tt>.
     * 
     * @return property value of isNeedPrerefund
     */
    public Boolean getIsNeedPrerefund() {
        return isNeedPrerefund;
    }

    /**
     * Setter method for property <tt>isNeedPrerefund</tt>.
     * 
     * @param isNeedPrerefund value to be assigned to property isNeedPrerefund
     */
    public void setIsNeedPrerefund(Boolean isNeedPrerefund) {
        this.isNeedPrerefund = isNeedPrerefund;
    }

    /**
     * Getter method for property <tt>prerefundQuantityType</tt>.
     * 
     * @return property value of prerefundQuantityType
     */
    public Integer getPrerefundQuantityType() {
        return prerefundQuantityType;
    }

    /**
     * Setter method for property <tt>prerefundQuantityType</tt>.
     * 
     * @param prerefundQuantityType value to be assigned to property prerefundQuantityType
     */
    public void setPrerefundQuantityType(Integer prerefundQuantityType) {
        this.prerefundQuantityType = prerefundQuantityType;
    }

    /**
     * Getter method for property <tt>isNeedProrefund</tt>.
     * 
     * @return property value of isNeedProrefund
     */
    public Boolean getIsNeedProrefund() {
        return isNeedProrefund;
    }

    /**
     * Setter method for property <tt>isNeedProrefund</tt>.
     * 
     * @param isNeedProrefund value to be assigned to property isNeedProrefund
     */
    public void setIsNeedProrefund(Boolean isNeedProrefund) {
        this.isNeedProrefund = isNeedProrefund;
    }

    /**
     * Getter method for property <tt>prorefundQuantityType</tt>.
     * 
     * @return property value of prorefundQuantityType
     */
    public Integer getProrefundQuantityType() {
        return prorefundQuantityType;
    }

    /**
     * Setter method for property <tt>prorefundQuantityType</tt>.
     * 
     * @param prorefundQuantityType value to be assigned to property prorefundQuantityType
     */
    public void setProrefundQuantityType(Integer prorefundQuantityType) {
        this.prorefundQuantityType = prorefundQuantityType;
    }

    /**
     * Getter method for property <tt>saleStartDate</tt>.
     * 
     * @return property value of saleStartDate
     */
    public Date getSaleStartDate() {
        return saleStartDate;
    }

    /**
     * Setter method for property <tt>saleStartDate</tt>.
     * 
     * @param saleStartDate value to be assigned to property saleStartDate
     */
    public void setSaleStartDate(Date saleStartDate) {
        this.saleStartDate = saleStartDate;
    }

    /**
     * Getter method for property <tt>saleEndDate</tt>.
     * 
     * @return property value of saleEndDate
     */
    public Date getSaleEndDate() {
        return saleEndDate;
    }

    /**
     * Setter method for property <tt>saleEndDate</tt>.
     * 
     * @param saleEndDate value to be assigned to property saleEndDate
     */
    public void setSaleEndDate(Date saleEndDate) {
        this.saleEndDate = saleEndDate;
    }

}
