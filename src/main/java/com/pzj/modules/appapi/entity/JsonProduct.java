package com.pzj.modules.appapi.entity;

import java.util.ArrayList;
import java.util.List;

public class JsonProduct {

    private List<Object> productReleases = new ArrayList<Object>();
    private String       productInfoId;
    private String       playtimeValue;
    private String       playtimeUnit;
    private String       playtimeMode;
    private String       remarks;
    private String       startTime;
    private String       endTime;
    private String       hourNode;
    private String       minutesNode;
    private String       productInfoName;
    private String       gainType;
    private String       isOunterSign;

    /*最低零售价 多少元起*/
    private String minPrice;
    /*产品市价  取minPrice相对的*/
    private String minMarketPrice;

    public List<Object> getProductReleases() {
        return productReleases;
    }

    public void setProductReleases(List<Object> productReleases) {
        this.productReleases = productReleases;
    }

    public String getProductInfoId() {
        return productInfoId;
    }

    public void setProductInfoId(String productInfoId) {
        this.productInfoId = productInfoId;
    }

    public String getPlaytimeValue() {
        return playtimeValue;
    }

    public void setPlaytimeValue(String playtimeValue) {
        this.playtimeValue = playtimeValue;
    }

    public String getPlaytimeUnit() {
        return playtimeUnit;
    }

    public void setPlaytimeUnit(String playtimeUnit) {
        this.playtimeUnit = playtimeUnit;
    }

    public String getPlaytimeMode() {
        return playtimeMode;
    }

    public void setPlaytimeMode(String playtimeMode) {
        this.playtimeMode = playtimeMode;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getHourNode() {
        return hourNode;
    }

    public void setHourNode(String hourNode) {
        this.hourNode = hourNode;
    }

    public String getMinutesNode() {
        return minutesNode;
    }

    public void setMinutesNode(String minutesNode) {
        this.minutesNode = minutesNode;
    }

    public String getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(String minPrice) {
        this.minPrice = minPrice;
    }

    public String getMinMarketPrice() {
        return minMarketPrice;
    }

    public void setMinMarketPrice(String minMarketPrice) {
        this.minMarketPrice = minMarketPrice;
    }

    public String getProductInfoName() {
        return productInfoName;
    }

    public void setProductInfoName(String productInfoName) {
        this.productInfoName = productInfoName;
    }

    public String getGainType() {
        return gainType;
    }

    public void setGainType(String gainType) {
        this.gainType = gainType;
    }

    public String getIsOunterSign() {
        return isOunterSign;
    }

    public void setIsOunterSign(String isOunterSign) {
        this.isOunterSign = isOunterSign;
    }

}
