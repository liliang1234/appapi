package com.pzj.platform.appapi.model;

import java.io.Serializable;
import java.util.Date;

public class WithdrawalstHistory implements Serializable{
	private String serialNumber;   //流水号
	private Date withdrawalsDate; //提现日期
	private Double withdrawalsPrice; //提现金额
	private String withdrawalsCardNum; //提现卡号
	private Integer withdrawalsState; //提现状态
	private Double actualWithdrawalAmount; //实退金额
	private Double netCharge;  //扣除费用
	
	public String getSerialNumber() {
		return serialNumber;
	}
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}
	public Date getWithdrawalsDate() {
		return withdrawalsDate;
	}
	public void setWithdrawalsDate(Date withdrawalsDate) {
		this.withdrawalsDate = withdrawalsDate;
	}
	public Double getWithdrawalsPrice() {
		return withdrawalsPrice;
	}
	public void setWithdrawalsPrice(Double withdrawalsPrice) {
		this.withdrawalsPrice = withdrawalsPrice;
	}
	public String getWithdrawalsCardNum() {
		return withdrawalsCardNum;
	}
	public void setWithdrawalsCardNum(String withdrawalsCardNum) {
		this.withdrawalsCardNum = withdrawalsCardNum;
	}
	public Integer getWithdrawalsState() {
		return withdrawalsState;
	}
	public void setWithdrawalsState(Integer withdrawalsState) {
		this.withdrawalsState = withdrawalsState;
	}
	public Double getActualWithdrawalAmount() {
		return actualWithdrawalAmount;
	}
	public void setActualWithdrawalAmount(Double actualWithdrawalAmount) {
		this.actualWithdrawalAmount = actualWithdrawalAmount;
	}
	public Double getNetCharge() {
		return netCharge;
	}
	public void setNetCharge(Double netCharge) {
		this.netCharge = netCharge;
	}
}
