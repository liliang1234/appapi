package com.pzj.modules.appapi.api;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pzj.base.common.utils.PageBean;
import com.pzj.base.common.utils.PageList;
import com.pzj.base.common.utils.PageModel;
import com.pzj.common.util.CheckUtils;
import com.pzj.common.util.DateUtils;
import com.pzj.customer.entity.Customer;
import com.pzj.modules.appapi.api.dto.AccountDetailFlowDto;
import com.pzj.modules.appapi.api.dto.AccountInfo;
import com.pzj.modules.appapi.entity.CodeHandle;
import com.pzj.modules.appapi.entity.JsonEntity;
import com.pzj.settlement.base.common.enums.ResellerAccountDict;
import com.pzj.settlement.base.common.global.BalanceDict;
import com.pzj.settlement.base.entity.AccountBalance;
import com.pzj.settlement.base.entity.AccountDetailFlow;
import com.pzj.settlement.base.entity.SellChecking;
import com.pzj.settlement.base.service.IAccountService;

/**
 * 结算
 * 
 * @author wangkai
 * @date 2015年11月6日 下午8:49:52
 */
@Component
public class SettleService {

    private static final Logger                         logger = LoggerFactory.getLogger(NewOrderService.class);

    @Autowired
    private BizAccountService                           bizAccountService;

    @Autowired
    private IAccountService                             accountService;

    @Autowired
    private com.pzj.settlement.base.api.SellCheckingApi sellCheckingApiImpl;

    /**
     * 格式化类 四舍五入保留两位小舒
     * @param value
     * @return
     */
    public Double formateRound(double value) {
        double doubleValue = new BigDecimal(value).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

        return doubleValue;
    }

    public JsonEntity getAccountInfo(JSONObject data, Customer customer, JsonEntity json) {

        AccountInfo info = new AccountInfo();
        info.setBalanceOfAvailable(0.0); //其他可用金额
        info.setBalanceOfFrozen(0.0); //其他冻结金额
        info.setBalanceOfAvailableW(0.0); // 微店可用金额
        info.setBalanceOfFrozenW(0.0); //微店冻结金额
        List<Long> dictList = Arrays.asList(ResellerAccountDict.OTHER_AVAILABLE.getId(), ResellerAccountDict.OTHER_FROZEN.getId(),
            ResellerAccountDict.OTHER_AVAILABLE_QRCODE.getId(), ResellerAccountDict.OTHER_FROZEN_QRCODE.getId());
        List<AccountBalance> accountNew = bizAccountService.getAccountNew(customer, dictList);
        if (CollectionUtils.isNotEmpty(accountNew)) {
            for (AccountBalance account : accountNew) {
                if (ResellerAccountDict.OTHER_AVAILABLE.getId() == account.getAccountDictId().longValue())
                    info.setBalanceOfAvailable(formateRound(account.getFinalPeriod()));
                else if (ResellerAccountDict.OTHER_FROZEN.getId() == account.getAccountDictId().longValue())
                    info.setBalanceOfFrozen(formateRound(account.getFinalPeriod()));
                else if (ResellerAccountDict.OTHER_AVAILABLE_QRCODE.getId() == account.getAccountDictId().longValue())
                    info.setBalanceOfAvailableW(formateRound(account.getFinalPeriod()));
                else if (ResellerAccountDict.OTHER_FROZEN_QRCODE.getId() == account.getAccountDictId().longValue()) {
                    info.setBalanceOfFrozenW(formateRound(account.getFinalPeriod()));
                }
            }
        }

        String currentPage = "1";// 当前页数
        String pageSize = "1";// 每页记录数
        String accountingMonthBegin = "";
        String accountingMonthEnd = "";
        int status = BalanceDict.SettlementBalanceStatus.CREATED.key;

        PageList<SellChecking> pageList = getSellCheckingPageList(customer, currentPage, pageSize, accountingMonthBegin, accountingMonthEnd, status);
        PageBean pageBean = pageList.getPageBean();
        if (pageBean != null)
            info.setSellCheckingCount(Integer.valueOf(String.valueOf(pageBean.getTotalCount())));

        if (json == null)
            json = new JsonEntity();
        json.setCode(CodeHandle.SUCCESS.getCode());
        json.setMessage(CodeHandle.SUCCESS.getMessage());
        json.setResponseBody(info);

        return json;
    }

