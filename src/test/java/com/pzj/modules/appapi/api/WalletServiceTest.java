package com.pzj.modules.appapi.api;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONObject;
import com.pzj.base.common.utils.PageList;
import com.pzj.common.test.BaseTest;
import com.pzj.modules.appapi.entity.JsonEntity;
import com.pzj.product.api.product.service.CommonProductService;
import com.pzj.product.api.product.service.impl.CommonProductServiceImpl;
import com.pzj.product.api.product.vo.CustomerCommonProuctRequestVO;
import com.pzj.product.api.product.vo.CustomerCommonProuctVO;

public class WalletServiceTest extends BaseTest {

    @Autowired
    WalletService        walletService;
    @Autowired
    CommonProductService commonProductService;

    @Test
    public void testWithdraw() throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userType", 1);
        jsonObject.put("resellerType", 0);
        jsonObject.put("cashPostalMoney", 0.1);
        JsonEntity result = walletService.withdraw(jsonObject, null, new JsonEntity());
        System.out.println(result);
    }

    @Test
    public void testPro() throws Exception {
        CustomerCommonProuctRequestVO request = new CustomerCommonProuctRequestVO();
        request.setUserId(2216619736566999l);
        PageList<CustomerCommonProuctVO> list = commonProductService.getSKUProductListForUser(request, null);
        List<CustomerCommonProuctVO> resultList = list.getResultList();
        for (CustomerCommonProuctVO customerCommonProuctVO : resultList) {
            System.out.println(customerCommonProuctVO);
        }

    }

    public static void main(String[] args) throws Exception {
        CommonProductService commonProductService = new CommonProductServiceImpl();
        CustomerCommonProuctRequestVO request = new CustomerCommonProuctRequestVO();
        request.setUserId(2216619736566999l);
        PageList<CustomerCommonProuctVO> list = commonProductService.getSKUProductListForUser(request, null);
        List<CustomerCommonProuctVO> resultList = list.getResultList();
        for (CustomerCommonProuctVO customerCommonProuctVO : resultList) {
            System.out.println(customerCommonProuctVO);
        }
    }
}
