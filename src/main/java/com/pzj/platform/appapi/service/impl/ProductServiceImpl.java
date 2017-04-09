/**
 * piaozhijia.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package com.pzj.platform.appapi.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.pzj.address.entity.Address;
import com.pzj.address.entity.AddressParam;
import com.pzj.address.service.AddressService;
import com.pzj.base.common.global.GlobalDict;
import com.pzj.base.common.global.GlobalParam;
import com.pzj.base.entity.SysRebateStrategy;
import com.pzj.base.entity.product.ProductScenic;
import com.pzj.base.service.product.IProductRelationService;
import com.pzj.base.service.product.IProductScenicService;
import com.pzj.base.service.sys.IRebateStrategyService;
import com.pzj.channel.Channel;
import com.pzj.channel.RebateStrategy;
import com.pzj.channel.Strategy;
import com.pzj.channel.vo.resultParam.PCStrategyResult;
import com.pzj.common.QueryPageList;
import com.pzj.common.mapper.JsonMapper;
import com.pzj.common.util.CheckUtils;
import com.pzj.contacts.entity.Contacts;
import com.pzj.contacts.entity.ContactsParam;
import com.pzj.contacts.service.ContactsService;
import com.pzj.core.stock.model.StockModel;
import com.pzj.core.stock.model.StockQueryRequestModel;
import com.pzj.core.stock.service.StockQueryService;
import com.pzj.framework.context.Result;
import com.pzj.framework.context.ServiceContext;
import com.pzj.framework.converter.JSONConverter;
import com.pzj.platform.appapi.constants.ApiDefault;
import com.pzj.platform.appapi.dao.HotWordsMapper;
import com.pzj.platform.appapi.dubbo.SkuProductApi;
import com.pzj.platform.appapi.entity.HotWords;
import com.pzj.platform.appapi.exception.Exceptions;
import com.pzj.platform.appapi.exception.UnExpectResultException;
import com.pzj.platform.appapi.model.CityModel;
import com.pzj.platform.appapi.model.ProvinceModel;
import com.pzj.platform.appapi.model.RebateStrategyModel;
import com.pzj.platform.appapi.model.ScenicModel;
import com.pzj.platform.appapi.model.SkuCloseTimeSlotModel;
import com.pzj.platform.appapi.model.SkuProductModel;
import com.pzj.platform.appapi.model.SkuStockModel;
import com.pzj.platform.appapi.model.SpuListModel;
import com.pzj.platform.appapi.model.SpuProductHeadModel;
import com.pzj.platform.appapi.model.SpuProductModel;
import com.pzj.platform.appapi.model.StrategyModel;
import com.pzj.platform.appapi.model.StrategyResultModel;
import com.pzj.platform.appapi.service.ProductService;
import com.pzj.platform.appapi.util.PropertyLoader;
import com.pzj.platform.appapi.util.StringUtils;
import com.pzj.product.service.ISkuFilledService;
import com.pzj.product.service.ISkuProductService;
import com.pzj.product.vo.product.SkuCloseTimeSlot;
import com.pzj.product.vo.product.SkuFilled;
import com.pzj.product.vo.product.SkuProduct;
import com.pzj.product.vo.product.SpuProduct;
import com.pzj.product.vo.voParam.queryParam.AppSearchQuery;
import com.pzj.product.vo.voParam.queryParam.SkuSaledDateModel;
import com.pzj.product.vo.voParam.resultParam.PCAddressResult;
import com.pzj.product.vo.voParam.resultParam.SkuSaledCalendarResult;
import com.pzj.product.vo.voParam.resultParam.SpuProductResultVO;
import com.pzj.settlement.base.common.util.StringUtil;

/**
 * 
 * @author Mark
 * @version $Id: ProductServiceImpl.java, v 0.1 2016年7月27日 上午11:53:39 pengliqing Exp $
 */
@Service("productServiceApp")
public class ProductServiceImpl implements ProductService {

    private static String           TICKET_CATEGRORY_KEY = "ticketCategory";
    private static final Logger     logger               = LoggerFactory.getLogger(ProductServiceImpl.class);
    @Resource(name = "propertyLoader")
    private PropertyLoader          propertyLoader;
    @Autowired
    private SkuProductApi           skuProductApi;
    @Autowired
    HotWordsMapper                  hotWordsMapper;
    @Autowired
    private StockQueryService       stockQueryService;

    @Autowired
    private IProductRelationService productRelationService;
    @Autowired
    private IProductScenicService   scenicService;

    @Autowired
    private IRebateStrategyService  irebateStrategyService;

    @Autowired
    private ContactsService         contactsService;

    @Autowired
    private AddressService          addressService;

    @Autowired
    private ISkuFilledService       skuFilledService;

    @Autowired
    private ISkuProductService      iSkuProductService;

    public static final int         DEFAULT_PAGENO       = 1;                                                //默认起始页
    public static final int         DEFAULT_PAGESIZE     = 10;                                               //默认每页大小

    public static final String      SUPLIT_P             = "</p>";

    /**
     * 查询景区下的产品组
     * @param distributorId 分销商ID
     * @param scenicId 景区ID
     * @param salesType 销售端口
     *           导游APP 4
     *           商户APP 5
     *           微店    7
     * @param pageNo 页号
     * @param pageSize 页大小
     *           
     * @throws Exception 
     */
    @Override
    public ScenicModel getSpuListByScenicId(Long distributorId, Long scenicId, String salesType, Integer pageNo, Integer pageSize) throws Exception {
        // 查询景区相关信息
        ProductScenic entity = scenicService.getById(scenicId);
        if (entity == null) {
            throw new UnExpectResultException(Exceptions.DATAERROR.getCode(), "获取景区信息失败！");
        }

        // 查询分销商景区下的产品组
        QueryPageList<SpuProductResultVO> pageList = skuProductApi.findSpuProductByScenicId(distributorId, scenicId, salesType, pageNo, pageSize);
        // 如果景区下面没有产品组，返回空
        if (pageList == null || pageList.getResultList() == null || pageList.getResultList().isEmpty()) {
            return null;
        }

        // 景区下的符合政策的产品组列表
        List<SpuProductModel> spuProductModels = new ArrayList<>();
        // 景区下产品组中最低建议零售价
        Double minAdvicePrice = 0d;
        // 景区下产品组中最低市场价
        Double minMarketPrice = 0d;
        // 景区下的产品组
        List<SpuProductResultVO> spuProductList = pageList.getResultList();

        for (SpuProductResultVO spuProductResultVO : spuProductList) {
            if (spuProductResultVO.getSpuProduct() == null || spuProductResultVO.getSkuProductList() == null
                || spuProductResultVO.getSkuProductList().isEmpty()) {
                continue;
            }

            SpuProductModel model = wrapSpuProductModel(spuProductResultVO.getSpuProduct());

            getSkuProductModel(spuProductResultVO.getSkuProductList().get(0), model);

            boolean hasStrategy = wrapSpuProductModelInfo(spuProductResultVO, model);
            if (!hasStrategy) { // 如果没有政策，则不添加产品组
                continue;
            }
            wrapAdvancedTime(spuProductResultVO, model);
            model.setSkuProducts(getSkuProductModel(spuProductResultVO, true, salesType));
            spuProductModels.add(model);
            if (minAdvicePrice == 0 || minAdvicePrice > new Double(model.getMinAdvicePrice())) {
                minAdvicePrice = new Double(model.getMinAdvicePrice());
            }
            if (minMarketPrice == 0 || minMarketPrice > new Double(model.getMinMarketPrice())) {
                minMarketPrice = new Double(model.getMinMarketPrice());
            }
        }

        ScenicModel scenicModel = new ScenicModel();
        // 设置景区的相关属性
        wrapScenicInfo(scenicId, entity, minAdvicePrice, minMarketPrice, scenicModel);
        // 设置分页
        setPageInfo(pageList, scenicModel);
        // 设置景区下的产品组信息
        scenicModel.setSpuLists(spuProductModels);
        logger.debug("query result" + JsonMapper.getInstance().toJson(pageList));
        logger.debug("show result" + JsonMapper.getInstance().toJson(scenicModel));

        return scenicModel;
    }

