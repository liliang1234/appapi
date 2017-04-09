/**
 * piaozhijia.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package com.pzj.modules.appapi.voucher;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.pzj.appapi.vo.OrderVo;
import com.pzj.common.util.DateUtils;
import com.pzj.core.stock.model.StockModel;
import com.pzj.core.stock.model.StockQueryRequestModel;
import com.pzj.core.stock.service.StockQueryService;
import com.pzj.framework.context.Result;
import com.pzj.framework.context.ServiceContext;
import com.pzj.platform.appapi.util.CheckUtils;
import com.pzj.product.api.enterprise.service.CheckInSiteService;
import com.pzj.product.api.enterprise.vo.CheckInSiteVO;
import com.pzj.product.api.product.service.CommonProductService;
import com.pzj.product.api.product.service.ProductService;
import com.pzj.product.api.product.vo.ProductPriceVO;
import com.pzj.product.vo.product.SpuProduct;
import com.pzj.voucher.common.ExecuteResult;
import com.pzj.voucher.entity.VoucherEntity;
import com.pzj.voucher.service.VoucherService;
import com.pzj.voucher.vo.InitVoucherVo;
import com.pzj.voucher.vo.VoucherProductInfoVO;

/**
 *
 * @author jinliang
 * @version $Id: AppVoucherServiceImpl.java, v 0.1 2016年6月8日 下午5:12:47 jinliang Exp $
 */
@Service("IVoucherCreateService")
public class VoucherCreateServiceImpl implements IVoucherCreateService {
    private static final Logger  logger = LoggerFactory.getLogger(VoucherCreateServiceImpl.class);

    @Autowired
    private VoucherService       voucherService;
    @Autowired
    private ProductService       productService;
    @Autowired
    private CheckInSiteService   checkInSiteService;
    @Autowired
    private CommonProductService commonProductService;
    @Autowired
    private StockQueryService    stockQueryService;

    /**
     * 通用产品创建voucher接口
     * @param productInfoMap  <productId， prouductNum> 产品Id和选购产品的数量
     * @param voucherCategory  产品类别
     * @param supplierId        供应商ID
     * @param contactMobile     联系人手机号
     * @param contactName       联系人姓名
     * @param startTime         有效时间
     * @param expireTime        过期时间
     * @param address           产品的收货人地址
     * @return  BaseVoucher
     * @throws Exception
     */
    @Override
    public VoucherEntity createCommonVoucher(Map<Long, Integer> productInfoMap, Integer voucherCategory, Long supplierId, String contactMobile,
                                             String contactName, Timestamp startTime, Timestamp expireTime, String address, Integer consumerCardType,
                                             SpuProduct spu) throws Exception {

        InitVoucherVo vo = new InitVoucherVo();
        Date now = new Date();
        vo.setCreateTime(now);
        //vo.setContactName(contactName);
        //vo.setContactMobile(contactMobile);
        vo.setAddress(address); //特产专用
        vo.setStartTime(startTime);
        vo.setExpireTime(expireTime);
        logger.info("expireTime ::" + expireTime);
        vo.setSupplierId(supplierId);
        vo.setVoucherCategory(voucherCategory);
        vo.setVoucherContent(contactMobile);
        vo.setVoucherContentType(consumerCardType);
        Result<StockModel> resultStock = new Result<StockModel>();
        String checkStartTime = spu.getStartTime();
        String checkEndTime = spu.getEndTime();
        logger.info("检票开始时间段 start time ::" + checkStartTime);
        logger.info("检票结束时间段 end time ::" + checkEndTime);
        vo.setCheckStartTime(checkStartTime);
        vo.setCheckEndTime(checkEndTime);
        //通用只有一个库存
        List<VoucherProductInfoVO> productInfoVoList = new ArrayList<>();
        for (Entry<Long, Integer> entry : productInfoMap.entrySet()) {
            Long productId = entry.getKey();
            Integer number = entry.getValue();
            ProductPriceVO ppVO = new ProductPriceVO();
            ppVO.setId(productId);
            ppVO.setNeedStrategy(true);
            List<ProductPriceVO> products = commonProductService.getProducts(ppVO);// TODO 通过productId 获取 产品信息
            if (!products.get(0).getUnlimitedInventory()) {
                if (CheckUtils.isEmpty(resultStock.getData())) {
                    StockQueryRequestModel param = new StockQueryRequestModel();
                    param.setRuleId(products.get(0).getStockRuleId());
                    //演艺的库存应该是同一个
                    param.setStockTime(DateUtils.formatDateTime(startTime));
                    logger.info("IVoucherCreateService rule id ::" + products.get(0).getStockRuleId());
                    logger.info("IVoucherCreateService stock time ::" + DateUtils.formatDateTime(startTime).toString());
                    resultStock = stockQueryService.queryStockByRule(param, ServiceContext.getServiceContext());
                }
            }
            if (CollectionUtils.isEmpty(products)) {
                continue;
            }

            ProductPriceVO priceVO = products.get(0);

            String productName = priceVO.getName();
            VoucherProductInfoVO vpiVO = new VoucherProductInfoVO();
            vpiVO.setProductId(productId);
            vpiVO.setProductName(productName);
            vpiVO.setProductNum(number);
            if (CheckUtils.isNotEmpty(resultStock.getData()) && CheckUtils.isNotEmpty(resultStock.getData().getId()))
                vpiVO.setStockId(resultStock.getData().getId());
            productInfoVoList.add(vpiVO);
        }
        vo.setProductInfoList(productInfoVoList);

        String jsonString = JSON.toJSONString(vo);
        logger.info("开始创建通用Voucher：" + jsonString);
        ExecuteResult<VoucherEntity> result = voucherService.createVoucher(vo);
        if (result.getStateCode() == ExecuteResult.SUCCESS.intValue()) {
            return result.getData();
        }

        logger.info(" result.getStateCode()  ===  " + result.getStateCode());
        logger.info(" result.getMessage()  ===  " + result.getMessage());
        logger.info(" result.getData()  ===  " + result.getData());

        return null;
    }

