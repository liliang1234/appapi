/**
 * piaozhijia.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package com.pzj.platform.appapi.dubbo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.pzj.channel.Strategy;
import com.pzj.common.QueryPageList;
import com.pzj.common.QueryPageModel;
import com.pzj.common.mapper.JsonMapper;
import com.pzj.framework.context.Result;
import com.pzj.framework.converter.JSONConverter;
import com.pzj.modules.appapi.api.utils.StrategyFilter;
import com.pzj.platform.appapi.service.impl.ProductIdType;
import com.pzj.product.service.ICloseTimeSlotService;
import com.pzj.product.service.ISkuProductService;
import com.pzj.product.service.ISkuScenicService;
import com.pzj.product.vo.product.SkuCloseTimeSlot;
import com.pzj.product.vo.product.SpuProduct;
import com.pzj.product.vo.voParam.queryParam.AppSearchQuery;
import com.pzj.product.vo.voParam.queryParam.CloseTimeSlotQueryParam;
import com.pzj.product.vo.voParam.queryParam.SkuProductQuery;
import com.pzj.product.vo.voParam.queryParam.SkuProductQueryParam;
import com.pzj.product.vo.voParam.queryParam.SkuSaledDateModel;
import com.pzj.product.vo.voParam.queryParam.SpuProductQuery;
import com.pzj.product.vo.voParam.queryParam.SpuProductQueryParamVO;
import com.pzj.product.vo.voParam.resultParam.PCAddressResult;
import com.pzj.product.vo.voParam.resultParam.SkuProductResultVO;
import com.pzj.product.vo.voParam.resultParam.SkuSaledCalendarResult;
import com.pzj.product.vo.voParam.resultParam.SkuScenicResult;
import com.pzj.product.vo.voParam.resultParam.SpuProductResultVO;

/**
 * 
 * @author Mark
 * @version $Id: SkuProductApi.java, v 0.1 2016年7月27日 上午11:57:26 pengliqing Exp $
 */
@Component
public class SkuProductApi {

    private static final Logger   logger = LoggerFactory.getLogger(SkuProductApi.class);
    @Autowired
    private ISkuProductService    iSkuProductService;
    @Autowired
    private ICloseTimeSlotService iCloseTimeSlotService;
    @Autowired
    private ISkuScenicService     skuScenicService;

    public SpuProductResultVO findSpuProductById(Long distributorId, Long id, ProductIdType idType, String salesType) {
        try {
            SpuProductQueryParamVO vo = new SpuProductQueryParamVO();
            SpuProductQuery spuProductQuery = new SpuProductQuery();
            spuProductQuery.setIds(new ArrayList<Long>());
            spuProductQuery.getIds().add(id);

            SkuProductQuery skuProductQuery = new SkuProductQuery();
            skuProductQuery.setIds(new ArrayList<Long>());
            skuProductQuery.getIds().add(id);
            //            skuProductQuery.setId(id);

            if (idType.equals(ProductIdType.SKU_ID)) {
                vo.setSkuProductParam(skuProductQuery);
            } else if (idType.equals(ProductIdType.SPU_ID)) {
                vo.setSpuProductParam(spuProductQuery);
            }
            vo.setDistributorId(distributorId);
            vo.setSalesType(salesType);
            logger.error("------------------->" + JsonMapper.toJsonString(vo));
            Long begin = System.currentTimeMillis();
            List<SpuProductResultVO> rvos = iSkuProductService.findSkuProductForApp(vo);
            logger.info("findSpuProductById query use time------------->" + (System.currentTimeMillis() - begin));
            logger.error("==++==++= 查询通用产品信息 =++==++===>\n{}", JSONConverter.toJson(rvos));
            if (rvos != null && rvos.size() > 0) {
                SpuProductResultVO result = rvos.get(0);
                StrategyFilter.filterChannel(result, salesType);
                return result;
            }
        } catch (Exception ex) {
            logger.error("", ex);
        }
        return null;
    }

    public QueryPageList<SpuProductResultVO> findSpuProductByScenicId(Long distributorId, Long scenicId, String salesType, Integer pageNo, Integer pageSize)
                                                                                                                                                            throws Exception {
        try {
            SpuProductQueryParamVO vo = new SpuProductQueryParamVO();
            vo.setDistributorId(distributorId);
            vo.setSalesType(salesType);
            AppSearchQuery query = new AppSearchQuery();
            query.setId(scenicId);
            vo.setAppSearchQuery(query);
            QueryPageModel queryPageModel = new QueryPageModel(pageNo, pageSize);
            logger.debug("查询景区下产品组入参：\n{} <====> {}", JSONConverter.toJson(vo), JSONConverter.toJson(queryPageModel));
            Long begin = System.currentTimeMillis();
            QueryPageList<SpuProductResultVO> pageResults = iSkuProductService.findSkuProductForApp(vo, queryPageModel);
            logger.info("findSpuProductByScenicId query use time------------->" + (System.currentTimeMillis() - begin));
            if (pageResults == null) {
                logger.debug("景区：{} 下没有查到产品组列表", scenicId);
                return pageResults;
            }
            List<SpuProductResultVO> vos = pageResults.getResultList();
            if (vos != null && !vos.isEmpty()) {
                for (SpuProductResultVO sprvo : vos) {
                    StrategyFilter.filterChannel(sprvo, salesType);
                }
            }
            logger.error("==++==++= 查询景区产品信息 =++==++===>\n{}, {}", scenicId, JSONConverter.toJson(pageResults));
            return pageResults;
        } catch (Exception ex) {
            logger.error("", ex);
        }
        return null;
    }

