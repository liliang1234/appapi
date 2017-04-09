/**
 * piaozhijia.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package com.pzj.platform.appapi.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pzj.base.entity.product.ProductInfo;
import com.pzj.base.entity.product.TicketRule;
import com.pzj.channel.Strategy;
import com.pzj.channel.vo.resultParam.PCStrategyResult;
import com.pzj.common.util.CheckUtils;
import com.pzj.platform.appapi.dubbo.ProductApi;
import com.pzj.platform.appapi.dubbo.SkuProductApi;
import com.pzj.platform.appapi.model.OrderMerchModel;
import com.pzj.platform.appapi.model.OrderMerchValueResult;
import com.pzj.platform.appapi.model.VoucherModel;
import com.pzj.platform.appapi.service.CommonProductRefundService;
import com.pzj.platform.appapi.service.OrderMerchService;
import com.pzj.platform.appapi.service.ProductService;
import com.pzj.platform.appapi.service.VoucherService;
import com.pzj.platform.appapi.util.MathUtils;
import com.pzj.product.global.SkuProductGlobal;
import com.pzj.product.vo.product.SkuProduct;
import com.pzj.product.vo.product.SpuProduct;
import com.pzj.product.vo.voParam.resultParam.SkuProductResultVO;
import com.pzj.trade.order.entity.MerchResponse;
import com.pzj.trade.order.entity.OrderStrategyResponse;
import com.pzj.voucher.entity.VoucherEntity;
import com.pzj.voucher.lineVo.TheaterProductVoucherVO;

/**
 * 
 * @author Mark
 * @version $Id: OrderMerchServiceImpl.java, v 0.1 2016年8月1日 上午9:48:27 pengliqing Exp $
 */
@Service
public class OrderMerchServiceImpl implements OrderMerchService {

    private static final Logger        logger = LoggerFactory.getLogger(OrderMerchServiceImpl.class);
    @Autowired
    private ProductApi                 productApi;
    @Autowired
    private ProductService             productService;
    @Autowired
    private CommonProductRefundService commonProductRefundService;
    @Autowired
    private VoucherService             voucherService;
    @Autowired
    private SkuProductApi              skuProductApi;

    /**
    * 模板方法组装订单商品返回值对象
    * 
    * @see #convertMerchResponse2MerchResponseVo(MerchResponse)
    */
    @Override
    public OrderMerchValueResult convert2MerchResponseVo(List<MerchResponse> merchs, Map<String, OrderMerchService> wrapperProcesses) {
        OrderMerchValueResult orderMerchValueResult = new OrderMerchValueResult();
        List<OrderMerchModel> merchVos = new ArrayList<OrderMerchModel>(merchs.size());
        Map<Long, OrderMerchModel> productMap = new HashMap<Long, OrderMerchModel>();
        Map<Long, VoucherEntity> voucherMap = new HashMap<Long, VoucherEntity>();
        for (MerchResponse merch : merchs) {
            OrderMerchModel merchVo = productMap.get(merch.getProductId());
            if (merchVo == null) {
                merchVo = convertMerchResponse2MerchResponseVo(merch);
                if (wrapperProcesses.containsKey("product")) {
                    wrapperProcesses.get("product").wrapperMerchProduct(merchVo);
                }
                productMap.put(merch.getProductId(), merchVo);
                merchVos.add(merchVo);
            } else {
                merchVo.setTotalNum(merchVo.getTotalNum() + merch.getTotalNum());
                merchVo.setCheckNum(merchVo.getCheckNum() + merch.getCheckNum());
                merchVo.setRefundNum(merchVo.getRefundNum() + merch.getRefundNum());
                merchVo.setRefundAmount(merchVo.getRefundAmount() + merch.getRefundAmount());
            }
            if (wrapperProcesses.containsKey("strategy")) {
                wrapperProcesses.get("strategy").wrapperMerchStrategy(orderMerchValueResult, merch, merchVo);
            }
            if (wrapperProcesses.containsKey("voucher")) {
                wrapperProcesses.get("voucher").wrapperMerchVoucher(merch, merchVo, voucherMap);
            }
            if (wrapperProcesses.containsKey("refundFlow")) {
                wrapperProcesses.get("refundFlow").wrapperRefundFlowResponse(merch, merchVo, voucherMap);
            }

            // 不再判断可不可退，因为有可能从不可退变成可退，没有意义。
            // wrapOrderMerchRefundInfo(merchVo);

            logger.debug("商品{}[{}]单价: {}, 结算价: {}, 总数量: {}, 退的数量: {}", merch.getMerchName(), merch.getMerchId(), merch.getPrice(), merch.getSettlement_price(),
                merch.getTotalNum(), merch.getRefundNum());
            double discountAmount2 = orderMerchValueResult.getDiscountAmount2();
            //总返利金额 = 返利金额 + (商品单价 - 结算价) * (商品购买数 - 退款数量)
            discountAmount2 = MathUtils.add(
                discountAmount2,
                MathUtils.multiply(MathUtils.subtract(merch.getPrice(), merch.getSettlement_price()),
                    Double.valueOf(Integer.toString(merch.getTotalNum() - merch.getRefundNum()))));

            orderMerchValueResult.setDiscountAmount2(discountAmount2);
            logger.debug("计算得出的后返金额：{}", orderMerchValueResult.getDiscountAmount2());
        }
        orderMerchValueResult.setMerchResponseVo(merchVos);
        return orderMerchValueResult;
    }

