/**
 * piaozhijia.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package com.pzj.platform.appapi.service.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pzj.base.common.utils.PageList;
import com.pzj.base.common.utils.PageModel;
import com.pzj.customer.entity.Customer;
import com.pzj.framework.context.Result;
import com.pzj.framework.entity.QueryResult;
import com.pzj.modules.appapi.api.BizAccountService;
import com.pzj.platform.appapi.dubbo.AccountApi;
import com.pzj.platform.appapi.dubbo.StatApi;
import com.pzj.platform.appapi.exception.Exceptions;
import com.pzj.platform.appapi.exception.ParamException;
import com.pzj.platform.appapi.exception.UnExpectResultException;
import com.pzj.platform.appapi.model.AccountBalance;
import com.pzj.platform.appapi.model.SimpleSellChecking;
import com.pzj.platform.appapi.model.SummarySellChecking;
import com.pzj.platform.appapi.model.WalletResultModel;
import com.pzj.platform.appapi.model.WithDrawalsModel;
import com.pzj.platform.appapi.model.WithdrawalstHistory;
import com.pzj.platform.appapi.service.WalletService;
import com.pzj.settlement.balance.request.WithDrawRequest;
import com.pzj.settlement.balance.response.WithDrawResponse;
import com.pzj.settlement.balance.service.SettlementCall;
import com.pzj.settlement.base.api.SellCheckingApi;
import com.pzj.settlement.base.common.enums.AccountType;
import com.pzj.settlement.base.common.enums.ResellerAccountDict;
import com.pzj.settlement.base.entity.SellChecking;
import com.pzj.settlement.base.entity.vo.AccountBalanceVo;
import com.pzj.stat.StatService;
import com.pzj.stat.entity.MyWalletEntity;
import com.pzj.stat.entity.OftenlyPurchaseEntity;
import com.pzj.stat.exception.StatException;
import com.pzj.trade.context.ServiceContext;
import com.pzj.trade.withdraw.model.CashPostalModel;
import com.pzj.trade.withdraw.service.CashPostalService;

/**
 * 
 * @author fanggang
 * @version $Id: WalletServiceImpl.java, v 0.1 2016年7月26日 下午5:04:30 fanggang Exp $
 */
@Service
public class WalletServiceImpl implements WalletService {

    private static final Logger   logger            = LoggerFactory.getLogger(WalletServiceImpl.class);

    /** 可用余额ID组 */
    protected static final Long[] AVAILABLE_BALANCE = { ResellerAccountDict.OTHER_AVAILABLE.getId(), ResellerAccountDict.PREPAID_AVAILABLE.getId(),
            ResellerAccountDict.OTHER_AVAILABLE_QRCODE.getId(), ResellerAccountDict.CREDIT_AVAILABLE.getId() };
    /** 冻结余额ID组 */
    protected static final Long[] FROZEN_BALANCE    = { ResellerAccountDict.OTHER_FROZEN.getId(), ResellerAccountDict.PREPAID_FROZEN.getId(),
            ResellerAccountDict.OTHER_FROZEN_QRCODE.getId(), ResellerAccountDict.CREDIT_FROZEN.getId() };

    @Autowired
    private SellCheckingApi       sellCheckingApi;
    @Autowired
    private AccountApi            accountApi;
    @Autowired
    private StatApi               statApi;
    @Autowired
    private StatService           statService;
    @Autowired
    private BizAccountService     bizAccountService;
    @Autowired
    private CashPostalService     cashPostalService;
    @Autowired
    private SettlementCall        settlementCall;
    SimpleDateFormat              sdf               = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /** 
     * @see com.pzj.platform.appapi.service.WalletService#findCheckings(com.pzj.customer.entity.Customer, java.lang.Integer, java.lang.Integer, java.lang.Integer)
     */
    @Override
    public QueryResult<SimpleSellChecking> findCheckings(Customer customer, Integer status, Integer pageNO, Integer pageSize) {
        if (status != null && status != 2 && status != 3 && status != 5) {
            throw new ParamException(Exceptions.CHECKING_STATUS_ILLEGAL);
        }
        SellChecking sellChecking = new SellChecking();
        sellChecking.setDistributorId(customer.getId());
        sellChecking.setStatus(status);
        PageModel pageModel = new PageModel(pageNO, pageSize, "create_time DESC");
        PageList<SellChecking> sellCheckings = sellCheckingApi.querySellCheck(pageModel, sellChecking);
        QueryResult<SimpleSellChecking> simpleSellCheckings = new QueryResult<>(pageNO, pageSize);
        List<SimpleSellChecking> simpleSellChecking = new ArrayList<SimpleSellChecking>();
        for (SellChecking sc : sellCheckings.getResultList()) {

            SimpleSellChecking ssc = new SimpleSellChecking(sc.getId(), sc.getCheckingNum(), Long.valueOf(sc.getCreateTime().getTime()), sdf.format(sc
                .getCreateTime()), sc.getStatus());
            simpleSellChecking.add(ssc);
        }
        simpleSellCheckings.setTotal((int) sellCheckings.getPageBean().getTotalCount());
        simpleSellCheckings.setRecords(simpleSellChecking);
        return simpleSellCheckings;
    }

