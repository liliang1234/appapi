/**
 * piaozhijia.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package com.pzj.platform.appapi.service;

import java.util.List;

import com.pzj.platform.appapi.entity.HotWords;
import com.pzj.platform.appapi.model.ProvinceModel;
import com.pzj.platform.appapi.model.ScenicModel;
import com.pzj.platform.appapi.model.SkuStockModel;
import com.pzj.platform.appapi.model.SpuListModel;
import com.pzj.platform.appapi.model.SpuProductHeadModel;
import com.pzj.platform.appapi.model.SpuProductModel;

/**
 * 
 * @author Mark
 * @version $Id: ProductService.java, v 0.1 2016年7月27日 上午11:53:25 pengliqing Exp $
 */
public interface ProductService {

    public ScenicModel getSpuListByScenicId(Long distributorId, Long id, String salesType, Integer pageNo, Integer pageSize) throws Exception;

    public SpuProductModel getSpuForOrder(Long distributorId, Long id, int idType, String salesType) throws Exception;

    public List<ProvinceModel> getCity(Long distributorId, String salesType);

    /**
     * 判断是否是票类型
     * 
     * */
    public boolean isTicketProduct(String proCategory);

    public boolean getSuppilerIdNeedSeats(Long supplierId);

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
    public List<SpuProductHeadModel> search(Long distributorId, String salesType, String productName, String province, String city,
                                            String county) throws Exception;

    /**
     * 查询产品组搜索的热门词列表
     * 
     * @return
     */
    public List<HotWords> getHotWords();

    /**
     * 
     * @param distributorId  登录用户id
     * @param county 县 
     * @param city 市
     * @param province 省
     * @param proCategory
     * @param salesType
     * @return
     * @throws Exception
     */
    SpuListModel getCommonSpuList(Long distributorId, String city, String county, String province, String proCategory, String salesType, Integer pageNo,
                                  Integer pageSize) throws Exception;

    /**
     * 
     * @param distributorId
     * @param spuId
     * @param queryDate
     * @param salseType
     * @return
     */
    List<SkuStockModel> getSpuStock(Long distributorId, Long spuId, String queryDate, String salseType);
}
