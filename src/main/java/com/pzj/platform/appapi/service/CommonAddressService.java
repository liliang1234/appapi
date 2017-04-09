/**
 * piaozhijia.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package com.pzj.platform.appapi.service;

import java.util.List;
import java.util.Map;

import com.pzj.address.entity.Address;
import com.pzj.customer.entity.Customer;
import com.pzj.platform.appapi.model.AddressModel;

/**
 * 钱包服务
 * @author fanggang
 * @version $Id: WalletService.java, v 0.1 2016年7月26日 下午5:02:00 fanggang Exp $
 */
public interface CommonAddressService {

    /**
     * 添加联系人地址
     * @param address
     * @return
     */
    public Map<String, Object> createAddress(Address address);

    /**
     * 删除联系人地址
     * @param addressIds
     * @return
     */
    public Map<String, Object> deleteAddress(List<Long> addressIds);

    /**
     * 修改联系人信息
     * @param address
     * @return
     */
    public Map<String, Object> updateAddress(Address address);

    /**
     * 编辑联系人地址前查询
     * @param addressId
     * @return
     */
    public Address editAddressById(Long addressId);

    /**
     * 分页查询地址
     * @param customer
     * @param string
     * @param string2
     * @return
     */
    public AddressModel queryAddressPage(Customer customer, String pageNO, String pageSize, Integer type);

    /**
     * 设置默认地址
     * @param addressId
     * @return
     */
    public Map<String, Object> asDefault(Long addressId);

}
