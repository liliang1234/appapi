package com.pzj.modules.appapi.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pzj.base.entity.SysChannelStrategy;
import com.pzj.base.entity.product.ProductInfo;
import com.pzj.base.entity.product.ProductRelease;
import com.pzj.base.service.product.IProductInfoService;
import com.pzj.base.service.product.IProductReleaseService;
import com.pzj.base.service.sys.IChannelStrategyService;
import com.pzj.product.api.product.vo.ProductPriceVO;

@Component("appApiProductInfoService")
public class ProductInfoService {

    @Autowired
    private com.pzj.product.api.product.service.ProductService productService;
    @Autowired
    private IProductInfoService                                productInfoService;
    @Autowired
    private IProductReleaseService                             productReleaseService;
    @Autowired
    private IChannelStrategyService                            channelStrategyService;

    public ProductPriceVO findProductPriceById(Long productId) {
        ProductPriceVO param = new ProductPriceVO();
        param.setId(productId);
        List<ProductPriceVO> productPriceVO;
        try {
            productPriceVO = productService.findListByParams(param);
            return (productPriceVO == null || productPriceVO.size() == 0) ? null : productPriceVO.get(0);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public ProductInfo findProductInfoById(Long productId) {
        try {
            ProductInfo productInfo = productInfoService.getById(productId);
            return productInfo;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public ProductRelease findProductReleaseById(Long productId) {
        try {
            ProductRelease productRelease = productReleaseService.getById(productId);
            return productRelease;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public List<SysChannelStrategy> findProductStrategy(Long productId) {
        List<SysChannelStrategy> channelStrategys = channelStrategyService.queryPageByPIDAndCID(productId, null);
        return channelStrategys;
    }
}