    /**
     * 查询销售对账列表
     */
    public JsonEntity querySellCheck(JSONObject data, Customer customer, JsonEntity json) {
        // 接收参数
        String currentPage = data.containsKey("currentPage") ? data.getString("currentPage") : "1";// 当前页数
        String pageSize = data.containsKey("pageSize") ? data.getString("pageSize") : "10";// 每页记录数
        String accountingMonthBegin = data.containsKey("accountingMonthBegin") ? data.getString("accountingMonthBegin") : "";
        String accountingMonthEnd = data.containsKey("accountingMonthEnd") ? data.getString("accountingMonthEnd") : "";
        Integer status = data.containsKey("status") ? Integer.valueOf(data.getString("status")) : 0;
        try {
            PageList<SellChecking> list = getSellCheckingPageList(customer, currentPage, pageSize, accountingMonthBegin, accountingMonthEnd, status);

            List<SellChecking> resultList = list.getResultList();
            List<Map<String, Object>> jsonArray = Lists.newArrayList();
            for (SellChecking checking : resultList) {
                Map<String, Object> jsonObject = Maps.newHashMap();
                jsonObject = jsonObjectInfo(checking, jsonObject);
                jsonArray.add(jsonObject);
            }
            Map<String, Object> object = Maps.newHashMap();
            object.put("list", jsonArray);
            json.setCode(CodeHandle.SUCCESS.getCode() + "");
            json.setMessage(CodeHandle.SUCCESS.getMessage());
            json.setResponseBody(object);
        } catch (Exception e) {
            e.printStackTrace();
            json.setCode(CodeHandle.FAILURE.getCode() + "");
            json.setMessage(CodeHandle.FAILURE.getMessage());
        }
        return json;
    }

    private PageList<SellChecking> getSellCheckingPageList(Customer customer, String currentPage, String pageSize, String accountingMonthBegin,
                                                           String accountingMonthEnd, int status) {
        // 设置分页信息
        PageModel page = new PageModel(Integer.parseInt(currentPage), Integer.parseInt(pageSize), null);
        // 设置参数
        SellChecking sellChecking = new SellChecking();
        sellChecking.setDistributorId(customer.getId());
        if (CheckUtils.isNotNull(accountingMonthBegin)) {
            sellChecking.setAccountingMonthBegin(DateUtils.parseDate(accountingMonthBegin));
        }
        if (CheckUtils.isNotNull(accountingMonthEnd)) {
            sellChecking.setAccountingMonthEnd(DateUtils.parseDate(accountingMonthEnd));
        }
        if (status > 0) {
            sellChecking.setStatus(status);
        }

        logger.info("getSellCheckingPageList,param = " + JSON.toJSONString(sellChecking));
        PageList<SellChecking> pages = sellCheckingApiImpl.querySellCheck(page, sellChecking);
        logger.info("getSellCheckingPageList,result = " + pages.isEmpty());
        return pages;
    }

    /**
     * 查询销售对账单
     * 
     * @param customer
     *            销售对账单id
     * @return
     */
    public JsonEntity querySellCheckById(JSONObject data, Customer customer, JsonEntity json) {
        Long sellCheckingId = data.containsKey("sellCheckingId") ? data.getLong("sellCheckingId") : 0;
        SellChecking checking = sellCheckingApiImpl.querySellCheckById(sellCheckingId);
        Map<String, Object> jsonObject = Maps.newHashMap();
        try {
            if (CheckUtils.isNull(checking)) {
                json.setCode(CodeHandle.ERROE_20000.getCode() + "");
                json.setMessage(CodeHandle.ERROE_20000.getMessage());
            } else {
                jsonObject = jsonObjectInfo(checking, jsonObject);
                json.setCode(CodeHandle.SUCCESS.getCode() + "");
                json.setMessage(CodeHandle.SUCCESS.getMessage());
                json.setResponseBody(jsonObject);
            }
        } catch (Exception e) {
            e.printStackTrace();
            json.setCode(CodeHandle.FAILURE.getCode() + "");
            json.setMessage(CodeHandle.FAILURE.getMessage());
        }
        return json;
    }

