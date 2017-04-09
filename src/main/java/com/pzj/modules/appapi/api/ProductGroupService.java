package com.pzj.modules.appapi.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pzj.base.common.global.GlobalDict;
import com.pzj.base.common.global.GlobalDict.ProductCategory;
import com.pzj.base.common.global.UserGlobalDict;
import com.pzj.channel.entity.ChannelVo;
import com.pzj.common.util.CheckUtils;
import com.pzj.customer.entity.Customer;
import com.pzj.modules.appapi.entity.CodeHandle;
import com.pzj.modules.appapi.entity.JsonEntity;
import com.pzj.platform.appapi.util.PropertyLoader;
import com.pzj.product.api.enterprise.vo.ProductDictVO;
import com.pzj.product.api.enterprise.vo.ScenicVO;
import com.pzj.product.api.product.service.ProductComposeService;
import com.pzj.product.api.product.vo.ProductComposeVO;
import com.pzj.product.api.product.vo.ProductPriceVO;
import com.pzj.regulation.entity.CusProductPriceVO;
import com.pzj.regulation.entity.RebateStrategyVo;
import com.pzj.regulation.entity.StrategySettlementRule;
import com.pzj.regulation.entity.StrategyVo;
import com.pzj.regulation.service.StrategySettlementRuleService;
import com.pzj.regulation.service.impl.StrategySettlementRuleServiceImpl;
import com.pzj.sale.api.order.service.OrderUtils;
import com.pzj.util.KeyValueVo;

/**
 * 产品service
 * @author Administrator
 *
 */
@Component
public class ProductGroupService {
    private Logger                                                    logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private com.pzj.product.api.enterprise.service.ScenicService      scenicService;
    @Autowired
    private com.pzj.product.api.enterprise.service.ProductDictService productDictService;
    @Autowired
    private OrderUtils                                                orderUtils;
    @Autowired
    private ProductComposeService                                     productComposeService;
    @Resource(name = "propertyLoader")
    private PropertyLoader                                            propertyLoader;
    @Autowired
    private com.pzj.platform.appapi.service.ProductService            productService;