    /**
     * @param salesType 销售端口
     * @param query app地区查询vo
     * @param distributorId 当前用id
     * @param proCategory 产品类别 @see com.pzj.base.common.global.GlobalDict.ProductCategory
     * 
     * */
    public QueryPageList<SpuProductResultVO> findCommonSpuProduct(Long distributorId, String proCategory, String salesType, AppSearchQuery query,
                                                                  Integer pageNo, Integer pageSize) throws Exception {
        SpuProductQueryParamVO vo = new SpuProductQueryParamVO();
        SkuProductQuery skuProductParam = new SkuProductQuery();
        skuProductParam.setProCategory(proCategory);
        vo.setAppSearchQuery(query);
        vo.setDistributorId(distributorId);
        vo.setSalesType(salesType);
        vo.setSkuProductParam(skuProductParam);
        QueryPageModel queryPageModel = new QueryPageModel(pageNo, pageSize);
        logger.debug("------------------->" + JsonMapper.toJsonString(vo));
        logger.debug("------------------->" + JsonMapper.toJsonString(queryPageModel));
        Long begin = System.currentTimeMillis();
        QueryPageList<SpuProductResultVO> pageResults = iSkuProductService.findSkuProductForApp(vo, queryPageModel);
        logger.info("findCommonSpuProduct query use time------------->" + (System.currentTimeMillis() - begin));
        if (pageResults == null) {
            return pageResults;
        }
        List<SpuProductResultVO> rvos = pageResults.getResultList();
        if (rvos != null && !rvos.isEmpty()) {
            for (SpuProductResultVO vo1 : rvos) {
                StrategyFilter.filterChannel(vo1, salesType);
            }
        }
        return pageResults;
    }

    public List<SkuProductResultVO> findSkuProductBySpuId(Long spuId) throws Exception {
        SkuProductQueryParam vo = new SkuProductQueryParam();
        SpuProductQuery sp = new SpuProductQuery();
        sp.setId(spuId);
        vo.setSpuProductParam(sp);
        logger.debug("------------------->" + JsonMapper.toJsonString(vo));
        Long begin = System.currentTimeMillis();
        List<SkuProductResultVO> lists = iSkuProductService.findSkuproductForSupplier(vo);
        logger.info("findSkuProductBySpuId query use time------------->" + (System.currentTimeMillis() - begin));
        return lists;
    }

    public List<PCAddressResult> findSpuAddressByDistributorId(Long distributorId, String salesType) {
        SpuProductQueryParamVO vo = new SpuProductQueryParamVO();
        vo.setDistributorId(distributorId);
        vo.setSalesType(salesType);
        return iSkuProductService.findSpuAddressByDistributorId(vo);
    }

    /**
     * 查询产品组关闭日期
     * 
     * @param id 产品组ID
     * */
    public List<SkuCloseTimeSlot> findSkuCloseTimeSlotByParams(Long id) {
        CloseTimeSlotQueryParam param = new CloseTimeSlotQueryParam();
        param.setProductInfoId(id);
        return iCloseTimeSlotService.querySkuCloseTimeSlotByParams(param);
    }

    /**
     * 产品搜索
     * 
     * @param distributorId 分销商ID
     * @param salesType 销售端口
     * @param productName 产品关键字
     * @param province 省
     * @param city 市
     * @param county 县
     * @return
     * @throws Exception
     */
    public List<SpuProductResultVO> searchProducts(Long distributorId, String salesType, String productName, String province, String city, String county)
                                                                                                                                                         throws Exception {
        SpuProductQueryParamVO vo = new SpuProductQueryParamVO();
        vo.setDistributorId(distributorId);
        vo.setSalesType(salesType);
        AppSearchQuery appSearchQuery = new AppSearchQuery();
        appSearchQuery.setSearchKey(StringUtils.isEmpty(productName) ? null : productName);
        appSearchQuery.setProvince(StringUtils.isEmpty(province) ? null : province);
        appSearchQuery.setCity(StringUtils.isEmpty(city) ? null : city);
        appSearchQuery.setCounty(StringUtils.isEmpty(county) ? null : county);
        vo.setAppSearchQuery(appSearchQuery);
        Long begin = System.currentTimeMillis();
        List<SpuProductResultVO> rvos = iSkuProductService.findSkuProductForApp(vo);
        logger.info("searchProducts query use time------------->" + (System.currentTimeMillis() - begin));
        if (rvos != null && !rvos.isEmpty()) {
            for (SpuProductResultVO vo1 : rvos) {
                StrategyFilter.filterChannel(vo1, salesType);
            }
        }
        return rvos;
    }

