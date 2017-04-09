/**
 * piaozhijia.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package com.pzj.platform.appapi.model;

import java.io.Serializable;
import java.util.List;

/**
 * 产品实体
 * @author Mark
 * @version $Id: SkuProductModel.java, v 0.1 2016年7月27日 下午5:04:02 pengliqing Exp $
 */
public class SkuProductModel implements Serializable {

    /**  */
    private static final long         serialVersionUID = 1L;
    private Long                      id;
    private String                    name;
    /**库存*/
    private SkuStockModel             stockModel;
    /** 参数名称 */
    private String                    skuParamsName;
    /**
     * 门市价
     */
    private String                    marketPrice;

    /**
     * 建议零售价
     */
    private String                    advicePrice;

    /** 购买时限：计时单位（1 小时，2 日） */
    private Integer                   expireMode;

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

    /**每单最少购买份数*/
    private Integer                   leastPerdueNumber;

    /**每单最多购买份数*/
    private Integer                   mostPerdueNumber;

    /** 适用范围、可多选(1周一 2周二 3周三 4周四 5周五 6周六 7周日) */
    private String                    scope;

    /** 产品类别1，普通 ，2，剧场 3，定向返利产品 ，4，普通票联票子票5，积分票联票子票 */
    private String                    proCategory;

    /** 是否二次确认 1是，0否 
     *  是：SkuProductGlobal.IS_TWICE_SURE
     *  否：SkuProductGlobal.IS_NOT_TWICE_SURE
     * 
     * */
    private String                    twiceSure;

    /** 剧场区域 */
    private String                    region;

    /** 剧场场次 */

    private String                    ronda;

    /** 渠道id */
    private Long                      channelId;

    /** 政策id */
    private Long                      strategrId;

    /**景区id*/
    private Long                      sceneId;

    /** 描述 */
    private String                    remarks;

    /** 供应商 */
    private String                    supplierId;

    /**是否选座*/
    private boolean                   flag;

    /**是否一证一票*/
    private int                       isUnique;

    /** 是否是无限库存 
     * 是：SkuProductGlobal.IS_UNLIMITED_INVENTORY
     * 否：SkuProductGlobal.IS_NOT_UNLIMITED_INVENTORY
     * */
    private Boolean                   unlimitedInventory;

    /**产品Id，政策，渠道*/
    private List<StrategyResultModel> strategyResults;

    /** 库存规则id */
    private Long                      stockRuleId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSkuParamsName() {
        return skuParamsName;
    }