    /** 
     * @see com.pzj.platform.appapi.service.WalletService#findCheckingDetail(java.lang.Long)
     */
    @Override
    public SummarySellChecking findCheckingDetail(Long id, Customer customer) {
        SellChecking sc = sellCheckingApi.querySellCheckById(id);

        if (sc == null) {
            throw new UnExpectResultException(Exceptions.CHECKING_NOT_FOUND);
        }

        SummarySellChecking ssc = new SummarySellChecking();
        ssc.setId(sc.getId());
        ssc.setCheckingNum(sc.getCheckingNum());
        ssc.setStatus(sc.getStatus());
        ssc.setCreateTime(sc.getCreateTime().getTime());
        ssc.setSettleAmount(sc.getSettleAmount());
        ssc.setSettleReceived(sc.getSettleReceived());
        ssc.setDefaulters(sc.getLackOfAmount());

        Date accountingBegin = sc.getAccountingMonthBegin();
        Date accountingEnd = sc.getAccountingMonthEnd();
        String accountingMonth = DateFormatUtils.format(accountingBegin, "yyyy/MM/dd") + " - " + DateFormatUtils.format(accountingEnd, "yyyy/MM/dd");

        ssc.setAccountingMonth(accountingMonth);
        //查询冻结可用金额
        List<Long> dictList = Arrays.asList(ResellerAccountDict.OTHER_AVAILABLE.getId(), ResellerAccountDict.OTHER_FROZEN.getId());
        List<com.pzj.settlement.base.entity.AccountBalance> accountNew = bizAccountService.getAccountNew(customer, dictList);
        for (com.pzj.settlement.base.entity.AccountBalance account : accountNew) {
            if (ResellerAccountDict.OTHER_AVAILABLE.getId() == account.getAccountDictId().longValue())
                ssc.setBalanceOfAvailable(formateRound(account.getFinalPeriod()));
            else if (ResellerAccountDict.OTHER_FROZEN.getId() == account.getAccountDictId().longValue())
                ssc.setBalanceOfFrozen(formateRound(account.getFinalPeriod()));
        }

        return ssc;
    }

    /** 
     * @see com.pzj.platform.appapi.service.WalletService#confirmChecking(java.lang.Long)
     */
    @Override
    public void confirmChecking(Long id) {
        Map<String, String> map = sellCheckingApi.confirmSellCheck(id);
        if (!"success".equals(map.get("msg"))) {
            logger.error("［确认对帐单］checkingId -> {}, {}", id, map.toString());
            throw new UnExpectResultException(Exceptions.CHECKING_CONFIRM_ERROE);
        }
    }

    /** 
     * @see com.pzj.platform.appapi.service.WalletService#refuseChecking(java.lang.Long)
     */
    @Override
    public void refuseChecking(Long id) {
        Map<String, String> map = sellCheckingApi.refuseSellCheck(id);
        if (!"success".equals(map.get("msg"))) {
            logger.error("［拒绝对帐单］checkingId -> {}, {}", id, map.toString());
            throw new UnExpectResultException(Exceptions.CHECKING_REFUSE_ERROE);
        }
    }

