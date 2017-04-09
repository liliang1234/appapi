package com.pzj.modules.appapi.api;

import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.pzj.customer.entity.Customer;
import com.pzj.modules.appapi.entity.JsonEntity;

/**
  *  统计返利相关
  * @author wangkai
  * @date 2015年11月6日 下午8:36:24
  */
@Component
public class RebateService {

    /**
      *  查询返利积分
      * @param data
      * @param customer
      * @param json
      * @return JsonEntity    返回类型
      * @throws
      */
    public JsonEntity rebateProduct(JSONObject data, Customer customer, JsonEntity json) {
        // TODO Auto-generated method stub
        return null;
    }

}
