package com.pzj.modules.appapi.api.order.impl;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import com.pzj.appapi.vo.OrderVo;
import com.pzj.appapi.vo.OrderVo.TicketVo;
import com.pzj.base.common.CheckUtils;
import com.pzj.base.common.global.GlobalDict;
import com.pzj.base.common.global.GlobalParam;
import com.pzj.base.common.global.UserGlobalDict;
import com.pzj.base.service.show.IScreeningChartService;
import com.pzj.channel.Channel;
import com.pzj.channel.Strategy;
import com.pzj.channel.vo.resultParam.PCStrategyResult;
import com.pzj.common.mapper.JsonMapper;
import com.pzj.common.util.DateUtils;
import com.pzj.core.product.model.ProScreeningsQueryModel;
import com.pzj.core.product.model.ScreeingsModel;
import com.pzj.core.product.service.ActingQueryService;
import com.pzj.core.product.service.ScreeingsQueryService;
import com.pzj.core.stock.context.ShowResponse;
import com.pzj.core.stock.model.CheckSeatsOptionalModel;
import com.pzj.core.stock.model.CheckStockModel;
import com.pzj.core.stock.model.SeatsOptionalResponse;
import com.pzj.core.stock.model.ShowModel;
import com.pzj.core.stock.model.StockModel;
import com.pzj.core.stock.model.StockQueryRequestModel;
import com.pzj.core.stock.service.SeatService;
import com.pzj.core.stock.service.ShowService;
import com.pzj.core.stock.service.StockQueryService;
import com.pzj.customer.entity.Customer;
import com.pzj.framework.context.Result;
import com.pzj.framework.context.ServiceContext;
import com.pzj.framework.converter.JSONConverter;
import com.pzj.modules.appapi.api.BizAccountService;
import com.pzj.modules.appapi.api.order.OrderCheckService;
import com.pzj.modules.appapi.api.order.OrderCreateService;
import com.pzj.modules.appapi.entity.CodeHandle;
import com.pzj.modules.appapi.entity.JsonEntity;
import com.pzj.modules.appapi.exception.AppapiParametersException;
import com.pzj.modules.appapi.voucher.IVoucherCreateService;
import com.pzj.platform.appapi.dubbo.SkuProductApi;
import com.pzj.platform.appapi.model.SkuCloseTimeSlotModel;
import com.pzj.platform.appapi.model.SpuProductModel;
import com.pzj.platform.appapi.service.impl.ProductIdType;
import com.pzj.platform.appapi.util.PropertyLoader;
import com.pzj.product.api.enterprise.service.ScenicService;
import com.pzj.product.api.enterprise.vo.ScenicVO;
import com.pzj.product.global.SkuProductGlobal;
import com.pzj.product.service.ProductActingService;
import com.pzj.product.vo.product.SkuCloseTimeSlot;
import com.pzj.product.vo.product.SkuProduct;
import com.pzj.product.vo.product.SpuProduct;
import com.pzj.product.vo.voParam.resultParam.SkuProductResultVO;
import com.pzj.product.vo.voParam.resultParam.SkuSaledCalendarResult;
import com.pzj.product.vo.voParam.resultParam.SpuProductResultVO;
import com.pzj.sale.api.order.service.OrderUtils;
import com.pzj.settlement.base.common.enums.ResellerAccountDict;
import com.pzj.settlement.base.entity.AccountBalance;
import com.pzj.trade.order.entity.OrderResponse;
import com.pzj.trade.order.model.FilledModel;
import com.pzj.trade.order.vo.PurchaseProductVO;
import com.pzj.trade.order.vo.TradeOrderVO;
import com.pzj.voucher.entity.VoucherEntity;
import com.pzj.voucher.service.VoucherService;

@Service
public class OrderCreateServiceImpl implements OrderCreateService {

    private static final Logger                            logger        = LoggerFactory.getLogger(OrderCreateServiceImpl.class);
    private static final DateFormat                        buyDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    @Resource(name = "propertyLoader")
    private PropertyLoader                                 propertyLoader;
    @Autowired
    private OrderUtils                                     orderUtils;
    @Autowired
    private com.pzj.trade.order.service.OrderService       orderService;
    @Autowired
    private ScenicService                                  scenicService;
    @Autowired
    private com.pzj.platform.appapi.service.ProductService productServicePlatform;
    @Autowired
    private OrderCheckService                              orderCheckService;
    @Autowired
    private IVoucherCreateService                          voucherCreateService;
    @Autowired
    private BizAccountService                              bizAccountService;
    @Resource(name = "voucherService")
    private VoucherService                                 voucherService;
    @Autowired
    private IScreeningChartService                         screeningChartService;
    @Autowired
    private ActingQueryService                             actingQueryService;
    @Autowired
    private SkuProductApi                                  skuApi;
    @Autowired
    private ScreeingsQueryService                          screeingsQueryService;
    @Autowired
    private ProductActingService                           productActingService;

    @Autowired
    private ShowService                                    showService;
    @Autowired
    private StockQueryService                              stockQueryService;
    @Autowired
    private SeatService                                    seatService;

