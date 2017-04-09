package com.pzj.modules.appapi.api.order.impl;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pzj.appapi.vo.OrderVo;
import com.pzj.appapi.vo.OrderVo.TicketVo;
import com.pzj.base.common.CheckUtils;
import com.pzj.base.common.global.GlobalDict;
import com.pzj.base.entity.product.ProductSiteData;
import com.pzj.base.service.product.IProductSiteDataService;
import com.pzj.channel.Strategy;
import com.pzj.channel.vo.resultParam.PCStrategyResult;
import com.pzj.common.util.DateUtils;
import com.pzj.core.stock.model.StockModel;
import com.pzj.core.stock.model.StockQueryRequestModel;
import com.pzj.core.stock.service.StockQueryService;
import com.pzj.framework.context.Result;
import com.pzj.framework.context.ServiceContext;
import com.pzj.framework.converter.JSONConverter;
import com.pzj.modules.appapi.api.order.OrderCheckService;
import com.pzj.modules.appapi.entity.CodeHandle;
import com.pzj.modules.appapi.exception.AppapiParametersException;
import com.pzj.platform.appapi.dubbo.SkuProductApi;
import com.pzj.platform.appapi.util.PropertyLoader;
import com.pzj.product.global.SkuProductGlobal;
import com.pzj.product.service.ISkuProductService;
import com.pzj.product.vo.product.SkuProduct;
import com.pzj.product.vo.voParam.resultParam.SkuProductResultVO;
import com.pzj.product.vo.voParam.resultParam.SpuProductResultVO;
import com.pzj.trade.order.entity.MerchResponse;
import com.pzj.voucher.common.ExecuteResult;
import com.pzj.voucher.entity.VoucherConfirm;
import com.pzj.voucher.entity.VoucherEntity;
import com.pzj.voucher.service.VoucherService;

@Service
public class OrderCheckServiceImpl implements OrderCheckService {

    private static final DateFormat buyDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private static final Logger     logger        = LoggerFactory.getLogger(OrderCheckServiceImpl.class);
    @Autowired
    private ISkuProductService      iskuProductService;
    @Autowired
    private StockQueryService       stockQueryService;
    @Resource(name = "propertyLoader")
    private PropertyLoader          propertyLoader;
    @Autowired
    private IProductSiteDataService productSiteDataService;
    @Resource(name = "voucherService")
    private VoucherService          voucherService;
    @Autowired
    private SkuProductApi           skuApi;

    @Override
    public CodeHandle checkOrderBuy(Long resellerId, String salesType, String buyDate, Long productId, Integer num, Integer productType, Integer totalNum) {
        SpuProductResultVO vo = skuApi.findSkuById(resellerId, productId, salesType);
        if (vo != null) {
            if (vo.getSkuProductList() == null || vo.getSkuProductList().isEmpty()) {
                return CodeHandle.ORDER_ERROE_20016;//无有效的产品
            }
            SkuProduct sku = vo.getSkuProductList().get(0);
            Map<Long, List<PCStrategyResult>> map = vo.getStrategyList();
            if (map == null || map.isEmpty()) {
                return CodeHandle.ORDER_ERROE_20015;//无有效渠道
            }

            List<PCStrategyResult> list = map.get(productId);
            if (list == null || list.isEmpty()) {
                return CodeHandle.ORDER_ERROE_20015;//无有效渠道
            }
            PCStrategyResult result = list.get(0);
            if (result.getStrategyList() == null || result.getStrategyList().isEmpty()) {
                return CodeHandle.ORDER_ERROE_20015;//无有效渠道
            }
            Strategy strategy = result.getStrategyList().get(0);

            //有控制日期的限制！
            CodeHandle re1 = checkOrderDate(buyDate, strategy);
            if (!CodeHandle.SUCCESS.getCode().equals(re1.getCode())) {
                return re1;
            }
            //如果是非演艺和景区，库存是挂在SKU上的，根据SKU进行查询，景区和演艺的库存是挂在SPU(实际上也是挂在sku上的，sku上的库存ID都相同)上的，不能这么查询
            if (productType != GlobalDict.ProductCategory.normal && productType != GlobalDict.ProductCategory.scenic) {
                //如果不是无限库存，验证库存
                if (!sku.getUnlimitedInventory())
                    re1 = checkOrderStock(sku, buyDate, num);
            }
            if (!CodeHandle.SUCCESS.getCode().equals(re1.getCode())) {
                return re1;
            }
            //产品确认，订单起定量为订单的总数
            re1 = checkOrderNum(strategy, totalNum);
            if (!CodeHandle.SUCCESS.getCode().equals(re1.getCode())) {
                return re1;
            }
        }
        return CodeHandle.SUCCESS;
    }