    private InitVoucherVo initCommonVoucher(Map<Long, OrderVo.TicketVo> productInfoMap, Integer voucherCategory, Long supplierId, String contactMobile,
                                            String contactName, Timestamp startTime, Timestamp expireTime, Timestamp showStartTime, Timestamp showEndTime,
                                            String address, String scenic, String scenicId, Integer ticketVarie, SpuProduct spu, Integer consumerCardType)
                                                                                                                                                          throws Exception {
        String qrCode;
        try {
            // 生成二维码
            qrCode = UUID.randomUUID().toString().toUpperCase().replaceAll("-", "");
        } catch (Throwable t) {
            logger.error(t.getMessage(), t);
            throw new RuntimeException(t);
        }
        InitVoucherVo vo = new InitVoucherVo();
        Date now = new Date();
        vo.setCreateTime(now);
        //vo.setContactName(contactName);
        // vo.setContactMobile(contactMobile);
        vo.setAddress(address);
        vo.setShowEndTime(showEndTime);
        vo.setShowStartTime(showStartTime);
        vo.setStartTime(startTime);
        vo.setExpireTime(expireTime);
        vo.setSupplierId(supplierId);
        vo.setVoucherCategory(voucherCategory);
        vo.setVoucherContent(qrCode);
        //景区或者演艺的类型由voucher来判断
        //vo.setVoucherContentType(consumerCardType);
        //非一证一票为false
        vo.setOneVote(false);
        //        vo.setScreenings(screenings);
        //        vo.setRegion(region);
        //        vo.setSeatNumbers(seatNumbers);
        vo.setScenicId(scenicId);
        vo.setScenic(scenic);
        vo.setTicketVarie(ticketVarie);
        String checkStartTime = spu.getStartTime();
        String checkEndTime = spu.getEndTime();
        logger.info("检票开始时间段 start time ::" + checkStartTime);
        logger.info("检票结束时间段 end time ::" + checkEndTime);
        vo.setCheckStartTime(checkStartTime);
        vo.setCheckEndTime(checkEndTime);
        List<VoucherProductInfoVO> productInfoVoList = new ArrayList<>();
        for (Entry<Long, OrderVo.TicketVo> entry : productInfoMap.entrySet()) {
            Long productId = entry.getKey();
            OrderVo.TicketVo ticketVo = entry.getValue();
            Integer number = ticketVo.getProductNum();

            ProductPriceVO priceVO = productService.getProductById(productId);

            List<CheckInSiteVO> checkInSiteVOList = priceVO.getCheckSiteVoList();
            String productName = priceVO.getTicketType().split(":")[1];
            VoucherProductInfoVO vpiVO = new VoucherProductInfoVO();
            vpiVO.setProductId(productId);
            vpiVO.setProductName(productName);
            vpiVO.setProductNum(number);
            Long stockId = ticketVo.getStockId() != null ? ticketVo.getStockId() : null;
            vpiVO.setStockId(stockId);
            logger.info("initOneCardVoucher stock id " + stockId);
            /* 演艺 */
            String screenings1 = ticketVo.getMatchs(); // 演出场次
            String region1 = ticketVo.getArea(); // 演出区域
            String seatNumbers1 = ticketVo.getSeat(); // 座位号

            if (screenings1 != null) {
                vpiVO.setScreening(screenings1);
                //如果是演艺产品，需要set产品组id
                vpiVO.setProductGroupId(spu.getId());
            }
            logger.info("voucher region :: " + ticketVo.getArea());
            if (region1 != null) {
                vpiVO.setRegion(region1);
            }
            if (seatNumbers1 != null) {
                vpiVO.setSeatNumbers(seatNumbers1);
            }

            Map<Long, Integer> childProductInfoMap = new HashMap<>();
            for (CheckInSiteVO cisVO : checkInSiteVOList) {
                Long childProductId = cisVO.getId();
                CheckInSiteVO checkInSiteVO = checkInSiteService.getById(childProductId);
                Integer maxUsedTimes = checkInSiteVO.getCheckCount() == 0 ? -99 : checkInSiteVO.getCheckCount();
                childProductInfoMap.put(childProductId, maxUsedTimes);
            }
            vpiVO.setChildProductMap(childProductInfoMap);
            productInfoVoList.add(vpiVO);
        }
        vo.setProductInfoList(productInfoVoList);

        return vo;
    }

