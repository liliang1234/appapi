/**
 * piaozhijia.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package com.pzj.platform.appapi.dubbo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pzj.base.entity.product.ProductInfo;
import com.pzj.base.entity.product.ProductRelease;
import com.pzj.base.service.product.IProductInfoService;
import com.pzj.base.service.product.IProductReleaseService;
import com.pzj.product.service.ISkuProductService;
import com.pzj.product.vo.voParam.resultParam.SkuProductResultVO;

/**
 * 
 * @author Mark
 * @version $Id: ProductApi.java, v 0.1 2016年7月22日 上午11:33:46 pengliqing Exp $
 */
@Component
public class ProductApi {

    @Autowired
    private IProductInfoService    productInfoService;
    @Autowired
    private IProductReleaseService productReleaseService;
    @Autowired
    private ISkuProductService     skuProductService;

    public ProductInfo findProductInfoById(Long productId) {
        return productInfoService.getById(productId);
    }

    public ProductRelease findProductReleaseById(Long productId) {
        return productReleaseService.getById(productId);
    }

    public SkuProductResultVO findSkuProductById(Long id) throws Exception {
        return skuProductService.findSkuProductById(id);
    }
}