    private void setPageInfo(QueryPageList<SpuProductResultVO> pageList, ScenicModel scenicModel) {
        scenicModel.setPageNo(pageList.getPageBean() != null ? pageList.getPageBean().getCurrentPage() : DEFAULT_PAGENO);
        scenicModel.setPageSize(pageList.getPageBean() != null ? pageList.getPageBean().getPageSize() : DEFAULT_PAGESIZE);
        scenicModel.setTotalCount(pageList.getPageBean() != null ? pageList.getPageBean().getTotalCount() : 0);
    }

    private void wrapScenicInfo(Long scenicId, ProductScenic entity, Double minAdvicePrice, Double minMarketPrice, ScenicModel scenicModel) {
        scenicModel.setCity(entity.getCity() == null ? "" : entity.getCity());
        scenicModel.setCounty(entity.getCounty() == null ? "" : entity.getCounty());
        scenicModel.setProvince(entity.getProvince() == null ? "" : entity.getProvince());
        scenicModel.setImg_url(entity.getImgUrl() == null ? "" : entity.getImgUrl());
        scenicModel.setSceneName(entity.getName() == null ? "" : entity.getName());
        scenicModel.setInfo(entity.getInfo() == null ? "" : entity.getInfo());
        scenicModel.setScenicId(scenicId);
        scenicModel.setMinAdvicePrice(StringUtils.doubleToString(minAdvicePrice));
        scenicModel.setMinMarketPrice(StringUtils.doubleToString(minMarketPrice));
    }

    private boolean wrapSpuProductModelInfo(SpuProductResultVO spuProductResultVO, SpuProductModel model) {
        model.setIsLimitAdvanceDue(0);
        Integer maxAdvancedDay = 0;
        Integer maxAdvancedDueHour = 0;
        Integer advacnedDueMin = 0;
        Double minMarketPrice = 0d;
        Double minAdvicePrice = 0d;
        Map<Long, List<PCStrategyResult>> strategyList = spuProductResultVO.getStrategyList();
        logger.debug("======++ StrategyList ++=====> {}", JSONConverter.toJson(strategyList));
        if (strategyList == null) {
            return false;
        }

        boolean hasStrategy = false; // 是否有政策

        for (List<PCStrategyResult> pcStrategyResults : strategyList.values()) {
            if (pcStrategyResults == null || pcStrategyResults.isEmpty()) {
                return false;
            }
            for (PCStrategyResult pcStrategy : pcStrategyResults) {
                for (Strategy strategy : pcStrategy.getStrategyList()) {
                    hasStrategy = true;
                    SysRebateStrategy sysrebate = new SysRebateStrategy();
                    sysrebate.setStrategyId(strategy.getId());
                    sysrebate.setProductId(strategy.getProductId());
                    sysrebate.setDelFlag(GlobalParam.FLAG.start());
                    List<SysRebateStrategy> syrebateList = irebateStrategyService.findListByParams(sysrebate);
                    if (syrebateList == null || syrebateList.size() < 1) {
                        strategy.setRebateStrategyList(new ArrayList<RebateStrategy>());
                    }
                    strategy.setRebateStrategyList(strategyVoConvertUtil(syrebateList));

                    if (strategy.getMarketPrice() != null && (minMarketPrice == 0d || minMarketPrice.compareTo(strategy.getMarketPrice()) > 0)) {
                        minMarketPrice = strategy.getMarketPrice();
                    }
                    if (strategy.getAdvicePrice() != null && (minAdvicePrice == 0d || minAdvicePrice.compareTo(strategy.getAdvicePrice()) > 0)) {
                        minAdvicePrice = strategy.getAdvicePrice();
                    }
                    //这个标签的判断未必正确！
                    if (strategy.getIsLimitAdvanceDue() == null || strategy.getIsLimitAdvanceDue() == 0)
                        continue;
                    model.setIsLimitAdvanceDue(strategy.getIsLimitAdvanceDue());
                    if (strategy.getAdvanceDueDays() > maxAdvancedDay) {
                        maxAdvancedDay = strategy.getAdvanceDueDays();
                    }
                    if (strategy.getAdvanceDueHour() > maxAdvancedDueHour) {
                        maxAdvancedDueHour = strategy.getAdvanceDueHour();
                        advacnedDueMin = strategy.getAdvanceDueMinute();
                    }
                    model.setIsLimitDelayConsume(strategy.getIsLimitDelayConsume());

                }
            }
        }

        if (!hasStrategy) {
            return false;
        }

        Integer isOneVote = 2;
        List<SkuProduct> skus = spuProductResultVO.getSkuProductList();
        if (spuProductResultVO.getSkuProductList() == null) {
            logger.error(Exceptions.DATAERROR.getCode(), "获取产品组中产品数据出错！");
            throw new UnExpectResultException(Exceptions.DATAERROR.getCode(), "获取产品组中产品数据出错！");
        }
        for (SkuProduct skuProduct : skus) {
            isOneVote = skuProduct.getIsOneVote() == null ? null : skuProduct.getIsOneVote() ? 1 : 2;
        }
        model.setIsUnique(isOneVote);
        model.setMinAdvicePrice(StringUtils.doubleToString(minAdvicePrice));
        model.setMinMarketPrice(StringUtils.doubleToString(minMarketPrice));
        model.setAdvanceDueDays(maxAdvancedDay);
        model.setAdvanceDueHour(maxAdvancedDueHour);
        model.setAdvanceDueMinute(advacnedDueMin);

        return true;
    }

