/**
 * piaozhijia.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package com.pzj.platform.appapi.service;

import java.util.List;
import java.util.Map;

import com.pzj.contacts.entity.Contacts;
import com.pzj.customer.entity.Customer;

/**
 * 钱包服务
 * @author fanggang
 * @version $Id: WalletService.java, v 0.1 2016年7月26日 下午5:02:00 fanggang Exp $
 */
public interface ContactService {

    /**
     * 创建联系人接口
     * @param contacts
     * @return
     */
    public Map<String, Object> createContact(Contacts contacts);

    /**
     * 删除联系人/批量删除联系人
     * @param contacts
     * @return
     */
    public Map<String, Object> deleteContact(List<Long> contactIds);

    /**
     * 编辑完成保存联系人
     * @param contacts
     * @return
     */
    public Map<String, Object> updateContact(Contacts contacts);

    /**
     * 编辑前查询联系人
     * @param contactId
     * @return
     */
    public Contacts editContactsById(Long contactId);

    /**
     * 设置默认联系人
     * @param id
     * @return
     */
    public Map<String, Object> asDefault(Long contactId);

    /**
     * 分页查询联系人
     * @param customer
     * @param string
     * @param string2
     * @return
     */
    public Object queryContactsPage(Customer customer, String pageNo, String pageSize);

}
