/**
 * piaozhijia.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package com.pzj.platform.appapi.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.pzj.base.common.global.GlobalDict;
import com.pzj.channel.Strategy;
import com.pzj.channel.vo.resultParam.PCStrategyResult;
import com.pzj.customer.entity.Customer;
import com.pzj.framework.converter.JSONConverter;
import com.pzj.modules.appapi.api.SceneService;
import com.pzj.platform.appapi.dao.HomeClassificationConfigMapper;
import com.pzj.platform.appapi.dao.HomeSlideConfigMapper;
import com.pzj.platform.appapi.dao.HotProductRecommendMapper;
import com.pzj.platform.appapi.dubbo.SkuProductApi;
import com.pzj.platform.appapi.entity.HomeClassificationConfig;
import com.pzj.platform.appapi.entity.HomeSlideConfig;
import com.pzj.platform.appapi.entity.HotProductRecommend;
import com.pzj.platform.appapi.exception.Exceptions;
import com.pzj.platform.appapi.model.HotProductModel;
import com.pzj.platform.appapi.model.ScenicModel;
import com.pzj.platform.appapi.model.SpuProductModel;
import com.pzj.platform.appapi.service.ProductService;
import com.pzj.platform.appapi.service.RecommendService;
import com.pzj.platform.appapi.util.CheckUtils;
import com.pzj.platform.appapi.util.PropertyLoader;
import com.pzj.platform.appapi.util.StringUtils;
import com.pzj.product.api.enterprise.vo.ScenicVO;
import com.pzj.product.vo.product.SkuProduct;
import com.pzj.product.vo.product.SpuProduct;
import com.pzj.product.vo.voParam.resultParam.SpuProductResultVO;

/**
 * 
 * @author Mark
 * @version $Id: RecommendServiceImpl.java, v 0.1 2016年7月22日 上午11:07:57 pengliqing Exp $
 */
@Service("recommendServiceApp")
public class RecommendServiceImpl implements RecommendService {

    private static final Logger    logger                 = LoggerFactory.getLogger(RecommendServiceImpl.class);

    @Autowired
    HomeSlideConfigMapper          homeSlideConfigMapper;
    @Autowired
    HomeClassificationConfigMapper homeClassificationConfigMapper;
    @Autowired
    HotProductRecommendMapper      hotProductRecommendMapper;

    @Autowired
    private SkuProductApi          skuProductApi;
    @Autowired
    private SceneService           sceneService;
    @Autowired
    private ProductService         productService;
    @Resource(name = "propertyLoader")
    private PropertyLoader         propertyLoader;

    private static final String    DEFAULT_SLIDE_PROVINCE = "其他";

    /** 
     * 查询APP首页轮播图配置
     * 
     * @param requestObject
     * @return
     * @see com.pzj.platform.appapi.service.RecommendService#getHomeSlide(com.alibaba.fastjson.JSONObject)
     */
    @Override
    public List<HomeSlideConfig> getHomeSlide(JSONObject requestObject) {
        Map<String, Object> bParam = commonParamWrapper(requestObject);
        List<HomeSlideConfig> result = homeSlideConfigMapper.queryByParamMap(bParam);
        if (result == null || result.isEmpty()) {
            bParam.put("province", DEFAULT_SLIDE_PROVINCE);
            result = homeSlideConfigMapper.queryByParamMap(bParam);
        }

        return result;
    }

    /** 
     * 查询APP首页分类配置信息
     * 
     * @param requestObject
     * @return
     * @see com.pzj.platform.appapi.service.RecommendService#getHomeClassification(com.alibaba.fastjson.JSONObject)
     */
    @Override
    public List<HomeClassificationConfig> getHomeClassification(JSONObject requestObject) {
        Map<String, Object> bParam = commonParamWrapper(requestObject);
        return homeClassificationConfigMapper.queryByParamMap(bParam);
    }

    /** 
     * 查询热门推荐产品
     * 
     * @param requestObject
     * @return
     * @see com.pzj.platform.appapi.service.RecommendService#getHotProduct(com.alibaba.fastjson.JSONObject)
     */
    @Override
    public List<HotProductModel> getHotProduct(JSONObject requestObject, Customer customer) {
        // 热门产品推荐查询参数组装
        Map<String, Object> bParam = commonParamWrapper(requestObject);

        logger.debug("热门产品推荐入参：\n{}", JSONConverter.toJson(bParam));
        // 热门产品推荐表配置的产品列表数据
        List<HotProductRecommend> hotProductRecommends = hotProductRecommendMapper.queryByParamMap(bParam);
        logger.debug("热门产品查询结果：\n{}", JSONConverter.toJson(hotProductRecommends));

        // 销售端口
        String salesPort = requestObject.getString("salesPort");
        // 返回的热门产品推荐列表
        List<HotProductModel> hotProductModels = new ArrayList<>();

        for (HotProductRecommend hotProductRecommend : hotProductRecommends) {
            switch (hotProductRecommend.getProductType()) {
                case GlobalDict.ProductCategory.normal:
                case GlobalDict.ProductCategory.scenic:
                    // 查询景区、演艺的
                    wrapHotProductByScenic(hotProductModels, hotProductRecommend, customer.getId(), salesPort);
                    break;
                case GlobalDict.ProductCategory.dayTour:
                case GlobalDict.ProductCategory.lineProduct:
                case GlobalDict.ProductCategory.tripPhotos:
                case GlobalDict.ProductCategory.busCharter:
                case GlobalDict.ProductCategory.nativeProduct:
                case GlobalDict.ProductCategory.shuttleBus:
                case GlobalDict.ProductCategory.guideProduct:
                case GlobalDict.ProductCategory.room:
                case GlobalDict.ProductCategory.restaurant:
                default:
                    // 查询通用产品的产品组信息
                    wrapHotProductByInfo(hotProductModels, hotProductRecommend, customer.getId(), salesPort);
                    break;
            }
        }
        return hotProductModels;
    }