    /**
     * 通用产品组列表接口
     * @param distributorId
     * @param id
     * @param salesType
     *           导游APP 4
     *           商户APP 5
     * @throws Exception 
     */
    @Override
    public SpuListModel getCommonSpuList(Long distributorId, String city, String county, String province, String proCategory, String salesType, Integer pageNo,
                                         Integer pageSize) throws Exception {
        SpuListModel spuListModel = new SpuListModel();
        List<SpuProductModel> models = new ArrayList<>();
        AppSearchQuery query = new AppSearchQuery();
        query.setCity(city);
        query.setCounty(county);
        query.setProvince(province);
        QueryPageList<SpuProductResultVO> pageList = skuProductApi.findCommonSpuProduct(distributorId, proCategory, salesType, query, pageNo, pageSize);
        if (pageList == null) {
            spuListModel.setPageNo(pageNo);
            spuListModel.setPageSize(pageSize);
            spuListModel.setTotalCount(0);
            spuListModel.setSpuLists(models);
            return spuListModel;
        }
        List<SpuProductResultVO> lists = pageList.getResultList();
        if (lists != null && !lists.isEmpty()) {
            for (SpuProductResultVO spuProductResultVO : lists) {
                SpuProductModel model = wrapSpuProductModel(spuProductResultVO.getSpuProduct());
                if (spuProductResultVO.getSkuProductList() != null && spuProductResultVO.getSkuProductList().size() > 0) {
                    getSkuProductModel(spuProductResultVO.getSkuProductList().get(0), model);
                }
                wrapAdvancedInfo(spuProductResultVO, model);
                model.setSkuProducts(getSkuProductModel(spuProductResultVO, true, salesType));
                models.add(model);
            }
        }
        spuListModel.setSpuLists(models);
        spuListModel.setPageNo(pageList.getPageBean() != null ? pageList.getPageBean().getCurrentPage() : DEFAULT_PAGENO);
        spuListModel.setPageSize(pageList.getPageBean() != null ? pageList.getPageBean().getPageSize() : DEFAULT_PAGESIZE);
        spuListModel.setTotalCount(pageList.getPageBean() != null ? pageList.getPageBean().getTotalCount() : 0);
        return spuListModel;
    }

    private SkuStockModel covertStockModel(StockModel stockModel) {
        SkuStockModel skuStockModel = new SkuStockModel();
        skuStockModel.setLeftNum(stockModel.getRemainNum() == null ? 0 : stockModel.getRemainNum());
        skuStockModel.setUsedNum(stockModel.getUsedNum() == null ? 0 : stockModel.getUsedNum());
        skuStockModel.setTotalNum(stockModel.getTotalNum() == null ? 0 : stockModel.getTotalNum());
        skuStockModel.setId(stockModel.getId());
        skuStockModel.setRuleId(stockModel.getRuleId());
        return skuStockModel;
    }

    /**
     * @param distributorId
     * @param id
     * @param idType
     *           产品ID 1
     *           产品组ID 2
     * @param salesType
     *           导游APP 4
     *           商户APP 5
     * @throws Exception 
     */
    @Override
    public SpuProductModel getSpuForOrder(Long distributorId, Long id, int idType, String salesType) throws Exception {
        if (idType == ProductIdType.SKU_ID.getCode()) {
            id = getSpuProductId(distributorId, id, salesType); // 取产品组ID
        }
        // 查询默认联系人
        ContactsParam contactQueryParam = new ContactsParam();
        contactQueryParam.setSupplierId(distributorId);
        logger.info("查询默认联系人contactsService.queryDefault传入的setSupplierId是：" + distributorId);
        Result<Contacts> contactsResult = contactsService.queryDefault(contactQueryParam, null);
        // 查询默认收货、上车、下车地址
        AddressParam addressQueryParam = new AddressParam();
        List<Address> addressList = new ArrayList<Address>();
        addressQueryParam.setCreateBy(distributorId);
        addressQueryParam.setIsDefault(true);
        addressQueryParam.setSupplierId(distributorId);
        logger.info("查询默认收货、上车、下车地址addressService.queryByParam传入的setCreateBy是：" + distributorId);
        Result<ArrayList<Address>> addressResult = addressService.queryByParam(addressQueryParam, null);

        SpuProductResultVO spuProductResultVO = getSpuProductResultVO(distributorId, id, salesType);
        SpuProductModel model = wrapSpuProductModel(spuProductResultVO.getSpuProduct());
        getSkuProductModel(spuProductResultVO.getSkuProductList().get(0), model);
        wrapAdvancedInfo(spuProductResultVO, model);
        model.setSkuProducts(getSkuProductModel(spuProductResultVO, true, salesType));
        model.setCloseTimes(getSkuCloseTimeSlot(id));
        if (contactsResult.getErrorCode() == 10000) {
            if (contactsResult.getData() != null) {
                model.setContacts(contactsResult.getData());
            } else {
                Contacts contacts = new Contacts();
                contacts.setDefault(false);
                model.setContacts(contacts);
            }
        }
        if (addressResult.getErrorCode() == 10000) {
            if (addressResult.getData() != null) {
                addressList = addressResult.getData();
            }
        }
        model.setAddressList(addressList);
        //查询填单项信息
        List<SkuFilled> skuFilledList = new ArrayList<SkuFilled>();
        logger.info("查询默认收货、上车、下车地址addressService.queryByParam传入的setCreateBy是：" + distributorId);
        Result<ArrayList<SkuFilled>> skuFilledResult = skuFilledService.getSkuFilledBySpuId(model.getId());
        if (skuFilledResult.getErrorCode() == 10000) {
            if (skuFilledResult.getData() != null) {
                skuFilledList = skuFilledResult.getData();
            }
        }
        model.setSkuFilledList(skuFilledList);
        //查询可买信息日期集合
        SkuSaledDateModel skuSaledDateModel = new SkuSaledDateModel();
        skuSaledDateModel.setUseStartDate(model.getStartDate()); //        useStartDate    Date    是   可用日期开始时间
        skuSaledDateModel.setUseEndDate(model.getEndDate()); //        useEndDate  Date    是   可用日期结束时间
        skuSaledDateModel.setComputeDate(new Date()); //        computeDate Date    是   要计算的时间点
        skuSaledDateModel.setSaleStartDate(model.getSaleStartDate()); //        saleStartDate   Date    是   销售日期开始时间
        skuSaledDateModel.setSaleEndDate(model.getSaleEndDate()); //        saleEndDate Date    是   销售日期结束时间
        //        skuSaledDateModel.setDays(model.getIsLimitAdvanceDue()); //        days    Integer 是   提前预定时间
        skuSaledDateModel.setCloseTimeList(spuProductResultVO.getCloseTimeSlotList());// closeTimeList   List<SkuCloseTimeSlot>  否   关闭时间列表
        skuSaledDateModel.setDays(model.getAdvanceDueDays()); //days    Integer 是   提前预定时间
        skuSaledDateModel.setHour(model.getAdvanceDueHour()); //   hour    Integer 是   提前预定时刻
        skuSaledDateModel.setMinute(model.getAdvanceDueMinute()); //      minute  Integer 是   提前预定分刻

        logger.info("查询可买信息日期集合传入的参数是：skuSaledDateModel.getUseStartDate()：" + skuSaledDateModel.getUseStartDate() + "|skuSaledDateModel.getUseEndDate()："
                    + skuSaledDateModel.getUseEndDate() + "|skuSaledDateModel.getUseComputeDate()：" + skuSaledDateModel.getComputeDate()
                    + "|skuSaledDateModel.getSaleStartDate()：" + skuSaledDateModel.getSaleStartDate() + "|skuSaledDateModel.getSaleEndDate："
                    + skuSaledDateModel.getSaleEndDate() + "|skuSaledDateModel.getDays()：" + skuSaledDateModel.getDays() + "|skuSaledDateModel.getHour()："
                    + skuSaledDateModel.getHour() + "|skuSaledDateModel.getMinute()：" + skuSaledDateModel.getMinute());

        Result<ArrayList<SkuSaledCalendarResult>> skuSaledCalendarResult = iSkuProductService.getSkuSaledCalendar(skuSaledDateModel);
        List<SkuSaledCalendarResult> skuSaledCalendarResultList = new ArrayList<SkuSaledCalendarResult>();
        if (skuSaledCalendarResult.getErrorCode() == 10000) {
            skuSaledCalendarResultList = skuSaledCalendarResult.getData();
        }
        model.setSkuSaledCalendarList(skuSaledCalendarResultList);
        if (model.getDefaultRemarks() == null || "".equals(model.getDefaultRemarks())) {
            model.setDefaultRemarks(propertyLoader.getProperty(ApiDefault.SYSTEM_FILENAME, "spufororder.default.remark", ""));
        }
        return model;
    }

