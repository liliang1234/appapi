package com.pzj.platform.appapi.model;

import java.io.Serializable;
import java.util.List;

public class WithDrawalsModel implements Serializable{

	private List<WithdrawalstHistory> WithdrawalstHistoryList; //返回数据封装
	
    private Integer           currentPage;//当前页

    private long              totalCount;//总记录

	public List<WithdrawalstHistory> getWithdrawalstHistoryList() {
		return WithdrawalstHistoryList;
	}

	public void setWithdrawalstHistoryList(
			List<WithdrawalstHistory> withdrawalstHistoryList) {
		WithdrawalstHistoryList = withdrawalstHistoryList;
	}

	public long getTotalCount() {
		return totalCount;
	}

	public Integer getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(Integer currentPage) {
		this.currentPage = currentPage;
	}

	public void setTotalCount(long totalCount) {
		this.totalCount = totalCount;
	}
    
}