    /**
     * 查询销售对账单-产品详情
     * 
     * @param customer
     *            销售对账单id
     * @return
     */
    public JsonEntity querySettlementByProductId(JSONObject data, Customer customer, JsonEntity json) {
        // 接收参数
        Long sellCheckingId = data.containsKey("sellCheckingId") ? data.getLong("sellCheckingId") : 0;
        try {
            List<Map<String, Object>> querySettlementByProductId = sellCheckingApiImpl.querySettlementByProductId(sellCheckingId);
            List<Map<String, Object>> jsonArray = Lists.newArrayList();
            for (Map<String, Object> map : querySettlementByProductId) {
                Map<String, Object> jsonObject = Maps.newHashMap();
                jsonObject.put("create_time", String.valueOf(map.get("create_time")));//创建时间
                jsonObject.put("currency", String.valueOf(map.get("currency")));//币种
                jsonObject.put("sell_order_id", String.valueOf(map.get("sell_order_id")));//订单号
                jsonObject.put("amount_payable", String.valueOf(map.get("amount_payable")));//本期应付平台费
                jsonObject.put("product_id", String.valueOf(map.get("product_id")));//产品id
                jsonObject.put("settle_id", String.valueOf(map.get("settle_id")));//结算id
                jsonObject.put("product_name", String.valueOf(map.get("product_name")));//产品名字
                jsonObject.put("oc", String.valueOf(map.get("oc")));//订单数量汇总
                jsonObject.put("oa", String.valueOf(map.get("oa")));//订单金额汇总
                jsonObject.put("rc", String.valueOf(map.get("rc")));//退票数量汇总
                jsonObject.put("ra", String.valueOf(map.get("ra")));//退票总额汇总
                jsonObject.put("sa", String.valueOf(map.get("sa")));//结算额汇总
                jsonObject.put("jinb", String.valueOf(map.get("jb")));//金币汇总
                jsonObject.put("jif", String.valueOf(map.get("jif")));// 积分汇总
                jsonArray.add(jsonObject);
            }
            Map<String, Object> object = Maps.newHashMap();
            object.put("list", jsonArray);
            json.setCode(CodeHandle.SUCCESS.getCode() + "");
            json.setMessage(CodeHandle.SUCCESS.getMessage());
            json.setResponseBody(object);
        } catch (Exception e) {
            e.printStackTrace();
            json.setCode(CodeHandle.FAILURE.getCode() + "");
            json.setMessage(CodeHandle.FAILURE.getMessage());
        }
        return json;
    }