    /**
     * 查询产品组信息
     * 
     * @param distributorId 分销商ID
     * @param id 产品组ID
     * @param salesType 销售端口
     *          导游APP 4
     *          商户APP 5
     * */
    private SpuProductResultVO getSpuProductResultVO(Long distributorId, Long id, String salesType) throws Exception {
        SpuProductResultVO spuProductResultVO = skuProductApi.findSpuProductById(distributorId, id, ProductIdType.SPU_ID, salesType);
        if (spuProductResultVO == null || spuProductResultVO.getSpuProduct() == null || spuProductResultVO.getSkuProductList() == null
            || spuProductResultVO.getSkuProductList().size() == 0) {
            logger.error("调用方法getSpuProductResultVO出错！");
            throw new UnExpectResultException(Exceptions.DATAERROR.getCode(), "没有产品数据");
        }
        return spuProductResultVO;
    }

    private Long getSpuProductId(Long distributorId, Long id, String salesType) throws Exception {
        SpuProductResultVO spuProductResultVO = skuProductApi.findSpuProductById(distributorId, id, ProductIdType.SKU_ID, salesType);
        if (spuProductResultVO.getSpuProduct() == null) {
            throw new UnExpectResultException(Exceptions.DATAERROR.getCode(), "没有产品组数据");
        }
        return spuProductResultVO.getSpuProduct().getId();
    }

    /**
     * 组装产品组数据
     * 
     * @param spuProductResultVO 产品服务返回的数据对象
     * @return SpuProductModel
     * */
    private SpuProductModel wrapSpuProductModel(SpuProduct spu) {
        SpuProductModel model = new SpuProductModel();
        model.setId(spu.getId());
        model.setName(spu.getName() == null ? "" : spu.getName());
        model.setOneWordFeature(spu.getOneWordFeature() == null ? "" : spu.getOneWordFeature());
        model.setPhotoinfoId(spu.getPhotoinfoId() == null ? "" : spu.getPhotoinfoId());
        model.setGainType(spu.getGainType() == null ? 1 : spu.getGainType());//身份证取票   二维码取票的标示
        model.setPlaytimeMode(spu.getPlaytimeMode());
        model.setPlaytimeUnit(spu.getPlaytimeUnit());
        model.setPlaytimeValue(spu.getPlaytimeValue() == null ? 0 : spu.getPlaytimeValue());
        model.setCheckInType(spu.getCheckInType() == null ? "2" : spu.getCheckInType());
        model.setOrderInfo(spu.getOrderInfo() == null ? "" : spu.getOrderInfo());
        model.setExpenseInfo(spu.getExpenseInfo() == null ? "" : spu.getExpenseInfo());
        model.setSalesmanship(spu.getSalesmanship() == null ? "" : spu.getSalesmanship());
        model.setImportantClause(spu.getImportantClause() == null ? "" : spu.getImportantClause());
        model.setAttentions(spu.getAttentions() == null ? "" : spu.getAttentions());
        model.setUseMethod(spu.getUseMethod() == null ? "" : spu.getUseMethod());
        model.setStartDate(spu.getStartDate());
        model.setEndDate(spu.getEndDate());
        model.setSlideImageList(spu.getPhotoinfoId() == null ? null : Arrays.asList(spu.getPhotoinfoId().split(",")));
        model.setGainPeopleNum(spu.getGainPeopleNum() == null ? -1 : spu.getGainPeopleNum());
        model.setGainPeopleTimeLimitUnit(spu.getGainPeopleTimeLimitUnit());
        model.setGainPeopleTimeLimitValue(spu.getGainPeopleTimeLimitValue() == null ? 0 : spu.getGainPeopleTimeLimitValue());
        model.setGainPeopleTimePurchaseNum(spu.getGainPeopleTimePurchaseNum() == null ? -1 : spu.getGainPeopleTimePurchaseNum());
        model.setMinPurchaseNumInOrder(spu.getMinPurchaseNumInOrder() == null ? 0 : spu.getMinPurchaseNumInOrder());
        model.setIsNeedPlaytime(spu.getIsNeedPlaytime() == null ? 0 : spu.getIsNeedPlaytime());
        model.setNoPlaytimeOrdertimeType(spu.getNoPlaytimeOrdertimeType());
        model.setOrdertimeUnit(spu.getOrdertimeUnit());
        model.setOrdertimeValue(spu.getOrdertimeValue() == null ? 0 : spu.getOrdertimeValue());
        model.setProductInfoDetail(spu.getProductInfoDetail() == null ? "" : spu.getProductInfoDetail());
        model.setOneWordFeature(spu.getOneWordFeature() == null ? "" : spu.getOneWordFeature());
        model.setStartTime(spu.getStartTime() == null ? "" : spu.getStartTime());
        model.setEndTime(spu.getEndTime() == null ? "" : spu.getEndTime());
        model.setCheckInAddress(spu.getCheckinAddress() == null ? "" : spu.getCheckinAddress());
        model.setReeaseInfo(spu.getReeaseInfo() == null ? "" : spu.getReeaseInfo());
        model.setSaleStartDate(spu.getSaleStartDate());
        model.setSaleEndDate(spu.getSaleEndDate());
        model.setCheckStartTime(spu.getStartTime() == null ? "" : spu.getStartTime());
        model.setCheckEndTime(spu.getEndTime() == null ? "" : spu.getEndTime());
        model.setCity(spu.getCity());//产品组所在城市
        model.setProvince(spu.getProvince());//产品组所在省份
        model.setDefaultRemarks(spu.getRondaName());
        //        /*是否一票一证*/
        //        boolean isUnique = false;
        //        if (null != spu.getSupplierId()) {
        //            isUnique = orderUtils.getSuppilerIdIsUnique(Long.parseLong(spu.getSupplierId()));
        //        }
        //        model.setIsUnique(isUnique == true ? 1 : 2);
        return model;
    }