    /**
     * 将模型对象转换成客户端值对象
     * 
    * @param MerchResponse
    * @return
    */
    protected OrderMerchModel convertMerchResponse2MerchResponseVo(MerchResponse merch) {
        OrderMerchModel merchVo = new OrderMerchModel();
        BeanUtils.copyProperties(merch, merchVo);
        return merchVo;
    }

    /**
    * 组装订单商品产品数据
    * 
    * @param orderMerchModel
    */
    @Override
    public void wrapperMerchProduct(OrderMerchModel orderMerchModel) {
        // ProductRelease productRelease = productApi.findProductReleaseById(orderMerchModel.getProductId());
        SkuProductResultVO productVo;
        try {
            productVo = productApi.findSkuProductById(orderMerchModel.getProductId());
        } catch (Exception e) {
            throw new RuntimeException("productId:" + orderMerchModel.getProductId() + " 查询接口出错！", e);
        }
        if (productVo == null) {
            throw new RuntimeException("productId:" + orderMerchModel.getProductId() + " 没有查询到！");
        }

        SkuProduct skuProduct = productVo.getSkuProduct();
        SpuProduct spuProduct = productVo.getSpuProduct();

        List<PCStrategyResult> PCStrategyResults = productVo.getStrategyList();
        for (PCStrategyResult str : PCStrategyResults) {
            List<Strategy> strategies = str.getStrategyList();
            for (Strategy strategy : strategies) {
                if (strategy.getId().equals(orderMerchModel.getStrategyId())) {
                    orderMerchModel.setTicketVarie(strategy.getTicketVarie()); // 设置票品
                }
            }
        }
        String productType = skuProduct.getProductType();
        orderMerchModel.setProductTypes(productType.split(":")[1]);
        orderMerchModel.setProductName(skuProduct.getName());
        orderMerchModel.setPopularName(spuProduct.getName());
        orderMerchModel.setSkuParamsName(skuProduct.getSkuParamsName());
        orderMerchModel.setProCategory(skuProduct.getProCategory());
        orderMerchModel.setTheaterArea(skuProduct.getRegion());
        orderMerchModel.setTheatercnum(skuProduct.getRonda());
        orderMerchModel.setUseMethod(spuProduct.getUseMethod());
        orderMerchModel.setSkuProduct(skuProduct);
        orderMerchModel.setTicketsStartTime(spuProduct.getStartTime());
        orderMerchModel.setTicketsEndTime(spuProduct.getEndTime());
        orderMerchModel.setCheckInAddress(spuProduct.getCheckinAddress());
        orderMerchModel.setCheckInType(spuProduct.getCheckInType());
        orderMerchModel.setConfirm(SkuProductGlobal.IS_TWICE_SURE.toString().equals(skuProduct.getTwiceSure()) ? true : false);
        if (productService.isTicketProduct(orderMerchModel.getProCategory())) { //非通用商品
            orderMerchModel.setIsTicketProduct(true);
            ProductInfo productInfo = productApi.findProductInfoById(skuProduct.getProductId());
            if (productInfo != null) {
                orderMerchModel.setPopularName(productInfo.getName());
                orderMerchModel.setReleaseThurl(productInfo.getReleaseThurl());
            }
        } else {
            orderMerchModel.setIsTicketProduct(false);
            orderMerchModel.setReleaseThurl(spuProduct.getReleaseThurl());
        }
        if (CheckUtils.isNotNull(skuProduct.getRegion()))
            orderMerchModel.setAreaDesc(skuProduct.getRegion());
        orderMerchModel.setSupplierId(Long.parseLong(skuProduct.getSupplierId()));

        SkuProductResultVO skuProductResultVO = skuProductApi.findSkuById(orderMerchModel.getProductId());
        if (skuProductResultVO != null && skuProductResultVO.getSkuProduct() != null) {
            boolean isOneVote = skuProductResultVO.getSkuProduct().getIsOneVote();
            orderMerchModel.setIsOneVote(isOneVote);
        } else {
            throw new RuntimeException("产品ID：" + orderMerchModel.getProductId() + "对应的产品没有查询到！");
        }
    }