    @Override
    public CodeHandle checkScenicProductStock(Long resellerId, String salesPort, String buyDate, Long productId, Integer num) {
        SpuProductResultVO vo = skuApi.findSkuById(resellerId, productId, salesPort);
        if (vo != null) {
            if (vo.getSkuProductList() == null || vo.getSkuProductList().isEmpty()) {
                return CodeHandle.ORDER_ERROE_20016;//无有效的产品
            }
            SkuProduct sku = vo.getSkuProductList().get(0);
            Map<Long, List<PCStrategyResult>> map = vo.getStrategyList();
            if (map == null || map.isEmpty()) {
                return CodeHandle.ORDER_ERROE_20015;//无有效渠道
            }

            List<PCStrategyResult> list = map.get(productId);
            if (list == null || list.isEmpty()) {
                return CodeHandle.ORDER_ERROE_20015;//无有效渠道
            }
            PCStrategyResult result = list.get(0);
            if (result.getStrategyList() == null || result.getStrategyList().isEmpty()) {
                return CodeHandle.ORDER_ERROE_20015;//无有效渠道
            }

            /*CodeHandle re1 = checkOrderStock(sku, buyDate, num);
            if (!CodeHandle.SUCCESS.getCode().equals(re1.getCode())) {
                return re1;
            }*/
        }
        return CodeHandle.SUCCESS;

    }

    private CodeHandle checkOrderNum(Strategy strategy, Integer num) {
        //最大购买限制和最小购买限制

        Integer maxNum = strategy.getMostPerdueNumber();
        Integer minNum = strategy.getLeastPerdueNumber();
        if (num < minNum) {
            return CodeHandle.ORDER_ERROE_20029;
        }
        if (num > maxNum) {
            return CodeHandle.ORDER_ERROE_20030;
        }
        return CodeHandle.SUCCESS;
    }