    private void wrapAdvancedInfo(SpuProductResultVO spuProductResultVO, SpuProductModel model) {
        model.setIsLimitAdvanceDue(0);
        Integer maxAdvancedDay = 0;
        Integer maxAdvancedDueHour = 0;
        Integer advacnedDueMin = 0;
        Double minMarketPrice = 0d;
        Double minAdvicePrice = 0d;
        Map<Long, List<PCStrategyResult>> strategyList = spuProductResultVO.getStrategyList();
        logger.debug("======++ strategyList ++=====> {}", JSONConverter.toJson(strategyList));
        if (strategyList != null) {
            for (List<PCStrategyResult> list : strategyList.values()) {
                if (list == null || list.isEmpty()) {
                    continue;
                }
                for (PCStrategyResult pcStrategy : list) {
                    for (Strategy strategy : pcStrategy.getStrategyList()) {
                        SysRebateStrategy sysrebate = new SysRebateStrategy();
                        sysrebate.setStrategyId(strategy.getId());
                        sysrebate.setProductId(strategy.getProductId());
                        List<SysRebateStrategy> syrebateList = irebateStrategyService.findListByParams(sysrebate);
                        if (syrebateList == null || syrebateList.size() < 1) {
                            strategy.setRebateStrategyList(new ArrayList<RebateStrategy>());
                        }
                        strategy.setRebateStrategyList(strategyVoConvertUtil(syrebateList));

                        if (strategy.getMarketPrice() != null && (minMarketPrice == 0d || minMarketPrice.compareTo(strategy.getMarketPrice()) > 0)) {
                            minMarketPrice = strategy.getMarketPrice();
                        }
                        if (strategy.getAdvicePrice() != null && (minAdvicePrice == 0d || minAdvicePrice.compareTo(strategy.getAdvicePrice()) > 0)) {
                            minAdvicePrice = strategy.getAdvicePrice();
                        }
                        //这个标签的判断未必正确！
                        if (strategy.getIsLimitAdvanceDue() == null || strategy.getIsLimitAdvanceDue() == 0)
                            continue;
                        model.setIsLimitAdvanceDue(strategy.getIsLimitAdvanceDue());
                        if (strategy.getAdvanceDueDays() > maxAdvancedDay) {
                            maxAdvancedDay = strategy.getAdvanceDueDays();
                        }
                        if (strategy.getAdvanceDueHour() > maxAdvancedDueHour) {
                            maxAdvancedDueHour = strategy.getAdvanceDueHour();
                        }
                        if (strategy.getAdvanceDueMinute() > 0) {
                            advacnedDueMin = strategy.getAdvanceDueMinute();
                        }
                        model.setIsLimitDelayConsume(strategy.getIsLimitDelayConsume());

                    }
                }
            }
        }
        Integer isOneVote = 2;
        List<SkuProduct> skus = spuProductResultVO.getSkuProductList();
        if (spuProductResultVO.getSkuProductList() == null) {
            logger.error(Exceptions.DATAERROR.getCode(), "获取产品组中产品数据出错！");
            throw new UnExpectResultException(Exceptions.DATAERROR.getCode(), "获取产品组中产品数据出错！");
        }
        for (SkuProduct skuProduct : skus) {
            isOneVote = skuProduct.getIsOneVote() == null ? null : skuProduct.getIsOneVote() ? 1 : 2;
        }
        model.setIsUnique(isOneVote);
        model.setMinAdvicePrice(StringUtils.doubleToString(minAdvicePrice));
        model.setMinMarketPrice(StringUtils.doubleToString(minMarketPrice));
        model.setAdvanceDueDays(maxAdvancedDay);
        model.setAdvanceDueHour(maxAdvancedDueHour);
        model.setAdvanceDueMinute(advacnedDueMin);

        //如果是通用产品，那么需要处理产品特色
        SpuProduct spu = spuProductResultVO.getSpuProduct();
        if (spu.getProductType() != GlobalDict.ProductCategory.normal && spu.getProductType() != GlobalDict.ProductCategory.scenic) {
            //处理通用产品特色
            if (CheckUtils.isNotNull(model.getProductInfoDetail()))
                getProductInfoDetail(model);
        }
    }

    private List<RebateStrategy> strategyVoConvertUtil(List<SysRebateStrategy> lists) {
        List<RebateStrategy> results = new ArrayList<>();
        for (SysRebateStrategy sysRebateStrategy : lists) {
            RebateStrategy rebateStrategy = new RebateStrategy();
            try {
                BeanUtils.copyProperties(rebateStrategy, sysRebateStrategy);
                results.add(rebateStrategy);
            } catch (Exception e) {
                logger.error("", e);
            }
        }
        return results;
    }

    /**
     * 组装退款信息
     * 
     * @param spuProductResultVO 产品服务返回的数据对象
     * @param includeRebateStrategy 是否包含返利数据
     * @return SpuProductModel
     * */

    private SpuProductModel getSkuProductModel(SkuProduct sku, SpuProductModel spu) {
        if (spu == null || sku == null) {
            return spu;
        }
        spu.setRefundDateType(sku.getRefundDateType());
        spu.setPrerefundDays(sku.getPrerefundDays() == null ? 0 : sku.getPrerefundDays());
        spu.setPrerefundHour(sku.getPrerefundHour() == null ? 0 : sku.getPrerefundHour());
        spu.setPrerefundMinute(sku.getPrerefundMinute() == null ? 0 : sku.getPrerefundMinute());
        spu.setPrerefundDistributorFeetype(sku.getPrerefundDistributorFeetype());
        spu.setPrerefundDistributorFeevalue(StringUtils.doubleToString(sku.getPrerefundDistributorFeevalue() == null ? 0 : sku
            .getPrerefundDistributorFeevalue()));
        spu.setProrefundDays(sku.getProrefundDays() == null ? 0 : sku.getProrefundDays());
        spu.setProrefundHour(sku.getProrefundHour() == null ? 0 : sku.getProrefundHour());
        spu.setProrefundMinute(sku.getProrefundMinute() == null ? 0 : sku.getProrefundMinute());
        spu.setProrefundDistributorFeetype(sku.getProrefundDistributorFeetype());
        spu.setProrefundDistributorFeevalue(StringUtils.doubleToString(sku.getProrefundDistributorFeevalue() == null ? 0 : sku
            .getProrefundDistributorFeevalue()));
        spu.setIsNeedPrerefund(sku.getIsNeedPrerefund());
        spu.setIsNeedProrefund(sku.getIsNeedProrefund());
        spu.setPrerefundQuantityType(sku.getPrerefundQuantityType());
        spu.setProrefundQuantityType(sku.getProrefundQuantityType());
        return spu;
    }