    /**
    * 组装订单商品退票换票规则信息
    * 
    * @param orderMerchModel
    */
    private void wrapOrderMerchRefundInfo(OrderMerchModel orderMerchModel) {
        if (orderMerchModel.getRefund() != null)
            return;
        if (productService.isTicketProduct(orderMerchModel.getProCategory())) { //非通用商品
            orderMerchModel.setRefund(true);
            return;
        }
        wrapOrderMerchRefundInfoForCommon(orderMerchModel); //通用商品
    }

    /**
    * 组装订单商品退票换票规则信息(通用商品)
    * 
    * @param orderMerchModel
    */
    private void wrapOrderMerchRefundInfoForCommon(OrderMerchModel orderMerchModel) {
        TicketRule ticketRule = commonProductRefundService.findActiveRefundRule(orderMerchModel.getProductId());
        if (ticketRule == null || (ticketRule.getRefund() != null && ticketRule.getRefund() == false)) { //没有查询到退票规则、退票规则中的退票标志为空或false，都设置为不允许退票
            orderMerchModel.setRefund(false);
            return;
        }
        if (orderMerchModel.getStartTime() == null)
            return;
        orderMerchModel.setRefund(commonProductRefundService.isRefund(ticketRule.getTimeType(), ticketRule.getDays(), ticketRule.getHours(),
            ticketRule.getMinutes(), orderMerchModel.getStartTime()));
        orderMerchModel.setTimeType(ticketRule.getTimeType());
        orderMerchModel.setDays(ticketRule.getDays());
        orderMerchModel.setHours(ticketRule.getHours());
        orderMerchModel.setMinutes(ticketRule.getMinutes());
    }

    /**
    * 组装订单商品政策数据
    * 
    * @param orderMerchValueResult
    * @param merch
    * @param orderMerchModel
    */
    @Override
    public void wrapperMerchStrategy(OrderMerchValueResult orderMerchValueResult, MerchResponse merch, OrderMerchModel orderMerchModel) {
        if (merch.getOrderStrategyList() == null || merch.getOrderStrategyList().size() == 0)
            return;
        orderMerchModel.setDiscountType(merch.getOrderStrategyList().get(0).getDiscountType());
        for (OrderStrategyResponse os : merch.getOrderStrategyList()) {
            double discountAmount = os.getDiscountAmount();
            if (discountAmount != 0.0) {
                orderMerchModel.setDiscountAmount(orderMerchModel.getDiscountAmount() + discountAmount);
            }
        }
    }

