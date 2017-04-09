package com.pzj.platform.appapi.model;

import java.io.Serializable;
import java.util.List;

import com.pzj.address.entity.Address;

/**
 * 联系人返回实体
 * 
 * @author liupeng
 * @version $Id: ContactsModel.java, v 0.1 2016年10月19日 上午10:10:16 cc Exp $
 */
public class AddressModel implements Serializable {

    private List<Address> addressList; //地址集合

    private Integer       currentPage; //当前页

    private Long          totalCount; //总记录

    private Integer       totalPages;  //总页数

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    public List<Address> getAddressList() {
        return addressList;
    }

    public void setAddressList(List<Address> addressList) {
        this.addressList = addressList;
    }

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }

}