    /**
     * 组装热门推荐的景区、演艺
     * 
     * @param hps
     * @param hot
     * @param resellerId
     * @param salesPort
     */
    private void wrapHotProductByScenic(List<HotProductModel> hps, HotProductRecommend hot, Long resellerId, String salesPort) {
        ScenicVO scenicVo = sceneService.getScenicById(hot.getProductId());
        if (scenicVo == null) {
            return;
        }

        try {
            ScenicModel scenicModel = productService.getSpuListByScenicId(resellerId, scenicVo.getId(), salesPort, 1, 100);
            if (scenicModel != null) {
                HotProductModel hp = new HotProductModel();
                hp.setPopularName(scenicModel.getSceneName());
                hp.setProductId(scenicModel.getScenicId());
                hp.setReleaseThurl(scenicModel.getImg_url());

                hp.setSeq(hot.getSeq());
                hp.setProCategory(hot.getProductType());
                hp.setMinAdvicePrice(scenicModel.getMinAdvicePrice());
                hp.setMinMarketPrice(scenicModel.getMinMarketPrice());
                //要一句话介绍！
                if (scenicModel.getSpuLists() != null && scenicModel.getSpuLists().size() > 0) {
                    SpuProductModel spuModel = scenicModel.getSpuLists().get(0);
                    hp.setReeaseInfo(spuModel.getOneWordFeature());
                }
                hps.add(hp);
            }
        } catch (Exception e) {
            logger.error("XXXXXXXXXXXXXXXXXXXXXXXXXXX", e);
        }
    }

    /**
     * 组装热门推荐的通用产品
     * 
     * @param hps
     * @param hot
     * @param resellerId
     * @param salesPort
     */
    private void wrapHotProductByInfo(List<HotProductModel> hps, HotProductRecommend hot, Long resellerId, String salesPort) {
        SpuProductResultVO vo = skuProductApi.findSpuProductById(resellerId, hot.getProductId(), ProductIdType.SPU_ID, salesPort);
        if (vo == null || vo.getSpuProduct() == null || !CheckUtils.isNotEmpty(vo.getSkuProductList())) {
            return;
        }
        SpuProduct spu = vo.getSpuProduct();
        HotProductModel hp = new HotProductModel();
        hp.setPopularName(spu.getName());
        hp.setProductId(spu.getId());
        hp.setReleaseThurl(spu.getReleaseThurl());
        //要一句话介绍！
        hp.setReeaseInfo(spu.getOneWordFeature());
        hp.setSeq(hot.getSeq());
        hp.setProCategory(spu.getProductType());
        Double minAdvicePrice = 0D;
        Double minMarketPrice = 0D;
        if (CheckUtils.isNotEmpty(vo.getSkuProductList())) {
            Iterator<SkuProduct> iter = vo.getSkuProductList().iterator();
            while (iter.hasNext()) {
                SkuProduct skup = iter.next();
                List<PCStrategyResult> stras = vo.getStrategyList().get(skup.getId());
                if (stras == null || stras.size() == 0) {
                    logger.error(Exceptions.DATAERROR.getCode(), "没有政策数据");
                    iter.remove();
                    continue;
                }
                PCStrategyResult st = stras.get(0);
                Strategy strategy = st.getStrategyList().get(0);
                Double currentAdvicePrice = strategy.getAdvicePrice() == null ? 0 : strategy.getAdvicePrice();
                Double currentMarketPrice = strategy.getMarketPrice() == null ? 0 : strategy.getMarketPrice();
                if (minAdvicePrice == 0 || minAdvicePrice > currentAdvicePrice) {
                    minAdvicePrice = currentAdvicePrice;
                }
                if (minMarketPrice == 0 || minMarketPrice > currentMarketPrice) {
                    minMarketPrice = currentMarketPrice;
                }
            }
        }
        hp.setMinAdvicePrice(StringUtils.doubleToString(minAdvicePrice));
        hp.setMinMarketPrice(StringUtils.doubleToString(minMarketPrice));
        hps.add(hp);
    }

    /**
     * 组装推荐查询的公共参数
     * 
     * @param requestObject
     * @return 
     * */
    private Map<String, Object> commonParamWrapper(JSONObject requestObject) {
        Map<String, Object> bParam = new HashMap<>();
        if (CheckUtils.isNotEmpty(requestObject.getInteger("appType"))) {
            bParam.put("appType", requestObject.getInteger("appType"));
        }
        if (CheckUtils.isNotEmpty(requestObject.getString("province"))) {
            bParam.put("province", requestObject.getString("province"));
        }

        int currentPage = requestObject.getIntValue("currentPage");
        int pageSize = requestObject.getIntValue("pageSize");
        String province = requestObject.getString("province");
        int defaultPageSize = Integer.parseInt(propertyLoader.getProperty("system", "default.recommend.page.size", "10"));
        if (currentPage > 0)
            currentPage--;//页数从1开始
        if (pageSize <= 0)
            pageSize = defaultPageSize;
        bParam.put("limitSize", currentPage * defaultPageSize); // 由于分页有问题，所以先临时使用 defaultPageSize 值
        bParam.put("pageSize", defaultPageSize); // 由于分页有问题，所以先临时使用 defaultPageSize 值
        bParam.put("province", province);
        return bParam;
    }

}
