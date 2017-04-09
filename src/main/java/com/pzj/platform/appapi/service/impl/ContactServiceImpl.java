/**
 * piaozhijia.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package com.pzj.platform.appapi.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pzj.base.common.utils.PageList;
import com.pzj.base.common.utils.PageModel;
import com.pzj.contacts.entity.Contacts;
import com.pzj.contacts.entity.ContactsParam;
import com.pzj.contacts.service.ContactsService;
import com.pzj.customer.entity.Customer;
import com.pzj.framework.context.Result;
import com.pzj.framework.context.ServiceContext;
import com.pzj.platform.appapi.model.ContactsModel;
import com.pzj.platform.appapi.service.ContactService;

/**
 * 
 * @author liupeng
 * @version $Id: ContactServiceImpl.java, v 0.1 2016年7月26日 下午5:04:30 fanggang Exp $
 */
@Service
public class ContactServiceImpl implements ContactService {

    private static final Logger logger = LoggerFactory.getLogger(ContactServiceImpl.class);

    @Autowired
    private ContactsService     contactsService;

    /** 
     * 创建联系人
     * @see com.pzj.platform.appapi.service.ContactService#createContact(java.util.List)
     */
    @Override
    public Map<String, Object> createContact(Contacts contacts) {
        ServiceContext serviceContext = new ServiceContext();
        Map<String, Object> map = new HashMap<String, Object>();
        if (contacts != null) {
            Result<Long> result = contactsService.createContacts(contacts, serviceContext);
            if (result.getErrorCode() == 10000) {
                map.put("code", result.getErrorCode());
                map.put("msg", result.getErrorMsg());
            } else {
                map.put("code", result.getErrorCode());
                map.put("msg", result.getErrorMsg());
            }
        } else {
            map.put("code", "40001");
            map.put("msg", "参数异常:常用联系人数据异常");
        }
        return map;
    }

    /** 
     * @see com.pzj.platform.appapi.service.ContactService#deleteContact(java.util.List)
     */
    @Override
    public Map<String, Object> deleteContact(List<Long> contactIds) {

        ServiceContext serviceContext = new ServiceContext();
        Map<String, Object> map = new HashMap<String, Object>();
        if (contactIds == null || contactIds.size() < 0) {
            map.put("code", "40001");
            map.put("msg", "参数异常:常用联系人主键ID不能为空");
        } else {
            Result<Integer> result = contactsService.deleteContacts(contactIds, serviceContext);
            if (result.getErrorCode() == 10000) {
                map.put("code", result.getErrorCode());
                map.put("msg", result.getErrorMsg());
            } else {
                map.put("code", result.getErrorCode());
                map.put("msg", result.getErrorMsg());
            }
        }

        return map;
    }

    /** 
     * @see com.pzj.platform.appapi.service.ContactService#updateContact(java.util.List)
     */
    @Override
    public Map<String, Object> updateContact(Contacts contacts) {
        ServiceContext serviceContext = new ServiceContext();
        Map<String, Object> map = new HashMap<String, Object>();
        if (contacts != null) {
            Result<Integer> result = contactsService.modifyContacts(contacts, serviceContext);
            if (result.getErrorCode() == 10000) {
                map.put("code", result.getErrorCode());
                map.put("msg", result.getErrorMsg());
            } else {
                map.put("code", result.getErrorCode());
                map.put("msg", result.getErrorMsg());
            }
        } else {
            map.put("code", "40001");
            map.put("msg", "参数异常:常用联系人数据异常");

        }

        return map;
    }

    /** 
     * @see com.pzj.platform.appapi.service.ContactService#queryContacts(com.pzj.customer.entity.Customer)
     */
    @Override
    public Contacts editContactsById(Long contactId) {
        ServiceContext serviceContext = new ServiceContext();
        Map<String, Object> map = new HashMap<String, Object>();
        Contacts contacts = new Contacts();
        if (contactId == null) {
            map.put("code", "40001");
            map.put("msg", "参数异常:常用联系人主键ID不能为空");
        } else {
            ContactsParam contactQueryParam = new ContactsParam();
            contactQueryParam.setId(contactId);
            Result<ArrayList<Contacts>> queryByParam = contactsService.queryByParam(contactQueryParam, serviceContext);
            if (queryByParam.getErrorCode() == 10000) {
                map.put("code", queryByParam.getErrorCode());
                map.put("msg", queryByParam.getErrorMsg());
            } else {
                map.put("code", queryByParam.getErrorCode());
                map.put("msg", queryByParam.getErrorMsg());
            }
            if (queryByParam.getData() != null && queryByParam.getData().size() > 0)
                contacts = queryByParam.getData().get(0);
        }

        return contacts;
    }

    /** 
     * 设置默认联系人
     * @see com.pzj.platform.appapi.service.ContactService#asDefault(java.lang.Long)
     */
    @Override
    public Map<String, Object> asDefault(Long contactId) {
        ServiceContext serviceContext = new ServiceContext();
        Map<String, Object> map = new HashMap<String, Object>();
        if (contactId == null) {
            map.put("code", "40001");
            map.put("msg", "参数异常:常用联系人主键ID不能为空");
        } else {
            Result<Integer> result = contactsService.asDefault(contactId, serviceContext);
            if (result.getErrorCode() == 10000) {
                map.put("code", result.getErrorCode());
                map.put("msg", result.getErrorMsg());
            } else {
                map.put("code", result.getErrorCode());
                map.put("msg", result.getErrorMsg());
            }
        }

        return map;
    }

    /** 
     * 分页查询联系人
     * @see com.pzj.platform.appapi.service.ContactService#queryContactsPage(com.pzj.customer.entity.Customer, java.lang.String, java.lang.String)
     */
    @Override
    public Object queryContactsPage(Customer customer, String pageNo, String pageSize) {
        ServiceContext serviceContext = new ServiceContext();
        Map<String, Object> map = new HashMap<String, Object>();
        if (customer == null) {
            map.put("code", "40001");
            map.put("msg", "参数异常:常用联系人主键ID不能为空");
        }
        PageModel pageModel = new PageModel();
        pageModel.setPageNo(Integer.valueOf(pageNo));
        pageModel.setPageSize(Integer.valueOf(pageSize));
        ContactsParam contactQueryParam = new ContactsParam();
        contactQueryParam.setCreateBy(customer.getId());
        Result<PageList<Contacts>> queryByParam = contactsService.queryByParam(contactQueryParam, pageModel, serviceContext);
        if (queryByParam.getErrorCode() == 10000) {
            map.put("code", queryByParam.getErrorCode());
            map.put("msg", queryByParam.getErrorMsg());
        } else {
            map.put("code", queryByParam.getErrorCode());
            map.put("msg", queryByParam.getErrorMsg());
        }

        ContactsModel contactsModel = new ContactsModel();
        if (queryByParam.getData() != null && queryByParam.getData().getResultList().size() > 0) {
            contactsModel.setCurrentPage(queryByParam.getData().getPageBean().getCurrentPage());
            contactsModel.setTotalCount(queryByParam.getData().getPageBean().getTotalCount());
            contactsModel.setContactsList(queryByParam.getData().getResultList());
            contactsModel.setTotalPages(queryByParam.getData().getPageBean().getPages().length);
        } else {
            List<Contacts> contactsList = new ArrayList<Contacts>();
            contactsModel.setContactsList(contactsList);
            contactsModel.setCurrentPage(Integer.valueOf(pageNo));
            contactsModel.setTotalCount(0L);
            contactsModel.setTotalPages(0);
        }
        return contactsModel;
    }

}