    /**
     * 查询销售对账单-结算单-产品详情
     * 
     * @param customer
     *            销售对账单id
     * @return
     */
    public JsonEntity querySettlementBySettleId(JSONObject data, Customer customer, JsonEntity json) {
        // 接收参数
        Long sellCheckingId = data.containsKey("sellCheckingId") ? data.getLong("sellCheckingId") : 0;

        try {
            List<Map<String, Object>> mentList = sellCheckingApiImpl.querySettlementBySettleId(sellCheckingId);
            List<Map<String, Object>> jsonArray = Lists.newArrayList();
            for (Map<String, Object> map : mentList) {
                Map<String, Object> jsonObject = Maps.newHashMap();

                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String create_time = format.format(format.parse(String.valueOf(map.get("create_time"))));
                logger.info("1测试查看create_time是：" + create_time);
                jsonObject.put("create_time", create_time);//创建时间
                logger.info("1测试查看jsonObject的createtime是：" + jsonObject.get("create_time"));
                jsonObject.put("currency", String.valueOf(map.get("currency")));//币种
                jsonObject.put("sell_order_id", String.valueOf(map.get("sell_order_id")));//订单号
                jsonObject.put("amount_payable", String.valueOf(map.get("amount_payable")));//本期应付平台费
                jsonObject.put("amount_received", String.valueOf(map.get("amount_received")));//本期应收
                jsonObject.put("received_actually", String.valueOf(map.get("received_actually")));//已收_实际
                jsonObject.put("received_balance_discount", String.valueOf(map.get("received_balance_discount")));//已收_账扣
                jsonObject.put("product_id", String.valueOf(map.get("product_id")));//产品id
                jsonObject.put("settle_id", String.valueOf(map.get("settle_id")));//结算id
                jsonObject.put("settle_num", String.valueOf(map.get("settle_num")));//结算单号
                jsonObject.put("product_name", map.get("product_name"));//产品名字
                jsonObject.put("order_count", String.valueOf(map.get("order_count")));//订单数量
                jsonObject.put("order_amount", String.valueOf(map.get("order_amount")));//订单金额
                jsonObject.put("refund_count", String.valueOf(map.get("refund_count")));//退票数量
                jsonObject.put("refund_amount", String.valueOf(map.get("refund_amount")));//退票总额
                jsonObject.put("settle_amount", String.valueOf(map.get("settle_amount")));//结算额
                jsonObject.put("rebate_item", String.valueOf(map.get("rebate_item")));//返佣项
                jsonObject.put("rebate_value", String.valueOf(map.get("rebate_value")));//返佣值
                jsonArray.add(jsonObject);
            }
            Map<String, Object> object = Maps.newHashMap();
            object.put("list", jsonArray);
            json.setCode(CodeHandle.SUCCESS.getCode() + "");
            json.setMessage(CodeHandle.SUCCESS.getMessage());
            json.setResponseBody(object);
        } catch (Exception e) {
            e.printStackTrace();
            json.setCode(CodeHandle.FAILURE.getCode() + "");
            json.setMessage(CodeHandle.FAILURE.getMessage());
        }
        return json;
    }

    /**
     * 分销商确认
     * 
     * @param customer
     *            销售对账单id
     * @return 10000:成功
     */
    public JsonEntity confirmSellCheck(JSONObject data, Customer customer, JsonEntity json) {
        Long sellCheckingId = data.containsKey("sellCheckingId") ? data.getLong("sellCheckingId") : 0;
        Map<String, String> map = sellCheckingApiImpl.confirmSellCheck(sellCheckingId);
        if ("success".equals(map.get("msg"))) {
            json.setCode(CodeHandle.SUCCESS.getCode() + "");
            json.setMessage(CodeHandle.SUCCESS.getMessage());
        } else {
            json.setCode(CodeHandle.ERROE_20014.getCode() + "");
            json.setMessage(CodeHandle.ERROE_20014.getMessage());
        }
        return json;
    }

    /**
     * 分销商拒绝
     * 
     * @param customer
     *            销售对账单id
     * @return 10000:成功
     */
    public JsonEntity refuseSellCheck(JSONObject data, Customer customer, JsonEntity json) {
        Long sellCheckingId = data.containsKey("sellCheckingId") ? data.getLong("sellCheckingId") : 0;
        Map<String, String> map = sellCheckingApiImpl.refuseSellCheck(sellCheckingId);
        json.setResponseBody(map);
        if ("success".equals(map.get("msg"))) {
            json.setCode(CodeHandle.SUCCESS.getCode() + "");
            json.setMessage(CodeHandle.SUCCESS.getMessage());
        } else {
            json.setCode(CodeHandle.ERROE_20013.getCode() + "");
            json.setMessage(CodeHandle.ERROE_20013.getMessage());
            CodeHandle.ERROE_20013.getCode();
        }
        return json;
    }

