/**
 * piaozhijia.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package com.pzj.platform.appapi.model;

import java.io.Serializable;
import java.util.List;

/**
 * 
 * @author daixuewei
 * @version $Id: StrategyModel.java, v 0.1 2016年9月8日 下午3:48:48 daixuewei Exp $
 */
public class StrategyModel implements Serializable {
    /**
     * 
     */
    private static final long         serialVersionUID = 8017127538570288760L;

    private Long                      id;

    /** 渠道政策类型1直销2分销 */
    private String                    type;

    /** 政策名称 */
    private String                    name;

    /** 渠道价 */
    private String                    price;

    /** 结算价 */
    private String                    settlementPrice;

    /** 销售端口（type：value，type=） */
    private String                    salesType;

    /** 购买时限：个数 */
    private Integer                   expire;

    /** 购买时限：计时单位（1 小时，2 日） */
    private Integer                   expireMode;

    /** 适用范围、可多选(1周一 2周二 3周三 4周四 5周五 6周六 7周日) */
    private String                    scope;

    /**
     * pms独有属性:返利比
     */
    private Double                    rebateRate;

    /**
     * 未满减结算规则类型
     * 
     */
    private Integer                   notTotalSettlementType;

    /**
     * 减少结算金额
     */
    private String                    reduceSettlementMoney;

    /**
     * 门市价
     */
    private String                    marketPrice;

    /**
     * 建议零售价
     */
    private String                    advicePrice;

    /**
     * 上级政策主键Id
     */
    private Long                      parentId;

    /**
     * 返利规则
     */
    private List<RebateStrategyModel> rebateStrategyList;

    /**
     * 提前预订时间限制
     */
    private Integer                   isLimitAdvanceDue;
    /**
     * 日期前天数可预订
     */
    private Integer                   advanceDueDays;
    /**
     * 日期前时刻-（时）可预订
     */
    private Integer                   advanceDueHour;
    /**
     * 日期前时刻-（分）可预订
     */
    private Integer                   advanceDueMinute;
    /**
     * 延迟消费时间限制
     */
    private Integer                   isLimitDelayConsume;
    /**
     * 预订时刻-（时）可兑换
     */
    private Integer                   delayConsumeHour;
    /**
     * 每单最少购买份数
     */
    private Integer                   leastPerdueNumber;
    /**
     * 每单最多购买份数
     */
    private Integer                   mostPerdueNumber;

    /**
     * 日期前可预订单位
     */
    private Integer                   advanceDueUnit;

    /**
     * 产品id
     */
    private Long                      productId;

    /**
     * 获取渠道政策类型1直销2分销
     * 
     * @return type 渠道政策类型1直销2分销
     */
    public String getType() {
        return type;
    }

    /**
     * 设置渠道政策类型1直销2分销
     * 
     * @param type
     *            渠道政策类型1直销2分销
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * 获取政策名称
     * 
     * @return name 政策名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置政策名称
     * 
     * @param name
     *            政策名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取销售端口（type：value，type=）
     * 
     * @return salesType 销售端口（type：value，type=）
     */
    public String getSalesType() {
        return salesType;
    }

    /**
     * 设置销售端口（type：value，type=）
     * 
     * @param salesType
     *            销售端口（type：value，type=）
     */
    public void setSalesType(String salesType) {
        this.salesType = salesType;
    }

    /**
     * 获取购买时限：个数
     * 
     * @return expire 购买时限：个数
     */
    public Integer getExpire() {
        return expire;
    }

    /**
     * 设置购买时限：个数
     * 
     * @param expire
     *            购买时限：个数
     */
    public void setExpire(Integer expire) {
        this.expire = expire;
    }

    /**
     * 获取购买时限：计时单位（1小时，2日）
     * 
     * @return expireMode 购买时限：计时单位（1小时，2日）
     */
    public Integer getExpireMode() {
        return expireMode;
    }

    /**
     * 设置购买时限：计时单位（1小时，2日）
     * 
     * @param expireMode
     *            购买时限：计时单位（1小时，2日）
     */
    public void setExpireMode(Integer expireMode) {
        this.expireMode = expireMode;
    }

    /**
     * 获取适用范围、可多选(1周一2周二3周三4周四5周五6周六7周日)
     * 
     * @return scope 适用范围、可多选(1周一2周二3周三4周四5周五6周六7周日)
     */
    public String getScope() {
        return scope;
    }

