package com.pzj.modules.appapi.api;

import java.text.ParseException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pzj.appapi.constants.ApiConstants;
import com.pzj.base.common.global.GlobalDict;
import com.pzj.base.common.utils.PageList;
import com.pzj.base.common.utils.PageModel;
import com.pzj.common.util.DateUtils;
import com.pzj.customer.entity.Customer;
import com.pzj.modules.appapi.entity.CodeHandle;
import com.pzj.modules.appapi.entity.JsonEntity;
import com.pzj.platform.appapi.util.PropertyLoader;
import com.pzj.product.api.enterprise.service.ProductDictService;
import com.pzj.product.api.enterprise.vo.ProductDictVO;
import com.pzj.product.api.product.service.CommonProductService;
import com.pzj.product.api.product.vo.CustomerCommonProuctRequestVO;
import com.pzj.product.api.product.vo.CustomerCommonProuctVO;
import com.pzj.product.api.product.vo.ProductParameterVO;

@Component
public class ProductService {
    private static final Logger logger               = LoggerFactory.getLogger(ProductService.class);
    private static String       TICKET_CATEGRORY_KEY = "ticketCategory";
    private static final String APPAPI_VERSION       = "2.0.0";

    @Resource(name = "propertyLoader")
    private PropertyLoader       propertyLoader;
    @Autowired
    private CommonProductService commonProductService;
    @Autowired
    private ProductDictService   productDictService;

    /**
     * 2016年6月8日
     * 查询spu产品列表--对应app首页产品展示接口
     * @param data
     * @param customer
     * @param json
     * @return
     * @throws ParseException 
     */
    public JsonEntity getSPUList(JSONObject data, Customer customer, JsonEntity json) throws ParseException {
        json.setCode(CodeHandle.SUCCESS.getCode());
        json.setMessage(CodeHandle.SUCCESS.getMessage());

        String version = data.getString("version");
        String categoryId = data.containsKey("categoryId") ? data.getString("categoryId") : "";
        String province = data.containsKey("province") ? data.getString("province") : "";// 景区省份
        int currentPage = data.containsKey("currentPage") ? data.getIntValue("currentPage") : 1;
        int pageSize = data.containsKey("pageSize") ? data.getIntValue("pageSize") : 6;
        PageModel pg = new PageModel();
        pg.setPageNo(currentPage);
        pg.setPageSize(pageSize);
        CustomerCommonProuctRequestVO request = new CustomerCommonProuctRequestVO();
        Date date = new Date();
        request.setUserId(customer.getId());
        request.setQueryStartDate(date);
        request.setProCategory(categoryId);
        request.setProvince(province);
        PageList<CustomerCommonProuctVO> list;
        List<CustomerCommonProuctVO> resultList = null;
        try {
            list = commonProductService.getSKUProductListForUser(request, pg);
            logger.info("spulist request currentPage=" + currentPage + ",pageSize=" + pageSize + ";result:" + JSON.toJSONString(list));
            if (list != null) {
                resultList = list.getResultList();
                List<Map<String, Object>> jsonArray = Lists.newArrayList();
                for (CustomerCommonProuctVO customerCommonProuctVO : resultList) {
                    Map<String, Object> jsonObject = Maps.newHashMap();
                    jsonObject.put("name", customerCommonProuctVO.getName());
                    jsonObject.put("remarks", customerCommonProuctVO.getRemarks());
                    jsonObject.put("categoryId", customerCommonProuctVO.getCategoryId());
                    jsonObject.put("startDate", DateUtils.formatDateTime(customerCommonProuctVO.getStartDate()));
                    jsonObject.put("endDate", DateUtils.formatDateTime(customerCommonProuctVO.getEndDate()));
                    jsonObject.put("photoinfoId", customerCommonProuctVO.getPhotoinfoId());
                    jsonObject.put("advicePrice", customerCommonProuctVO.getAdvicePrice());
                    jsonObject.put("marketPrice", customerCommonProuctVO.getAdviceMarketPrice());
                    jsonObject.put("channelIds", String.valueOf(customerCommonProuctVO.getChannelIds().get(0)));
                    jsonObject.put("skuId", customerCommonProuctVO.getSkuId());
                    jsonArray.add(jsonObject);
                }
                if (APPAPI_VERSION.equals(version)) {
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("spuList", jsonArray);
                    map.put("currentPage", currentPage);
                    map.put("pageSize", pageSize);
                    map.put("pageCount", list.getPageBean().getPages().length);
                    map.put("itemCount", list.getPageBean().getTotalCount());
                    map.put("hasNextPage", list.getPageBean().getHasNextPage());
                    json.setResponseBody(map);
                } else {//兼容2.4版本
                    json.setResponseBody(jsonArray);
                }
            } else {
                if (APPAPI_VERSION.equals(version)) {
                    Map<String, List<Object>> map = new HashMap<String, List<Object>>();
                    map.put("spuList", Collections.emptyList());
                    json.setResponseBody(map);
                } else {//兼容2.4版本
                    json.setResponseBody(Collections.emptyList());
                }
                return json;
            }
        } catch (Exception e) {
            e.printStackTrace();
            json.setCode(CodeHandle.FAILURE.getCode() + "");
            json.setMessage(CodeHandle.FAILURE.getMessage());
        }
        return json;
    }