    private CodeHandle checkOrderDate(String buyDate, Strategy strategy) {
        //现判断不可购买当天日期之前的门票！
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(DateUtils.parseDate(buyDate));

        Date curentDate = new Date();
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(curentDate);

        boolean checkYear = cal1.get(Calendar.YEAR) >= cal2.get(Calendar.YEAR);
        //        boolean checkMonth = checkYear && cal1.get(Calendar.MONTH) >= cal2.get(Calendar.MONTH);
        boolean checkMonth = true;
        if (!checkYear) {
            if (cal1.get(Calendar.MONTH) <= cal2.get(Calendar.MONTH)) {
                checkMonth = false;
            }
        }
        if (!checkMonth) {
            return CodeHandle.ERROE_20009;
        }
        int aDay = cal1.get(Calendar.DAY_OF_MONTH);
        int bDay = cal2.get(Calendar.DAY_OF_MONTH);
        //同月才会比较天数，如果购买月大于当前月。不需要比较天数
        if (cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) && aDay < bDay)
            return CodeHandle.ERROE_20009;
        /*       else if (aDay < bDay) {
                   SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                   String format1 = sdf.format(buyDate);
                   String format2 = sdf.format(curentDate);
                   logger.error("不可购买以前时间的票，请求的时间是 " + format1 + "， 现在的时间是 " + format2);
                   return CodeHandle.ERROE_20009;
               }*/
        //判断是否有日期限制
        if (strategy.getIsLimitAdvanceDue() != null && strategy.getIsLimitAdvanceDue() > 0) {
            Calendar now = Calendar.getInstance();
            Calendar buy = Calendar.getInstance();
            try {
                Date buyTime = buyDateFormat.parse(buyDate);
                buy.setTime(buyTime);
            } catch (Exception ex) {
                logger.error("", ex);
                logger.error("buyDate输入格式错误：{}", buyDate);
            }
            //这个判断有点绕
            //用当前时间，加上往前拨多少时间，然后和下单时间进行判断来处理的
            buy.set(Calendar.HOUR_OF_DAY, strategy.getAdvanceDueHour());
            buy.set(Calendar.MINUTE, strategy.getAdvanceDueMinute());
            buy.set(Calendar.SECOND, 0);
            buy.set(Calendar.MILLISECOND, 0);
            now.add(Calendar.DATE, strategy.getAdvanceDueDays());
            if (now.after(buy)) {
                return CodeHandle.ORDER_ERROE_20028;
            }
        }
        return CodeHandle.SUCCESS;
    }

    public CodeHandle checkOrderStock(SkuProduct sku, String buyDate, Integer num) {
        //TODO ，现在这个数据也有问题，因为SKU上的unlimitedInventory是false，但是获取不到ruleID
        //也即是说，他不是无限库存，但是还没有库存ID，所以数据有问题，需要跟胡晓娜沟通一下
        if (!sku.getUnlimitedInventory()) {
            Long ruleId = sku.getStockRuleId();
            if (ruleId == null) {
                logger.error("SKU{} is not unlimitedInventory,but no ruleId.", sku.getId());
                return CodeHandle.SUCCESS;
            }
            StockQueryRequestModel param = new StockQueryRequestModel();
            param.setRuleId(ruleId);
            param.setStockTime(buyDate);
            logger.info("OrderCheckServiceImpl rule id ::" + ruleId);
            logger.info("OrderCheckServiceImpl stock time ::" + buyDate);
            Result<StockModel> result = stockQueryService.queryStockByRule(param, ServiceContext.getServiceContext());
            if (result.getData().getRemainNum() < num) {
                return CodeHandle.ERROE_20002;
            }
        }
        return CodeHandle.SUCCESS;
    }

    @Override
    public CodeHandle checkOrderRefund(Long resellerId, String salesPort, List<MerchResponse> refundMerchandiseList) {
        try {
            for (MerchResponse vo : refundMerchandiseList) {
                SkuProductResultVO skuResult = iskuProductService.findSkuProductById(vo.getProductId());
                SkuProduct sku = skuResult.getSkuProduct();
                //只要不是不退，这里就不处理
                if (!SkuProductGlobal.REFUND_MONEY_TYPE_NO.equals(sku.getPrerefundDistributorFeetype()))
                    continue;
                //如果是出游日期当天计算
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DATE, sku.getProrefundDays());
                calendar.set(Calendar.HOUR_OF_DAY, sku.getProrefundHour());
                calendar.set(Calendar.MINUTE, sku.getProrefundMinute());
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);

                if (SkuProductGlobal.REFUND_DATE_TYPE_PLAYTIME.equals(sku.getRefundDateType())) {
                    if (calendar.after(vo.getStart_time())) {
                        return CodeHandle.ORDER_ERROE_20031;
                    }
                } else if (SkuProductGlobal.REFUND_DATE_TYPE_LASTTIME.equals(sku.getRefundDateType())) {
                    if (calendar.after(vo.getExpire_time())) {
                        return CodeHandle.ORDER_ERROE_20031;
                    }
                }
            }
            return CodeHandle.SUCCESS;

        } catch (Exception e) {
            logger.error("", e);
        }
        return CodeHandle.FAILURE;
    }

    @Override
    public boolean findExpVerification(OrderVo orderVo, Timestamp startTime, Timestamp expireTime) {
        logger.debug("验证一证一票, 入参：orderVo: {}, startTime: {}, expireTime: {}", JSONConverter.toJson(orderVo), startTime, expireTime);
        //获取有效游客信息及产品信息
        List<TicketVo> tiList = orderVo.getTickets();
        if (CheckUtils.isNull(tiList)) {
            logger.debug("1");
            return false;
        }
        if (!findExpProduct(tiList)) {
            logger.debug("2");
            return false;
        }
        if (!findExpTicket(tiList, startTime, expireTime)) {
            logger.debug("3");
            return false;
        }
        return true;
    }

    private boolean findExpProduct(List<TicketVo> tiList) {
        //获取检票点相同的产品
        /*  String productSiteDataId = propertyLoader.getProperty(ApiConstants.SYSTEM_FILENAME, "product_site_data");
          //为了方便处理，将检票点相同的配置产品全部默认成一个
          Long defaultProduct = null;
          if (CheckUtils.isNotNull(productSiteDataId))
              defaultProduct = Long.valueOf(productSiteDataId.split(",")[0]);
          //游客购买产品的map
        */
        String sparpreisProduct = this.propertyLoader.getProperty("system", "sparpreis_product");
        List<String> sparpreisPList = new ArrayList<String>();
        if (CheckUtils.isNotNull(sparpreisProduct)) {
            sparpreisPList = Arrays.asList(sparpreisProduct.split(","));
        }

        Map<String, String> buyMap = new HashMap<>();
        //一个身份证购买多个产品的名单
        List<String> buyCardList = new ArrayList<>();
        for (TicketVo ticketVo : tiList) {
            String buyCard = ticketVo.getBuyCard();
            /*Long byPorudtcId = (defaultProduct != null && productSiteDataId.contains(ticketVo.getProductId().toString())) ? defaultProduct : ticketVo
                .getProductId();*/
            Long byPorudtcId = ticketVo.getProductId();
            //一证一票身份证和产品id都不可为空
            if (CheckUtils.isNull(buyCard) || CheckUtils.isNull(byPorudtcId))
                return false;

            logger.debug("验证特价产品是否符合,特价产品{},购买的产品{} ", sparpreisProduct, ticketVo.getProductId());

            if ((sparpreisPList.contains(ticketVo.getProductId().toString())) && (!findSparpreisProduct(ticketVo.getProductId(), ticketVo.getBuyCard()))) {
                return false;
            }

            //如果有游客购买了多个产品，需要验证当前购买产品的检票点不冲突
            if (buyMap.get(buyCard) != null) {
                buyMap.put(buyCard, buyMap.get(buyCard).toString() + "," + byPorudtcId);
                buyCardList.add(buyCard);
            } else {
                buyMap.put(buyCard, byPorudtcId.toString());
            }
        }

        if (CheckUtils.isNotNull(buyCardList)) {
            //验证一个游客购买的产品检票点是否冲突
            for (String buyCard : buyCardList) {
                String productIds = buyMap.get(buyCard).toString();
                if (CheckUtils.isNull(productIds))
                    throw new AppapiParametersException(CodeHandle.ERROR10034.getCode(), CodeHandle.ERROR10034.getMessage());
                String productArr[] = productIds.split(",");
                //当前用户购买产品的总检票点数量
                Integer checkCount = 0;
                //用于去除的集合
                List<Long> countList = new ArrayList<>();
                //根据单个查询分别去查询检票点
                for (int i = 0; i < productArr.length; i++) {
                    List<ProductSiteData> siList = productSiteDataService.findByProductId(Long.valueOf(productArr[i]));
                    //获取检票点id集合
                    List<Long> psIds = new ArrayList<>();
                    checkCount += psIds.size();
                    for (ProductSiteData productSiteData : siList) {
                        psIds.add(productSiteData.getId());
                    }
                    countList.removeAll(psIds);
                    countList.addAll(psIds);
                }
                //如果最后去重后的检票点数量和总数量不一致。说明当前用户购买的检票点由重复
                if (checkCount != countList.size())
                    throw new AppapiParametersException(CodeHandle.ERROR10035.getCode(), buyCard + CodeHandle.ERROR10034.getMessage());
            }
        }
        return true;
    }

    private boolean findExpTicket(List<TicketVo> tiList, Timestamp startTime, Timestamp expireTime) {
        for (TicketVo ticketVo : tiList) {
            List<VoucherEntity> voucherList = new ArrayList<>();
            //获取当前身份证的有效voucher
            ExecuteResult<List<VoucherEntity>> executeResult = voucherService.queryVoucherInfo(null, ticketVo.getBuyCard());
            logger.info("根据购买人身份证查询可用凭证信息. buycard: {}, result: {}.", ticketVo.getBuyCard(), executeResult);
            if (!executeResult.getStateCode().equals(ExecuteResult.SUCCESS))
                return false;
            voucherList = executeResult.getData();
            if (CheckUtils.isNotNull(voucherList)) {
                //查询当前产品下所有的检票点
                List<ProductSiteData> siList = productSiteDataService.findByProductId(ticketVo.getProductId());
                //获取检票点id集合
                List<Long> psIds = new ArrayList<>();
                for (ProductSiteData productSiteData : siList) {
                    psIds.add(productSiteData.getId());
                }
                //有效期的voucher集合
                List<VoucherEntity> checkVoucherList = new ArrayList<>();
                for (VoucherEntity voucherEntity : voucherList) {
                    List<VoucherConfirm> cList = voucherEntity.getVoucherConfirmList();
                    //此处，需要先验证product是否是一证一票
                    if (CheckUtils.isNotNull(cList)) {
                        VoucherConfirm vc = cList.get(0);
                        SkuProductResultVO skuProductResultVO = skuApi.findSkuById(vc.getProductId());
                        if (CheckUtils.isNull(skuProductResultVO) || CheckUtils.isNull(skuProductResultVO.getSkuProduct()))
                            throw new AppapiParametersException(CodeHandle.ORDER_ERROE_20016.getCode(), CodeHandle.ORDER_ERROE_20016.getMessage());
                        SkuProduct skuProduct = skuProductResultVO.getSkuProduct();
                        if (!skuProduct.getIsOneVote())
                            continue;
                        //如果是一证一票才会往下验证,否则直接continue;
                        for (VoucherConfirm voucherConfirm : cList) {
                            if (psIds.contains(voucherConfirm.getChildProductId()))
                                checkVoucherList.add(voucherEntity);
                        }
                    }
                }

                //只验证通过以上验证的voucher
                if (CheckUtils.isNotNull(checkVoucherList)) {
                    logger.warn("当前用户在有效期内已经有有效voucher，需要验证时间. checkVoucherList: {}.", JSONConverter.toJson(checkVoucherList));
                    //如果当前用户在有效期内已经有有效voucher，返回fasle
                    if (!findExpDate(voucherList, startTime, expireTime))
                        throw new AppapiParametersException(CodeHandle.ERROR10035.getCode(), CodeHandle.ERROR10035.getMessage());
                }
            }
        }
        return true;
    }

    private boolean findExpDate(List<VoucherEntity> voucherList, Timestamp startTime, Timestamp expireTime) {
        //本次下单的时间
        Long startTimes = startTime.getTime();
        Long endTimes = expireTime.getTime();
        for (VoucherEntity voucherEntity : voucherList) {
            //数据库查询的有效voucehr时间
            Date voucherStartDate = voucherEntity.getStartTime();
            Date voucherEndDate = voucherEntity.getExpireTime();
            if ((startTimes >= voucherStartDate.getTime() && voucherEndDate.getTime() >= startTimes)
                || (endTimes >= voucherStartDate.getTime() && voucherEndDate.getTime() >= endTimes))
                return false;
        }
        return true;

    }

    private boolean findSparpreisProduct(Long productId, String buyCard) {
        logger.debug("开始验证次是否满足购买特价产品条件...");

        Integer sparpreis_age = Integer.valueOf(this.propertyLoader.getProperty("system", productId.toString(), "0"));
        logger.debug("特价票身份证号 {} ", buyCard);
        logger.debug("特价票限制年龄  {} ", sparpreis_age);
        if (buyCard.length() != 18)
            return false;
        if (sparpreis_age.intValue() != 0) {
            String brith = buyCard.substring(6, 14);
            Calendar cl = Calendar.getInstance();
            cl.set(Integer.valueOf(brith.substring(0, 4)).intValue(), Integer.valueOf(brith.substring(4, 6)).intValue() - 1, Integer
                .valueOf(brith.substring(6)).intValue());

            Calendar thisCl = Calendar.getInstance();
            thisCl.setTime(new Date());

            Integer year = Integer.valueOf(thisCl.get(1));

            Integer birthYear = Integer.valueOf(cl.get(1));
            Integer age = Integer.valueOf(year.intValue() - birthYear.intValue());

            Long nowTimers = Long.valueOf(thisCl.getTimeInMillis());

            thisCl.set(year.intValue(), Integer.valueOf(brith.substring(4, 6)).intValue() - 1, Integer.valueOf(brith.substring(6)).intValue());
            Long birthdayTimes = Long.valueOf(thisCl.getTimeInMillis());

            if (nowTimers.longValue() <= birthdayTimes.longValue()) {
                age = Integer.valueOf(age.intValue() - 1);
            }
            logger.debug("计算后的当天年龄 {} ", age);
            if (age.intValue() >= sparpreis_age.intValue())
                return false;
        }
        return true;
    }
}