    /**
     * 组装产品数据
     * 
     * @param spuProductResultVO 产品服务返回的数据对象
     * @param includeRebateStrategy 是否包含返利数据
     * @return SpuProductModel
     * */
    private List<SkuProductModel> getSkuProductModel(SpuProductResultVO spuProductResultVO, boolean includeRebateStrategy, String salesType) {
        List<SkuProductModel> skus = new ArrayList<>();

        List<Map<String, Object>> scenicList = null;
        List<Long> productIdList = Lists.newArrayList();
        if (spuProductResultVO.getSkuProductList() == null) {
            logger.error(Exceptions.DATAERROR.getCode(), "获取产品组中产品数据出错！");
            throw new UnExpectResultException(Exceptions.DATAERROR.getCode(), "获取产品组中产品数据出错！");
        }
        for (SkuProduct skup : spuProductResultVO.getSkuProductList()) {
            productIdList.add(skup.getId());
        }

        scenicList = productRelationService.findScenicByProductId(productIdList);

        Iterator<SkuProduct> iter = spuProductResultVO.getSkuProductList().iterator();
        while (iter.hasNext()) {
            SkuProduct skup = iter.next();
            SkuProductModel sku = new SkuProductModel();
            sku.setId(skup.getId());
            sku.setName(skup.getName());
            sku.setSkuParamsName(!StringUtil.isEmpty(skup.getProductType()) ? skup.getProductType().split(":")[1] : "");
            sku.setFlag(skup.getIsSelectSeat() == null ? false : skup.getIsSelectSeat());
            sku.setIsUnique(skup.getIsOneVote() == null ? 2 : skup.getIsOneVote() ? 1 : 2);
            sku.setProCategory(skup.getProCategory());
            sku.setUnlimitedInventory(skup.getUnlimitedInventory());
            sku.setRegion(skup.getRegion() == null ? "" : skup.getRegion());
            sku.setSupplierId(skup.getSupplierId() == null ? "" : skup.getSupplierId());
            sku.setStockRuleId(skup.getStockRuleId());
            sku.setRonda(skup.getRonda() == null ? "" : skup.getRonda());
            sku.setStockModel(new SkuStockModel());
            // 封装景区
            if (scenicList != null && !scenicList.isEmpty()) {
                for (Map<String, Object> map : scenicList) {
                    if (sku.getId().toString().equals(map.get("productId").toString())) {
                        if (map != null && map.get("scenicId") != null) {
                            sku.setSceneId(Long.valueOf(map.get("scenicId").toString()));
                        }
                    }
                }
            }
            List<PCStrategyResult> stras = spuProductResultVO.getStrategyList().get(skup.getId());
            if (stras == null || stras.size() == 0) {
                logger.error(Exceptions.DATAERROR.getCode(), "没有政策数据");
                iter.remove();
                continue;
            }
            PCStrategyResult st = stras.get(0);
            Channel channel = st.getChannel();
            sku.setChannelId(channel.getId());
            if (st.getStrategyList() == null || st.getStrategyList().size() < 1) {
                logger.error(Exceptions.DATAERROR.getCode(), "没有政策数据");
                iter.remove();
                continue;
            }
            Strategy strategy = st.getStrategyList().get(0);
            sku.setLeastPerdueNumber(strategy.getLeastPerdueNumber());
            sku.setMostPerdueNumber(strategy.getMostPerdueNumber());
            sku.setAdvicePrice(StringUtils.doubleToString(strategy.getAdvicePrice() == null ? 0 : strategy.getAdvicePrice()));
            sku.setMarketPrice(StringUtils.doubleToString(strategy.getMarketPrice() == null ? 0 : strategy.getMarketPrice()));
            sku.setStrategrId(strategy.getId());
            sku.setScope(strategy.getScope());
            sku.setAdvanceDueDays(strategy.getAdvanceDueDays() == null ? 0 : strategy.getAdvanceDueDays());
            sku.setAdvanceDueHour(strategy.getAdvanceDueHour() == null ? 0 : strategy.getAdvanceDueHour());
            sku.setAdvanceDueMinute(strategy.getAdvanceDueMinute() == null ? 0 : strategy.getAdvanceDueMinute());
            sku.setIsLimitAdvanceDue(strategy.getIsLimitAdvanceDue());
            if (includeRebateStrategy) {
                sku.setStrategyResults(covertToStrategyResultModel(stras));
            }
            skus.add(sku);
        }
        return skus;
    }

    /**
     * 
     * @param stras
     * @return
     */
    private List<StrategyResultModel> covertToStrategyResultModel(List<PCStrategyResult> stras) {
        List<StrategyResultModel> results = new ArrayList<>();
        for (PCStrategyResult pcStrategyResult : stras) {
            StrategyResultModel resultModel = new StrategyResultModel();
            resultModel.setChannel(pcStrategyResult.getChannel());
            resultModel.setProductId(pcStrategyResult.getProductId());
            resultModel
                .setStrategyList(pcStrategyResult.getStrategyList() != null && pcStrategyResult.getStrategyList().size() > 0 ? covertToStrategyModels(pcStrategyResult
                    .getStrategyList()) : new ArrayList<StrategyModel>());
            results.add(resultModel);
        }
        return results;
    }

    /**
     * 
     * @param strategyList
     * @return
     */
    private List<StrategyModel> covertToStrategyModels(List<Strategy> strategyList) {

        List<StrategyModel> strategyModels = new ArrayList<>();
        for (Strategy strategy : strategyList) {
            StrategyModel strategyModel = covertToStrategyModel(strategy);
            strategyModels.add(strategyModel);
        }
        return strategyModels;
    }

    /**
     * 
     * 
     * @param strategy
     * @return
     */
    private StrategyModel covertToStrategyModel(Strategy strategy) {
        StrategyModel strategyModel = new StrategyModel();
        try {
            BeanUtils.copyProperties(strategyModel, strategy);
        } catch (Exception e) {
            logger.error("Strategy->StrategyModel转换失败！", e);
        }
        List<RebateStrategy> rebates = strategy.getRebateStrategyList();
        if (rebates != null && rebates.size() > 0) {
            List<RebateStrategyModel> rebateStrategyList = new ArrayList<>();
            for (RebateStrategy rebateStrategy : rebates) {
                RebateStrategyModel rebateStrategyModel = covertToRebateStrategyModel(rebateStrategy);
                rebateStrategyList.add(rebateStrategyModel);
            }
            strategyModel.setRebateStrategyList(rebateStrategyList);
        }
        strategyModel.setAdvicePrice(StringUtils.doubleToString(strategy.getAdvicePrice()));
        strategyModel.setMarketPrice(StringUtils.doubleToString(strategy.getMarketPrice()));
        strategyModel.setPrice(StringUtils.doubleToString(strategy.getPrice()));
        strategyModel.setSettlementPrice(StringUtils.doubleToString(strategy.getSettlementPrice()));
        strategyModel.setReduceSettlementMoney(StringUtils.doubleToString(strategy.getReduceSettlementMoney()));
        return strategyModel;
    }