    /** 
     * @see com.pzj.platform.appapi.service.WalletService#findAccountBalance(com.pzj.customer.entity.Customer)
     */
    @Override
    public AccountBalance findAccountBalance(Customer customer) {
        AccountBalanceVo accountBalanceVo = new AccountBalanceVo();
        accountBalanceVo.setCustomerId(customer.getId());
        accountBalanceVo.setAccountType(AccountType.DISTRIBUTOR.key);
        Double availableBalance = 0d;
        Double frozenBalance = 0d;
        try {
            List<com.pzj.settlement.base.entity.AccountBalance> accountBalances = accountApi.queryAccountBalance(accountBalanceVo);

            if (null == accountBalances || accountBalances.isEmpty()) {
                throw new UnExpectResultException(Exceptions.ACCOUNT_BALANCE_NOT_FOUND);
            }

            for (com.pzj.settlement.base.entity.AccountBalance accountBalance : accountBalances) {
                for (Long accountDictId : AVAILABLE_BALANCE) {
                    if (accountDictId.equals(accountBalance.getAccountDictId())) {
                        availableBalance += accountBalance.getFinalPeriod();
                    }
                }
                for (Long accountDictId : FROZEN_BALANCE) {
                    if (accountDictId.equals(accountBalance.getAccountDictId())) {
                        frozenBalance += accountBalance.getFinalPeriod();
                    }
                }
            }
        } catch (Exception e) {
            throw new UnExpectResultException(Exceptions.ACCOUNT_BALANCE_ERROE);
        }

        AccountBalance accountBalance = new AccountBalance(availableBalance + frozenBalance, availableBalance, frozenBalance);
        return accountBalance;
    }

    /** 
     * @see com.pzj.platform.appapi.service.WalletService#findMonthlySales(com.pzj.customer.entity.Customer)
     */
    @Override
    public double findMonthlySales(Customer customer) {
        try {
            Result<QueryResult<MyWalletEntity>> rpcResult = statApi.queryMonthlySales(customer.getId());
            if (rpcResult.isOk()) {
                if (rpcResult.getData() != null && !rpcResult.getData().getRecords().isEmpty()) {
                    return rpcResult.getData().getRecords().get(0).getTotalAmount();
                } else {
                    logger.error("调用月销售额接口结果为空，可能是没有找到这个用户的数据。uid -> {}", customer.getId());
                    throw new UnExpectResultException(Exceptions.ACCOUNT_SALES_ERROE);
                }
            } else {
                logger.error("调用月销售额接口返回失败：uid -> {}, code:{}, msg:{}", customer.getId(), rpcResult.getErrorCode(), rpcResult.getErrorMsg());
                throw new UnExpectResultException(Exceptions.ACCOUNT_SALES_ERROE);
            }
        } catch (StatException e) {
            logger.error("调用月销售额接口异常：uid -> {}, {}", customer.getId(), e.getErrorMessage(), e);
            throw new UnExpectResultException(Exceptions.ACCOUNT_SALES_ERROE);
        }
    }

    /** 
     * @see com.pzj.platform.appapi.service.WalletService#findOftenlyPurchased(com.pzj.customer.entity.Customer, java.lang.Integer, java.lang.Integer)
     */
    @Override
    public List<OftenlyPurchaseEntity> findOftenlyPurchased(Customer customer, Integer pageNO, Integer pageSize) {
        try {
            Result<QueryResult<OftenlyPurchaseEntity>> rpcResult = statApi.queryOftenlyPurchase(customer.getId(), pageNO, pageSize);
            if (rpcResult.isOk()) {
                return rpcResult.getData().getRecords();
            } else {
                logger.error("调用常购接口返回失败：uid -> {}, code:{}, msg:{}", customer.getId(), rpcResult.getErrorCode(), rpcResult.getErrorMsg());
                throw new UnExpectResultException(Exceptions.SERVICEERROR);
            }
        } catch (StatException e) {
            logger.error("调用常购接口异常：uid -> {}, {}", customer.getId(), e.getErrorMessage(), e);
            throw new UnExpectResultException(Exceptions.SERVICEERROR);
        }
    }

