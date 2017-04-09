/**
 * piaozhijia.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package com.pzj.platform.appapi.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.pzj.base.common.utils.DateUtil;
import com.pzj.common.mapper.JsonMapper;
import com.pzj.common.test.BaseTest;
import com.pzj.core.stock.model.StockQueryRequestModel;
import com.pzj.core.stock.model.StockRuleModel;
import com.pzj.core.stock.model.StockRuleQueryRequestModel;
import com.pzj.core.stock.service.StockRuleQueryService;
import com.pzj.framework.context.Result;
import com.pzj.framework.context.ServiceContext;
import com.pzj.platform.appapi.model.SpuListModel;
import com.pzj.platform.appapi.model.SpuProductModel;

/**
 * 
 * @author Mark
 * @version $Id: ProductServiceTest.java, v 0.1 2016年7月28日 上午9:37:46 pengliqing Exp $
 */
public class ProductServiceTest extends BaseTest {

    private static final Logger   logger = LoggerFactory.getLogger(CustomerServiceTest.class);

    @Autowired
    private StockRuleQueryService stockRuleQueryService;

    @Autowired
    private ProductService        productService;

    @Test
    public void getProductById() {
        try {
            //            ScenicModel model = productService.getSpuListByScenicId(15110018965l, 2216619736563728l, "5", 1, 10); //2216619736566727l
            //            logger.debug(JsonMapper.getInstance().toJson(model));
            //            System.out.println("-------------------------------");
            SpuListModel model2 = productService.getCommonSpuList(15110018965l, null, null, "陕西省", "1000", "5", 1, 10);
            logger.debug(JsonMapper.getInstance().toJson(model2));
            System.out.println("-------------------------------");
            SpuProductModel model3 = productService.getSpuForOrder(15110018965l, 2216619736566873l, 2, "5");
            logger.debug(JSON.toJSONString(model3));
            //            List<SpuProductModel> lists = productService.getCommonSpuList(15110018965l, "", null, null, "1000", "5");
            //            logger.debug(JSON.toJSONString(lists));
            //            StockQueryRequestModel stock = new StockQueryRequestModel();
            //            StockRuleQueryRequestModel st = new StockRuleQueryRequestModel();
            //            List<Long> lists = new ArrayList<>();
            //            lists.add(2l);
            //            lists.add(1l);
            //            st.setRuleIds(lists);
            //            stock.setStockTime(DateUtil.format(new Date(), "yyyyMMdd"));
            //            stock.setRuleId(12l);
            //            //            Result<StockModel> result = stockQueryService.queryStockByDate(stock, getServiceContext());
            //            Result<ArrayList<StockRuleModel>> list22s = stockRuleQueryService.queryStockRulesByParam(st, getServiceContext());
            //            //            model = productService.getSpuForOrder(15110018965l, 2216619736567007l, "4");
            //            logger.debug(JSON.toJSONString(list22s));
            StockQueryRequestModel stock = new StockQueryRequestModel();
            StockRuleQueryRequestModel st = new StockRuleQueryRequestModel();
            List<Long> lists = new ArrayList<>();
            lists.add(2l);
            lists.add(1l);
            st.setRuleIds(lists);
            stock.setStockTime(DateUtil.format(new Date(), "yyyyMMdd"));
            stock.setRuleId(12l);
            //            Result<StockModel> result = stockQueryService.queryStockByDate(stock, getServiceContext());
            Result<ArrayList<StockRuleModel>> list22s = stockRuleQueryService.queryStockRulesByParam(st, getServiceContext());
            //            model = productService.getSpuForOrder(15110018965l, 2216619736567007l, "4");
            logger.debug(JSON.toJSONString(list22s));
        } catch (Exception e) {
            logger.error(JSON.toJSONString(e));
            e.printStackTrace();
        }
    }

    //    @Test
    //    public void getSpuForOrderTest() {
    //        try {
    //            SpuProductModel model = productService.getSpuForOrder(2216619736563725l, 2216619736563721l, "5");
    //            logger.debug(JSON.toJSONString(model));
    //        } catch (Exception e) {
    //            logger.error("", e);
    //        }
    //    }
    //
    //    @Test
    //    public void getSkuProductById() {
    //        try {
    //            //            SpuProductModel model = productService.getSpuProductById(15110018965l, 2216619736566720l, "4");
    //            SkuProductResultVO sku = iSkuProductService.findSkuProductById(2216619736567079l);
    //            logger.debug(JSON.toJSONString(sku));
    //        } catch (Exception e) {
    //            logger.error(JSON.toJSONString(e));
    //            e.printStackTrace();
    //        }
    //    }
    //
    //    @Test
    //    public void findHotWords() {
    //        List<HotWords> words = productService.getHotWords();
    //        logger.debug(JSON.toJSONString(words));
    //    }

    // 获取交易上下文
    protected ServiceContext getServiceContext() {
        ServiceContext serviceContext = new ServiceContext();
        serviceContext.setReference("AppAPI");
        return serviceContext;
    }
}
