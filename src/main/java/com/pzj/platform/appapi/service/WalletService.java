/**
 * piaozhijia.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package com.pzj.platform.appapi.service;

import java.util.List;
import java.util.Map;

import com.pzj.customer.entity.Customer;
import com.pzj.framework.entity.QueryResult;
import com.pzj.platform.appapi.model.AccountBalance;
import com.pzj.platform.appapi.model.SimpleSellChecking;
import com.pzj.platform.appapi.model.SummarySellChecking;
import com.pzj.platform.appapi.model.WalletResultModel;
import com.pzj.platform.appapi.model.WithDrawalsModel;
import com.pzj.stat.entity.OftenlyPurchaseEntity;

/**
 * 钱包服务
 * @author fanggang
 * @version $Id: WalletService.java, v 0.1 2016年7月26日 下午5:02:00 fanggang Exp $
 */
public interface WalletService {

    /**
     * 分页查询分销商对账单；
     * 按对账单创建时间倒序
     * 
     * @param customer 用户
     * @param status 对账单状态；如果为null，则查询全部状态。
     * @param pageNO 页码
     * @param pageSize 页的大小
     * @return
     */
    public QueryResult<SimpleSellChecking> findCheckings(Customer customer, Integer status, Integer pageNO, Integer pageSize);

    /**
     * 查询对账单明细
     * 
     * @param id 对账单ID（主键）
     * @param customer 用户
     * @return
     */
    public SummarySellChecking findCheckingDetail(Long id, Customer customer);

    /**
     * 确认对帐单
     * 
     * @param id 对账单ID（主键）
     */
    public void confirmChecking(Long id);

    /**
     * 拒绝对帐单
     * @param id 对账单ID（主键）
     */
    public void refuseChecking(Long id);

    /**
     * 查询我的账户余额；
     * 可用余额、冻结余额；
     * @param customer 用户
     * @return
     */
    public AccountBalance findAccountBalance(Customer customer);

    /**
     * 查询分销商的余额销售额
     * @param customer 用户
     * @return 如果存在此分销商，那么就返回对应的月销售额
     */
    public double findMonthlySales(Customer customer);

    /**
     * 查询用户经常购买的产品列表（常购）
     * 
     * @param customer 用户
     * @param pageNO 页码
     * @param pageSize 页的大小
     * @return
     */
    public List<OftenlyPurchaseEntity> findOftenlyPurchased(Customer customer, Integer pageNO, Integer pageSize);

    /**
     * 根据用户查询 钱包提现
     * @param customer
     * @return
     */
    public WalletResultModel queryWithdrawalsByCustomer(Customer customer);

    /**
     *  根据用户查询钱包提现历史
     * @param customer 用户
     * @param pageNO 页码
     * @param pageSize 页的大小
     * @return
     */
    public WithDrawalsModel queryWithdrawalsHistory(Customer customer, Integer pageNO, Integer pageSize);

    /**
     * 钱包提现接口
     * @param customer  用户
     * @param withdrawalsPrice  提现金额
     * @param withdrawalsType  提现类型   0 其他  1 卖油翁
     * @return
     */
    public Map<String, Object> withdrawals(Customer customer, String withdrawalsPrice, Integer withdrawalsType, Integer isWithdrawalsFirst);
}