    /**提取出公共方法将SellChecking中信息放入jsonObject中
     * 
     * @param checking
     * @param jsonObject
     */
    private Map<String, Object> jsonObjectInfo(SellChecking checking, Map<String, Object> jsonObject) {
        jsonObject.put("checkingId", String.valueOf(checking.getId()));// 对账单id
        jsonObject.put("checkingNum", String.valueOf(checking.getCheckingNum()));// 对账单号 对账单生成日期（YYMMDD+5位小流水） 
        jsonObject.put("status", String.valueOf(checking.getStatus()));// 状态 已创建 对账中 已对账 退回 取消 
        jsonObject.put("distributorId", String.valueOf(checking.getDistributorId()));//分销商 ID从订单获取
        jsonObject.put("distributor", checking.getDistributor());//分销商 从订单获取 
        jsonObject.put("accountingMonthBegin",
            checking.getAccountingMonthBegin() == null ? "" : String.valueOf(DateUtils.formatDateTime(checking.getAccountingMonthBegin())));//账期开始日期
        jsonObject.put("accountingMonthEnd",
            checking.getAccountingMonthEnd() == null ? "" : String.valueOf(DateUtils.formatDateTime(checking.getAccountingMonthEnd())));//账期结束日期
        jsonObject.put("sendDate", checking.getSendDate() == null ? "" : String.valueOf(DateUtils.formatDateTime(checking.getSendDate())));//出账日期 财务点击发送对账单至分销商的日期
        jsonObject.put("confirmDate", checking.getConfirmDate() == null ? "" : String.valueOf(DateUtils.formatDateTime(checking.getConfirmDate())));// 分销商确认日期 自动获取分销商整单确认完毕的日期 即状态更新为 已对账 的日期 
        jsonObject.put("createTime", checking.getCreateTime() == null ? "" : String.valueOf(DateUtils.formatDateTime(checking.getCreateTime())));// 创建时间
        jsonObject.put("updateTime", checking.getUpdateTime() == null ? "" : String.valueOf(DateUtils.formatDateTime(checking.getUpdateTime())));//更新时间 
        jsonObject.put("settleAmount", String.valueOf(checking.getSettleAmount()));//本期应收   求和结算单中结算额的汇总数
        jsonObject.put("orderPayment", String.valueOf(checking.getOrderPayment()));//订单总额 结算单头上“订单总金额”汇总
        jsonObject.put("remainingDeposits", String.valueOf(checking.getRemainingDeposits()));//预存余额 =账户-（大于账期至日期）的账户明细记录的金额
        jsonObject.put("operatorId", String.valueOf(checking.getOperatorId()));//操作人ID 
        jsonObject.put("operator", checking.getOperator());//操作人 
        jsonObject.put("otherSalePromotion", String.valueOf(checking.getOtherSalePromotion()));// 其他促销
        jsonObject.put("providerId", String.valueOf(checking.getProviderId()));//供应商id
        jsonObject.put("provider", checking.getProvider());//供应商
        jsonObject.put("mfSalePromotion", String.valueOf(checking.getMfSalePromotion()));// 魔方促销 
        jsonObject.put("settleReceived", String.valueOf(checking.getSettleReceived()));//本期已收
        jsonObject.put("otherPayable", String.valueOf(checking.getOtherPayable()));//应付_其它
        jsonObject.put("otherReceivedAmount", String.valueOf(checking.getOtherReceivedAmount()));//预收_其它 金额  结算单分配行上“其它结算项目”=金币的汇总值 
        jsonObject.put("accountPayable", String.valueOf(checking.getAccountPayable()));//本期应付平台费
        jsonObject.put("lackOfAmount", String.valueOf(checking.getLackOfAmount()));//本期欠费=本期应收-本期已收-预存余额+本期应付平台费
        jsonObject.put("returnAmount", String.valueOf(checking.getReturnAmount()));//退票总额  结算单头上“退票总金额”汇总 
        jsonObject.put("accountingCompany", checking.getAccountingCompany());//核算单位 从订单获取 北京魔方旅游/成都魔方旅游 
        return jsonObject;
    }