    /**
     * 查询景区产品组的产品类型
     * 
     * @param data
     *            json参数包
     * @param customer
     *            当前登录用户
     * @param json
     *            返回结果包装
     * @key sceneId
     * @key type 0:是团 1：是散
     * @return JsonEntity 返回类型
     * @key marketPrice 挂牌价
     * @key retailPrice 渠道价
     * @key productName 产品名称
     * @key productId 产品id
     * @key area 演出区域
     * @key screenings 演出场次
     * @key standardName 票型 成人、儿童等
     */
    public JsonEntity getProductInfo(JSONObject data, Customer customer, JsonEntity json) {
        String productInfoId = data.containsKey("productInfoId") ? data.getString("productInfoId") : "";
        String sceneId = data.containsKey("sceneId") ? data.getString("sceneId") : "";
        String supplierId = data.containsKey("supplierId") ? data.getString("supplierId") : "";
        String type = data.containsKey("productType") ? data.getString("productType") : "1";// 0:是团
        // 1：是散
        String salesType = data.containsKey("salesType") ? data.getString("salesType") : "";//销售端口 导游4 商户5
        // 1：是散
        String travelAgencyId = data.containsKey("travelAgencyId") ? data.getString("travelAgencyId") : "1";// 旅行社id
        if (CheckUtils.isNull(supplierId)) {
            logger.info("supplierId&&productId 为空");
            json.setCode(CodeHandle.CODE_90001.getCode() + "");
            json.setMessage(CodeHandle.CODE_90001.getMessage());
            return json;
        }
        if (CheckUtils.isNull(sceneId)) {
            logger.info("sceneId 为空");
            json.setCode(CodeHandle.CODE_90001.getCode() + "");
            json.setMessage(CodeHandle.CODE_90001.getMessage());
            return json;
        }
        Integer salePort = null;
        String isGroup = "1";
        Long userId = customer.getId();
        boolean guide = true;
        if (customer.getResellerType().equals(UserGlobalDict.guideUserType())) {
            if (type.equals("0")) {
                if (CheckUtils.isNull(travelAgencyId)) {
                    logger.info("travelAgencyId 为空");
                    json.setCode(CodeHandle.CODE_90001.getCode() + "");
                    json.setMessage(CodeHandle.CODE_90001.getMessage());
                    return json;
                }
                userId = null;
                isGroup = OrderUtils.GROUP_ORDER;
            } else {
                isGroup = OrderUtils.BULK_ORDER;
            }
            salePort = OrderUtils.OrderSalePortType.TOUR_GUIDE_APP.getKey();
        } else {
            guide = false;
            isGroup = OrderUtils.BULK_ORDER;
            salePort = OrderUtils.OrderSalePortType.MERCHANT_APP.getKey();
        }
        Map<String, Object> jsonObject = Maps.newHashMap();
        List<Map<String, Object>> jsonArray = Lists.newArrayList();
        try {
            ScenicVO scenicVO = scenicService.getById(Long.parseLong(sceneId));
            ProductDictVO pd = productDictService.getByVal(scenicVO.getGrade() == null ? "" : scenicVO.getGrade().toString(), GlobalDict.PRODUCT.scenicLevel());
            if (pd != null) {
                jsonObject.put("sceneLevel", pd.getLabel());
            }
            jsonObject.put("sceneId", String.valueOf(scenicVO.getId()));
            jsonObject.put("sceneName", scenicVO.getName());
            jsonObject.put("city", scenicVO.getCity());
            jsonObject.put("county", scenicVO.getCounty());
            jsonObject.put("province", scenicVO.getProvince());
            jsonObject.put("img_url", scenicVO.getImgUrl());
            jsonObject.put("stock ", 100000);
            List<StrategyVo> list = null;
            boolean isGudit = false;
            if (userId == null) {
                isGudit = true;
                list = orderUtils.findStrategyByScenic(customer.getId(), Long.parseLong(sceneId), isGroup, salePort);
                System.out.println("get list buy guide app");
            } else {
                list = orderUtils.findStrategyByScenic(userId, Long.parseLong(sceneId), customer.getSupplierId(), isGroup, salePort);
                System.out.println("get list buy merchant app");
                System.out.println("params: " + userId + ", " + sceneId + ", " + customer.getSupplierId() + ", " + isGroup + ", " + salePort);
            }

            System.out.println("List size : " + list.size());
            if (isGudit) {
                if (list != null && list.size() > 0) {
                    List<Long> haveProduct = new ArrayList<Long>();
                    for (StrategyVo strategyVo : list) {
                        List<Long> productIds = strategyVo.getProductList();
                        ProductPriceVO prov = null;
                        String strategrId = String.valueOf(strategyVo.getId());
                        for (int i = 0; i < productIds.size(); i++) {
                            if (haveProduct.contains(productIds.get(i))) {
                                continue;
                            }
                            haveProduct.add(productIds.get(i));
                            Map<String, Object> obj = Maps.newHashMap();
                            if (CheckUtils.isNotNull(productIds.get(i))) {
                                ProductComposeVO productComposeVo = new ProductComposeVO();
                                productComposeVo.setId(productIds.get(i));
                                prov = productComposeService.queryComposeById(productComposeVo);
                                if (String.valueOf(prov.getProductId()).equals(productInfoId)) {
                                    if (prov.getProCategory() != null && prov.getProCategory().equals("11")) {
                                        for (int j = 0; j < prov.getProductList().size(); j++) {
                                            if (prov.getProductList().get(j).getProCategory() != null
                                                && prov.getProductList().get(j).getProCategory().equals("13")) {
                                                prov.setTheaterArea(prov.getProductList().get(j).getTheaterArea());//如果是演艺联票，区域就在子票里边取
                                                prov.setTheatercnum(prov.getProductList().get(j).getTheatercnum());//如果是演艺联票，场次就在子票里边取
                                                break;
                                            }
                                        }
                                    }
                                    Integer expire = strategyVo.getExpire();
                                    Integer expireMode = strategyVo.getExpireMode();// 预售计时方式（1小时，2日)
                                    obj.put("expire", expire + "");
                                    obj.put("expireMode", expireMode + "");
                                    obj.put("ParentTickets", "");
                                    obj.put("chileTicket", "");
                                    obj.put("scope", "");// 适用日期范围
                                    obj.put("gold", "");// 金币
                                    obj.put("marketPrice", String.valueOf(prov.getMarketPrice()) + "");// 门市价
                                    obj.put("retailPrice", String.valueOf(prov.getMarketPrice()) + "");// 渠道价
                                    String typ = prov.getTicketType();
                                    if (typ != null) {
                                        String[] split = typ.split(":");
                                        obj.put("productName", split[1]); //产品的名字
                                    }
                                    obj.put("productId", String.valueOf(prov.getId()) + "");
                                    obj.put("productInfoId", String.valueOf(prov.getProductId()) + "");
                                    obj.put("supplierId", String.valueOf(prov.getSupplierId()) + "");
                                    obj.put("channelId", "");
                                    obj.put("expire", "");
                                    obj.put("expireMode", "");
                                    obj.put("area", prov.getTheaterArea() + "");// 剧场区域
                                    obj.put("screenings", prov.getTheatercnum() + "");// 剧场场次
                                    obj.put("startTime", prov.getStartTime() + ""); //开始时间
                                    obj.put("endTime", prov.getEndTime() + ""); //结束时间
                                    obj.put("mfPrice", String.valueOf(prov.getMarketPrice()) + ""); //魔方价
                                    obj.put("hourNode", prov.getHourNode() + ""); //当日最后购买时限（小时）
                                    obj.put("minutesNode", prov.getMinutesNode() + ""); //当日最后购买时限（分钟）
                                    obj.put("payAmount", strategyVo.getPrice());
                                    obj.put("strategrId", strategrId);
                                    if (prov.getTicketType() != null) {
                                        String str = prov.getTicketType();
                                        String updateStr = str.substring(0, str.indexOf(":", str.indexOf(":")));
                                        ProductDictVO dictvo = new ProductDictVO();
                                        dictvo.setValue(updateStr);
                                        dictvo.setSupplierId(supplierId);
                                        List<ProductDictVO> dict = productDictService.getListByParams(dictvo);
                                        if (dict != null) {
                                            obj.put("remark", dict.get(0).getRemarks());
                                            obj.put("dictId", dict.get(0).getId());
                                        } else {
                                            obj.put("remark", prov.getName());
                                        }
                                    }
                                    if (prov.getProduct() != null) {
                                        obj.put("playtime",
                                            prov.getProduct().getPlaytimeValue() == null ? "" : String.valueOf(prov.getProduct().getPlaytimeValue()));
                                        obj.put("playtimeUnit",
                                            prov.getProduct().getPlaytimeUnit() == null ? "" : String.valueOf(prov.getProduct().getPlaytimeUnit()));
                                        obj.put("playtimeMode",
                                            prov.getProduct().getPlaytimeMode() == null ? "" : String.valueOf(prov.getProduct().getPlaytimeMode()));
                                    }
                                    String isCandidate = "0";//0   没有 后选座
                                    obj.put("isCandidate", isCandidate);
                                    boolean seatSelected = productService.getSuppilerIdNeedSeats(Long.parseLong(prov.getSupplierId()));
                                    obj.put("flag", seatSelected);
                                    CusProductPriceVO cppvo = strategyVo.getProductVoList().get(0);
                                    if (ProductCategory.compose().equals(cppvo.getProCategory())) {
                                        List<ProductPriceVO> prod = prov.getProductList();
                                        boolean flag = false;
                                        for (ProductPriceVO childPro : prod) {
                                            if (ProductCategory.performingPack().equals(childPro.getProCategory())) {
                                                flag = true;
                                                break;
                                            }
                                        }
                                        obj.put("proCategory", "3");
                                        obj.put("subProCategory", flag == true ? "2" : "1");
                                        if (salesType.equals("4")) {//目前是导游登录账号有后选座
                                            if (flag == true) {
                                                isCandidate = "1";//1   有 后选座
                                            }
                                            obj.put("isCandidate", isCandidate);
                                        }
                                    } else {
                                        String proCate = "";
                                        if (null != prov.getProCategory() && ProductCategory.scenic().equals(prov.getProCategory())) {
                                            proCate = ProductCategory.scenic();
                                            proCate = "2";
                                        } else {
                                            proCate = ProductCategory.normal();
                                        }
                                        obj.put("proCategory", proCate);
                                    }
                                    jsonArray.add(obj);
                                }
                            }
                        }
                    }
                }
            } else {
                if (list != null && list.size() > 0) {
                    for (StrategyVo strategyVo : list) {
                        List<Long> productIds = strategyVo.getProductList();
                        ProductPriceVO prov = null;
                        for (int i = 0; i < productIds.size(); i++) {
                            String strategrId = String.valueOf(strategyVo.getId());
                            Map<String, Object> obj = Maps.newHashMap();
                            if (CheckUtils.isNotNull(productIds.get(i))) {
                                ProductComposeVO productComposeVo = new ProductComposeVO();
                                productComposeVo.setId(productIds.get(i));
                                prov = productComposeService.queryComposeById(productComposeVo);
                                if (String.valueOf(prov.getProductId()).equals(productInfoId)) {
                                    if (prov.getProCategory() != null && prov.getProCategory().equals("11")) {
                                        for (int j = 0; j < prov.getProductList().size(); j++) {
                                            if (prov.getProductList().get(j).getProCategory() != null
                                                && prov.getProductList().get(j).getProCategory().equals("13")) {
                                                prov.setTheaterArea(prov.getProductList().get(j).getTheaterArea());//如果是演艺联票，区域就在子票里边取
                                                prov.setTheatercnum(prov.getProductList().get(j).getTheatercnum());//如果是演艺联票，场次就在子票里边取
                                                break;
                                            }
                                        }
                                    }
                                    List<ChannelVo> channelList = strategyVo.getChannelList();
                                    Long channelId = null;
                                    if (channelList != null) {
                                        ChannelVo channelVo = strategyVo.getChannelList().get(0);
                                        channelId = channelVo.getId();
                                    }

                                    Integer expire = strategyVo.getExpire();
                                    Integer expireMode = strategyVo.getExpireMode();// 预售计时方式（1小时，2日)
                                    Double price = strategyVo.getPrice();
                                    List<RebateStrategyVo> strategyList = strategyVo.getRebateStrategyList();

                                    Double rebateAmount = 0d;
                                    if (strategyList != null) {
                                        for (RebateStrategyVo rebateStrategyVo : strategyList) {
                                            Integer rebateType = rebateStrategyVo.getRebateType();
                                            if (rebateType.equals(0)) // 只统计现金返利
                                            {

                                                String rebateObject = rebateStrategyVo.getRebateObject();
                                                if (guide) {
                                                    if (CheckUtils.isNotNull(rebateObject) && rebateObject.equals("G")) {
                                                        rebateAmount += rebateStrategyVo.getRebateAmount();
                                                        continue;
                                                    }
                                                } else {
                                                    if (CheckUtils.isNotNull(rebateObject) && rebateObject.equals("P")) {
                                                        rebateAmount += rebateStrategyVo.getRebateAmount();
                                                        continue;
                                                    }
                                                }

                                            }
                                        }
                                    }
                                    List<KeyValueVo> scope = strategyVo.getScope();
                                    List<String> scopeValueList = new ArrayList<String>();
                                    for (KeyValueVo keyValueVo : scope) {
                                        String value = keyValueVo.getValue();
                                        scopeValueList.add(value);
                                    }
                                    //查询联票里边的每张子票的 未满检金额
                                    Map<String, Object> object1 = Maps.newHashMap();
                                    List<Long> queryStartegyIdList = new ArrayList<Long>();
                                    List<Long> queryParentProductIdList = new ArrayList<Long>(), queryProductIdList = new ArrayList<Long>();
                                    StrategySettlementRule vo = new StrategySettlementRule();
                                    if (null != prov && null != prov.getProductList() && prov.getProductList().size() > 0) {
                                        queryParentProductIdList.add(prov.getProductId());
                                        queryStartegyIdList.add(Long.parseLong(strategrId));
                                        vo.setQueryParentProductIdList(queryProductIdList);
                                        vo.setQueryStartegyIdList(queryStartegyIdList);
                                        StrategySettlementRuleService strategySettlementRuleService = new StrategySettlementRuleServiceImpl();
                                        List<StrategySettlementRule> strategySettRuleList = strategySettlementRuleService.queryByParamMap(vo);
                                        if (null != strategySettRuleList && strategySettRuleList.size() > 0) {
                                            for (StrategySettlementRule ssr : strategySettRuleList) {
                                                if (ssr.getProductId().longValue() != prov.getProductId()) {
                                                    long sonProductId = ssr.getProductId();
                                                    ProductComposeVO pcvo = new ProductComposeVO();
                                                    pcvo.setId(sonProductId);
                                                    ProductPriceVO ppvo = productComposeService.queryComposeById(pcvo);
                                                    object1.put(ppvo.getName(), ppvo.getReduceSettlementMoney());
                                                }

                                            }
                                        }
                                    }
                                    obj.put("ParentTickets",
                                        strategyVo.getReduceSettlementMoney() == null ? "" : String.valueOf(strategyVo.getReduceSettlementMoney()));
                                    obj.put("chileTicket", object1);
                                    obj.put("scope", scopeValueList);// 适用日期范围
                                    obj.put("gold", rebateAmount + "");// 金币
                                    obj.put("marketPrice", String.valueOf(prov.getMarketPrice()) + "");// 门市价
                                    obj.put("retailPrice", String.valueOf(price) + "");// 渠道价
                                    String typ = prov.getTicketType();
                                    if (typ != null) {
                                        String[] split = typ.split(":");
                                        obj.put("productName", split[1]); //产品的名字
                                    }
                                    obj.put("productId", String.valueOf(prov.getId()) + "");
                                    obj.put("productInfoId", String.valueOf(prov.getProductId()) + "");
                                    obj.put("supplierId", String.valueOf(prov.getSupplierId()) + "");
                                    obj.put("channelId", channelId + "");
                                    obj.put("expire", expire + "");
                                    obj.put("expireMode", expireMode + "");
                                    obj.put("area", prov.getTheaterArea() + "");// 剧场区域
                                    obj.put("screenings", prov.getTheatercnum() + "");// 剧场场次
                                    obj.put("startTime", prov.getStartTime() + ""); //开始时间
                                    obj.put("endTime", prov.getEndTime() + ""); //结束时间
                                    obj.put("mfPrice", prov.getMfPrice() + ""); //魔方价
                                    obj.put("hourNode", prov.getHourNode() + ""); //当日最后购买时限（小时）
                                    obj.put("minutesNode", prov.getMinutesNode() + ""); //当日最后购买时限（分钟）
                                    obj.put("payAmount", strategyVo.getPrice());
                                    obj.put("strategrId", strategrId);
                                    if (prov.getTicketType() != null) {
                                        String str = prov.getTicketType();
                                        String updateStr = str.substring(0, str.indexOf(":", str.indexOf(":")));
                                        ProductDictVO dictvo = new ProductDictVO();
                                        dictvo.setValue(updateStr);
                                        dictvo.setSupplierId(supplierId);
                                        List<ProductDictVO> dict = productDictService.getListByParams(dictvo);
                                        if (dict != null) {
                                            obj.put("remark", dict.get(0).getRemarks());
                                            obj.put("dictId", dict.get(0).getId());
                                        } else {
                                            obj.put("remark", prov.getName());
                                        }
                                    }
                                    if (prov.getProduct() != null) {
                                        obj.put("playtime",
                                            prov.getProduct().getPlaytimeValue() == null ? "" : String.valueOf(prov.getProduct().getPlaytimeValue()));
                                        obj.put("playtimeUnit",
                                            prov.getProduct().getPlaytimeUnit() == null ? "" : String.valueOf(prov.getProduct().getPlaytimeUnit()));
                                        obj.put("playtimeMode",
                                            prov.getProduct().getPlaytimeMode() == null ? "" : String.valueOf(prov.getProduct().getPlaytimeMode()));
                                    }
                                    String isCandidate = "0";//0   没有 后选座
                                    obj.put("isCandidate", isCandidate);
                                    boolean seatSelected = productService.getSuppilerIdNeedSeats(Long.parseLong(prov.getSupplierId()));
                                    obj.put("flag", seatSelected);
                                    CusProductPriceVO cppvo = strategyVo.getProductVoList().get(0);
                                    if (ProductCategory.compose().equals(cppvo.getProCategory())) {
                                        List<ProductPriceVO> prod = prov.getProductList();
                                        boolean flag = false;
                                        for (ProductPriceVO childPro : prod) {
                                            if (ProductCategory.performingPack().equals(childPro.getProCategory())) {
                                                flag = true;
                                                break;
                                            }
                                        }
                                        obj.put("proCategory", "3");
                                        obj.put("subProCategory", flag == true ? "2" : "1");
                                        if (salesType.equals("4")) {//目前是导游登录账号有后选座
                                            if (flag == true) {
                                                isCandidate = "1";//1   有 后选座
                                            }
                                            obj.put("isCandidate", isCandidate);
                                        }
                                    } else {
                                        String proCate = "";
                                        if (null != prov.getProCategory() && ProductCategory.scenic().equals(prov.getProCategory())) {
                                            proCate = ProductCategory.scenic();
                                            proCate = "2";
                                        } else {
                                            proCate = ProductCategory.normal();
                                        }
                                        obj.put("proCategory", proCate);
                                    }
                                    jsonArray.add(obj);
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            json.setCode(CodeHandle.FAILURE.getCode() + "");
            json.setMessage(CodeHandle.FAILURE.getMessage());
            json.setResponseBody(jsonObject);
            return json;
        }
        jsonObject.put("list", jsonArray);
        json.setCode(CodeHandle.SUCCESS.getCode() + "");
        json.setMessage(CodeHandle.SUCCESS.getMessage());
        json.setResponseBody(jsonObject);
        return json;
    }

}
