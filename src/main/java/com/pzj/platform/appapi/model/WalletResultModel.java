package com.pzj.platform.appapi.model;

import java.io.Serializable;

public class WalletResultModel implements Serializable{

	private Long resellerId;  //分销商ID 
	
	private Double appIncoming; //卖游翁收入
	private Double weshopIncoming; //微店收入
	private Double appFrozenIncoming; // 卖油翁冻结金额
	private Double weshopFrozenIncoming;//微店冻结金额

	private Integer appWithdrawScheduleType; //卖油翁提现类型 0:其他 1：微店
	private String appWithdrawScheduleTime; //卖游翁收入提现周期
	private Integer appWithdrawPoundagePercent; //卖游翁提现手续费(千分比)

	private Integer weshopWithdrawScheduleType; //微店提现类型   0:其他 1：微店
	private String weshopWithdrawScheduleTime; //微店收入提现周期
	private Integer weshopWithdrawPoundagePercent;  //微店提现手续费(千分比)
	private Double lastMonthSales; //上月销售额
	private Integer SaleLevle;  //销售等级
	private Integer SaleLevleDivision; //销售等级分割值
	
	public Long getResellerId() {
		return resellerId;
	}
	public void setResellerId(Long resellerId) {
		this.resellerId = resellerId;
	}
	public Double getAppIncoming() {
		return appIncoming;
	}
	public void setAppIncoming(Double appIncoming) {
		this.appIncoming = appIncoming;
	}
	public Double getWeshopIncoming() {
		return weshopIncoming;
	}
	public void setWeshopIncoming(Double weshopIncoming) {
		this.weshopIncoming = weshopIncoming;
	}
	public Double getAppFrozenIncoming() {
		return appFrozenIncoming;
	}
	public void setAppFrozenIncoming(Double appFrozenIncoming) {
		this.appFrozenIncoming = appFrozenIncoming;
	}
	public Double getWeshopFrozenIncoming() {
		return weshopFrozenIncoming;
	}
	public void setWeshopFrozenIncoming(Double weshopFrozenIncoming) {
		this.weshopFrozenIncoming = weshopFrozenIncoming;
	}
	public Integer getAppWithdrawScheduleType() {
		return appWithdrawScheduleType;
	}
	public void setAppWithdrawScheduleType(Integer appWithdrawScheduleType) {
		this.appWithdrawScheduleType = appWithdrawScheduleType;
	}
	public String getAppWithdrawScheduleTime() {
		return appWithdrawScheduleTime;
	}
	public void setAppWithdrawScheduleTime(String appWithdrawScheduleTime) {
		this.appWithdrawScheduleTime = appWithdrawScheduleTime;
	}
	public Integer getAppWithdrawPoundagePercent() {
		return appWithdrawPoundagePercent;
	}
	public void setAppWithdrawPoundagePercent(Integer appWithdrawPoundagePercent) {
		this.appWithdrawPoundagePercent = appWithdrawPoundagePercent;
	}
	public Integer getWeshopWithdrawScheduleType() {
		return weshopWithdrawScheduleType;
	}
	public void setWeshopWithdrawScheduleType(Integer weshopWithdrawScheduleType) {
		this.weshopWithdrawScheduleType = weshopWithdrawScheduleType;
	}
	public String getWeshopWithdrawScheduleTime() {
		return weshopWithdrawScheduleTime;
	}
	public void setWeshopWithdrawScheduleTime(String weshopWithdrawScheduleTime) {
		this.weshopWithdrawScheduleTime = weshopWithdrawScheduleTime;
	}
	public Integer getWeshopWithdrawPoundagePercent() {
		return weshopWithdrawPoundagePercent;
	}
	public void setWeshopWithdrawPoundagePercent(
			Integer weshopWithdrawPoundagePercent) {
		this.weshopWithdrawPoundagePercent = weshopWithdrawPoundagePercent;
	}
	public Double getLastMonthSales() {
		return lastMonthSales;
	}
	public void setLastMonthSales(Double lastMonthSales) {
		this.lastMonthSales = lastMonthSales;
	}
	public Integer getSaleLevle() {
		return SaleLevle;
	}
	public void setSaleLevle(Integer saleLevle) {
		SaleLevle = saleLevle;
	}
	public Integer getSaleLevleDivision() {
		return SaleLevleDivision;
	}
	public void setSaleLevleDivision(Integer saleLevleDivision) {
		SaleLevleDivision = saleLevleDivision;
	}
}