    /**
     * 提现前查询
     */
    @Override
    public WalletResultModel queryWithdrawalsByCustomer(Customer customer) {

        WalletResultModel walletResultModel = new WalletResultModel();
        List<Long> dictList = Arrays.asList(ResellerAccountDict.OTHER_AVAILABLE.getId(), ResellerAccountDict.OTHER_FROZEN.getId(),
            ResellerAccountDict.OTHER_AVAILABLE_QRCODE.getId(), ResellerAccountDict.OTHER_FROZEN_QRCODE.getId());

        List<com.pzj.settlement.base.entity.AccountBalance> accountNew = bizAccountService.getAccountNew(customer, dictList);
        if (CollectionUtils.isNotEmpty(accountNew)) {
            for (com.pzj.settlement.base.entity.AccountBalance account : accountNew) {
                if (ResellerAccountDict.OTHER_AVAILABLE.getId() == account.getAccountDictId().longValue())
                    walletResultModel.setAppIncoming(formateRound(account.getFinalPeriod())); //其他 可用金额
                else if (ResellerAccountDict.OTHER_FROZEN.getId() == account.getAccountDictId().longValue())
                    walletResultModel.setAppFrozenIncoming(formateRound(account.getFinalPeriod())); //其他冻结金额
                else if (ResellerAccountDict.OTHER_AVAILABLE_QRCODE.getId() == account.getAccountDictId().longValue())
                    walletResultModel.setWeshopIncoming(formateRound(account.getFinalPeriod())); //微店可用金额
                else if (ResellerAccountDict.OTHER_FROZEN_QRCODE.getId() == account.getAccountDictId().longValue()) {
                    walletResultModel.setWeshopFrozenIncoming(formateRound(account.getFinalPeriod())); //微店冻结金额
                }

            }
        }

        //获取当前月的上一个月
        /*Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH, -1);
        String month = DateUtil.getMonth(c.getTime());
        
        Result<QueryResult<MyWalletEntity>> queryMyWallet = statService.queryMyWallet(customer.getId(), month);
        List<MyWalletEntity> records =new ArrayList<MyWalletEntity>();
        if(queryMyWallet.getData()!=null){
        	records = queryMyWallet.getData().getRecords();
        }
        if(records!=null&&records.size()>0){
        	walletResultModel.setLastMonthSales(records.get(0).getTotalAmount());
        }else{
        	walletResultModel.setLastMonthSales(0.0d);
        }*/

        walletResultModel.setResellerId(customer.getId());
        walletResultModel.setAppWithdrawPoundagePercent(60);
        walletResultModel.setAppWithdrawScheduleTime("3到5个工作日");
        walletResultModel.setAppWithdrawScheduleType(0);
        walletResultModel.setWeshopWithdrawPoundagePercent(60);
        walletResultModel.setWeshopWithdrawScheduleTime("3到5个工作日");
        walletResultModel.setWeshopWithdrawScheduleType(1);
        return walletResultModel;
    }

    /**
     * 提现历史查询
     */
    @Override
    public WithDrawalsModel queryWithdrawalsHistory(Customer customer, Integer pageNO, Integer pageSize) {
        List<WithdrawalstHistory> withdrawalstHistoryList = new ArrayList<WithdrawalstHistory>();

        WithDrawRequest withDrawRequest = new WithDrawRequest();
        withDrawRequest.setAccountId(customer.getId());
        withDrawRequest.setPageNum(pageNO - 1);
        withDrawRequest.setPageSize(pageSize);
        Result<QueryResult<WithDrawResponse>> queryAccountWithdraw = settlementCall.queryAccountWithdraw(withDrawRequest);

        int currentPage = pageNO;
        Integer totalCount = 0;
        List<WithDrawResponse> records = new ArrayList<WithDrawResponse>();
        if (queryAccountWithdraw.getData() != null) {
            currentPage = queryAccountWithdraw.getData().getCurrentPage() + 1;
            totalCount = queryAccountWithdraw.getData().getTotal();
            records = queryAccountWithdraw.getData().getRecords();
        }
        if (records != null && records.size() > 0) {
            for (WithDrawResponse withDrawResponse : records) {
                WithdrawalstHistory withdrawalstHistory = new WithdrawalstHistory();

                withdrawalstHistory.setSerialNumber(withDrawResponse.getSerialNumber());
                withdrawalstHistory.setWithdrawalsCardNum(withDrawResponse.getWithdrawalsCardNum());
                withdrawalstHistory.setWithdrawalsDate(withDrawResponse.getWithdrawalsDate());
                withdrawalstHistory.setWithdrawalsPrice(formateRound(withDrawResponse.getWithdrawalsPrice()));
                withdrawalstHistory.setWithdrawalsState(withDrawResponse.getWithdrawalsState());
                withdrawalstHistory.setActualWithdrawalAmount(withDrawResponse.getActualWithdrawalAmount());
                withdrawalstHistory.setNetCharge(withDrawResponse.getNetCharge() == null ? 0d : withDrawResponse.getNetCharge());

                withdrawalstHistoryList.add(withdrawalstHistory);
            }
        }
        WithDrawalsModel drawalsModel = new WithDrawalsModel();
        drawalsModel.setCurrentPage(currentPage);
        drawalsModel.setTotalCount(totalCount);
        drawalsModel.setWithdrawalstHistoryList(withdrawalstHistoryList);

        return drawalsModel;
    }

