/**
 * piaozhijia.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package com.pzj.platform.appapi.dubbo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pzj.framework.context.Result;
import com.pzj.framework.entity.QueryResult;
import com.pzj.stat.StatService;
import com.pzj.stat.entity.MyWalletEntity;
import com.pzj.stat.entity.OftenlyPurchaseEntity;
import com.pzj.stat.exception.StatException;

/**
 * 
 * @author fanggang
 * @version $Id: AccountApi.java, v 0.1 2016年7月29日 下午4:11:18 fanggang Exp $
 */
@Component
public class StatApi {

    @Autowired
    private StatService statService;

    /**
     *  根据传入的用户ID,月份查询这个用户在指定月份的销售额
     *  (与苏鏊商定暂不计算返利,也不约束已核销状态,只要销售过即可),
     *  如未传递月份字符串或传递的月份字符串无法解析,将从运行时服务器环境中获取上一个自然月作为条件进行查询;
     * @param userId
     * @return
     * @throws StatException
     */
    public Result<QueryResult<MyWalletEntity>> queryMonthlySales(Long userId) throws StatException {
        return statService.queryMyWallet(userId, null);
    }

    /**
     * 根据传入的用户ID,月份查询这个用户在最近的上一个自然月内购买过的经过去重的产品信息(产品名称和产品ID).
     * 
     * @param userId
     * @param currentPage
     * @param pageSize
     * @return
     * @throws StatException
     */
    public Result<QueryResult<OftenlyPurchaseEntity>> queryOftenlyPurchase(Long userId, Integer currentPage, Integer pageSize) throws StatException {
        return statService.queryOftenlyPurchase(userId, null, null, currentPage, pageSize);
    }
}