    private List<InitVoucherVo> initOneCardVoucher(Map<String, OrderVo.TicketVo> productInfoMap, Integer voucherCategory, Long supplierId,
                                                   String contactMobile, Timestamp startTime, Timestamp expireTime, Timestamp showStartTime,
                                                   Timestamp showEndTime, String address, String scenic, String scenicId, Integer ticketVarie, SpuProduct spu,
                                                   Integer consumerCardType) throws Exception {

        List<InitVoucherVo> voList = new ArrayList<>();

        for (Entry<String, OrderVo.TicketVo> entry : productInfoMap.entrySet()) {

            Date now = new Date();
            String idcard = entry.getKey();
            OrderVo.TicketVo ticketVo = entry.getValue();
            Long productId = ticketVo.getProductId();
            Integer number = 1;

            // 创建初始化Voucher的VO
            InitVoucherVo vo = new InitVoucherVo();
            // 设置创建时间
            vo.setCreateTime(now);
            // 设置联系人名字
            if (CheckUtils.isNotEmpty(ticketVo.getBuyName())) {
                vo.setContactName(ticketVo.getBuyName());
            } else {
                vo.setContactName(ticketVo.getContactee());//兼容旧版
            }
            // 设置联系人手机号
            //vo.setContactMobile(contactMobile);
            // 设置收货地址
            vo.setAddress(address);
            // 设置演艺的开始时间
            vo.setShowEndTime(showEndTime);
            // 设置演艺的结束时间
            vo.setShowStartTime(showStartTime);
            // 设置游玩的开始时间（有效开始时间）
            vo.setStartTime(startTime);
            // 设置游玩的线束时间（有效结束时间）
            vo.setExpireTime(expireTime);
            // 设置供应商ID
            vo.setSupplierId(supplierId);
            // 设置产品类型
            vo.setVoucherCategory(voucherCategory);
            // 设置身份证号或二维码
            vo.setVoucherContent(idcard);
            //景区或者演艺的类型由voucher来判断
            //vo.setVoucherContentType(consumerCardType);
            //一证一票为true
            vo.setOneVote(true);
            // 设置景区ID
            vo.setScenicId(scenicId);
            // 设置景区名称
            vo.setScenic(scenic);
            // 设置团散类型
            vo.setTicketVarie(ticketVarie);

            //设置检票时间段
            String checkStartTime = spu.getStartTime();
            String checkEndTime = spu.getEndTime();
            logger.info("检票开始时间段 start time ::" + checkStartTime);
            logger.info("检票结束时间段 end time ::" + checkEndTime);
            vo.setCheckStartTime(checkStartTime);
            vo.setCheckEndTime(checkEndTime);
            ProductPriceVO priceVO = productService.getProductById(productId);
            List<CheckInSiteVO> checkInSiteVOList = priceVO.getCheckSiteVoList();
            String productName = priceVO.getTicketType().split(":")[1];

            VoucherProductInfoVO vpiVO = new VoucherProductInfoVO();
            vpiVO.setProductId(productId);
            vpiVO.setProductName(productName);
            vpiVO.setProductNum(number);
            Long stockId = ticketVo.getStockId() != null ? ticketVo.getStockId() : null;
            vpiVO.setStockId(stockId);
            logger.info("initOneCardVoucher stock id " + stockId);
            /* 演艺 */
            if (ticketVo.getMatchs() != null) {
                vpiVO.setScreening(ticketVo.getMatchs());
                //演艺产品需要set产品组id
                vpiVO.setProductGroupId(spu.getId());
            }
            logger.info("voucher region :: " + ticketVo.getArea());
            if (ticketVo.getArea() != null) {
                vpiVO.setRegion(ticketVo.getArea());
            }
            if (ticketVo.getSeat() != null) {
                vpiVO.setSeatNumbers(ticketVo.getSeat());
            }

            Map<Long, Integer> childProductInfoMap = new HashMap<>();
            List<VoucherProductInfoVO> productInfoVoList = new ArrayList<>();
            for (CheckInSiteVO cisVO : checkInSiteVOList) {
                Long childProductId = cisVO.getId();
                CheckInSiteVO checkInSiteVO = checkInSiteService.getById(childProductId);
                Integer maxUsedTimes = checkInSiteVO.getCheckCount() == 0 ? -99 : checkInSiteVO.getCheckCount();
                childProductInfoMap.put(childProductId, maxUsedTimes);
            }
            vpiVO.setChildProductMap(childProductInfoMap);
            productInfoVoList.add(vpiVO);
            vo.setProductInfoList(productInfoVoList);
            voList.add(vo);
        }

        return voList;
    }

