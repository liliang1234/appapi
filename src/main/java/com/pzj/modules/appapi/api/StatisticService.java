package com.pzj.modules.appapi.api;

import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.pzj.customer.entity.Customer;
import com.pzj.modules.appapi.entity.JsonEntity;

/**
 *  app统计相关功能
 * @author wangkai
 * @date 2015年11月6日 下午8:40:56
 */
@Component
public class StatisticService {

    /**
     * 统计返利、余额流水
     * @param data
     * @param customer
     * @param json
     * @return JsonEntity 返回类型
     * @throws
     */
    public JsonEntity walletRecord(JSONObject data, Customer customer, JsonEntity json) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * 统计订单交易流水
     * @param data
     * @param customer
     * @param json
     * @return JsonEntity 返回类型
     * @throws
     */
    public JsonEntity queryFlowOrders(JSONObject data, Customer customer, JsonEntity json) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * 统计每天的采购账单
     * @param data
     * @param customer
     * @param json
     * @return JsonEntity 返回类型
     * @throws
     */
    public JsonEntity bills(JSONObject data, Customer customer, JsonEntity json) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * 对账单列表
     * @param data
     * @param customer
     * @param json
     * @return JsonEntity 返回类型
     * @throws
     */
    public JsonEntity statement(JSONObject data, Customer customer, JsonEntity json) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * 确认对账单
     * @param data
     * @param customer
     * @param json
     * @return JsonEntity 返回类型
     * @throws
     */
    public JsonEntity updateStatement(JSONObject data, Customer customer, JsonEntity json) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * 获取每日返利信息
     * @param data
     * @param customer
     * @param json
     * @return JsonEntity 返回类型
     * @throws
     */
    public JsonEntity getStatistics(JSONObject data, Customer customer, JsonEntity json) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * 销售统计，汇总订单信息 如：金额、检票数
     * @param data
     * @param customer
     * @param json
     * @return JsonEntity 返回类型
     * @throws
     */
    public JsonEntity getSale(JSONObject data, Customer customer, JsonEntity json) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * 销售统计，汇总每日订单信息 如：金额、检票数
     * @param data
     * @param customer
     * @param json
     * @return JsonEntity 返回类型
     * @throws
     */
    public JsonEntity getSaleStatistics(JSONObject data, Customer customer, JsonEntity json) {
        // TODO Auto-generated method stub
        return null;
    }

}
