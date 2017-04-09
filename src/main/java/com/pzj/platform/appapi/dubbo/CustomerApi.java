/**
 * piaozhijia.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package com.pzj.platform.appapi.dubbo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pzj.base.common.utils.PageList;
import com.pzj.customer.entity.Customer;
import com.pzj.customer.entity.CustomerRelation;
import com.pzj.customer.service.CustomerRelationService;

/**
 * 
 * @author Mark
 * @version $Id: CustomerApi.java, v 0.1 2016年7月13日 下午8:45:10 pengliqing Exp $
 */
@Component
public class CustomerApi {

    @Autowired
    private com.pzj.cache.UserCacheService           userCacheService;
    @Autowired
    private com.pzj.customer.service.CustomerService customerService;
    @Autowired
    CustomerRelationService                          customerRelationService;

    /**
     * 通过token查询用户
     *
     * @param token 
     * @return Customer
     * @throws Exception 
     */
    public Customer getUserToken(String token) throws Exception {
        return userCacheService.getUserToken(token);
    }

    public PageList<CustomerRelation> findCustomerRelation(CustomerRelation relation) throws Exception {
        return customerRelationService.queryPageByParamMap(null, relation);
    }

    /**
     * 新建用户
     *
     * @param customer 
     * @return
     * @throws Exception 
     */
    public Long create(Customer customer) throws Exception {
        return customerService.createCustomer(customer);
    }

    /**
     * 登录
     *
     * @param loginName 用户名
     * @param password 密码
     * @return Customer
     * @throws Exception 
     */
    public Customer login(String loginName, String password) throws Exception {
        return customerService.login(loginName, password);
    }

    /**
     * 保存用户信息
     *
     * @param customer 用户信息
     * @return  <code>1</code> 成功
     * @throws Exception 
     */
    public Integer saveCustomer(Customer customer) throws Exception {
        return customerService.saveCustomer(customer);
    }

    /**
     * 根据登陆用户名查询用户
     *
     * @param loginName
     * @return
     * @throws Exception 
     */
    public List<Customer> findCustomerByLoginName(String loginName) throws Exception {
        return customerService.findCustomerByLoginName(loginName, null);
    }

    /**
     * 根据营业执照查询用户
     *
     * @param loginName
     * @return
     * @throws Exception 
     */
    public List<Customer> findCustomerByBusinessLicense(String businessLicense) throws Exception {
        Customer omer = new Customer();
        omer.setBusinessLicense(businessLicense);
        return customerService.findCustomerByParams(omer);
    }

    /**
     * 根据公司名查询用户
     *
     * @param loginName
     * @return
     * @throws Exception 
     */
    public List<Customer> findCustomerByDirectCompany(String directCompany) throws Exception {
        Customer omer = new Customer();
        omer.setName(directCompany);
        return customerService.findCustomerByParams(omer);
    }

    /**
     * 根据身份证查询用户
     *
     * @param loginName
     * @return
     * @throws Exception 
     */
    public List<Customer> findCustomerByGuideIdCard(String guideIdCard) throws Exception {
        Customer omer = new Customer();
        omer.setCorporaterCredentials(guideIdCard);
        return customerService.findCustomerByParams(omer);
    }

    public Customer getCustomerById(long customerId) {
        Customer customerRequest = new Customer();
        customerRequest.setId(customerId);
        List<Customer> customers;
        try {
            customers = customerService.findCustomerByParams(customerRequest);
            return (customers == null || customers.size() == 0) ? null : customers.get(0);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
}