    public SpuProductResultVO findSkuById(Long resellerId, Long skuId, String salesType) {

        try {
            SpuProductQueryParamVO param = new SpuProductQueryParamVO();
            param.setDistributorId(resellerId);
            param.setSalesType(salesType);
            SkuProductQuery skuParam = new SkuProductQuery();
            //            skuParam.setIds(new ArrayList<Long>());
            //            skuParam.getIds().add(skuId);
            skuParam.setId(skuId);
            param.setSkuProductParam(skuParam);
            Long begin = System.currentTimeMillis();
            List<SpuProductResultVO> spuList = iSkuProductService.findSkuProductForApp(param);
            logger.info("findSkuById query use time------------->" + (System.currentTimeMillis() - begin));

            if (spuList != null && !spuList.isEmpty()) {
                SpuProductResultVO result = spuList.get(0);
                StrategyFilter.filterChannel(result, salesType);
                return result;
            }
        } catch (Exception ex) {
            logger.error("", ex);
        }
        return null;
    }

    public SkuProductResultVO findSkuById(Long skuId) {
        try {
            return iSkuProductService.findSkuProductById(skuId);
        } catch (Exception e) {
            logger.error("", e);
        }
        return null;
    }

    /**
     * 
     * 
     * @param queryPageModel 分页
     * @param province   省
     * @param productTypes   产品类型 1、普通 10剧场
     * @param salePort   销售端口
     * @param isGroup    团散
     * @param userId     用户id
     * @return
     * @throws Exception
     */
    public Result<QueryPageList<SkuScenicResult>> findSkuScenicForApp(QueryPageModel queryPageModel, String province, List<Integer> productTypes,
                                                                      String salePort, String isGroup, Long userId) throws Exception {
        //用户，端口相关查询信息
        SpuProductQueryParamVO productQueryParamVO = new SpuProductQueryParamVO();
        productQueryParamVO.setDistributorId(userId);
        productQueryParamVO.setSalesType(salePort);
        productQueryParamVO.setDistributorId(userId);
        productQueryParamVO.setTicketVarie(isGroup);
        //景区查询信息
        AppSearchQuery appSearchQuery = new AppSearchQuery();
        appSearchQuery.setProvince(province);
        //产品类型查询信息
        SpuProductQuery spuProductQuery = new SpuProductQuery();
        spuProductQuery.setProductTypes(productTypes);

        productQueryParamVO.setAppSearchQuery(appSearchQuery);
        productQueryParamVO.setSpuProductParam(spuProductQuery);
        logger.debug("skuScenicService.findSkuScenicForApp 查询入参 {}", JSONConverter.toJson(productQueryParamVO));
        logger.debug("skuScenicService.findSkuScenicForApp 分页入参 {}", JSONConverter.toJson(queryPageModel));
        Long begin = System.currentTimeMillis();
        Result<QueryPageList<SkuScenicResult>> resultList = skuScenicService.findSkuScenicForApp(productQueryParamVO, queryPageModel);
        logger.info("findSkuScenicForApp query use time------------->" + (System.currentTimeMillis() - begin));
        logger.debug("skuScenicService.findSkuScenicForApp 查询出参 {}", JSONConverter.toJson(resultList));
        return resultList;
    }

    public Result<ArrayList<SkuSaledCalendarResult>> getSkuSaledCalendar(SpuProduct spu, List<SkuCloseTimeSlot> closeTimeList, Strategy strategy)
                                                                                                                                                 throws Exception {
        SkuSaledDateModel dateModel = new SkuSaledDateModel();
        dateModel.setComputeDate(new Date());
        dateModel.setUseStartDate(spu.getStartDate());
        dateModel.setUseEndDate(spu.getEndDate());
        dateModel.setSaleStartDate(spu.getSaleStartDate());
        dateModel.setSaleEndDate(spu.getSaleEndDate());
        dateModel.setCloseTimeList(closeTimeList);
        dateModel.setDays(strategy.getAdvanceDueDays());
        dateModel.setHour(strategy.getAdvanceDueHour());
        dateModel.setMinute(strategy.getAdvanceDueMinute());
        logger.debug("获取3个月销售日历{}::", JSONConverter.toJson(dateModel));
        return iSkuProductService.getSkuSaledCalendar(dateModel);
    }
}