    /**
    * 组装订单商品服务属性数据
    * 
    * @param merch
    * @param orderMerchModel
    */
    @Override
    public void wrapperMerchVoucher(MerchResponse merch, OrderMerchModel orderMerchModel, Map<Long, VoucherEntity> voucherMap) {
        VoucherModel voucherVo = new VoucherModel();
        voucherVo.setMerchId(merch.getMerchId());
        voucherVo.setMerchState(merch.getMerchState());
        VoucherEntity baseVoucher = voucherMap.get(merch.getVoucherId());
        if (baseVoucher == null) {
            baseVoucher = voucherService.getVoucherById(merch.getVoucherId());
            if (baseVoucher != null)
                voucherMap.put(merch.getVoucherId(), baseVoucher);
        }
        if (baseVoucher != null) {
            if (orderMerchModel.getStartTime() == null)
                orderMerchModel.setStartTime(baseVoucher.getStartTime());
            if (orderMerchModel.getShowStartTime() == null)
                orderMerchModel.setShowStartTime(baseVoucher.getShowStartTime());
            if (orderMerchModel.getVoucherCategory() == null)
                orderMerchModel.setVoucherCategory(baseVoucher.getVoucherCategory());
            voucherVo.setVoucherId(baseVoucher.getVoucherId());
            voucherVo.setVoucherContent(baseVoucher.getVoucherContent());
            voucherVo.setVoucherContentType(baseVoucher.getVoucherContentType());
            voucherVo.setVoucherContentImageUrl(baseVoucher.getVoucherContentImageUrl());
            voucherVo.setContactMobile(baseVoucher.getContactMobile());
            voucherVo.setContactName(baseVoucher.getContactName());
            voucherVo.setVoucherState(baseVoucher.getVoucherState());
            voucherVo.setScreenings("");
            voucherVo.setRegion("");
            voucherVo.setAreaDesc(orderMerchModel.getAreaDesc());

            SkuProductResultVO skuProductResultVO = skuProductApi.findSkuById(orderMerchModel.getProductId());
            if (skuProductResultVO != null && skuProductResultVO.getSkuProduct() != null) {
                boolean isSelectSeat = skuProductResultVO.getSkuProduct().getIsSelectSeat();
                voucherVo.setNeedSeat(isSelectSeat);
            } else {
                throw new RuntimeException("产品ID：" + orderMerchModel.getProductId() + "对应的产品没有查询到！");
            }

            // voucherVo.setNeedSeat(productService.getSuppilerIdNeedSeats(orderMerchModel.getSupplierId()));
            voucherVo.setSeatNumbers("");

            if (baseVoucher.getTheaterProductVoucherVOList() != null) {
                for (int i = 0; i < baseVoucher.getTheaterProductVoucherVOList().size(); i++) {
                    TheaterProductVoucherVO theater = baseVoucher.getTheaterProductVoucherVOList().get(i);
                    if (theater.getProductId() != null && theater.getProductId().longValue() == orderMerchModel.getProductId()) {
                        voucherVo.setScreenings(theater.getScreenings());
                        voucherVo.setRegion(theater.getRegion());
                        voucherVo.setSeatNumbers(theater.getSeatNumbers());
                        break;
                    }
                }
            }
        }
        orderMerchModel.getVouchers().add(voucherVo);
    }

    /** 
     * @see com.pzj.platform.appapi.service.OrderMerchService#wrapperRefundFlowResponse(com.pzj.trade.order.entity.MerchResponse, com.pzj.platform.appapi.model.OrderMerchModel, java.util.Map)
     */
    @Override
    public void wrapperRefundFlowResponse(MerchResponse merch, OrderMerchModel orderMerchModel, Map<Long, VoucherEntity> voucherMap) {
        orderMerchModel.setRefundInfos(merch.getRefundInfos());
    }
}
