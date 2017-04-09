package com.pzj.modules.appapi.api.order;

import com.alibaba.fastjson.JSONObject;
import com.pzj.customer.entity.Customer;
import com.pzj.modules.appapi.entity.JsonEntity;

public interface OrderCreateService {
    public JsonEntity saveOrder(JSONObject jsonObject, Customer user);

    public JsonEntity checkOrderRelevant(JSONObject jsonObject, Customer user) throws Exception;
}