    /**
     * 格式化类 四舍五入保留两位小舒
     * @param value
     * @return
     */
    public Double formateRound(double value) {
        double doubleValue = new BigDecimal(value).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

        return doubleValue;
    }

    /**
     * 钱包提现接口
     */
    @Override
    public Map<String, Object> withdrawals(Customer customer, String withdrawalsPrice, Integer withdrawalsType, Integer isWithdrawalsFirst) {
        Map<String, Object> resultMap = new HashMap<String, Object>();

        WalletResultModel walletResultModel = new WalletResultModel();
        List<Long> dictList = Arrays.asList(ResellerAccountDict.OTHER_AVAILABLE.getId(), ResellerAccountDict.OTHER_AVAILABLE_QRCODE.getId());

        List<com.pzj.settlement.base.entity.AccountBalance> accountNew = bizAccountService.getAccountNew(customer, dictList);
        if (CollectionUtils.isNotEmpty(accountNew)) {
            for (com.pzj.settlement.base.entity.AccountBalance account : accountNew) {
                if (ResellerAccountDict.OTHER_AVAILABLE.getId() == account.getAccountDictId().longValue()) {
                    walletResultModel.setAppIncoming(formateRound(account.getFinalPeriod())); //其他 可用金额
                } else if (ResellerAccountDict.OTHER_AVAILABLE_QRCODE.getId() == account.getAccountDictId().longValue()) {
                    walletResultModel.setWeshopIncoming(formateRound(account.getFinalPeriod())); //微店可用金额
                }
            }
        }
        if (withdrawalsType == 0) {
            if (isWithdrawalsFirst == 0) {
                if (Double.valueOf(withdrawalsPrice) < 1) {
                    resultMap.put("msg", "最低提现1元,请重新申请");
                    resultMap.put("code", "11000");
                } else {
                    resultMap.put("msg", "提现金额¥" + withdrawalsPrice + "元");
                    resultMap.put("code", "12000");
                }
            } else {
                //调用提现接口
                ServiceContext serviceContext = new ServiceContext();
                CashPostalModel cashPstalModel = new CashPostalModel();
                cashPstalModel.setAccountId(customer.getId());
                cashPstalModel.setCashPostalMoney(Double.valueOf(withdrawalsPrice));
                cashPstalModel.setResellerType(0);
                cashPstalModel.setUserType(2);
                Result<Boolean> cashPostal = cashPostalService.cashPostal(cashPstalModel, serviceContext);
                if (cashPostal.getData() != null) {
                    if (cashPostal.getData()) {
                        resultMap.put("code", "10000");
                        resultMap.put("msg", "提现成功");
                    } else {
                        resultMap.put("code", "99999");
                        resultMap.put("msg", "提现失败");
                        logger.info("错误代码" + cashPostal.getErrorCode() + "-------" + "错误提示--------" + cashPostal.getErrorMsg());
                    }
                } else {
                    resultMap.put("code", "88888");
                    resultMap.put("msg", "亲,两次提现间隔不能小于1分钟");
                }
            }
        } else {
            if (isWithdrawalsFirst == 0) {
                if (Double.valueOf(withdrawalsPrice) < 1) {
                    resultMap.put("msg", "最低提现1元,请重新申请");
                    resultMap.put("code", "11000");
                } else {
                    resultMap.put("msg", "提现金额¥" + withdrawalsPrice + "元,由财务MM代您向国家缴纳6％的营业税");
                    resultMap.put("code", "12000");
                }
            } else {
                //调用提现接口 
                ServiceContext serviceContext = new ServiceContext();
                CashPostalModel cashPstalModel = new CashPostalModel();
                cashPstalModel.setAccountId(customer.getId());
                cashPstalModel.setCashPostalMoney(Double.valueOf(withdrawalsPrice));
                cashPstalModel.setResellerType(1);
                cashPstalModel.setUserType(2);

                Result<Boolean> cashPostal = cashPostalService.cashPostal(cashPstalModel, serviceContext);
                if (cashPostal.getData() != null) {
                    if (cashPostal.getData()) {
                        resultMap.put("code", "10000");
                        resultMap.put("msg", "提现成功");
                    } else {
                        resultMap.put("code", "99999");
                        resultMap.put("msg", "提现失败");
                        logger.info("错误代码" + cashPostal.getErrorCode() + "-------" + "错误提示--------" + cashPostal.getErrorMsg());
                    }
                } else {
                    resultMap.put("code", "88888");
                    resultMap.put("msg", "亲,两次提现间隔不能小于1分钟");
                }
            }
        }
        return resultMap;
    }

}