    /**
     * 保存订单(导游下单时取旅行社渠道)
     *
     * @param jsonObject
     *            json参数包
     * @param user
     *            当前登录用户
     * @param json
     *            返回结果包装
     * @key startTime 游玩时间
     * @key cPaidAmount 支付金额
     * @key groupin 是否团进团出
     * @key ticketData 游客信息集合
     * @return JsonEntity 返回类型
     */
    /**
     * 下单需要判断几个东西
     * 1)渠道政策是否存在。如果是魔方渠道和直签渠道同时存在，需要删除魔方渠道
     * 2)产品提前购买时间是否足够
     * 3)产品如果有库存的话，是否足够
     * 4)设置正确的场次和区域信息（未做）
     * 
     * 需要设置几个东西：
     * 1)下单的产品有效期
     * 2)创建voucher
     * 3)设置正确的渠道
     * 
     * 
     */
    @Override
    public JsonEntity saveOrder(JSONObject jsonObject, Customer user) {
        JsonEntity json = new JsonEntity();
        try {
            initJson(jsonObject);
            return saveOrderDo(jsonObject, user, json);
        } catch (AppapiParametersException e) {
            logger.error(e.getMessage(), e);
            json.setCode(e.getCode());
            json.setMessage(e.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            json.setCode(CodeHandle.CODE_90001.getCode() + "");
            json.setMessage(e.getMessage());
        }
        return json;
    }

    private void initJson(JSONObject jsonObject) throws Exception {
        try {

            DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            if (jsonObject.containsKey("buyDate") && CheckUtils.isNotNull(jsonObject.getString("buyDate")))
                jsonObject.put("buyDate", sdf.parse(jsonObject.getString("buyDate")));

            if (jsonObject.containsKey("buyDateEnd") && CheckUtils.isNotNull(jsonObject.getString("buyDateEnd")))
                jsonObject.put("buyDateEnd", sdf.parse(jsonObject.getString("buyDateEnd")));

            if (jsonObject.containsKey("showTime") && CheckUtils.isNotNull(jsonObject.getString("showTime")))
                jsonObject.put("showTime", sdf.parse(jsonObject.getString("showTime")));

        } catch (ParseException e) {
            logger.error(e.getMessage(), e);
            throw AppapiParametersException.createByCodeHandle(CodeHandle.ORDER_ERROE_20024);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        }
    }

    private JsonEntity saveOrderDo(JSONObject jsonObject, Customer user, JsonEntity json) throws Exception {

        logger.info("进入保存订单...");
        // TODO 暂时在这里转换一下
        OrderVo orderVo = toOrderVo(jsonObject);
        TradeOrderVO tradeOrderVo = toTradeOrderVo(orderVo);
        tradeOrderVo.setOperatorId(user.getId());
        tradeOrderVo.setResellerAgentId(0); // TODO 搁置
        tradeOrderVo.setSupplierAgentId(0); // TODO 搁置
        tradeOrderVo.setPayerId(user.getId()); // TOTO 付款者ID暂时不传

        String groupOrder;
        if (user.getResellerType().equals(UserGlobalDict.guideUserType())) {
            groupOrder = String.valueOf(UserGlobalDict.StrategyGlobalDict.groupTicketVarie());
        } else {
            groupOrder = String.valueOf(UserGlobalDict.StrategyGlobalDict.scatterTicketVarie());
        }

        Integer salesPort = UserGlobalDict.StrategyGlobalDict.windowTenantApp();
        Long resellerId = orderVo.getTravel();
        // 如果是导游
        if (user.getResellerType().equals(UserGlobalDict.guideUserType())) {

            tradeOrderVo.setTravel(resellerId);
            tradeOrderVo.setGuideId(user.getId());
            salesPort = UserGlobalDict.StrategyGlobalDict.windowGuideApp();
        } else { // 如果是商户
            resellerId = user.getId();
        }
        //SalePort只有微店才会传，所以值不为空，那么销售端口就是微店
        if (CheckUtils.isNotNull(orderVo.getSalePort()) && orderVo.getSalePort() != 0)
            salesPort = UserGlobalDict.StrategyGlobalDict.windowTenantMicroshop();
        tradeOrderVo.setResellerId(resellerId);
        tradeOrderVo.setSalePort(salesPort);
        orderVo.setSalePort(salesPort);

        List<OrderVo.TicketVo> tickets = orderVo.getTickets();
        Integer productType = orderVo.getProType();
        Long channelId = orderVo.getChannelId();
        Long productInfoId = orderVo.getProductInfoId();
        Long sceneId = orderVo.getScienicId();
        Long supplierId = orderVo.getSupplierId(); // 产品所属供应商
        ScenicVO scenicVO;
        String scenicName = null;
        String contactMobile = orderVo.getContactMobile(); // 下订单人的手机号
        if (StringUtils.isBlank(contactMobile))
            contactMobile = orderVo.getBookMobile();
        String contactName = orderVo.getBookName(); // 下订单人的名字
        String address = orderVo.getRemark(); // 地址，只有特产才有

        if (address != null && address.length() > 500)
            throw AppapiParametersException.createByCodeHandle(CodeHandle.ORDER_ERROE_20018);

        //这个地方需要根据SKUID，查出来真正的screeningId和region，而不能直接用前台的，直接用前台的实际上是有问题的
        //一个订单内只会有一个场次和区域
        SpuProductResultVO spuResultVO = skuApi.findSpuProductById(resellerId, productInfoId, ProductIdType.SPU_ID, String.valueOf(salesPort));
        if (CheckUtils.isNull(spuResultVO))
            throw new AppapiParametersException(CodeHandle.ORDER_ERROE_20016.getCode(), CodeHandle.ORDER_ERROE_20016.getMessage());

        List<SkuProduct> skuProducts = spuResultVO.getSkuProductList();

        String region = CheckUtils.isNotNull(skuProducts) ? skuProducts.get(0).getRegion() : null;
        String matchs = null;

        SpuProduct spu = spuResultVO.getSpuProduct();
        Integer consumerCardType = spuResultVO.getSkuProductList().get(0).getConsumerCardType();
        //验证关闭时间不可购买
        SpuProductModel spuProductModel = productServicePlatform.getSpuForOrder(user.getId(), productInfoId, 2, salesPort.toString());
        SkuCloseTimeSlotModel closeTimeSlotModel = spuProductModel.getCloseTimes();

        logger.info("页面是否选择购买日期 :: buy date " + orderVo.getBuyDate());
        //购买时间可不填，先验证购买时间
        if (CheckUtils.isNull(orderVo.getBuyDate()))
            setBuyDate(orderVo, spu, tradeOrderVo.getProducts(), resellerId, closeTimeSlotModel);

        if (closeTimeSlotModel != null && closeTimeSlotModel.getCloseDateList() != null && !closeTimeSlotModel.getCloseDateList().isEmpty()) {
            for (Date closeDate : closeTimeSlotModel.getCloseDateList()) {
                if (closeDate.equals(orderVo.getBuyDate()))
                    throw new AppapiParametersException(CodeHandle.ORDER_ERROE_20028.getCode(), CodeHandle.ORDER_ERROE_20028.getMessage());
            }
        }

        Timestamp startTime = orderVo.getBuyDate();

        Timestamp expireTime = null; // 游玩结束时间
        Timestamp showStartTime = orderVo.getShowTime(); // 演艺开始时间
        OrderVo.TicketVo ticketVo = tickets.get(0);
        if (showStartTime == null)
            showStartTime = ticketVo.getShowTime();
        logger.info("showStartTime :: " + showStartTime);
        Timestamp showEndTime = null; // 演艺结束时间

        //无有效渠道或政策
        if (CheckUtils.isNull(spuResultVO.getStrategyList())) {
            throw AppapiParametersException.createByCodeHandle(CodeHandle.ORDER_ERROE_20015);
        }

        // 设置游玩时间
        Integer productPlayUnit = spu.getOrdertimeUnit();
        Integer productPlayValue = spu.getOrdertimeValue();
        logger.info("游玩类型  ::" + productPlayUnit);
        logger.info("单是否填写游玩时间  ::" + spu.getIsNeedPlaytime());
        logger.info("不填写游玩时间下单计算时间类型  ::" + spu.getNoPlaytimeOrdertimeType());
        //通用可不设置下单有效期，那么有效期为可用日期
        if (spu.getIsNeedPlaytime() == SkuProductGlobal.NEED_PLAYTIME
            || (spu.getIsNeedPlaytime() == SkuProductGlobal.NOT_NEED_PLAYTIME && (spu.getNoPlaytimeOrdertimeType() == null || spu.getNoPlaytimeOrdertimeType() != 1))) {
            Long playTime = null;
            switch (productPlayUnit) {
                case GlobalParam.DateTime.day:
                    playTime = TimeUnit.DAYS.toMillis(productPlayValue);
                    break;
                case GlobalParam.DateTime.hour:
                    playTime = TimeUnit.HOURS.toMillis(productPlayValue);
                    break;
                case GlobalParam.DateTime.minute:
                    playTime = TimeUnit.MINUTES.toMillis(productPlayValue);
                    break;
            }
            /*            //如果没设置startTime，代表此产品应该是无需设置有效期的，那么从当天开始
                    if (startTime == null) {
                        startTime = DateUtils.getToday();
                    }
                    //如果spu可使用时间比当前时间靠后，那么取spu的开始时间
                    if (spu.getStartDate() != null && spu.getStartDate().after(startTime)) {
                        startTime = new Timestamp(spu.getStartDate().getTime());
                    }*/

            expireTime = new Timestamp(startTime.getTime() + playTime - 1000);
        } else {
            expireTime = new Timestamp(DateUtils.getDateEnd(spu.getEndDate()).getTime());
        }

        if (spu.getEndDate() != null && expireTime.after(spu.getEndDate())) {
            expireTime = new Timestamp(spu.getEndDate().getTime());
            logger.info("expireTime.after(spu.getEndDate())");
        }
        logger.info("是否设置下单有效期 ::" + spu.getIsNeedPlaytime());

        logger.info("expireTime ::" + expireTime);
        //是否一证一票
        boolean isOneVote = false;
        if (sceneId != null) {
            // 景区id不为null，说明是演艺或景区产品下单
            productType = GlobalDict.ProductCategory.normal;
            scenicVO = scenicService.getById(orderVo.getScienicId());

            scenicName = scenicVO.getName();

            //            ProductVO productInfoVO = productService.getProductVOById(productInfoId);
            //            if (supplierId == null) {
            //                supplierId = Long.valueOf(productInfoVO.getSupplierId());
            //                orderVo.setSupplierId(supplierId);
            //            }

            //获取验证一证一票的供应商，sku增加字段产品，是否一证一票产品
            //先验证一证一票
            for (OrderVo.TicketVo tv : orderVo.getTickets()) {
                SkuProductResultVO skuProductResultVO = skuApi.findSkuById(tv.getProductId());
                if (CheckUtils.isNull(skuProductResultVO) || CheckUtils.isNull(skuProductResultVO.getSkuProduct()))
                    throw new AppapiParametersException(CodeHandle.ORDER_ERROE_20016.getCode(), CodeHandle.ORDER_ERROE_20016.getMessage());
                //如果sku上的属性为true，则进行一证一票
                isOneVote = skuProductResultVO.getSkuProduct().getIsOneVote() == null ? false : skuProductResultVO.getSkuProduct().getIsOneVote();
                if (isOneVote)
                    if (!orderCheckService.findExpVerification(orderVo, startTime, expireTime))
                        throw new AppapiParametersException(CodeHandle.ERROR10034.getCode(), CodeHandle.ERROR10034.getMessage());
            }

            //            ProductPriceVO price = null;
            //            for (OrderVo.TicketVo ticket : tickets) {
            //                Long strategyId = ticket.getStrategyId();
            //                StrategyVo strategyVo = strategyService.getById(strategyId);
            //                if (strategyVo == null) {
            //                    logger.warn("无法找到政策，ID为 {}", strategyId);
            //                    throw new AppapiParametersException("21002", "无法找到政策");
            //                }
            //                ProductPriceVO params = new ProductPriceVO();
            //                params.setId(ticket.getProductId());
            //                List<ProductPriceVO> priceList = productService.findListByParams(params);
            //                if (CollectionUtils.isEmpty(priceList)) {
            //                    logger.error("产品无效 ID 为 {}", ticket.getProductId());
            //                    throw new AppapiParametersException("产品无效");
            //                }
            //
            //                ProductPriceVO priceA = priceList.get(0);
            //                if (price == null)
            //                    price = priceA;
            //                //这里没啥用了，前面都做过了
            //                //                try {
            //                //                    orderUtils.getExpiryTime(strategyVo, orderVo.getBuyDate(), priceA);
            //                //                } catch (Exception e) {
            //                //                    logger.error(e.getMessage(), e);
            //                //                    throw new AppapiParametersException("21003", e.getMessage());
            //                //                }
            //            }

            // 如果是演艺票，需要设置演艺时间

            //把本次所有的下单的产品的ID取出来，便于去查询SKU的渠道政策是否完备
            List<Long> ticketProductIds = new ArrayList<>();
            for (OrderVo.TicketVo tvo : tickets) {
                if (!ticketProductIds.contains(tvo.getProductId()))
                    ticketProductIds.add(tvo.getProductId());
            }

            // SPU -> sku1,sku2,sku3
            // 下单的->sku1,sku2
            Iterator<SkuProduct> iter = spuResultVO.getSkuProductList().iterator();
            while (iter.hasNext()) {
                SkuProduct sku = iter.next();
                Long skuId = sku.getId();
                //如果查出来的SKU列表（因为是根据SPU查询的，所以SKU列表肯定比下单的产品要多），去掉SKU3
                if (!ticketProductIds.contains(skuId)) {
                    iter.remove();
                    spuResultVO.getStrategyList().remove(skuId);
                    continue;
                }
                //如果当前找不到SKU1或者SKU2的政策，那么我们认为政策不存在，无法进行购票
                if (!spuResultVO.getStrategyList().containsKey(skuId)) {
                    logger.warn("skuId:" + skuId + ", 下单有的sku无法找到政策！");
                    throw new AppapiParametersException("21002", "无法找到政策");

                }
            }
            boolean isUnlimitedInventory = skuProducts.get(0).getUnlimitedInventory();
            logger.info("无限库存 ::" + isUnlimitedInventory);
            //库存id
            Long stockId = null;
            if (!isUnlimitedInventory) {
                StockQueryRequestModel param = new StockQueryRequestModel();
                param.setRuleId(skuProducts.get(0).getStockRuleId());
                param.setStockTime(DateUtils.formatDateTime(startTime));
                logger.info("OrderCreateServiceImpl rule id ::" + skuProducts.get(0).getStockRuleId());
                logger.info("OrderCreateServiceImpl stock time ::" + DateUtils.formatDateTime(startTime).toString());
                Result<StockModel> result = stockQueryService.queryStockByRule(param, ServiceContext.getServiceContext());
                if (!result.isOk())
                    throw new AppapiParametersException(CodeHandle.ERROE_20007.getCode(), CodeHandle.ERROE_20007.getMessage());
                if (CheckUtils.isNull(result) || CheckUtils.isNull(result.getData()))
                    throw new AppapiParametersException(CodeHandle.ERROE_20007.getCode(), CodeHandle.ERROE_20007.getMessage());

                if (CheckUtils.isNull(stockId))
                    stockId = result.getData().getId();
            }
            logger.info("stock id ::" + stockId);
            //这个地方实际上就是以前的设置演出时间的地方。
            String productCategory = String.valueOf(spu.getProductType());
            if (GlobalDict.ProductCategory.scenic().equals(productCategory)) {
                if (showStartTime == null && orderVo.getBuyDate() != null)
                    showStartTime = orderVo.getBuyDate();
                if (showStartTime == null)
                    throw new AppapiParametersException("缺少演出时间，请设置");
                if (supplierId == null)
                    throw new AppapiParametersException("缺少供应商，请设置");

                //查到场次信息
                ProScreeningsQueryModel proScreeningsQueryModel = new ProScreeningsQueryModel();
                proScreeningsQueryModel.setActProId(skuProducts.get(0).getTheaterId());
                Result<ScreeingsModel> screeResult = productActingService.queryScreeningsByActProductId(proScreeningsQueryModel,
                    ServiceContext.getServiceContext());
                if (screeResult.getErrorCode() != new Result<>().getErrorCode())
                    throw new AppapiParametersException("90011", "场次信息为空");

                ScreeingsModel screeingsModel = screeResult.getData();
                //如果有场次信息。赋值场次name
                matchs = screeingsModel.getName();
                //计算演出开始时间和结束时间
                ShowTimeInfo showInfo = initShowDate(showStartTime, screeingsModel);
                showStartTime = showInfo.showStartTime;
                showEndTime = showInfo.showEndTime;

                //演艺会有无限库存，只不过没有座位信息
                //如果非无限库存。获取库存信息及座位
                if (!isUnlimitedInventory) {
                    //判断sku的自动选座和手动选座属性配置
                    boolean isSelect = skuProducts.get(0).getIsSelectSeat();
                    String seatNumbers[] = new String[tickets.size()];
                    if (!isSelect) {

                        ShowModel showModel = new ShowModel();
                        // showModel.setOperateBusiness(OperateSeatBussinessType.ORDER_OCCUPY_SEAT.key);
                        showModel.setRandomNum(tickets.size());
                        showModel.setAreaScreeingsId(skuProducts.get(0).getTheaterId());
                        showModel.setStockId(stockId);
                        Result<ShowResponse> showResult = showService.randomAssignSeat(showModel, ServiceContext.getServiceContext());
                        if (showResult.getErrorCode() != new Result<>().getErrorCode())
                            throw new AppapiParametersException(CodeHandle.ERROR10037.getCode(), CodeHandle.ERROR10037.getMessage());
                        //获取到自动分配的座位
                        seatNumbers = showResult.getData().getRandomAssignSeats();
                        if (seatNumbers.length != tickets.size())
                            throw new AppapiParametersException(CodeHandle.CODE_90010.getCode(), CodeHandle.CODE_90010.getMessage());

                        for (int i = 0; i < tickets.size(); i++) {
                            tickets.get(i).setSeat(seatNumbers[i]);
                        }
                    }
                }
                productType = GlobalDict.ProductCategory.scenic;
            }
            logger.info("region :: " + region);
            logger.info("matchs :: " + matchs);
            //set演艺属性，验证是否有座位。
            for (OrderVo.TicketVo t : tickets) {
                if (productType == GlobalDict.ProductCategory.scenic) {
                    t.setMatchs(matchs);
                    t.setArea(region);
                    if (!isUnlimitedInventory && CheckUtils.isNull(t.getSeat()))
                        throw new AppapiParametersException(CodeHandle.CODE_90010.getCode(), CodeHandle.CODE_90010.getMessage());
                }
                t.setStockId(stockId);
            }
        } /*else {
            logger.info("productType ::" + productType);
            switch (productType) {
                case GlobalDict.ProductCategory.lineProduct:
                case GlobalDict.ProductCategory.tripPhotos:
                case GlobalDict.ProductCategory.restaurant:
                case GlobalDict.ProductCategory.guideProduct:
                case GlobalDict.ProductCategory.shuttleBus:
                case GlobalDict.ProductCategory.busCharter:
                    Timestamp buyDate = orderVo.getBuyDate();
                    expireTime = getExpireTime(buyDate);
                    break;
                case GlobalDict.ProductCategory.room:
                    Timestamp buyDateEnd = orderVo.getBuyDateEnd();
                    expireTime = getExpireTime(buyDateEnd);
                    break;
                case GlobalDict.ProductCategory.nativeProduct:
                    startTime = new Timestamp(System.currentTimeMillis());
                    Calendar calendar = Calendar.getInstance();
                    calendar.add(Calendar.YEAR, 1);
                    expireTime = new Timestamp(calendar.getTime().getTime());
                    break;
            }
          }*/

        //这一段实际上没啥用！
        //        if (channelId == null) {
        //            List<StrategyVo> list1 = orderUtils.findStrategyByScenic(tradeOrderVo.getResellerId(), orderVo.getScienicId(), supplierId, groupOrder,
        //                tradeOrderVo.getSalePort());
        //            if (list1 != null && list1.size() > 0) {
        //                for (StrategyVo strategyVo2 : list1) {
        //                    List<Long> productIds = strategyVo2.getProductList();
        //                    boolean a = false;
        //                    for (OrderVo.TicketVo ticketVoItem : tickets) {
        //                        if (productIds.get(0).equals(ticketVoItem.getProductId())) {
        //                            ChannelVo channelVo = strategyVo2.getChannelList().get(0);
        //                            channelId = channelVo.getId();
        //                            a = true;
        //                            break;
        //                        }
        //                    }
        //                    if (a)
        //                        break;
        //                }
        //            }
        //        }
        //        if (sceneId == null) {
        //            SysLabelRelationKey key = new SysLabelRelationKey();
        //            key.setRelId(user.getId().toString());
        //            key.setRelType(UserGlobalParam.ChannelMapKeyParam.CHANNEL_USER_RELATION_TYPE);
        //            key.setsId(GlobalParam.SUPPILER);
        //
        //            List<SysLabelRelationKey> listByParams = labelRelationService.findListByParams(key);
        //            if (CollectionUtils.isNotEmpty(listByParams) && listByParams.size() == 1) {
        //                SysLabelRelationKey key1 = listByParams.get(0);
        //                String objId = key1.getObjId();
        //                if (StringUtils.isNotBlank(objId))
        //                    channelId = Long.valueOf(objId);
        //            }
        //        }
        //

        //此处是获取渠道ID的地方
        List<PurchaseProductVO> products = tradeOrderVo.getProducts();
        Map<Long, SkuProduct> skuProductMap = new HashMap<>();
        Channel channel = null;
        for (Entry<Long, List<PCStrategyResult>> entry : spuResultVO.getStrategyList().entrySet()) {
            List<PCStrategyResult> tmp = entry.getValue();
            if (CheckUtils.isNotNull(tmp)) {
                channel = tmp.get(0).getChannel();
                channelId = channel.getId();
            }
        }

        for (SkuProduct sku : spuResultVO.getSkuProductList()) {
            if (!skuProductMap.containsKey(sku.getId())) {
                skuProductMap.put(sku.getId(), sku);
            }
        }
        //是否代下单
        Integer isNeedTakeOrder = 1;
        //这里是二次确认的判断
        String confirm = "0";
        for (PurchaseProductVO productVo : products) {
            long productId = productVo.getProductId();
            {
                //                ProductPriceVO p = productService.getProductById(productId);
                SkuProduct sku = skuProductMap.get(productId);

                if (sku == null) {
                    throw new AppapiParametersException("没有找到产品！ID为" + productId);
                }
                if (sku.getIsNeedTakeOrder() == 1) {
                    logger.info("是否代下单 :: product id :" + productId + " sku.getIsNeedTakeOrder() : " + sku.getIsNeedTakeOrder());
                    isNeedTakeOrder = 2;
                }

                String twiceSure = sku.getTwiceSure();
                logger.info("getTwiceSure : " + twiceSure);
                if ("1".equals(twiceSure)) {
                    confirm = twiceSure;
                    productVo.setConfirm(true);
                    logger.info("productVo.setConfirm设置成ture");
                }

                productVo.setCombined(sku.getIsComposed());
                productVo.setProductName(sku.getName());
                productVo.setSupplierId(Long.parseLong(sku.getSupplierId()));
                productVo.setProduct_type(productType);
                productVo.setProduct_varie(groupOrder);
                productVo.setChannelId(channelId);

                int productNum = productVo.getProductNum();
                if (productNum < 1) {
                    productVo.setProductNum(1);
                }
            }
        }

        //判断购买时间及库存
        //由于以后有联票，可能存在多个有效期，所以playDateMap里面存的是多个有效期的
        //只有当景区和演艺票的时候playDate才起作用，因为景区和演艺票的库存是针对整单的，不是针对单个merchandise的
        //例如：景区演艺是这样的
        // SPU -> sku1,sku2,sku3，sku1,2,3其实是共享库存的
        //但是针对特产、包车、一日游，则不是这样的
        //按照产品给的规划，是一个sku占用一个库存，至于是不是共享的，暂时无法在此处判断，需要更深层次的处理，例如库存RuleID归并。
        //而非景区和演艺票，库存是针对单个SKU的，所以在playTimeProduct的Map中进行单个SKU的库存判断
        //这里有点绕，不过根据产品的PRD文档是这样的
        // by jinliang 20160821
        Map<String, Integer> playTimeProductMap = new HashMap<>();
        Map<String, Integer> playDateMap = new HashMap<>();
        //订单总量
        Integer totalNum = 0;
        for (PurchaseProductVO productVo : products) {
            //PlayTime是针对单个产品的，菲景区演艺的。所以Key是productId_有效时间(2016-04-12)，例如213312341_2016-04-12，value是number
            String dateKey = DateUtils.formatDateTime(productVo.getPlay_start_time());
            String key = productVo.getProductId() + "_" + dateKey;
            Integer num = productVo.getProductNum();
            if (playTimeProductMap.containsKey(key)) {
                num += playTimeProductMap.get(key);
            }
            playTimeProductMap.put(key, num);

            int dateNum = productVo.getProductNum();
            //对于playDate，是专门为景区演艺做库存判断的，这里的key只需要用date来处理就足够了，所有的skuProductId共享同一个date的库存
            if (playDateMap.containsKey(dateKey)) {
                dateNum += playDateMap.get(dateKey);
            }
            playDateMap.put(dateKey, dateNum);
            //赋值是否代下单
            productVo.setAgent_flag(isNeedTakeOrder);
            totalNum += num;
        }

        //以下是判断每个产品购买限制的接口
        for (Entry<String, Integer> entry : playTimeProductMap.entrySet()) {
            String key = entry.getKey();
            Integer num = entry.getValue();
            String[] buyParam = key.split("_");
            Long productId = Long.parseLong(buyParam[0]);
            String buyDate = buyParam[1];
            CodeHandle handle = orderCheckService.checkOrderBuy(resellerId, String.valueOf(orderVo.getSalePort()), buyDate, productId, num, productType,
                totalNum);
            if (!CodeHandle.SUCCESS.getCode().equals(handle.getCode()))
                throw AppapiParametersException.createByCodeHandle(handle);
        }

        //如果是景区或演艺，那么在这里判断库存，上面的接口不会判断景区和演艺的库存
        if (productType == GlobalDict.ProductCategory.normal || productType == GlobalDict.ProductCategory.scenic) {
            Long productId = products.get(0).getProductId();
            for (Entry<String, Integer> entry : playDateMap.entrySet()) {
                String buyDate = entry.getKey();
                Integer num = entry.getValue();
                CodeHandle handle = orderCheckService.checkScenicProductStock(resellerId, String.valueOf(orderVo.getSalePort()), buyDate, productId, num);
                if (!CodeHandle.SUCCESS.getCode().equals(handle.getCode()))
                    throw AppapiParametersException.createByCodeHandle(handle);
            }
        }

        /*
         * 下单判断库存及购买时间，以及购买数量限制结束
         */

        //开始下单，和交易系统的人碰一下，是否还有下单接口的改动之类的东西
        Map<String, PurchaseProductVO> purchaseProductVOMap = null;
        Map<Long, Integer> commonVoucherProductInfoMap = null; // 非一证一票的Map，创建voucher要用，key为产品ID，value为数量
        Map<Long, OrderVo.TicketVo> commonVoucherProductInfoMap2 = null; // 非一证一票的Map，创建voucher要用，key为产品ID，value为数量
        Map<String, OrderVo.TicketVo> oneCardVoucherProductInfoMap = null; // 一证一票的Map，创建voucher要用，key为产品ID，value为身份证号'
        logger.info("是否一证一票 ::" + isOneVote);
        if (sceneId == null) { // 通用产品
            commonVoucherProductInfoMap = new HashMap<>();
            for (PurchaseProductVO productVo : products) {
                commonVoucherProductInfoMap.put(productVo.getProductId(), productVo.getProductNum());
            }
        } else if (!isOneVote) { //  非一证一票
            commonVoucherProductInfoMap2 = new HashMap<>();
            for (OrderVo.TicketVo ticketVoItem : tickets) {
                commonVoucherProductInfoMap2.put(ticketVoItem.getProductId(), ticketVoItem);
            }
        } else { // 一证一票
            purchaseProductVOMap = new HashMap<>(); // 将身份证关联商品信息，当创建voucher成功后，可以根据些关系把voucherId设置到商品上
            oneCardVoucherProductInfoMap = new HashMap<>();
            for (OrderVo.TicketVo ticketVoItem : tickets) {
                PurchaseProductVO purchaseProductVO = ticketVoItem.getPurchaseProductVO();
                purchaseProductVOMap.put(ticketVoItem.getBuyCard(), purchaseProductVO);
                oneCardVoucherProductInfoMap.put(ticketVoItem.getBuyCard(), ticketVoItem);
            }
        }

        /*初始化订单商品中的联票属性*/
        //        initPurchaseProducts(tradeOrderVo);
        if (sceneId == null) {
            // 通用产品创建voucher
            VoucherEntity voucher = voucherCreateService.createCommonVoucher(commonVoucherProductInfoMap, productType, supplierId, contactMobile, contactName,
                startTime, expireTime, address, consumerCardType, spu);

            if (voucher == null)
                throw AppapiParametersException.createByCodeHandle(CodeHandle.ORDER_ERROE_20017);

            for (PurchaseProductVO productVo : products) {
                productVo.setVoucherId(voucher.getVoucherId());
            }
        } else if (!isOneVote) {
            // 非一证一票创建voucher
            VoucherEntity voucher = voucherCreateService.createCommonVoucher(commonVoucherProductInfoMap2, productType, supplierId, contactMobile, contactName,
                startTime, expireTime, showStartTime, showEndTime, address, scenicName, String.valueOf(sceneId), Integer.valueOf(groupOrder), spu,
                consumerCardType);

            if (voucher == null)
                throw AppapiParametersException.createByCodeHandle(CodeHandle.ORDER_ERROE_20017);

            for (PurchaseProductVO productVo : products) {
                productVo.setVoucherId(voucher.getVoucherId());
                productVo.setIsTicket(true);
            }

        } else {
            // 一证一票创建voucher
            List<VoucherEntity> voucherList = voucherCreateService.createOneCardVoucher(oneCardVoucherProductInfoMap, productType, supplierId, contactMobile,
                startTime, expireTime, showStartTime, showEndTime, address, scenicName, String.valueOf(sceneId), Integer.valueOf(groupOrder), spu,
                consumerCardType);

            System.out.println(voucherList.toString());
            if (CollectionUtils.isEmpty(voucherList))
                throw AppapiParametersException.createByCodeHandle(CodeHandle.ORDER_ERROE_20017);

            for (VoucherEntity voucher : voucherList) {
                String voucherContent = voucher.getVoucherContent();
                PurchaseProductVO purchase = purchaseProductVOMap.get(voucherContent);

                if (purchase != null) {
                    purchase.setVoucherId(voucher.getVoucherId());
                    purchase.setIsTicket(true);
                }
            }
        }

        for (PurchaseProductVO voucher : tradeOrderVo.getProducts()) {
            logger.info("是否需要二次确认? " + voucher.isConfirm());
        }

        logger.info("下面开始创建订单...");
        Result<OrderResponse> responseResult = null;
        try {
            responseResult = orderService.createOrder(tradeOrderVo, null);
        } catch (Throwable e) {
            String msg = e.getMessage();
            logger.info("Throwable msg :: " + msg);
            //如果是下单调用其他接口造成的报错，会返回两层异常
            //比如：下单失败:com.pzj.core.stock.exception.seat.CannotOccupySeatException: 不能占用座位列表：无法占座的座位号：VIPA10_24，
            //所以这边截取处理
            if (msg.contains("CannotOccupySeatException")) {
                logger.info("创建订单异常  CannotOccupySeatException::", msg);
                String msgs[] = msg.split("CannotOccupySeatException:");
                throw new Exception(msgs[msgs.length - 1].trim());
            } else if (msg.contains("ShortageStockException")) {
                logger.info("创建订单异常  ShortageStockException::", msg);
                String msgs[] = msg.split("ShortageStockException:");
                throw new Exception(msgs[msgs.length - 1].trim());
            } else if (msg.contains("CalculateException")) {
                logger.info("创建订单异常 OrderException::" + e.getMessage());
                throw new Exception("该产品暂时无法预订，请选择其他产品");
            } else {
                logger.info("创建订单异常 OrderException::" + e.getMessage());
                throw e;
            }
        }

        if (responseResult.getErrorCode() == new Result<>().getErrorCode()) {
            json.setCode(CodeHandle.SUCCESS.getCode() + "");
            json.setMessage(CodeHandle.SUCCESS.getMessage());
            Map<String, Object> object = Maps.newHashMap();

            logger.info("创建订单成功...");
            logger.info("将要查询账户余额...");

            Double balanceOfAvailable = 0.0;
            List<AccountBalance> accountNew = bizAccountService.getAccountNew(user, null);
            if (CollectionUtils.isNotEmpty(accountNew)) {
                for (AccountBalance account : accountNew) {
                    if (ResellerAccountDict.OTHER_AVAILABLE.getId() == account.getAccountDictId())
                        balanceOfAvailable = account.getFinalPeriod();
                }
            }
            object.put("avaBalance", balanceOfAvailable);
            object.put("orderId", responseResult.getData().getOrderId());
            object.put("cPaidAmount", responseResult.getData().getTotalAmount());
            object.put("confirm", confirm);
            json.setResponseBody(object);
            logger.info("创建订单结束，返回数据...");
            return json;
        } else {
            logger.error("创建订单失败!");
            logger.error("ErrorCode:" + responseResult.getErrorCode() + ", ErrorMsg:" + responseResult.getErrorMsg());
            json.setCode(responseResult.getErrorCode() + "");
            json.setMessage(responseResult.getErrorMsg());
            return json;
        }
    }

    private OrderVo toOrderVo(JSONObject jsonObject) throws IOException, JsonParseException, JsonMappingException {
        logger.info("[toOrderVO] before check jsonObject:" + jsonObject.toJSONString());

        if (jsonObject.containsKey("buyDate") && jsonObject.get("buyDate") != null && jsonObject.getString("buyDate").trim().length() == 0) {
            logger.warn("buyDate is empty string");
            jsonObject.put("buyDate", null);
        }
        if (jsonObject.containsKey("showTime") && jsonObject.get("showTime") != null && jsonObject.getString("showTime").trim().length() == 0) {
            logger.warn("showTime is empty string");
            jsonObject.put("showTime", null);
        }

        if (jsonObject.containsKey("buyDateEnd") && jsonObject.get("buyDateEnd") != null && jsonObject.getString("buyDateEnd").trim().length() == 0) {
            logger.warn("buyDateEnd is empty string");
            jsonObject.put("buyDateEnd", null);
        }
        if (jsonObject.containsKey("showTimeEnd") && jsonObject.get("showTimeEnd") != null && jsonObject.getString("showTimeEnd").trim().length() == 0) {
            logger.warn("showTimeEnd is empty string");
            jsonObject.put("showTimeEnd", null);
        }

        if (jsonObject.containsKey("tickets")) {
            JSONArray tickets = jsonObject.getJSONArray("tickets");
            Iterator<Object> iter = tickets.iterator();
            while (iter.hasNext()) {
                JSONObject ticket = (JSONObject) iter.next();
                if (ticket.containsKey("showTime") && ticket.get("showTime") != null && ticket.getString("showTime").trim().length() == 0) {
                    logger.warn("showTime is empty string");
                    ticket.put("showTime", null);
                }
            }
        }

        String requestJson = jsonObject.toJSONString();

        logger.info("[toOrderVO] after check jsonObject:" + requestJson);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        //前端有可能会传一个空字符串作为空值，这里我们默认他们都是空值
        //need test
        //2016-11-23 by jinliang
        //        objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);

        OrderVo vo = objectMapper.readValue(requestJson, OrderVo.class);
        return vo;
    }

    private TradeOrderVO toTradeOrderVo(OrderVo orderVo) {
        TradeOrderVO tradeOrderVo = new TradeOrderVO();
        BeanUtils.copyProperties(orderVo, tradeOrderVo);
        tradeOrderVo.setIdcard_no(orderVo.getBuyCard());
        List<PurchaseProductVO> productVoList = new ArrayList<>();
        for (OrderVo.TicketVo ticketVo : orderVo.getTickets()) {
            PurchaseProductVO productVo = new PurchaseProductVO();
            BeanUtils.copyProperties(ticketVo, productVo);
            productVo.setPlay_start_time(orderVo.getBuyDate());
            productVo.setPlay_end_time(orderVo.getBuyDateEnd());
            ticketVo.setPurchaseProductVO(productVo);
            productVoList.add(productVo);
        }
        tradeOrderVo.setProducts(productVoList);
        return setfilledModelList(tradeOrderVo, orderVo);
    }

    private TradeOrderVO setfilledModelList(TradeOrderVO tradeOrderVo, OrderVo orderVo) {
        //上车地址
        List<FilledModel> filledModelList = new ArrayList<FilledModel>();
        if (CheckUtils.isNotNull(orderVo.getStartAddress())) {
            String startAddress = orderVo.getStartAddress();
            if (startAddress != null) {
                String addressArr[] = startAddress.split(",");
                if (addressArr.length > 0) {
                    filledModelList.add(setFilledModel("user_car", "get_on_addr_detail", addressArr[1]));
                    String provinces[] = addressArr[0].split("#");
                    filledModelList.add(setFilledModel("user_car", "get_on_addr_province", provinces[0]));
                    filledModelList.add(setFilledModel("user_car", "get_on_addr_city", provinces[1]));
                    if (provinces.length > 2) {
                        if (CheckUtils.isNotNull(provinces[2]))
                            filledModelList.add(setFilledModel("user_car", "get_on_addr_county", provinces[2]));
                    }
                }
            }
        }
        //下车地址
        if (CheckUtils.isNotNull(orderVo.getEndAddress())) {
            String endAddress = orderVo.getEndAddress();
            if (endAddress != null) {
                String addressArr[] = endAddress.split(",");
                if (addressArr.length > 0) {
                    filledModelList.add(setFilledModel("user_car", "get_off_addr_detail", addressArr[1]));
                    String provinces[] = addressArr[0].split("#");
                    filledModelList.add(setFilledModel("user_car", "get_off_addr_province", provinces[0]));
                    filledModelList.add(setFilledModel("user_car", "get_off_addr_city", provinces[1]));
                    if (provinces.length > 2) {
                        if (CheckUtils.isNotNull(provinces[2]))
                            filledModelList.add(setFilledModel("user_car", "get_off_addr_county", provinces[2]));
                    }
                }
            }
        }

        //用车时间
        if (CheckUtils.isNotNull(orderVo.getCarTime()))
            filledModelList.add(setFilledModel("other", "expect_use_car_time", orderVo.getCarTime()));
        //用车时间
        if (CheckUtils.isNotNull(orderVo.getArrivalTime()))
            filledModelList.add(setFilledModel("other", "expect_to_shop_time", orderVo.getArrivalTime()));

        //航班号
        if (CheckUtils.isNotNull(orderVo.getFlightNumber()))
            filledModelList.add(setFilledModel("other", "flight_no", orderVo.getFlightNumber()));

        //列车号
        if (CheckUtils.isNotNull(orderVo.getTrainNumber()))
            filledModelList.add(setFilledModel("other", "train_no", orderVo.getTrainNumber()));

        //收货地址
        if (CheckUtils.isNotNull(orderVo.getReceiptAddress())) {
            String receiptAddress = orderVo.getReceiptAddress();
            if (receiptAddress != null) {
                String addressArr[] = receiptAddress.split(",");
                if (addressArr.length > 0) {
                    filledModelList.add(setFilledModel("delivery", "delivery_addr_detail", addressArr[1]));
                    String provinces[] = addressArr[0].split("#");
                    filledModelList.add(setFilledModel("delivery", "delivery_addr_province", provinces[0]));
                    filledModelList.add(setFilledModel("delivery", "delivery_addr_city", provinces[1]));
                    if (provinces.length > 2) {
                        if (CheckUtils.isNotNull(provinces[2]))
                            filledModelList.add(setFilledModel("delivery", "delivery_addr_county", provinces[2]));
                    }
                }
            }
        }

        //联系人拼音
        if (CheckUtils.isNotNull(orderVo.getContactSpelling()))
            filledModelList.add(setFilledModel("contact", "contact_spelling", orderVo.getContactSpelling()));
        //联系人邮箱
        if (CheckUtils.isNotNull(orderVo.getContactEmail()))
            filledModelList.add(setFilledModel("contact", "contact_email", orderVo.getContactEmail()));
        //联系人姓名
        if (CheckUtils.isNotNull(orderVo.getContactee()))
            filledModelList.add(setFilledModel("contact", "contact_name", orderVo.getContactee()));
        //联系人手机号
        if (CheckUtils.isNotNull(orderVo.getContactMobile()))
            filledModelList.add(setFilledModel("contact", "contact_mobile", orderVo.getContactMobile()));
        //联系人身份证号
        if (CheckUtils.isNotNull(orderVo.getBuyCard()))
            filledModelList.add(setFilledModel("contact", "contact_idcard", orderVo.getBuyCard()));

        logger.debug("填单项信息{} ::", JSONConverter.toJson(filledModelList));
        tradeOrderVo.setFilledModelList(filledModelList);
        return tradeOrderVo;
    }

    private FilledModel setFilledModel(String group, String attrKey, String attrValue) {
        FilledModel filledModel = new FilledModel();
        filledModel.setGroup(group);
        filledModel.setAttr_key(attrKey);
        filledModel.setAttr_value(attrValue);
        return filledModel;
    }

    /**
    *
    * @param showStartTime 演艺开始时间
    * @param supplierId 供应商ID
    * @param screenings 场次
    * @param sceneId 景区ID
    * @param seatNum 座位号
    * @return
    * @throws Exception
    */
    private ShowTimeInfo initShowDate(Timestamp showStartTime, ScreeingsModel screeingsModel) throws Exception {

        String stime = screeingsModel.getStartTime();
        String etime = screeingsModel.getEndTime();
        boolean flag = false;
        int shour = 0, smin = 0, ehour = 0, emin = 0;
        if (stime == null || etime == null) {
            flag = true;
        } else {
            Pattern pattern = Pattern.compile("^[0-9]{4}$");
            Matcher matcherSTime = pattern.matcher(stime);
            Matcher matcherETime = pattern.matcher(etime);
            if (!matcherSTime.matches() || !matcherETime.matches()) {
                flag = true;
            }
            shour = Integer.parseInt(stime.substring(0, 2));
            smin = Integer.parseInt(stime.substring(2));

            ehour = Integer.parseInt(etime.substring(0, 2));
            emin = Integer.parseInt(etime.substring(2));
        }
        if (flag) {
            throw new Exception("演艺场次有效期有效时间格式错误");
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(showStartTime);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        Long buyTime = calendar.getTimeInMillis();
        ShowTimeInfo info = new ShowTimeInfo();

        calendar.set(Calendar.HOUR_OF_DAY, shour);
        calendar.set(Calendar.MINUTE, smin);
        info.showStartTime = new Timestamp(calendar.getTime().getTime());

        calendar.set(Calendar.HOUR_OF_DAY, ehour);
        calendar.set(Calendar.MINUTE, emin);
        info.showEndTime = new Timestamp(calendar.getTime().getTime());

        //判断是否可买选中的演出
        Date thisDate = DateUtils.getDateStart(new Date());
        //如果购买的天小于当前天
        logger.info("验证是否可买演出票 :: this date :" + thisDate.getTime() + ",buy time :" + buyTime);
        if (buyTime < thisDate.getTime()) {
            throw new Exception("购买的演出已结束");
            //如果购买的是当天票，判断当前时间演出是否结束，如果大于当前天，不必验证
        } else if (buyTime == thisDate.getTime()) {
            Long showEnd = info.showEndTime.getTime();
            Long thisTime = new Date().getTime();
            logger.info("验证是否可买演出票 :: this time :" + thisTime + ",show end :" + showEnd);
            if (thisTime >= showEnd)
                throw new Exception("购买的演出已结束");
        }
        return info;
    }

    private static class ShowTimeInfo {
        Timestamp showStartTime = null; // 演艺开始时间
        Timestamp showEndTime   = null; // 演艺结束时间
    }

    private Timestamp getExpireTime(Timestamp buyDateEnd) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(buyDateEnd);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return new Timestamp(calendar.getTime().getTime());
    }

    /**
     * 下单buyDate可不填，此处处理购买时间
     * 
     * @param orderVo
     * @return
     */
    private void setBuyDate(OrderVo orderVo, SpuProduct spu, List<PurchaseProductVO> proList, Long resellerId, SkuCloseTimeSlotModel closeTimeSlotModel)
                                                                                                                                                        throws Exception {
        //首先获取产品可用日期
        Date startDate = spu.getStartDate();
        Long productId = proList.get(0).getProductId();
        //获取政策，产品确认，一个spu下的所有sku的提前购买时间是一样的
        SpuProductResultVO vo = skuApi.findSkuById(resellerId, productId, orderVo.getSalePort() + "");
        if (vo.getSkuProductList() == null || vo.getSkuProductList().isEmpty()) {
            throw new AppapiParametersException(CodeHandle.ORDER_ERROE_20016.getCode(), CodeHandle.ORDER_ERROE_20016.getMessage());//无有效的产品
        }
        Map<Long, List<PCStrategyResult>> map = vo.getStrategyList();
        if (map == null || map.isEmpty()) {
            throw new AppapiParametersException(CodeHandle.ORDER_ERROE_20015.getCode(), CodeHandle.ORDER_ERROE_20015.getMessage());//无有效渠道
        }

        List<PCStrategyResult> list = map.get(productId);
        if (list == null || list.isEmpty()) {
            throw new AppapiParametersException(CodeHandle.ORDER_ERROE_20015.getCode(), CodeHandle.ORDER_ERROE_20015.getMessage());//无有效渠道
        }
        PCStrategyResult result = list.get(0);
        if (result.getStrategyList() == null || result.getStrategyList().isEmpty()) {
            throw new AppapiParametersException(CodeHandle.ORDER_ERROE_20015.getCode(), CodeHandle.ORDER_ERROE_20015.getMessage());//无有效渠道
        }
        Strategy strategy = result.getStrategyList().get(0);
        Calendar now = Calendar.getInstance();
        Calendar buy = Calendar.getInstance();
        //判断是否有日期限制
        if (strategy.getIsLimitAdvanceDue() > 0) {
            Integer dvanceDueDays = strategy.getAdvanceDueDays();
            //这个判断有点绕
            //需要加入关闭时间
            //用当前时间，加上往前拨多少时间，然后和下单时间进行判断来处理的
            try {
                Date buyTime = buyDateFormat.parse(DateUtils.formatDateTime(now.getTime()));
                buy.setTime(buyTime);
            } catch (Exception ex) {
                logger.error("", ex);
                logger.error("buyDate输入格式错误：{}", DateUtils.formatDateTime(now.getTime()));
            }
            //这个判断有点绕
            //用当前时间，加上往前拨多少时间，然后和下单时间进行判断来处理的
            buy.set(Calendar.HOUR_OF_DAY, strategy.getAdvanceDueHour());
            buy.set(Calendar.MINUTE, strategy.getAdvanceDueMinute());
            buy.set(Calendar.SECOND, 0);
            buy.set(Calendar.MILLISECOND, 0);

            if (now.after(buy))
                dvanceDueDays++;
            now.add(Calendar.DATE, dvanceDueDays);
            if (closeTimeSlotModel != null && closeTimeSlotModel.getCloseDateList() != null && !closeTimeSlotModel.getCloseDateList().isEmpty()) {
                while (closeTimeSlotModel.getCloseDateList().contains(DateUtils.getDateStart(now.getTime()))) {
                    now.add(Calendar.DATE, dvanceDueDays++);
                }
            }
        }
        Date buyDate = DateUtils.getDateStart(now.getTime());
        //如果当前时间小于可用日期，说明当天不可使用，需要把开始日期赋为可用日期
        if (buyDate.getTime() < startDate.getTime()) {
            orderVo.setBuyDate(new Timestamp(startDate.getTime()));
        } else {
            orderVo.setBuyDate(new Timestamp(buyDate.getTime()));
        }
        logger.info("orderVO info : " + JsonMapper.toJsonString(orderVo));
        if (orderVo.getProType() == GlobalDict.ProductCategory.scenic)
            orderVo.setShowTime(orderVo.getBuyDate());
        for (PurchaseProductVO productVo : proList) {
            productVo.setPlay_start_time(orderVo.getBuyDate());
            productVo.setPlay_end_time(orderVo.getBuyDateEnd());
        }
    }

    /**
     * 
     * @throws Exception 
     * @see com.pzj.modules.appapi.api.order.OrderCreateService#checkOrderRelevant(com.alibaba.fastjson.JSONObject, com.pzj.customer.entity.Customer)
     */

    public JsonEntity checkOrderRelevantDo(JSONObject jsonObject, Customer user) throws Exception {
        JsonEntity json = new JsonEntity();
        OrderVo orderVo = toOrderVo(jsonObject);
        TradeOrderVO tradeOrderVo = toTradeOrderVo(orderVo);
        Long resellerId = orderVo.getTravel();
        Integer salesPort = UserGlobalDict.StrategyGlobalDict.windowTenantApp();

        // 如果是导游
        if (user.getResellerType().equals(UserGlobalDict.guideUserType())) {
            salesPort = UserGlobalDict.StrategyGlobalDict.windowGuideApp();
        } else { // 如果是商户
            resellerId = user.getId();
        }
        //SalePort只有微店才会传，所以值不为空，那么销售端口就是微店
        if (CheckUtils.isNotNull(orderVo.getSalePort()) && orderVo.getSalePort() != 0) {
            if (salesPort.intValue() != orderVo.getSalePort()) {
                salesPort = UserGlobalDict.StrategyGlobalDict.windowTenantMicroshop();
            }
        }

        orderVo.setSalePort(salesPort);
        logger.info("orderVo.setSalePort(salesPort)是：" + salesPort);
        Long productInfoId = orderVo.getProductInfoId();
        SpuProductResultVO spuResultVO = skuApi.findSpuProductById(resellerId, productInfoId, ProductIdType.SPU_ID, String.valueOf(salesPort));
        if (orderVo.getProType() == null || orderVo.getProductType() == null) {
            orderVo.setProductType(spuResultVO.getSpuProduct().getProductType());
            orderVo.setProType(spuResultVO.getSpuProduct().getProductType());
        }
        if (CheckUtils.isNull(spuResultVO))
            throw new AppapiParametersException(CodeHandle.ORDER_ERROE_20016.getCode(), CodeHandle.ORDER_ERROE_20016.getMessage());

        Map<Long, List<PCStrategyResult>> map = spuResultVO.getStrategyList();
        if (map == null || map.isEmpty()) {
            throw new AppapiParametersException(CodeHandle.ORDER_ERROE_20015.getCode(), CodeHandle.ORDER_ERROE_20015.getMessage());//无有效渠道
        }
        Iterator<Long> strIter = map.keySet().iterator();
        PCStrategyResult pcStrategyResult = new PCStrategyResult();
        while (strIter.hasNext()) {
            Long productId = strIter.next();
            List<PCStrategyResult> pcStrategyResultList = map.get(productId);
            pcStrategyResult = pcStrategyResultList.get(0);
            break;

        }
        logger.info("页面是否选择购买日期 :: buy date " + orderVo.getBuyDate());
        //购买时间可不填，先验证购买时间
        if (CheckUtils.isNull(orderVo.getBuyDate())) {
            SpuProduct spu = spuResultVO.getSpuProduct();
            //验证关闭时间不可购买
            SpuProductModel spuProductModel = productServicePlatform.getSpuForOrder(user.getId(), productInfoId, 2, salesPort.toString());
            SkuCloseTimeSlotModel closeTimeSlotModel = spuProductModel.getCloseTimes();
            try {
                setBuyDate(orderVo, spu, tradeOrderVo.getProducts(), resellerId, closeTimeSlotModel);
            } catch (Exception e) {
                logger.error("", e);
                throw new AppapiParametersException(CodeHandle.ERROR10038.getCode(), CodeHandle.ERROR10038.getMessage());
            }
        }
        //验证时间是否可买
        List<SkuCloseTimeSlot> closeTimes = skuApi.findSkuCloseTimeSlotByParams(productInfoId);
        Result<ArrayList<SkuSaledCalendarResult>> saleResult = skuApi.getSkuSaledCalendar(spuResultVO.getSpuProduct(), closeTimes, pcStrategyResult
            .getStrategyList().get(0));
        if (saleResult.getErrorCode() != new Result<>().getErrorCode())
            throw new AppapiParametersException(CodeHandle.ORDER_ERROE_20024.getCode(), CodeHandle.ORDER_ERROE_20024.getMessage());

        List<SkuSaledCalendarResult> saleList = saleResult.getData();
        if (saleList.contains(orderVo.getBuyDate()))
            throw new AppapiParametersException(CodeHandle.ORDER_ERROE_20024.getCode(), CodeHandle.ORDER_ERROE_20024.getMessage());
        //判断sku的自动选座和手动选座属性配置
        boolean isSelect = spuResultVO.getSkuProductList().get(0).getIsSelectSeat();
        //获取到订单下每个商品购买的数量key 产品id  value 数量 
        Map<Long, Integer> buyProductNum = new HashMap<Long, Integer>();
        //获取订单下每个商品对应的座位号 key 产品id value 座位号
        Map<Long, String> buySeats = new HashMap<Long, String>();
        for (TicketVo tvo : orderVo.getTickets()) {
            logger.info(" tvo.getProductId()是：" + tvo.getProductId());
            buyProductNum.put(tvo.getProductId(),
                (buyProductNum.get(tvo.getProductId()) == null ? 0 : buyProductNum.get(tvo.getProductId())) + tvo.getProductNum());
            if (isSelect) {
                if (CheckUtils.isNull(tvo.getSeat()))
                    throw new AppapiParametersException(CodeHandle.CODE_90012.getCode(), CodeHandle.CODE_90012.getMessage());
                buySeats.put(tvo.getProductId(), tvo.getSeat());

            }
        }
        //验证库存是否够
        //1.获取每个库存各购买多少产品,获取每个库存对应的座位信息
        Map<Long, Integer> buyStockNum = new HashMap<Long, Integer>();
        Map<Long, List<String>> buySeatsStock = new HashMap<Long, List<String>>();
        StockQueryRequestModel stock = new StockQueryRequestModel();
        stock.setStockTime(DateUtils.formatDateTime(orderVo.getBuyDate()));
        for (SkuProduct sku : spuResultVO.getSkuProductList()) {
            //判断是否无限库存
            if (!sku.getUnlimitedInventory()) {//非无限库存
                stock.setRuleId(sku.getStockRuleId());//库存规则id

                logger.info("query param是：" + JsonMapper.getInstance().toJson(stock));
                Result<StockModel> result = stockQueryService.queryStockByRule(stock, ServiceContext.getServiceContext());
                logger.info("query result是" + JsonMapper.getInstance().toJson(result));
                if (result.isOk() && result.getData() != null) {
                    StockModel stockModel = result.getData();
                    logger.info("sku.getId()是：" + sku.getId());

                    if (buyProductNum.get(sku.getId()) != null) {
                        logger.info("进入到buyStockNum.put(stockModel.getId(), buyProductNum.get(sku.getProductId()))");
                        logger.info("stockModel.getId()是：" + stockModel.getId());
                        buyStockNum.put(stockModel.getId(), buyProductNum.get(sku.getId()));
                        //如果需要选座座位，那么需要验证座位是否可买
                        logger.info("isSelect是：" + isSelect);
                        if (isSelect) {
                            List<String> seatList = buySeatsStock.get(sku.getProductId());
                            if (seatList == null)
                                seatList = new ArrayList<String>();
                            seatList.add(buySeats.get(sku.getId()));
                            buySeatsStock.put(sku.getProductId(), seatList);
                        }
                    }
                }
            }
        }
        //2.调用验证库存接口  验证座位是否可买
        Iterator<Long> buyStockIdKey = buyStockNum.keySet().iterator();
        CheckStockModel checkStockModel = new CheckStockModel();
        while (buyStockIdKey.hasNext()) {
            Long stockId = buyStockIdKey.next();
            checkStockModel.setStockId(stockId);
            checkStockModel.setOccupyNum(buyStockNum.get(stockId));
            logger.info("checkStockModel.getStockId是：" + checkStockModel.getStockId() + "checkStockModel.getOccupyNum是：" + checkStockModel.getOccupyNum());
            Result<Boolean> isOk = stockQueryService.judgeStockIsEnough(checkStockModel, ServiceContext.getServiceContext());
            logger.info("isOk.getData()是：" + isOk.getData());
            if (!isOk.getData())
                throw new AppapiParametersException(CodeHandle.ERROE_20002.getCode(), CodeHandle.ERROE_20002.getMessage());
            CheckSeatsOptionalModel checkSeatsModel = new CheckSeatsOptionalModel();
            checkSeatsModel.setStockId(stockId);
            List<String> seatList = buySeatsStock.get(orderVo.getProductInfoId());
            List<String> seats = new ArrayList<String>();
            if (null != seatList && !seatList.isEmpty()) {
                for (String str : seatList) {
                    if (str.indexOf(",") > 0) {
                        String[] seatarr = str.split(",");
                        for (String s : seatarr) {
                            seats.add(s);
                        }
                    } else {
                        seats.add(str);
                    }

                }
            }
            checkSeatsModel.setSeats(seats);
            checkSeatsModel.setOperateUserId(resellerId);
            logger.info("checkSeatsModel.getStockId是：" + checkSeatsModel.getStockId() + "|checkSeatsModel.getSeats是：" + checkSeatsModel.getSeats()
                        + "checkSeatsModel.getOperateUserId是：" + checkSeatsModel.getOperateUserId());

            if (!checkSeatsModel.getSeats().isEmpty()) {//如果是不选座的话，则不走判断座位的方法

                Result<SeatsOptionalResponse> seatResult = seatService.judgeSeatWheatherOptional(checkSeatsModel, ServiceContext.getServiceContext());
                if (!seatResult.getData().getFlag())
                    throw new AppapiParametersException(CodeHandle.CODE_90011.getCode(), "座位号为" + seatResult.getData().getNotOptionalSeats() + "已被下单");
            }

        }
        json.setCode(CodeHandle.SUCCESS.getCode() + "");
        json.setMessage(CodeHandle.SUCCESS.getMessage());
        return json;

    }

    @Override
    public JsonEntity checkOrderRelevant(JSONObject jsonObject, Customer user) {
        JsonEntity json = new JsonEntity();
        try {
            return checkOrderRelevantDo(jsonObject, user);
        } catch (AppapiParametersException e) {
            logger.error(e.getMessage(), e);
            json.setCode(e.getCode());
            json.setMessage(e.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            json.setCode(CodeHandle.CODE_90001.getCode() + "");
            json.setMessage(e.getMessage());
        }
        logger.info("下单下一步验证json是：" + json);
        return json;
    }
}