    public void setSkuParamsName(String skuParamsName) {
        this.skuParamsName = skuParamsName;
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

    public Integer getExpireMode() {
        return expireMode;
    }

    public void setExpireMode(Integer expireMode) {
        this.expireMode = expireMode;
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

    /**
     * Getter method for property <tt>leastPerdueNumber</tt>.
     * 
     * @return property value of leastPerdueNumber
     */
    public Integer getLeastPerdueNumber() {
        return leastPerdueNumber;
    }

    /**
     * Setter method for property <tt>leastPerdueNumber</tt>.
     * 
     * @param leastPerdueNumber value to be assigned to property leastPerdueNumber
     */
    public void setLeastPerdueNumber(Integer leastPerdueNumber) {
        this.leastPerdueNumber = leastPerdueNumber;
    }

    /**
     * Getter method for property <tt>mostPerdueNumber</tt>.
     * 
     * @return property value of mostPerdueNumber
     */
    public Integer getMostPerdueNumber() {
        return mostPerdueNumber;
    }

    /**
     * Setter method for property <tt>mostPerdueNumber</tt>.
     * 
     * @param mostPerdueNumber value to be assigned to property mostPerdueNumber
     */
    public void setMostPerdueNumber(Integer mostPerdueNumber) {
        this.mostPerdueNumber = mostPerdueNumber;
    }

    /**
     * Getter method for property <tt>stockModel</tt>.
     * 
     * @return property value of stockModel
     */
    public SkuStockModel getStockModel() {
        return stockModel;
    }

    /**
     * Setter method for property <tt>stockModel</tt>.
     * 
     * @param stockModel value to be assigned to property stockModel
     */
    public void setStockModel(SkuStockModel stockModel) {
        this.stockModel = stockModel;
    }

    /**
     * Getter method for property <tt>proCategory</tt>.
     * 
     * @return property value of proCategory
     */
    public String getProCategory() {
        return proCategory;
    }

    /**
     * Setter method for property <tt>proCategory</tt>.
     * 
     * @param proCategory value to be assigned to property proCategory
     */
    public void setProCategory(String proCategory) {
        this.proCategory = proCategory;
    }

    /**
     * Getter method for property <tt>twiceSure</tt>.
     * 
     * @return property value of twiceSure
     */
    public String getTwiceSure() {
        return twiceSure;
    }

    /**
     * Setter method for property <tt>twiceSure</tt>.
     * 
     * @param twiceSure value to be assigned to property twiceSure
     */
    public void setTwiceSure(String twiceSure) {
        this.twiceSure = twiceSure;
    }

    /**
     * Getter method for property <tt>region</tt>.
     * 
     * @return property value of region
     */
    public String getRegion() {
        return region;
    }

    /**
     * Setter method for property <tt>region</tt>.
     * 
     * @param region value to be assigned to property region
     */
    public void setRegion(String region) {
        this.region = region;
    }

    /**
     * Getter method for property <tt>ronda</tt>.
     * 
     * @return property value of ronda
     */
    public String getRonda() {
        return ronda;
    }

    /**
     * Setter method for property <tt>ronda</tt>.
     * 
     * @param ronda value to be assigned to property ronda
     */
    public void setRonda(String ronda) {
        this.ronda = ronda;
    }

    /**
     * Getter method for property <tt>channelId</tt>.
     * 
     * @return property value of channelId
     */
    public Long getChannelId() {
        return channelId;
    }

    /**
     * Setter method for property <tt>channelId</tt>.
     * 
     * @param channelId value to be assigned to property channelId
     */
    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    /**
     * Getter method for property <tt>strategrId</tt>.
     * 
     * @return property value of strategrId
     */
    public Long getStrategrId() {
        return strategrId;
    }

    /**
     * Setter method for property <tt>strategrId</tt>.
     * 
     * @param strategrId value to be assigned to property strategrId
     */
    public void setStrategrId(Long strategrId) {
        this.strategrId = strategrId;
    }

    /**
     * Getter method for property <tt>sceneId</tt>.
     * 
     * @return property value of sceneId
     */
    public Long getSceneId() {
        return sceneId;
    }

    /**
     * Setter method for property <tt>sceneId</tt>.
     * 
     * @param sceneId value to be assigned to property sceneId
     */
    public void setSceneId(Long sceneId) {
        this.sceneId = sceneId;
    }

    /**
     * Getter method for property <tt>remarks</tt>.
     * 
     * @return property value of remarks
     */
    public String getRemarks() {
        return remarks;
    }

    /**
     * Setter method for property <tt>remarks</tt>.
     * 
     * @param remarks value to be assigned to property remarks
     */
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    /**
     * Getter method for property <tt>supplierId</tt>.
     * 
     * @return property value of supplierId
     */
    public String getSupplierId() {
        return supplierId;
    }

    /**
     * Setter method for property <tt>supplierId</tt>.
     * 
     * @param supplierId value to be assigned to property supplierId
     */
    public void setSupplierId(String supplierId) {
        this.supplierId = supplierId;
    }

    /**
     * Getter method for property <tt>flag</tt>.
     * 
     * @return property value of flag
     */
    public boolean isFlag() {
        return flag;
    }

    /**
     * Setter method for property <tt>flag</tt>.
     * 
     * @param flag value to be assigned to property flag
     */
    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    /**
     * Getter method for property <tt>strategyResults</tt>.
     * 
     * @return property value of strategyResults
     */
    public List<StrategyResultModel> getStrategyResults() {
        return strategyResults;
    }

    /**
     * Setter method for property <tt>strategyResults</tt>.
     * 
     * @param strategyResults value to be assigned to property strategyResults
     */
    public void setStrategyResults(List<StrategyResultModel> strategyResults) {
        this.strategyResults = strategyResults;
    }

    /**
     * Getter method for property <tt>isUnique</tt>.
     * 
     * @return property value of isUnique
     */
    public int getIsUnique() {
        return isUnique;
    }

    /**
     * Setter method for property <tt>isUnique</tt>.
     * 
     * @param isUnique value to be assigned to property isUnique
     */
    public void setIsUnique(int isUnique) {
        this.isUnique = isUnique;
    }

    /**
     * Getter method for property <tt>unlimitedInventory</tt>.
     * 
     * @return property value of unlimitedInventory
     */
    public Boolean getUnlimitedInventory() {
        return unlimitedInventory;
    }

    /**
     * Setter method for property <tt>unlimitedInventory</tt>.
     * 
     * @param unlimitedInventory value to be assigned to property unlimitedInventory
     */
    public void setUnlimitedInventory(Boolean unlimitedInventory) {
        this.unlimitedInventory = unlimitedInventory;
    }

    /**
     * Getter method for property <tt>stockRuleId</tt>.
     * 
     * @return property value of stockRuleId
     */
    public Long getStockRuleId() {
        return stockRuleId;
    }

    /**
     * Setter method for property <tt>stockRuleId</tt>.
     * 
     * @param stockRuleId value to be assigned to property stockRuleId
     */
    public void setStockRuleId(Long stockRuleId) {
        this.stockRuleId = stockRuleId;
    }

    /**
     * Getter method for property <tt>scope</tt>.
     * 
     * @return property value of scope
     */
    public String getScope() {
        return scope;
    }

    /**
     * Setter method for property <tt>scope</tt>.
     * 
     * @param scope value to be assigned to property scope
     */
    public void setScope(String scope) {
        this.scope = scope;
    }
}
