/**
 * piaozhijia.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package com.pzj.platform.appapi.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pzj.address.entity.Address;
import com.pzj.address.entity.AddressParam;
import com.pzj.address.service.AddressService;
import com.pzj.base.common.utils.PageList;
import com.pzj.base.common.utils.PageModel;
import com.pzj.customer.entity.Customer;
import com.pzj.framework.context.Result;
import com.pzj.framework.context.ServiceContext;
import com.pzj.platform.appapi.model.AddressModel;
import com.pzj.platform.appapi.service.CommonAddressService;

/**
 * 
 * @author liupeng
 * @version $Id: WalletServiceImpl.java, v 0.1 2016年7月26日 下午5:04:30 fanggang Exp $
 */
@Service
public class CommonAddressServiceImpl implements CommonAddressService {

    private static final Logger logger = LoggerFactory.getLogger(CommonAddressServiceImpl.class);

    @Autowired
    private AddressService      addressService;

    /** 
     * @see com.pzj.platform.appapi.service.CommonAddressService#createAddress(com.pzj.address.entity.Address)
     */
    @Override
    public Map<String, Object> createAddress(Address address) {
        ServiceContext serviceContext = new ServiceContext();
        Map<String, Object> map = new HashMap<String, Object>();
        if (address != null) {
            Date createDate = new Date();
            address.setCreateDate(createDate);
            address.setCreateBy(address.getSupplierId());
            Result<Long> result = addressService.createAddress(address, serviceContext);
            if (result.getErrorCode() == 10000) {
                map.put("code", result.getErrorCode());
                map.put("msg", result.getErrorMsg());
            } else {
                map.put("code", result.getErrorCode());
                map.put("msg", result.getErrorMsg());
            }
        } else {
            map.put("code", "40001");
            map.put("msg", "参数异常:地址信息异常");
        }
        return map;
    }

    /** 
     * @see com.pzj.platform.appapi.service.CommonAddressService#deleteAddress(java.util.List)
     */
    @Override
    public Map<String, Object> deleteAddress(List<Long> addressIds) {
        ServiceContext serviceContext = new ServiceContext();
        Map<String, Object> map = new HashMap<String, Object>();
        if (addressIds != null && addressIds.size() > 0) {
            Result<Integer> result = addressService.deleteAddress(addressIds, serviceContext);
            if (result.getErrorCode() == 10000) {
                map.put("code", result.getErrorCode());
                map.put("msg", result.getErrorMsg());
            } else {
                map.put("code", result.getErrorCode());
                map.put("msg", result.getErrorMsg());
            }
        } else {
            map.put("code", "40001");
            map.put("msg", "参数异常:id 参数有问题");
        }
        return map;
    }

    /** 
     * @see com.pzj.platform.appapi.service.CommonAddressService#updateAddress(com.pzj.address.entity.Address)
     */
    @Override
    public Map<String, Object> updateAddress(Address address) {
        ServiceContext serviceContext = new ServiceContext();
        Map<String, Object> map = new HashMap<String, Object>();
        if (address != null) {
            Result<Integer> result = addressService.modifyAddress(address, serviceContext);
            if (result.getErrorCode() == 10000) {
                map.put("code", result.getErrorCode());
                map.put("msg", result.getErrorMsg());
            } else {
                map.put("code", result.getErrorCode());
                map.put("msg", result.getErrorMsg());
            }
        } else {
            map.put("code", "40001");
            map.put("msg", "参数异常:传参不能为空");
        }
        return map;
    }

    /** 
     * @see com.pzj.platform.appapi.service.CommonAddressService#editAddressById(java.lang.Long)
     */
    @Override
    public Address editAddressById(Long addressId) {
        ServiceContext serviceContext = new ServiceContext();
        Map<String, Object> map = new HashMap<String, Object>();
        Address address = new Address();
        if (addressId != null) {
            AddressParam AddressQueryParam = new AddressParam();
            AddressQueryParam.setId(addressId);
            Result<ArrayList<Address>> result = addressService.queryByParam(AddressQueryParam, serviceContext);
            if (result.getErrorCode() == 10000) {
                map.put("code", result.getErrorCode());
                map.put("msg", result.getErrorMsg());
            } else {
                map.put("code", result.getErrorCode());
                map.put("msg", result.getErrorMsg());
            }
            if (result.getData().size() > 0 && result.getData() != null)
                address = result.getData().get(0);
        } else {
            map.put("code", "40001");
            map.put("msg", "参数异常:传参不能为空");
        }
        return address;
    }

    /** 
     * @see com.pzj.platform.appapi.service.CommonAddressService#queryAddressPage(com.pzj.customer.entity.Customer, java.lang.String, java.lang.String)
     */
    @Override
    public AddressModel queryAddressPage(Customer customer, String pageNO, String pageSize, Integer type) {
        ServiceContext serviceContext = new ServiceContext();
        Map<String, Object> map = new HashMap<String, Object>();
        AddressModel addressModel = new AddressModel();
        if (customer != null) {
            AddressParam addressQueryParam = new AddressParam();
            addressQueryParam.setCreateBy(customer.getId());
            addressQueryParam.setType(type);
            addressQueryParam.setSupplierId(customer.getId());
            PageModel pageModel = new PageModel();
            pageModel.setPageNo(Integer.valueOf(pageNO));
            pageModel.setPageSize(Integer.valueOf(pageSize));
            Result<PageList<Address>> result = addressService.queryByParam(addressQueryParam, pageModel, serviceContext);
            if (result.getErrorCode() == 10000) {
                map.put("code", result.getErrorCode());
                map.put("msg", result.getErrorMsg());
            } else {
                map.put("code", result.getErrorCode());
                map.put("msg", result.getErrorMsg());
            }

            if (result.getData() != null && result.getData().getResultList().size() > 0) {
                addressModel.setAddressList(result.getData().getResultList());
                addressModel.setCurrentPage(result.getData().getPageBean().getCurrentPage());
                addressModel.setTotalCount(result.getData().getPageBean().getTotalCount());
                addressModel.setTotalPages(result.getData().getPageBean().getPages().length);
            } else {
                List<Address> addressList = new ArrayList<Address>();
                addressModel.setAddressList(addressList);
                addressModel.setCurrentPage(Integer.valueOf(pageNO));
                addressModel.setTotalCount(0L);
                addressModel.setTotalPages(0);
            }
        } else {
            map.put("code", "40001");
            map.put("msg", "参数异常:传参不能为空");
        }

        return addressModel;
    }

    /** 
     * @see com.pzj.platform.appapi.service.CommonAddressService#asDefault(java.lang.Long)
     */
    @Override
    public Map<String, Object> asDefault(Long addressId) {
        ServiceContext serviceContext = new ServiceContext();
        Map<String, Object> map = new HashMap<String, Object>();
        if (addressId != null) {
            Result<Integer> result = addressService.asDefault(addressId, serviceContext);
            if (result.getErrorCode() == 10000) {
                map.put("code", result.getErrorCode());
                map.put("msg", result.getErrorMsg());
            } else {
                map.put("code", result.getErrorCode());
                map.put("msg", result.getErrorMsg());
            }
        } else {
            map.put("code", "40001");
            map.put("msg", "参数异常:传参不能为空");
        }
        return map;
    }

}