    /**
     * 
     * @param rebateStrategy
     * @return
     */
    private RebateStrategyModel covertToRebateStrategyModel(RebateStrategy rebateStrategy) {
        RebateStrategyModel rebateStrategyModel = new RebateStrategyModel();
        try {
            BeanUtils.copyProperties(rebateStrategyModel, rebateStrategy);
        } catch (Exception e) {
            logger.error("RebateStrategy-->RebateStrategyModel转换失败！", e);
        }
        rebateStrategyModel.setRebatePrice(StringUtils.doubleToString(rebateStrategy.getRebatePrice()));
        rebateStrategyModel.setRebateAmount(StringUtils.doubleToString(rebateStrategy.getRebateAmount()));
        return rebateStrategyModel;
    }

    /**
     * 组装产品组关闭时间数据(产品组可用时间段内，关闭时间亦不可购买)
     * 
     * @param productInfoId 产品组ID
     * */
    private SkuCloseTimeSlotModel getSkuCloseTimeSlot(long productInfoId) {
        List<SkuCloseTimeSlot> closeTimes = skuProductApi.findSkuCloseTimeSlotByParams(productInfoId);
        SkuCloseTimeSlotModel slotModel = new SkuCloseTimeSlotModel();
        if (closeTimes != null && !closeTimes.isEmpty()) {
            for (SkuCloseTimeSlot slot : closeTimes) {
                slotModel.getCloseDateList().add(slot.getColseDate());
                slotModel.setStartDate(slot.getStartDate());
                slotModel.setEndDate(slot.getEndDate());
            }
        }
        return slotModel;
    }

    private void getProductInfoDetail(SpuProductModel model) {
        //需求，产品特色为产品详情的前5句话
        String productInfoDetail = model.getProductInfoDetail();
        String productInfo = "";
        String infoDetail[] = {};
        Integer productCount = 1;
        if (productInfoDetail.contains(SUPLIT_P)) {
            infoDetail = productInfoDetail.split(SUPLIT_P);
        }
        for (int i = 0; i < infoDetail.length; i++) {
            String info = infoDetail[i];
            if (info.contains("<img"))
                continue;
            productInfo += infoDetail[i].replace("<p>", "<font>");
            if (i != infoDetail.length - 1 && productCount != 5) {
                productInfo += "</font>";
            } else {
                if (productInfo.startsWith("<font>") && !productInfo.endsWith("</font>"))
                    productInfo += "</font>";
                break;
            }
            productCount++;
        }
        model.setProductInfoMsg(productInfo);
        //需求，产品特色为图片列表的第二章图片
        String photoinfoId = model.getPhotoinfoId();
        String photoinfoArr[] = photoinfoId.split(",");
        if (photoinfoArr.length != 1) {
            model.setProductInfoImg(photoinfoArr[1]);
        } else {
            model.setProductInfoImg(photoinfoArr[0]);
        }
    }

    public static void main(String[] args) {
        String productInfoDetail = "<p>08：30 &nbsp;乘车前往天坛公园（活动时间1小时30分钟）</p><p>10：30 &nbsp;乘车前往中央电视塔（活动时间1小时30分钟）</p><p>12：30 &nbsp;午餐自理（活动时间2小时）</p>";
        String productInfo = "";
        String infoDetail[] = {};
        Integer productCount = 1;
        if (productInfoDetail.contains(SUPLIT_P)) {
            infoDetail = productInfoDetail.split(SUPLIT_P);
        }
        for (int i = 0; i < infoDetail.length; i++) {
            String info = infoDetail[i];
            if (info.contains("<img"))
                continue;
            productInfo += infoDetail[i];
            if (i != infoDetail.length - 1 && productCount != 5) {
                productInfo += "</p>";
            } else {
                if (productInfo.startsWith("<p>") && !productInfo.endsWith("</p>"))
                    productInfo += "</p>";
                break;
            }
            productCount++;
        }
        System.out.println(productInfo);
    }

    /**
     * 判断是否是票类型
     * 
     * @param proCategory 产品类型
     * @return 
     * */
    @Override
    public boolean isTicketProduct(String proCategory) {
        String ticketCategory = propertyLoader.getProperty(ApiDefault.SYSTEM_FILENAME, TICKET_CATEGRORY_KEY);
        if (ticketCategory != null && ticketCategory.indexOf(proCategory + ",") >= 0)
            return true;
        return false;
    }

    /**
     * 判断某个供应商的产品是否需要手动选座
     * 
     * @param supplierId 供应商ID
     * @return <code>true</code> 需要手动选座
     *         <code>false</code> 自动派座
     * */
    @Override
    public boolean getSuppilerIdNeedSeats(Long supplierId) {
        if (propertyLoader.getProperty("system", "supplierIds").contains(supplierId + "")) {
            return true;
        }
        return false;
    }

    /**
     * 获取分销商及相关销售端口可见的省份城市级联列表
     * 
     * @param distributorId 分销商ID
     * @param salesType 销售端口
     * @see com.pzj.platform.appapi.service.ProductService#getCity(java.lang.Long, java.lang.String)
     */
    @Override
    public List<ProvinceModel> getCity(Long distributorId, String salesType) {
        List<PCAddressResult> addresses = skuProductApi.findSpuAddressByDistributorId(distributorId, salesType);
        List<ProvinceModel> provinces = new ArrayList<>();
        Map<String, ProvinceModel> pcMap = new HashMap<>();
        for (PCAddressResult address : addresses) {
            ProvinceModel pm = pcMap.get(address.getProvince());
            if (pm == null) {
                pm = new ProvinceModel();
                pm.setCitys(new ArrayList<CityModel>());
                pm.setProvince(address.getProvince());
                pcMap.put(address.getProvince(), pm);
            }
            CityModel cm = new CityModel();
            cm.setCity(address.getCity());
            pm.getCitys().add(cm);
        }
        for (ProvinceModel prov : pcMap.values()) {
            provinces.add(prov);
        }
        return provinces;
    }