    /**
    *
    * 创建非一证一票的voucher
    * @param productInfoMap 传入参数为产品id, 个数<productId, productNum>
    * @param voucherCategory 本次购买的产品类别
    * @param supplierId 供应商ID
    * @param contactMobile 联系人手机号
    * @param startTime 游玩开始时间
    * @param expireTime 游玩结束时间
    * @param showStartTime 演艺开始时间
    * @param showEndTime 演艺结束时间
    * @param address 地址，只有特产商品才有
    * @param // screenings 场次
    * @param // region 区域
    * @param // seatNumbers 座位号
    * @param scenic 景区名称
    * @param ticketVarie 团散
    * @return
    * @throws Exception
    */
    @Override
    public VoucherEntity createCommonVoucher(Map<Long, OrderVo.TicketVo> productInfoMap, Integer voucherCategory, Long supplierId, String contactMobile,
                                             String contactName, Timestamp startTime, Timestamp expireTime, Timestamp showStartTime, Timestamp showEndTime,
                                             String address, String scenic, String scenicId, Integer ticketVarie, SpuProduct spu, Integer consumerCardType)
                                                                                                                                                           throws Exception {

        InitVoucherVo vo = initCommonVoucher(productInfoMap, voucherCategory, supplierId, contactMobile, contactName, startTime, expireTime, showStartTime,
            showEndTime, address, scenic, scenicId, ticketVarie, spu, consumerCardType);

        String jsonString = JSON.toJSONString(vo);
        logger.info("开始创建非一证一票Voucher：" + jsonString);

        ExecuteResult<VoucherEntity> voucher = voucherService.createVoucher(vo);

        return voucher.getData();
    }

    /**
     *
     * 创建一证一票的voucher
     * @param productInfoMap 传入参数为身份号，产品id,<idCard， productId>
     * @param voucherCategory 本次购买的产品类别
     * @param supplierId 供应商ID
     * @param contactMobile 联系人手机号
     * @param startTime 游玩开始时间
     * @param expireTime 游玩结束时间
     * @param showStartTime 演艺开始时间
     * @param showEndTime 演艺结束时间
     * @param address 地址，只有特产商品才有
     * @param // screenings 场次
     * @param // region 区域
     * @param // seatNumbers 座位号
     * @param scenic 景区名称
     * @param ticketVarie 团散
     * @return
     * @throws Exception
     */
    @Override
    public List<VoucherEntity> createOneCardVoucher(Map<String, OrderVo.TicketVo> productInfoMap, Integer voucherCategory, Long supplierId,
                                                    String contactMobile, Timestamp startTime, Timestamp expireTime, Timestamp showStartTime,
                                                    Timestamp showEndTime, String address, String scenic, String scenicId, Integer ticketVarie, SpuProduct spu,
                                                    Integer consumerCardType) throws Exception {
        List<InitVoucherVo> voList = initOneCardVoucher(productInfoMap, voucherCategory, supplierId, contactMobile, startTime, expireTime, showStartTime,
            showEndTime, address, scenic, scenicId, ticketVarie, spu, consumerCardType);
        List<VoucherEntity> baseVoucherList = new ArrayList<>();

        for (InitVoucherVo vo : voList) {
            String jsonString = JSON.toJSONString(vo);
            logger.info("开始创建一证一票Voucher：" + jsonString);
            ExecuteResult<VoucherEntity> voucher = voucherService.createVoucher(vo);
            baseVoucherList.add(voucher.getData());
        }

        return baseVoucherList;
    }
}