    /**
     * 2016年6月8日
     * 查询spu产品列表--对应app详情页面
     * @param data
     * @param customer
     * @param json
     * @return
     */
    public JsonEntity getSPUDetail(JSONObject data, Customer customer, JsonEntity json) {
        String skuId = data.containsKey("skuId") ? data.getString("skuId") : "";
        CustomerCommonProuctVO detalist;
        List<Map<String, Object>> jsonArray = Lists.newArrayList();
        Map<String, Object> jsonObject = Maps.newHashMap();
        try {
            detalist = commonProductService.getSKUProductDetailBySkuIdAndUserId(skuId, customer.getId());
            if (detalist != null) {
                List<ProductParameterVO> resultList = detalist.getProductParameterList();
                jsonObject.put("name", detalist.getName());
                jsonObject.put("remarks", detalist.getRemarks());
                jsonObject.put("categoryId", detalist.getCategoryId());
                jsonObject.put("details", detalist.getDetails());
                jsonObject.put("startDate", detalist.getStartDate() == null ? "" : DateUtils.formatDateTime(detalist.getStartDate()));
                jsonObject.put("moreInfoUrl", detalist.getMoreInfoUrl());
                jsonObject.put("endDate", detalist.getEndDate() == null ? "" : DateUtils.formatDateTime(detalist.getEndDate()));
                jsonObject.put("advicePrice", detalist.getAdvicePrice());
                jsonObject.put("marketPrice", detalist.getAdviceMarketPrice());
                jsonObject.put("photos", detalist.getPhotos());
                jsonObject.put("skuId", detalist.getSkuId());
                jsonObject.put("inventory", detalist.getInventory());
                jsonObject.put("latestPresetDays", detalist.getLatestPresetDays());
                jsonObject.put("latestPresetTime", detalist.getLatestPresetTime());
                jsonObject.put("supplierId", detalist.getSupplierId());
                jsonObject.put("channelIds", detalist.getChannelIds().get(0));
                for (ProductParameterVO list : resultList) {
                    Map<String, Object> obj = Maps.newHashMap();
                    Boolean flag = detalist.getProductPriceIsRequiredMap().get(list.getProductId());
                    obj.put("productId", list.getProductId());
                    obj.put("parameterName", list.getParameterName());
                    obj.put("inventory", list.getInventory());
                    obj.put("advicePrice", list.getAdvicePrice());
                    obj.put("marketPrice", list.getMarketPrice());
                    obj.put("flag", flag);
                    jsonArray.add(obj);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            json.setCode(CodeHandle.FAILURE.getCode() + "");
            json.setMessage(CodeHandle.FAILURE.getMessage());
        }
        jsonObject.put("list", jsonArray);
        json.setCode(CodeHandle.SUCCESS.getCode());
        json.setMessage(CodeHandle.SUCCESS.getMessage());
        json.setResponseBody(jsonObject);
        return json;
    }

    /**
     * 2016年6月8日
     * 查询spu产品列表--查询类别
     * @param data
     * @param customer
     * @param json
     * @return
     */
    public JsonEntity getScenicType(JSONObject data, Customer customer, JsonEntity json) {
        String type = GlobalDict.PRODUCT.proSKUCategory();
        List<LinkedHashMap<String, Object>> jsonArray = Lists.newArrayList();
        try {
            List<ProductDictVO> list = productDictService.getListByType(type);
            for (ProductDictVO productDictVO : list) {
                LinkedHashMap<String, Object> obj = new LinkedHashMap<String, Object>();
                obj.put("id", productDictVO.getId());
                obj.put("value", productDictVO.getValue());
                obj.put("name", productDictVO.getLabel());
                jsonArray.add(obj);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        json.setCode(CodeHandle.SUCCESS.getCode());
        json.setMessage(CodeHandle.SUCCESS.getMessage());
        json.setResponseBody(jsonArray);

        return json;
    }

    /**
     * 判断是否是票类型
     * 
     * */
    public boolean isTicketProduct(String proCategory) {
        String ticketCategory = propertyLoader.getProperty(ApiConstants.SYSTEM_FILENAME, TICKET_CATEGRORY_KEY);
        if (ticketCategory != null && ticketCategory.indexOf(proCategory + ",") >= 0)
            return true;
        return false;
    }
}