    public JsonEntity getAccountDetailFlow(JSONObject data, Customer customer, JsonEntity json) {
        try {
            Integer pageIndex = 1;
            if (data.containsKey("currentPage"))
                pageIndex = data.getInteger("currentPage");
            Integer pageSize = 10;
            if (data.containsKey("pageSize"))
                pageSize = data.getInteger("pageSize");

            AccountDetailFlow vo = new AccountDetailFlow();
            vo.setResellerId(customer.getId());
            vo.setResellerAccountName(ResellerAccountDict.OTHER_AVAILABLE.getName());
            logger.info("查询交易明细accountService.queryPageByParamMap的type是：" + data.getInteger("type"));
            if (data.containsKey("type")) {
                Integer type = data.getInteger("type");
                logger.info("查询交易明细accountService.queryPageByParamMap进入判断的type是：" + type
                            + "type.toString().equals(String.valueOf(ResellerAccountDict.OTHER_FROZEN.getId()))为："
                            + type.toString().equals(String.valueOf(ResellerAccountDict.OTHER_FROZEN.getId())));
                if (type.toString().equals(String.valueOf(ResellerAccountDict.OTHER_FROZEN.getId()))) {
                    logger.info("进入到判断查询交易明细");
                    vo.setResellerAccountName(ResellerAccountDict.OTHER_FROZEN.getName());
                    List<Integer> typeList = new ArrayList<Integer>();
                    typeList.add(20);
                    typeList.add(21);
                    typeList.add(36);
                    typeList.add(37);
                    typeList.add(7);
                    logger.info("vo.getTypes的size111为：" + typeList.size());
                    vo.setTypes(typeList);
                }
            }
            PageModel pager = new PageModel(pageIndex, pageSize, " create_time desc");
            logger.info("查询交易明细accountService.queryPageByParamMap传入的参数是：setResellerAccountName:" + vo.getResellerAccountName() + "vo.setResellerId:"
                        + vo.getResellerId());
            PageList<AccountDetailFlow> accountDetailFlowPageList = accountService.queryPageByParamMap(vo, pager);

            List<AccountDetailFlow> resultList = accountDetailFlowPageList.getResultList();
            //            logger.info("resultList的size是：" + resultList.size());
            accountService.fillAccountBalance(resultList);
            logger.info("resultList的size是：" + resultList.size());
            //            Iterator<AccountDetailFlow> iterator = resultList.iterator();
            //            while (iterator.hasNext()) {
            //                AccountDetailFlow accountDetailFlow = iterator.next();
            //                Integer businessType = accountDetailFlow.getBusinessType();
            //                if (notRebate(businessType)) {
            //                    iterator.remove();
            //                }
            //            }
            //            logger.info("resultList的size是：" + resultList.size());
            Map<String, Object> result = new HashMap<String, Object>(1);
            result.put("resultList", convert(resultList));
            json.setCode(CodeHandle.SUCCESS.getCode() + "");
            json.setMessage(CodeHandle.SUCCESS.getMessage());

            json.setResponseBody(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }

    /**
     * 判断是否是返利
     * @param businessType
     * @return
     */
    //    private boolean notRebate(Integer businessType) {
    //        if (businessType.equals(20)) {
    //            return false;
    //        }
    //        if (businessType.equals(21)) {
    //            return false;
    //        }
    //        if (businessType.equals(36)) {
    //            return false;
    //        }
    //        if (businessType.equals(37)) {
    //            return false;
    //        }
    //        return true;
    //    }

    public List<AccountDetailFlowDto> convert(List<AccountDetailFlow> resultList) {
        if (CollectionUtils.isNotEmpty(resultList)) {
            List<AccountDetailFlowDto> result = new ArrayList<AccountDetailFlowDto>(resultList.size());
            for (AccountDetailFlow detailFlow : resultList) {
                AccountDetailFlowDto dto = new AccountDetailFlowDto();
                dto.setAccountBalanceMoney(detailFlow.getAccountBalanceMoney());
                dto.setCreateTime(detailFlow.getCreateTime());
                dto.setOrderId(detailFlow.getOrderId());
                dto.setResellerAcountMoney(detailFlow.getResellerAcountMoney());
                dto.setSupplierName(detailFlow.getSupplierName());
                dto.setRemarks(detailFlow.getRemarks());
                result.add(dto);
            }
            return result;
        }
        return Collections.EMPTY_LIST;
    }

}
