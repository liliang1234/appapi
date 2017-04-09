package com.pzj.modules.appapi.api;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pzj.customer.entity.Customer;
import com.pzj.modules.appapi.entity.CodeHandle;
import com.pzj.modules.appapi.entity.JsonEntity;
import com.pzj.platform.appapi.util.PropertyLoader;

/**
 * app辅助功能
 * 
 * @author wangkai
 * @date 2015年11月6日 下午6:57:49
 */
@Component
public class OtherService {
    @Resource(name = "propertyLoader")
    private PropertyLoader propertyLoader;

    /**
     * 返回H5帮助页面
     * 
     * @param data
     *            json参数包
     * @param customer
     *            当前登录用户
     * @param json
     *            返回结果包装
     * @return JsonEntity 返回类型
     * @key about 关于我们地址
     * @key help  帮助地址
     */
    public JsonEntity getHelp() {
        JsonEntity jsonEntity = new JsonEntity();
        Map<String, Object> json = Maps.newHashMap();
        json.put("about", propertyLoader.getProperty("system", "about"));
        json.put("help", propertyLoader.getProperty("system", "help"));
        jsonEntity.setCode(CodeHandle.SUCCESS.getCode() + "");
        jsonEntity.setMessage(CodeHandle.SUCCESS.getMessage());
        jsonEntity.setResponseBody(json);
        return jsonEntity;
    }

    /**
     * 返回订单状态信息列表
     * 
     * @param data
     *            json参数包
     * @param customer
     *            当前登录用户
     * @param json
     *            返回结果包装
     * @return JsonEntity 返回类型
     * @key id 类型
     * @key name 状态意义
     */
    public JsonEntity queryOrderStatus(JSONObject data, Customer customer, JsonEntity json) {
        List<Map<String, Object>> jsonArray = Lists.newArrayList();
        String string = propertyLoader.getProperty("system", "orderStatus", "");
        String[] split = string.split(";");
        for (String string2 : split) {
            String[] split2 = string2.split(":");
            Map<String, Object> jsonObject = Maps.newHashMap();
            jsonObject.put("id", split2[0]);
            jsonObject.put("name", split2[1]);
            jsonArray.add(jsonObject);
        }
        Map<String, Object> object = Maps.newHashMap();
        object.put("list", jsonArray);
        json.setCode(CodeHandle.SUCCESS.getCode() + "");
        json.setMessage(CodeHandle.SUCCESS.getMessage());
        json.setResponseBody(object);
        return json;
    }

    /**
     * 返回是由列表
     * 
     * @param data
     *            json参数包
     * @param customer
     *            当前登录用户
     * @param json
     *            返回结果包装
     * @key type 1:收入 2:支出
     * @return JsonEntity 返回类型
     * @key list 类型集合
     */
    public JsonEntity queryDealTypes(JSONObject data, Customer customer, JsonEntity json) {
        String type = data.containsKey("type") ? data.getString("type") : "1";
        String dealType = "";
        if (type.equals("1")) {
            dealType = propertyLoader.getProperty("system", "DealType-1", "");
        } else {
            dealType = propertyLoader.getProperty("system", "DealType-0", "");
        }
        Map<String, Object> object = Maps.newHashMap();
        object.put("list", dealType);
        json.setCode(CodeHandle.SUCCESS.getCode() + "");
        json.setMessage(CodeHandle.SUCCESS.getMessage());
        json.setResponseBody(object);
        return json;
    }

}