    /** 
     * @throws Exception 
     * @see com.pzj.platform.appapi.service.ProductService#search(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public List<SpuProductHeadModel> search(Long distributorId, String salesType, String productName, String province, String city, String county)
                                                                                                                                                  throws Exception {
        List<SpuProductHeadModel> spuProductHeadModels = new ArrayList<>();
        List<SpuProductResultVO> spuProducts = skuProductApi.searchProducts(distributorId, salesType, productName, province, city, county);
        if (spuProducts == null) {
            return spuProductHeadModels;
        }
        for (SpuProductResultVO spuProductResultVO : spuProducts) {
            SpuProductHeadModel spuProductHeadModel = new SpuProductHeadModel();
            spuProductHeadModels.add(spuProductHeadModel);

            if (spuProductResultVO.getSpuProduct() == null || spuProductResultVO.getSkuProductList() == null
                || spuProductResultVO.getSkuProductList().size() == 0) {
                throw new UnExpectResultException(Exceptions.DATAERROR.getCode(), "没有产品数据");
            }

            SpuProduct spu = spuProductResultVO.getSpuProduct();
            spuProductHeadModel.setId(spu.getId());
            spuProductHeadModel.setName(spu.getName());
            spuProductHeadModel.setOneWordFeature(spu.getOneWordFeature());
            spuProductHeadModel.setReleaseThurl(spu.getReleaseThurl());
            spuProductHeadModel.setProductType(spu.getProductType());

            for (SkuProduct skup : spuProductResultVO.getSkuProductList()) {
                List<PCStrategyResult> stras = spuProductResultVO.getStrategyList().get(skup.getId());
                if (stras == null || stras.size() == 0) {
                    throw new UnExpectResultException(Exceptions.DATAERROR.getCode(), "没有政策数据");
                }
                PCStrategyResult st = stras.get(0);
                if ((st.getStrategyList() != null) && (st.getStrategyList().size() != 0)) {
                    Strategy strategy = st.getStrategyList().get(0);

                    if (spuProductHeadModel.getMinAdvicePrice() == null || strategy.getAdvicePrice() < new Double(spuProductHeadModel.getMinAdvicePrice())
                        && (strategy.getAdvicePrice() != null)) {
                        spuProductHeadModel.setMinAdvicePrice(StringUtils.doubleToString(strategy.getAdvicePrice()));
                    }
                    if (spuProductHeadModel.getMinMarketPrice() == null || strategy.getMarketPrice() < new Double(spuProductHeadModel.getMinMarketPrice())
                        && (strategy.getMarketPrice() != null)) {
                        spuProductHeadModel.setMinMarketPrice(StringUtils.doubleToString(strategy.getMarketPrice()));
                    }
                }
            }
        }
        // TODO 排序：精确匹配前置，模糊匹配后置
        return spuProductHeadModels;
    }

    /** 
     * @see com.pzj.platform.appapi.service.RecommendService#getHotWords(com.alibaba.fastjson.JSONObject)
     */
    @Override
    public List<HotWords> getHotWords() {
        return hotWordsMapper.queryByParamMap(null);
    }

    // 获取交易上下文
    protected ServiceContext getServiceContext() {
        ServiceContext serviceContext = new ServiceContext();
        serviceContext.setReference("AppAPI");
        return serviceContext;
    }

    /** 
     * @see com.pzj.platform.appapi.service.ProductService#getSpuStock(java.lang.Long, java.lang.String, java.lang.String)
     */
    @Override
    public List<SkuStockModel> getSpuStock(Long distributorId, Long spuId, String queryDate, String salseType) {
        List<SkuStockModel> lists = new ArrayList<>();
        SpuProductResultVO spuProductResultVO = skuProductApi.findSpuProductById(distributorId, spuId, ProductIdType.SPU_ID, salseType);
        if (spuProductResultVO == null || spuProductResultVO.getSpuProduct() == null || spuProductResultVO.getSkuProductList() == null
            || spuProductResultVO.getSkuProductList().size() == 0) {
            logger.error("调用方法getSpuProductResultVO出错！");
            throw new UnExpectResultException(Exceptions.DATAERROR.getCode(), "没有产品数据");
        }
        SkuStockModel skuStockModel = null;
        StockQueryRequestModel stock = new StockQueryRequestModel();
        stock.setStockTime(queryDate);
        for (SkuProduct sku : spuProductResultVO.getSkuProductList()) {
            //判断是否无限库存
            if (!sku.getUnlimitedInventory()) {//非无限库存
                stock.setRuleId(sku.getStockRuleId());//库存规则id
                logger.info("query param" + JsonMapper.getInstance().toJson(stock));
                Result<StockModel> result = stockQueryService.queryStockByRule(stock, getServiceContext());
                logger.info("query result" + JsonMapper.getInstance().toJson(result));
                if (result.isOk() && result.getData() != null) {
                    skuStockModel = covertStockModel(result.getData());
                    lists.add(skuStockModel);
                }
            }
        }
        return lists;
    }

    /**
     * 取得spu提前购买时间
     * 
     * 
     * */

    private void wrapAdvancedTime(SpuProductResultVO spuProductResultVO, SpuProductModel model) {
        Integer maxAdvancedDay = 0;
        Integer maxAdvancedDueHour = 0;
        Integer advacnedDueMin = 0;
        Map<Long, List<PCStrategyResult>> strategyList = spuProductResultVO.getStrategyList();
        logger.debug("======++ strategyList ++=====> {}", JSONConverter.toJson(strategyList));
        if (strategyList != null) {
            for (List<PCStrategyResult> list : strategyList.values()) {
                if (list == null || list.isEmpty()) {
                    continue;
                }
                for (PCStrategyResult pcStrategy : list) {
                    for (Strategy strategy : pcStrategy.getStrategyList()) {
                        SysRebateStrategy sysrebate = new SysRebateStrategy();
                        sysrebate.setStrategyId(strategy.getId());
                        sysrebate.setProductId(strategy.getProductId());
                        List<SysRebateStrategy> syrebateList = irebateStrategyService.findListByParams(sysrebate);
                        if (syrebateList == null || syrebateList.size() < 1) {
                            strategy.setRebateStrategyList(new ArrayList<RebateStrategy>());
                        }
                        strategy.setRebateStrategyList(strategyVoConvertUtil(syrebateList));

                        //这个标签的判断未必正确！
                        if (strategy.getIsLimitAdvanceDue() == null || strategy.getIsLimitAdvanceDue() == 0)
                            continue;
                        model.setIsLimitAdvanceDue(strategy.getIsLimitAdvanceDue());
                        if (strategy.getAdvanceDueDays() > maxAdvancedDay) {
                            maxAdvancedDay = strategy.getAdvanceDueDays();
                        }
                        if (strategy.getAdvanceDueHour() > maxAdvancedDueHour) {
                            maxAdvancedDueHour = strategy.getAdvanceDueHour();
                        }
                        if (strategy.getAdvanceDueMinute() > 0) {
                            advacnedDueMin = strategy.getAdvanceDueMinute();
                        }
                        model.setIsLimitDelayConsume(strategy.getIsLimitDelayConsume());

                    }
                }
            }
        }
        Integer isOneVote = 2;
        List<SkuProduct> skus = spuProductResultVO.getSkuProductList();
        if (spuProductResultVO.getSkuProductList() == null) {
            logger.error(Exceptions.DATAERROR.getCode(), "获取产品组中产品数据出错！");
            throw new UnExpectResultException(Exceptions.DATAERROR.getCode(), "获取产品组中产品数据出错！");
        }
        for (SkuProduct skuProduct : skus) {
            isOneVote = skuProduct.getIsOneVote() == null ? null : skuProduct.getIsOneVote() ? 1 : 2;
        }
        model.setIsUnique(isOneVote);
        model.setAdvanceDueDays(maxAdvancedDay);
        model.setAdvanceDueHour(maxAdvancedDueHour);
        model.setAdvanceDueMinute(advacnedDueMin);
    }
}