    /**
     * 设置适用范围、可多选(1周一2周二3周三4周四5周五6周六7周日)
     * 
     * @param scope
     *            适用范围、可多选(1周一2周二3周三4周四5周五6周六7周日)
     */
    public void setScope(String scope) {
        this.scope = scope;
    }

    /**
     * 获取pms独有属性:返利比
     * 
     * @return rebateRate pms独有属性:返利比
     */
    public Double getRebateRate() {
        return rebateRate;
    }

    /**
     * 设置pms独有属性:返利比
     * 
     * @param rebateRate
     *            pms独有属性:返利比
     */
    public void setRebateRate(Double rebateRate) {
        this.rebateRate = rebateRate;
    }

    /**
     * 获取未满减结算规则类型
     * 
     * @return notTotalSettlementType 未满减结算规则类型
     */
    public Integer getNotTotalSettlementType() {
        return notTotalSettlementType;
    }

    /**
     * 设置未满减结算规则类型
     * 
     * @param notTotalSettlementType
     *            未满减结算规则类型
     */
    public void setNotTotalSettlementType(Integer notTotalSettlementType) {
        this.notTotalSettlementType = notTotalSettlementType;
    }

    /**
     * 获取减少结算金额
     * 
     * @return reduceSettlementMoney 减少结算金额
     */
    public String getReduceSettlementMoney() {
        return reduceSettlementMoney;
    }

    /**
     * 设置减少结算金额
     * 
     * @param reduceSettlementMoney
     *            减少结算金额
     */
    public void setReduceSettlementMoney(String reduceSettlementMoney) {
        this.reduceSettlementMoney = reduceSettlementMoney;
    }

    /**
     * 获取上级政策主键Id
     * 
     * @return parentId 上级政策主键Id
     */
    public Long getParentId() {
        return parentId;
    }

    /**
     * 设置上级政策主键Id
     * 
     * @param parentId
     *            上级政策主键Id
     */
    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public List<RebateStrategyModel> getRebateStrategyList() {
        return rebateStrategyList;
    }

    public void setRebateStrategyList(List<RebateStrategyModel> rebateStrategyList) {
        this.rebateStrategyList = rebateStrategyList;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMarketPrice() {
        return marketPrice;
    }

    public void setMarketPrice(String marketPrice) {
        this.marketPrice = marketPrice;
    }

    public String getAdvicePrice() {
        return advicePrice;
    }

    public void setAdvicePrice(String advicePrice) {
        this.advicePrice = advicePrice;
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

    public Integer getDelayConsumeHour() {
        return delayConsumeHour;
    }

    public void setDelayConsumeHour(Integer delayConsumeHour) {
        this.delayConsumeHour = delayConsumeHour;
    }

    public Integer getLeastPerdueNumber() {
        return leastPerdueNumber;
    }

    public void setLeastPerdueNumber(Integer leastPerdueNumber) {
        this.leastPerdueNumber = leastPerdueNumber;
    }

    public Integer getMostPerdueNumber() {
        return mostPerdueNumber;
    }

    public void setMostPerdueNumber(Integer mostPerdueNumber) {
        this.mostPerdueNumber = mostPerdueNumber;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Integer getAdvanceDueUnit() {
        return advanceDueUnit;
    }

    public void setAdvanceDueUnit(Integer advanceDueUnit) {
        this.advanceDueUnit = advanceDueUnit;
    }

    /**
     * Getter method for property <tt>price</tt>.
     * 
     * @return property value of price
     */
    public String getPrice() {
        return price;
    }

    /**
     * Setter method for property <tt>price</tt>.
     * 
     * @param price value to be assigned to property price
     */
    public void setPrice(String price) {
        this.price = price;
    }

    /**
     * Getter method for property <tt>settlementPrice</tt>.
     * 
     * @return property value of settlementPrice
     */
    public String getSettlementPrice() {
        return settlementPrice;
    }

    /**
     * Setter method for property <tt>settlementPrice</tt>.
     * 
     * @param settlementPrice value to be assigned to property settlementPrice
     */
    public void setSettlementPrice(String settlementPrice) {
        this.settlementPrice = settlementPrice;
    }
}
