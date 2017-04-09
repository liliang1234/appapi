package com.pzj.platform.appapi.model;

import java.io.Serializable;
import java.util.List;

import com.pzj.contacts.entity.Contacts;

/**
 * 联系人返回实体
 * 
 * @author liupeng
 * @version $Id: ContactsModel.java, v 0.1 2016年10月19日 上午10:10:16 cc Exp $
 */
public class ContactsModel implements Serializable {

    private List<Contacts> contactsList; //联系人集合

    private Integer        currentPage; //当前页

    private Long           totalCount;  //总记录

    private Integer        totalPages;   //总页数

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    public List<Contacts> getContactsList() {
        return contactsList;
    }

    public void setContactsList(List<Contacts> contactsList) {
        this.contactsList = contactsList;
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
