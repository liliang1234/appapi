package com.pzj.modules.appapi.api.dto;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Administrator on 2016-6-8.
 */
public class AccountInfo implements Serializable {
    private Integer currentPage = 1;     // 当前页数
    private Integer pageSize    = 1;     // 每页记录数
    private Date    accountingMonthBegin;
    private Date    accountingMonthEnd;
    private Integer status;
    private Integer sellCheckingCount;
    private Double  balanceOfAvailable;
    private Double  balanceOfFrozen;
    private Double  balanceOfAvailableW;
    private Double  balanceOfFrozenW;

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    } 

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Date getAccountingMonthBegin() {
        return accountingMonthBegin;
    }

    public void setAccountingMonthBegin(Date accountingMonthBegin) {
        this.accountingMonthBegin = accountingMonthBegin;
    }

    public Date getAccountingMonthEnd() {
        return accountingMonthEnd;
    }

    public void setAccountingMonthEnd(Date accountingMonthEnd) {
        this.accountingMonthEnd = accountingMonthEnd;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getSellCheckingCount() {
        return sellCheckingCount;
    }

    public void setSellCheckingCount(Integer sellCheckingCount) {
        this.sellCheckingCount = sellCheckingCount;
    }

    public Double getBalanceOfAvailable() {
        return balanceOfAvailable;
    }

    public void setBalanceOfAvailable(Double balanceOfAvailable) {
        this.balanceOfAvailable = balanceOfAvailable;
    }

    public Double getBalanceOfFrozen() {
        return balanceOfFrozen;
    }

    public void setBalanceOfFrozen(Double balanceOfFrozen) {
        this.balanceOfFrozen = balanceOfFrozen;
    }

    public Double getBalanceOfAvailableW() {
        return balanceOfAvailableW;
    }

    public void setBalanceOfAvailableW(Double balanceOfAvailableW) {
        this.balanceOfAvailableW = balanceOfAvailableW;
    }

    public Double getBalanceOfFrozenW() {
        return balanceOfFrozenW;
    }

    public void setBalanceOfFrozenW(Double balanceOfFrozenW) {
        this.balanceOfFrozenW = balanceOfFrozenW;
    }

}
