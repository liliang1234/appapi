package com.pzj.modules.appapi.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pzj.base.common.CheckUtils;
import com.pzj.channel.Strategy;
import com.pzj.channel.vo.resultParam.PCStrategyResult;
import com.pzj.modules.appapi.api.utils.StrategyFilter;
import com.pzj.product.service.ISkuProductService;
import com.pzj.product.vo.voParam.queryParam.SkuProductQuery;
import com.pzj.product.vo.voParam.queryParam.SpuProductQueryParamVO;
import com.pzj.product.vo.voParam.resultParam.SpuProductResultVO;

@Service
public class ProductStrategyService {

    private static final Logger logger = LoggerFactory.getLogger(ProductStrategyService.class);

    @Autowired
    private ISkuProductService  skuProductService;

    public List<SpuProductResultVO> getProductStrategy(SpuProductQueryParamVO param, String salesType) {

        try {
            List<SpuProductResultVO> spuProductList = skuProductService.findSkuProductForApp(param);
            if (CheckUtils.isNotNull(spuProductList)) {
                for (SpuProductResultVO vo : spuProductList) {
                    StrategyFilter.filterChannel(vo, salesType);
                }
            }
            return spuProductList;
        } catch (Exception e) {
            logger.error("", e);
            return null;
        }
    }

    public List<Strategy> getStrategyList(Long resellerId, String salesPort, Long productId) {
        try {
            SpuProductQueryParamVO param = new SpuProductQueryParamVO();
            param.setDistributorId(resellerId);
            param.setSalesType(salesPort);
            SkuProductQuery skuParam = new SkuProductQuery();
            skuParam.setIds(new ArrayList<Long>());
            skuParam.getIds().add(productId);
            param.setSkuProductParam(skuParam);
            List<SpuProductResultVO> spuList = skuProductService.findSkuProductForApp(param);

            SpuProductResultVO vo = spuList.get(0);
            StrategyFilter.filterChannel(vo, salesPort);
            Map<Long, List<PCStrategyResult>> strategyList = vo.getStrategyList();
            if (strategyList == null || strategyList.isEmpty()) {
                return null;
            }
            for (Entry<Long, List<PCStrategyResult>> entry : strategyList.entrySet()) {

                if (entry.getValue() != null && !entry.getValue().isEmpty()) {
                    PCStrategyResult pcStrategyResult = entry.getValue().get(0);
                    if (CheckUtils.isNotNull(pcStrategyResult.getStrategyList())) {
                        return pcStrategyResult.getStrategyList();
                    }
                }
            }
        } catch (Exception e) {
            logger.error("", e);

        }
        return null;
    }

}
