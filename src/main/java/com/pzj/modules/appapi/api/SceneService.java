package com.pzj.modules.appapi.api;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
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
import com.pzj.base.common.global.GlobalDict;
import com.pzj.base.common.global.GlobalDict.ProductCategory;
import com.pzj.base.common.global.GlobalParam;
import com.pzj.base.common.global.UserGlobalDict;
import com.pzj.base.common.utils.PageList;
import com.pzj.base.common.utils.PageModel;
import com.pzj.channel.entity.ChannelVo;
import com.pzj.common.QueryPageList;
import com.pzj.common.QueryPageModel;
import com.pzj.common.util.CheckUtils;
import com.pzj.common.util.DateUtils;
import com.pzj.common.util.PinyinUtilsPro;
import com.pzj.customer.entity.Customer;
import com.pzj.framework.context.Result;
import com.pzj.modules.appapi.api.utils.StrategyFilter;
import com.pzj.modules.appapi.entity.CodeHandle;
import com.pzj.modules.appapi.entity.JsonEntity;
import com.pzj.modules.appapi.entity.JsonProduct;
import com.pzj.platform.appapi.dubbo.SkuProductApi;
import com.pzj.platform.appapi.service.impl.ProductIdType;
import com.pzj.platform.appapi.util.PropertyLoader;
import com.pzj.platform.appapi.util.StringUtils;
import com.pzj.product.api.enterprise.vo.ProductDictVO;
import com.pzj.product.api.enterprise.vo.ScenicVO;
import com.pzj.product.api.product.service.ProductComposeService;
import com.pzj.product.api.product.service.TicketBackRuleService;
import com.pzj.product.api.product.vo.ProductComposeVO;
import com.pzj.product.api.product.vo.ProductPriceVO;
import com.pzj.product.api.product.vo.ProductVO;
import com.pzj.product.api.product.vo.TicketBackRuleVO;
import com.pzj.product.vo.product.SkuScenic;
import com.pzj.product.vo.voParam.resultParam.SkuScenicResult;
import com.pzj.product.vo.voParam.resultParam.SpuProductResultVO;
import com.pzj.regulation.entity.CusProductPriceVO;
import com.pzj.regulation.entity.RebateStrategyVo;
import com.pzj.regulation.entity.StrategySettlementRule;
import com.pzj.regulation.entity.StrategyVo;
import com.pzj.regulation.service.StrategySettlementRuleService;
import com.pzj.regulation.service.impl.StrategySettlementRuleServiceImpl;
import com.pzj.sale.api.order.service.OrderUtils;
import com.pzj.sale.api.order.vo.ExpiryDate;
import com.pzj.util.KeyValueVo;

/**
 * 景区服务
 * 
 * @author wangkai
 * @date 2015年11月6日 下午8:37:11
 */
@Component
public class SceneService {

    private final Logger                                              logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private com.pzj.customer.service.CustomerService                  customerService;
    @Autowired
    private com.pzj.product.api.product.service.ProductService        productService;
    @Autowired
    private com.pzj.platform.appapi.service.ProductService            productServiceForApi;
    @Autowired
    private com.pzj.product.api.enterprise.service.ScenicService      scenicService;
    @Autowired
    private com.pzj.product.api.enterprise.service.ProductDictService productDictService;
    @Autowired
    private com.pzj.regulation.service.StrategyService                strategyService;

    @Autowired
    private TicketBackRuleService                                     ticketBackRuleService;
    @Autowired
    private OrderUtils                                                orderUtils;
    @Resource(name = "propertyLoader")
    private PropertyLoader                                            propertyLoader;

    @Autowired
    private ProductStrategyService                                    productStrategyService;
    @Autowired
    private ProductComposeService                                     productComposeService;
    @Autowired
    private SkuProductApi                                             skuApi;

    public ScenicVO getScenicById(Long scenieId) {
        try {
            ScenicVO scenicVo = new ScenicVO();
            scenicVo.setId(scenieId);
            PageModel model = new PageModel(1, 100, "create_date desc");
            /*得到用户能看到所有景区的 热门景区*/
            PageList<ScenicVO> scenicVoList = scenicService.queryPageByParamMap(model, scenicVo);
            if (scenicVoList.getResultList() != null && scenicVoList.getResultList().size() > 0) {
                return scenicVoList.get(0);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("", ex);
        }
        return null;
    }

    /**
     * 查询登录用户能看到的省份下的所有景区
     * 
     * @param data
     *            json参数包
     * @param customer
     *            当前登录用户
     * @param json
     *            返回结果包装
     * @key currentPage 当前页数
     * @key pageSize 每页记录数
     * @key sceneName 景区名称
     * @key sceneLevel 景区级别
     * @key city 景区城市
     * @key productType 产品类型
     * @key theatre 是否是演出景区
     * @return JsonEntity 返回类型
     * @key marketPrice 挂牌价
     * @key retailPrice 渠道价
     * @key imageurl 景区图片地址
     */
    public JsonEntity querySceneByProvince(JSONObject data, Customer customer, JsonEntity json) {
        String province = data.containsKey("province") ? data.getString("province") : "";// 景区省份
        String type = data.containsKey("productType") ? data.getString("productType") : "1";// 0:是团
        // 1：是散
        String currentPage = data.containsKey("currentPage") ? data.getString("currentPage") : "";// 第几页
        String pageSize = data.containsKey("pageSize") ? data.getString("pageSize") : "";// 每页多少数据

        if (CheckUtils.isNull(type)) {
            json.setCode(CodeHandle.CODE_90001.getCode() + "");
            json.setMessage(CodeHandle.CODE_90001.getMessage());
            return json;
        }
        if (CheckUtils.isNull(province)) {
            json.setCode(CodeHandle.CODE_90001.getCode() + "");
            json.setMessage(CodeHandle.CODE_90001.getMessage());
            return json;
        }
        List<Customer> customerList = Lists.newArrayList();
        Customer ct = new Customer();
        ct.setId(customer.getId());
        customerList.add(ct);
        Map<String, List<Map<String, Object>>> teMap = Maps.newHashMap();
        try {
            ScenicVO scenicVo = new ScenicVO();
            scenicVo.setProvince(province.trim());

            PageModel model = new PageModel("sort desc");
            model.setPageSize(Integer.valueOf(pageSize));
            model.setPageNo(Integer.valueOf(currentPage));

            PageList<ScenicVO> scenicVoList = scenicService.queryPageByParamMap(model, scenicVo);
            System.out.println(scenicVoList);
            if (scenicVoList != null && (scenicVoList.getResultList() != null && scenicVoList.getResultList().size() > 0)) {

                List<Map<String, Object>> jsonArray = Lists.newArrayList();
                for (ScenicVO scenicVO2 : scenicVoList.getResultList()) {
                    Map<String, Object> jsonObject = Maps.newHashMap();

                    jsonObject.put("sceneId", String.valueOf(scenicVO2.getId()));
                    jsonObject.put("sceneName", scenicVO2.getName());
                    jsonObject.put("city", scenicVO2.getCity());
                    jsonObject.put("province", scenicVO2.getProvince());

                    jsonObject.put("sceneLevel", String.valueOf(scenicVO2.getGrade()));
                    String imgUrl = scenicVO2.getImgUrl();
                    if (CheckUtils.isNotNull(imgUrl)) {
                        imgUrl = imgUrl.split(",")[0];
                    } else {
                        imgUrl = "";
                    }
                    jsonObject.put("imageurl", imgUrl);
                    List<Long> suppilerIdList = scenicVO2.getSuppilerIdList();
                    if (suppilerIdList == null || suppilerIdList.size() == 0) {
                        continue;
                    }
                    Long suppilerId = suppilerIdList.get(0);
                    jsonObject.put("supplierId", suppilerId + "");
                    jsonArray.add(jsonObject);
                }
                teMap.put("scenicList", jsonArray);
            }

            json.setCode(CodeHandle.SUCCESS.getCode() + "");
            json.setMessage(CodeHandle.SUCCESS.getMessage());
            json.setResponseBody(teMap);
            return json;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }

    /**
     * 查询登录用户能看到的所有景区,并以首字母分组
     * 
     * @param data
     *            json参数包
     * @param customer
     *            当前登录用户
     * @param json
     *            返回结果包装
     * @key currentPage 当前页数
     * @key pageSize 每页记录数
     * @key sceneName 景区名称
     * @key sceneLevel 景区级别
     * @key city 景区城市
     * @key productType 产品类型
     * @key theatre 是否是演出景区
     * @return JsonEntity 返回类型
     * @key marketPrice 挂牌价
     * @key retailPrice 渠道价
     * @key imageurl 景区图片地址
     */
    public JsonEntity queryAllScenes(JSONObject data, Customer customer, JsonEntity json) {
        String type = data.containsKey("productType") ? data.getString("productType") : "1";// 0:是团// 1：是散
        if (CheckUtils.isNull(type)) {
            json.setCode(CodeHandle.CODE_90001.getCode() + "");
            json.setMessage(CodeHandle.CODE_90001.getMessage());
            return json;
        }
        Map<String, List<Map<String, Object>>> teMap = Maps.newHashMap();
        try {
            ScenicVO sv = new ScenicVO();
            PageList<ScenicVO> scenicVoList = scenicService.queryPageByParamMap(null, sv);
            if (scenicVoList != null && (scenicVoList.getResultList() != null && scenicVoList.getResultList().size() > 0)) {
                PageModel page = new PageModel(1, 1000);
                for (ScenicVO scenicVO2 : scenicVoList.getResultList()) {
                    ScenicVO sv2 = new ScenicVO();
                    sv2.setId(scenicVO2.getId());
                    PageList<ProductVO> productVOs = productService.findProductInfoByInfoScenic(new ProductVO(), sv2, page);
                    if (productVOs == null || productVOs.getResultList() == null || productVOs.getResultList().size() == 0) {
                        continue;
                    }
                    String py = PinyinUtilsPro.getHeadPinyin(scenicVO2.getName());
                    List<Long> suppilerIdList = scenicVO2.getSuppilerIdList();
                    if (suppilerIdList == null || suppilerIdList.size() == 0) {
                        continue;
                    }
                    Long suppilerId = suppilerIdList.get(0);
                    if (py != null && py.length() > 0) {
                        Map<String, Object> jsonObject = Maps.newHashMap();
                        jsonObject.put("sceneId", String.valueOf(scenicVO2.getId()));
                        jsonObject.put("sceneName", scenicVO2.getName());
                        jsonObject.put("city", scenicVO2.getCity());
                        jsonObject.put("province", scenicVO2.getProvince());
                        jsonObject.put("sceneLevel", String.valueOf(scenicVO2.getGrade()));
                        jsonObject.put("type", String.valueOf(scenicVO2.getType()));
                        String imgUrl = scenicVO2.getImgUrl();
                        if (CheckUtils.isNotNull(imgUrl)) {
                            imgUrl = imgUrl.split(",")[0];
                        } else {
                            imgUrl = "";
                        }
                        jsonObject.put("imageurl", imgUrl);
                        jsonObject.put("supplierId", suppilerId + "");
                        if (teMap.get(py.substring(0, 1)) == null) {
                            List<Map<String, Object>> jsonArray = Lists.newArrayList();
                            jsonObject.put("initial", py.substring(0, 1));
                            jsonArray.add(jsonObject);
                            teMap.put(py.substring(0, 1), jsonArray);
                        } else {
                            List<Map<String, Object>> list = teMap.get(py.substring(0, 1));
                            jsonObject.put("initial", py.substring(0, 1));
                            list.add(jsonObject);
                            teMap.put(py.substring(0, 1), list);
                        }
                    }
                }
            }
            json.setCode(CodeHandle.SUCCESS.getCode() + "");
            json.setMessage(CodeHandle.SUCCESS.getMessage());
            json.setResponseBody(teMap);
            return json;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }

    /**
     * 查询产品介绍 及退换票规则
     * 
     * @param data
     * @param json
     * @return
     */
    public JsonEntity queryProductDetail(JSONObject data, JsonEntity json) {
        String productId = data.containsKey("productId") ? data.getString("productId") : "";
        String productInfoId = data.containsKey("productInfoId") ? data.getString("productInfoId") : "";
        if (CheckUtils.isNull(productInfoId)) {
            json.setCode(CodeHandle.CODE_90001.getCode() + "");
            json.setMessage(CodeHandle.CODE_90001.getMessage());
            return json;
        }
        Map<String, Object> jsonMap = Maps.newHashMap();
        try {
            ProductVO ppv = productService.getProductVOById(Long.valueOf(productInfoId));
            jsonMap.put("reeaseInfo", ppv.getReeaseInfo());
            List<TicketBackRuleVO> tbrList = ticketBackRuleService.findTicketBackRuleByProductId(Long.valueOf(productId),
                String.valueOf(GlobalParam.FLAG.start()));
            List<Map<String, Object>> jsonArray = Lists.newArrayList();
            for (TicketBackRuleVO tbrv : tbrList) {
                Map<String, Object> jsonObject = Maps.newHashMap();
                jsonObject.put("id", String.valueOf(tbrv.getId()));
                jsonObject.put("name", tbrv.getName());
                jsonObject.put("beforeExpire", String.valueOf(tbrv.getBeforeExpire()));
                jsonObject.put("expireUnit", tbrv.getExpireUnit());
                jsonObject.put("returnType", tbrv.getReturnType());
                jsonObject.put("returnDeductType", tbrv.getReturnDeductType());
                jsonObject.put("returnDeductQuantity", String.valueOf(tbrv.getReturnDeductQuantity()));
                jsonObject.put("changeType", tbrv.getChangeType());
                jsonObject.put("changeDeductType", tbrv.getChangeDeductType());
                jsonObject.put("changeDeductQuantity", String.valueOf(tbrv.getChangeDeductQuantity()));
                jsonObject.put("flag", tbrv.getFlag());
                jsonObject.put("supplierId", tbrv.getSupplierId());
                jsonArray.add(jsonObject);
            }
            jsonMap.put("ticketBackRule_list", jsonArray);
        } catch (Exception e) {
            e.printStackTrace();
        }
        json.setCode(CodeHandle.SUCCESS.getCode() + "");
        json.setMessage(CodeHandle.SUCCESS.getMessage());
        json.setResponseBody(jsonMap);
        return json;
    }

    /**
     * 通过产品名称，匹配产品
     * 
     * @param data
     * @param customer
     * @param json
     * @return JsonEntity 返回类型
     * @throws
     */
    public JsonEntity queryProductComplete(JSONObject data, Customer customer, JsonEntity json) {
        ProductPriceVO product = new ProductPriceVO();
        // product.setName(name);
        try {
            List<ProductPriceVO> list = productService.findListByParams(product);
            for (ProductPriceVO productPriceVO : list) {
                productPriceVO.toString();
            }
        } catch (Exception e) {

            e.printStackTrace();
        }
        return null;
    }

    /**
     * 查询单个景区信息及景区里的的产品列表，这些产品必须是用户能看到的
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
    public JsonEntity queryProductBySceneId(JSONObject data, Customer customer, JsonEntity json) {
        String supplierId = data.containsKey("supplierId") ? data.getString("supplierId") : "";
        String sceneId = data.containsKey("sceneId") ? data.getString("sceneId") : "";
        String type = data.containsKey("productType") ? data.getString("productType") : "1";// 0:是团
        // 1：是散
        String salesType = data.containsKey("salesType") ? data.getString("salesType") : "";//销售端口 导游4 商户5 // 1：是散
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
                //userId = Long.parseLong(travelAgencyId);
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
            Customer customer2 = new Customer();
            customer2.setId(userId);
            List<Customer> cus = customerService.findCustomerByParams(customer2);
            List<StrategyVo> list = orderUtils.findStrategyByScenic(userId, Long.parseLong(sceneId), cus.get(0).getSupplierId(), isGroup, salePort);

            // 产品集合（通过产品返回政策数据）
            //List<ProductPriceVO> retPriceList = null;
            if (list != null && list.size() > 0) {
                double payAmount = 0;
                for (StrategyVo strategyVo : list) {
                    if (null != strategyVo.getRebateStrategyList() && strategyVo.getRebateStrategyList().size() > 0) {
                        payAmount = strategyVo.getPrice();
                    } else {
                        payAmount = strategyVo.getSettlementPrice() == null ? 0 : strategyVo.getSettlementPrice();
                    }
                    List<Long> productIds = strategyVo.getProductList();
                    ProductPriceVO prov = null;
                    for (int i = 0; i < productIds.size(); i++) {
                        String strategrId = String.valueOf(strategyVo.getId());
                        Map<String, Object> obj = Maps.newHashMap();
                        if (CheckUtils.isNotNull(productIds.get(i))) {
                            ProductComposeVO productComposeVo = new ProductComposeVO();
                            productComposeVo.setId(productIds.get(i));
                            prov = productComposeService.queryComposeById(productComposeVo);
                            if (prov.getProCategory() != null && prov.getProCategory().equals("11")) {
                                for (int j = 0; j < prov.getProductList().size(); j++) {
                                    if (prov.getProductList().get(j).getProCategory() != null && prov.getProductList().get(j).getProCategory().equals("13")) {
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
                            List<String> scopeValueList = new ArrayList<>();
                            for (KeyValueVo keyValueVo : scope) {
                                String value = keyValueVo.getValue();
                                scopeValueList.add(value);
                            }
                            //查询联票里边的每张子票的 未满检金额
                            Map<String, Object> object1 = Maps.newHashMap();
                            List<Long> queryStartegyIdList = new ArrayList<>();
                            List<Long> queryParentProductIdList = new ArrayList<>(), queryProductIdList = new ArrayList<>();
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
                            obj.put("ParentTickets", strategyVo.getReduceSettlementMoney() == null ? "" : String.valueOf(strategyVo.getReduceSettlementMoney()));
                            obj.put("chileTicket", object1);
                            obj.put("isUnique", orderUtils.getSuppilerIdIsUnique(Long.valueOf(prov.getSupplierId())) ? 1 : 0);
                            obj.put("scope", scopeValueList);// 适用日期范围
                            obj.put("gold", rebateAmount + "");// 金币
                            obj.put("marketPrice", String.valueOf(prov.getMarketPrice()) + "");// 门市价
                            obj.put("retailPrice", String.valueOf(price) + "");// 渠道价
                            obj.put("productName", prov.getName() + ""); //产品的名字
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
                            obj.put("payAmount", payAmount);
                            obj.put("strategrId", strategrId);
                            if (prov.getProduct() != null) {
                                obj.put("playtime", prov.getProduct().getPlaytimeValue() == null ? "" : String.valueOf(prov.getProduct().getPlaytimeValue()));
                                obj.put("playtimeUnit", prov.getProduct().getPlaytimeUnit() == null ? "" : String.valueOf(prov.getProduct().getPlaytimeUnit()));
                                obj.put("playtimeMode", prov.getProduct().getPlaytimeMode() == null ? "" : String.valueOf(prov.getProduct().getPlaytimeMode()));
                            }
                            String isCandidate = "0";//0   没有 后选座
                            obj.put("isCandidate", isCandidate);
                            //if(null != strategyVo.getSubProductId()){
                            //if(prov.getProCategory().equals("11")){
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

    /*
     * 获取检票时长
     */
    public JsonEntity checkTimeCount(JSONObject data, Customer customer, JsonEntity json) {
        Map<String, Object> object = Maps.newHashMap();
        try {
            String productId = data.containsKey("productId") ? data.getString("productId") : "";//产品id
            String aroundTime = data.containsKey("aroundTime") ? data.getString("aroundTime") : "";//出行时间
            String strategrId = data.containsKey("strategrId") ? data.getString("strategrId") : "";//政策id
            Long strateId = strategrId == null || "".equals(strategrId) ? null : Long.parseLong(strategrId);
            StrategyVo strategyVo = strategyService.getById(strateId);
            ProductPriceVO prov = null;
            ProductComposeVO productComposeVo = new ProductComposeVO();
            productComposeVo.setId(Long.parseLong(productId));
            prov = productComposeService.queryComposeById(productComposeVo);
            ExpiryDate expiryDate = orderUtils.getExpiryTime(strategyVo, DateUtils.parseDate(aroundTime), prov);
            String starttime = "", endtime = "";
            if (null != expiryDate) {
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                Date startTime = expiryDate.getStartTime();
                Date endTime = expiryDate.getEndTime();
                starttime = df.format(startTime);//检票开始时间
                endtime = df.format(endTime);//检票结束时间
            }

            object.put("starttime", starttime);
            object.put("endtime", endtime);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            json.setCode(CodeHandle.FAILURE.getCode() + "");
            json.setMessage(CodeHandle.FAILURE.getMessage());
            return json;
        } catch (Exception e) {
            e.printStackTrace();
            json.setCode(CodeHandle.FAILURE.getCode() + "");
            json.setMessage("当前购买日期超过最后购买日期");
            return json;
        }
        json.setCode(CodeHandle.SUCCESS.getCode() + "");
        json.setMessage(CodeHandle.SUCCESS.getMessage());
        json.setResponseBody(object);
        return json;
    }

    /**
     * 轮播图列表＋本地热门列表
     * @return
     */
    public JsonEntity queryForAppIndex(JSONObject data, Customer customer, JsonEntity json) {
        /**测试数据**/
        String province = data.getString("province");// 景区省份
        int currentPage = data.containsKey("currentPage") ? data.getIntValue("currentPage") : 1;// 第几页
        int pageSize = data.containsKey("pageSize") ? data.getIntValue("pageSize") : 6;// 每页多少数据
        //普通还是演艺
        String sceneType = data.containsKey("sceneType") ? data.getString("sceneType") : "1";//类型（1景区 2剧场）

        logger.info("queryForAppIndex param : " + JSON.toJSONString(data));

        if (CheckUtils.isNull(province)) {
            json.setCode(CodeHandle.CODE_90001.getCode() + "");
            json.setMessage(CodeHandle.CODE_90001.getMessage());
            return json;
        }
        Long s0 = System.currentTimeMillis();
        try {
            Map<String, Object> object = Maps.newHashMap();
            ScenicVO scenicVo = new ScenicVO();
            scenicVo.setProvince(province.trim());
            scenicVo.setStatus(1);
            scenicVo.setType(Integer.parseInt(sceneType));
            PageModel model = new PageModel(1, 100, "sort desc");
            /*得到用户能看到所有景区的 热门景区*/
            PageList<ScenicVO> scenicVoList = scenicService.queryPageByParamMap(model, scenicVo);
            Long s1 = System.currentTimeMillis();
            logger.error("s1 cost : " + (s1 - s0));
            int size = (scenicVoList == null || scenicVoList.getResultList() == null ? 0 : scenicVoList.getResultList().size());
            List<Map<String, Object>> hotlist = new ArrayList<>();
            List<Map<String, Object>> carousellist = new ArrayList<>();
            if (currentPage == 1) {
                Integer salePort = null;
                String isGroup = "1";
                Long userId = customer.getId();
                isGroup = OrderUtils.GROUP_ORDER;
                salePort = OrderUtils.OrderSalePortType.TOUR_GUIDE_APP.getKey();
                if (customer.getResellerType().equals(UserGlobalDict.guideUserType())) {
                    //导游不走散票 一律查团票
                    userId = null;
                    isGroup = OrderUtils.GROUP_ORDER;
                    salePort = OrderUtils.OrderSalePortType.TOUR_GUIDE_APP.getKey();
                } else {
                    userId = customer.getId();
                    isGroup = OrderUtils.BULK_ORDER;
                    salePort = OrderUtils.OrderSalePortType.MERCHANT_APP.getKey();
                }
                List<StrategyVo> list = null;
                PageModel page = new PageModel(1, 1000);
                for (int i = 0; i < scenicVoList.getResultList().size(); i++) {
                    Long s20 = System.currentTimeMillis();
                    ScenicVO sv = scenicVoList.getResultList().get(i);
                    ScenicVO sv2 = new ScenicVO();
                    sv2.setId(sv.getId());
                    PageList<ProductVO> productVOs = productService.findProductInfoByInfoScenic(new ProductVO(), sv2, page);
                    if (productVOs == null || productVOs.getResultList() == null || productVOs.getResultList().size() == 0) {
                        continue;
                    }
                    Long s21 = System.currentTimeMillis();
                    logger.error("s21 cost : " + (s21 - s20));

                    if (userId == null) {
                        list = orderUtils.findStrategyByScenic(customer.getId(), sv.getId(), isGroup, salePort);
                    } else {
                        list = orderUtils.findStrategyByScenic(userId, sv.getId(), customer.getSupplierId(), isGroup, salePort);
                    }
                    if (list == null || list.size() <= 0) {
                        continue;
                    }
                    Long s22 = System.currentTimeMillis();
                    logger.error("s22 cost : " + (s22 - s21));

                    Map<String, Object> jsonObject = Maps.newHashMap();
                    jsonObject.put("sceneId", String.valueOf(sv.getId()));
                    jsonObject.put("sceneName", sv.getName());
                    jsonObject.put("city", sv.getCity());
                    jsonObject.put("province", sv.getProvince());
                    ProductDictVO pd = productDictService.getByVal(sv.getGrade() == null ? "" : sv.getGrade().toString(), GlobalDict.PRODUCT.scenicLevel());
                    if (pd != null) {
                        jsonObject.put("sceneLevel", pd.getValue());
                        jsonObject.put("sceneLevelValue", pd.getValue());
                    }
                    jsonObject.put("imageurl", sv.getImgUrl());
                    jsonObject.put("supplierId", customer.getSupplierId() + "");
                    jsonObject.put("info", sv.getInfo());

                    /**查询景区下的产品接口*/
                    Double minPrice = null;
                    if (list != null && list.size() > 0) {
                        StrategyFilter.filterChannel(list, salePort + "");
                        for (StrategyVo strategyVo : list) {
                            Double advicePrice = strategyVo.getAdvicePrice();
                            if (minPrice == null) {
                                minPrice = advicePrice;
                            } else {
                                if (advicePrice != null && (advicePrice > 0 && advicePrice < minPrice)) {
                                    minPrice = advicePrice;
                                }
                            }
                        }
                    }

                    jsonObject.put("minPrice", StringUtils.doubleToString(minPrice == null ? 0 : minPrice));
                    /**查询景区下的产品接口*/

                    hotlist.add(jsonObject);
                    /*目前取景区都是按照日期倒序排，临时将前3个放入轮播列表*/
                    if (i < 3) {
                        carousellist.add(jsonObject);
                    }
                    Long s23 = System.currentTimeMillis();
                    logger.error("s23 cost : " + (s23 - s22));

                }
            }

            long s2 = System.currentTimeMillis();
            logger.error("s2 cost : " + (s2 - s1));

            object.put("carousellist", carousellist);
            object.put("hotlist", hotlist);
            int pageCount = 1;
            if (size > 0) {
                if (size <= pageSize) {
                    pageCount = 1;
                } else {
                    pageCount = size % pageSize > 0 ? size / pageSize + 1 : size / pageSize;
                }
            }
            object.put("pageCount", pageCount);
            object.put("itemCount", size);
            json.setCode(CodeHandle.SUCCESS.getCode() + "");
            json.setMessage(CodeHandle.SUCCESS.getMessage());
            json.setResponseBody(object);
        } catch (Exception e) {
            e.printStackTrace();
            json.setCode(CodeHandle.FAILURE.getCode() + "");
            json.setMessage(CodeHandle.FAILURE.getMessage());
        }
        return json;
    }

    /**
     * 轮播图列表＋本地热门列表
     * @return
     */
    public JsonEntity queryForAppIndexNew(JSONObject data, Customer customer, JsonEntity json) {
        /**测试数据**/
        String province = data.getString("province");// 景区省份
        int currentPage = data.containsKey("currentPage") ? data.getIntValue("currentPage") : 1;// 第几页
        int pageSize = data.containsKey("pageSize") ? data.getIntValue("pageSize") : 6;// 每页多少数据
        //普通还是演艺
        String sceneType = data.containsKey("sceneType") ? data.getString("sceneType") : "1";//类型（1景区 2剧场）
        //        String salesType = data.containsKey("salesType") ? data.getString("salesType") : String.valueOf(UserGlobalDict.StrategyGlobalDict.windowTenantApp());

        logger.info("queryForAppIndex param : " + JSON.toJSONString(data));

        if (CheckUtils.isNull(province)) {
            json.setCode(CodeHandle.CODE_90001.getCode() + "");
            json.setMessage(CodeHandle.CODE_90001.getMessage());
            return json;
        }
        Long s0 = System.currentTimeMillis();
        List<Map<String, Object>> hotlist = new ArrayList<>();
        List<Map<String, Object>> carousellist = new ArrayList<>();
        Map<String, Object> object = Maps.newHashMap();
        //查询到的景区数量
        Integer size = 0;
        try {
            if (currentPage == 1) {
                Integer salePort = null;
                Long userId = customer.getId();
                String isGroup = OrderUtils.GROUP_ORDER;
                salePort = OrderUtils.OrderSalePortType.TOUR_GUIDE_APP.getKey();
                if (customer.getResellerType().equals(UserGlobalDict.guideUserType())) {
                    //导游不走散票 一律查团票
                    userId = null;
                    isGroup = OrderUtils.GROUP_ORDER;
                    salePort = OrderUtils.OrderSalePortType.TOUR_GUIDE_APP.getKey();
                } else {
                    userId = customer.getId();
                    isGroup = OrderUtils.BULK_ORDER;
                    salePort = OrderUtils.OrderSalePortType.MERCHANT_APP.getKey();
                }

                //SalePort如果参数为空，使用默认值，否则
                if (CheckUtils.isNotNull(data.getString("salesType")))
                    salePort = Integer.valueOf(data.getString("salesType"));
                ScenicVO scenicVo = new ScenicVO();
                scenicVo.setProvince(province.trim());
                scenicVo.setStatus(1);
                scenicVo.setType(Integer.parseInt(sceneType));
                QueryPageModel model = new QueryPageModel(1, 100, "sort desc");

                List<Integer> productTypes = new ArrayList<Integer>();
                productTypes.add(sceneType.equals("1") ? 1 : 10);
                Result<QueryPageList<SkuScenicResult>> resultList = skuApi.findSkuScenicForApp(model, province.trim(), productTypes, salePort.toString(),
                    isGroup, userId);
                Long s1 = System.currentTimeMillis();
                logger.info("调用 findSkuScenicForApp 接口所用时间 ::" + (s1 - s0));
                if (CheckUtils.isNotNull(resultList) && resultList.getErrorCode() == new Result<>().getErrorCode() && resultList.getData() != null) {
                    QueryPageList<SkuScenicResult> skuResultList = resultList.getData();
                    if (skuResultList != null) {
                        List<SkuScenicResult> scenicList = skuResultList.getResultList();
                        size = scenicList.size();
                        for (int i = 0; i < size; i++) {
                            SkuScenic sv = scenicList.get(i).getSkuScenic();
                            Map<String, Object> jsonObject = Maps.newHashMap();
                            jsonObject.put("sceneId", String.valueOf(sv.getId()));
                            jsonObject.put("sceneName", sv.getName());
                            jsonObject.put("city", sv.getCity());
                            jsonObject.put("province", sv.getProvince());
                            ProductDictVO pd = productDictService.getByVal(sv.getGrade() == null ? "" : sv.getGrade().toString(),
                                GlobalDict.PRODUCT.scenicLevel());
                            if (pd != null) {
                                jsonObject.put("sceneLevel", pd.getValue());
                                jsonObject.put("sceneLevelValue", pd.getValue());
                            }
                            jsonObject.put("imageurl", sv.getImgUrl());
                            jsonObject.put("supplierId", customer.getSupplierId() + "");
                            jsonObject.put("info", sv.getInfo());

                            jsonObject.put("minPrice", scenicList.get(i).getMinPrice());
                            /**查询景区下的产品接口*/

                            hotlist.add(jsonObject);
                            /*目前取景区都是按照日期倒序排，临时将前3个放入轮播列表*/
                            if (i < 3) {
                                carousellist.add(jsonObject);
                            }
                        }
                    }
                }
            }
            object.put("carousellist", carousellist);
            object.put("hotlist", hotlist);
            int pageCount = 1;
            if (size > 0) {
                if (size <= pageSize) {
                    pageCount = 1;
                } else {
                    pageCount = size % pageSize > 0 ? size / pageSize + 1 : size / pageSize;
                }
            }
            object.put("pageCount", pageCount);
            object.put("itemCount", size);
            json.setCode(CodeHandle.SUCCESS.getCode() + "");
            json.setMessage(CodeHandle.SUCCESS.getMessage());
            json.setResponseBody(object);
        } catch (Exception e) {
            e.printStackTrace();
            json.setCode(CodeHandle.FAILURE.getCode() + "");
            json.setMessage(CodeHandle.FAILURE.getMessage());
        }
        return json;
    }

    /**
     * 根据用户customer，以及spuId获取单个SPU的招财猫信息
     * 
     * @param data
     * @param customer
     * @param json
     * @return
     */
    public JsonEntity queryFortuneCatBySpuId(JSONObject data, Customer customer, JsonEntity json) {

        Long resellerId = customer.getId();
        Long spuId = data.containsKey("spuId") ? data.getLong("spuId") : null;
        if (CheckUtils.isNull(spuId)) {
            logger.info("spuId 为空");
            json.setCode(CodeHandle.CODE_90001.getCode() + "");
            json.setMessage(CodeHandle.CODE_90001.getMessage());
            return json;
        }

        Integer salePort = null;
        //这里其实是有问题的，招财猫只能返回分销商信息，无法返回导游App的东西！
        String isGroup = "1";
        Long userId = customer.getId();
        boolean guide = true;
        isGroup = String.valueOf(UserGlobalDict.StrategyGlobalDict.groupTicketVarie());
        salePort = UserGlobalDict.StrategyGlobalDict.windowGuideApp();
        if (customer.getResellerType().equals(UserGlobalDict.guideUserType())) {
            //导游不走散票 一律查团票
            userId = null;
        } else {
            userId = customer.getId();
            guide = false;
            isGroup = String.valueOf(UserGlobalDict.StrategyGlobalDict.scatterTicketVarie());
            salePort = UserGlobalDict.StrategyGlobalDict.windowTenantApp();
        }

        SpuProductResultVO spuVO = skuApi.findSpuProductById(resellerId, spuId, ProductIdType.SPU_ID, String.valueOf(salePort));
        if (spuVO == null) {
            json.setCode(CodeHandle.CODE_90003.getCode());
            json.setMessage(CodeHandle.CODE_90003.getMessage());
            return json;
        }
        Map<String, Object> jsonObject = Maps.newHashMap();
        jsonObject.put("spu", spuVO);
        json.setCode(CodeHandle.SUCCESS.getCode() + "");
        json.setMessage(CodeHandle.SUCCESS.getMessage());
        json.setResponseBody(jsonObject);
        return json;
    }

    /**
     * 
     * @param data
     * @param customer
     * @param json
     * @return
     */
    public JsonEntity querySenceDetail(JSONObject data, Customer customer, JsonEntity json) {
        /**测试数据**/
        String sceneId = data.containsKey("sceneId") ? data.getString("sceneId") : "";
        // 1：是散
        String salesType = "4";
        if (data != null) {
            salesType = data.containsKey("salesType") ? data.getString("salesType") : "";//销售端口 导游4 商户5 // 1：是散

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
        isGroup = String.valueOf(UserGlobalDict.StrategyGlobalDict.groupTicketVarie());
        salePort = UserGlobalDict.StrategyGlobalDict.windowGuideApp();
        if (customer.getResellerType().equals(UserGlobalDict.guideUserType())) {
            //导游不走散票 一律查团票
            userId = null;
        } else {
            userId = customer.getId();
            guide = false;
            isGroup = String.valueOf(UserGlobalDict.StrategyGlobalDict.scatterTicketVarie());
            salePort = UserGlobalDict.StrategyGlobalDict.windowTenantApp();
        }

        Map<String, Object> jsonObject = Maps.newHashMap();
        List<Map<String, Object>> jsonArray = Lists.newArrayList();
        List<JsonProduct> jsonProductArray = null;
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
            jsonObject.put("imgurl", scenicVO.getImgUrl());
            jsonObject.put("suppilerId", scenicVO.getSuppilerId());
            jsonObject.put("info", scenicVO.getInfo());
            List<StrategyVo> list = null;
            boolean isGudit = false;
            String isOunterSign = "1";
            if (userId == null) {
                isGudit = true;
                list = orderUtils.findStrategyByScenic(customer.getId(), Long.parseLong(sceneId), isGroup, salePort);
            } else {
                list = orderUtils.findStrategyByScenic(userId, Long.parseLong(sceneId), customer.getSupplierId(), isGroup, salePort);
            }
            Map<String, ProductVO> productVOs = Maps.newHashMap();
            if (isGudit) {
                if (list != null && list.size() > 0) {
                    double payAmount = 0;
                    for (StrategyVo strategyVo : list) {
                        if (null != strategyVo.getRebateStrategyList() && strategyVo.getRebateStrategyList().size() > 0) {
                            payAmount = strategyVo.getPrice();
                        } else {
                            payAmount = strategyVo.getSettlementPrice() == null ? 0 : strategyVo.getSettlementPrice();
                        }
                        List<Long> productIds = strategyVo.getProductList();
                        ProductPriceVO prov = null;
                        if (strategyVo.getRebateStrategyList() != null && strategyVo.getRebateStrategyList().size() > 0) {
                            isOunterSign = "0";
                        }
                        String strategrId = String.valueOf(strategyVo.getId());
                        for (int i = 0; i < productIds.size(); i++) {
                            String productId_ = String.valueOf(productIds.get(i));
                            if (productVOs.containsKey(productId_)) {//存在
                                continue;
                            }
                            Map<String, Object> productReleaseMapJson = Maps.newHashMap();
                            if (CheckUtils.isNotNull(productIds.get(i))) {
                                ProductComposeVO productComposeVo = new ProductComposeVO();
                                productComposeVo.setId(productIds.get(i));
                                prov = productComposeService.queryComposeById(productComposeVo);
                                if (prov.getProCategory() != null && prov.getProCategory().equals("11")) {
                                    for (int j = 0; j < prov.getProductList().size(); j++) {
                                        if (prov.getProductList().get(j).getProCategory() != null && prov.getProductList().get(j).getProCategory().equals("13")) {
                                            prov.setTheaterArea(prov.getProductList().get(j).getTheaterArea());//如果是演艺联票，区域就在子票里边取
                                            prov.setTheatercnum(prov.getProductList().get(j).getTheatercnum());//如果是演艺联票，场次就在子票里边取
                                            break;
                                        }
                                    }
                                }
                                /*产品组部分*/
                                ProductVO productVO = productService.getProductVOById(prov.getProductId());
                                productVOs.put(String.valueOf(productVO.getId()), productVO);
                                productReleaseMapJson.put("productVO", productVO);
                                productReleaseMapJson.put("gainType", productVO.getGainType() == null ? 1 : productVO.getGainType());//身份证取票   二维码取票的标示
                                productReleaseMapJson.put("isOunterSign", isOunterSign);//前返后返的标示

                                /*产品组部分*/
                                /*是否一票一证*/
                                boolean isUnique = false;
                                if (null != prov.getSupplierId()) {
                                    isUnique = orderUtils.getSuppilerIdIsUnique(Long.parseLong(prov.getSupplierId()));
                                }
                                productReleaseMapJson.put("isUnique", isUnique == true ? 1 : 2);
                                productReleaseMapJson.put("productInfoId", String.valueOf(prov.getProductId()) + "");
                                productReleaseMapJson.put("productReleaseId", prov.getId());
                                productReleaseMapJson.put("productReleaseName", prov.getName());
                                productReleaseMapJson.put("marketPrice", prov.getMarketPrice());
                                productReleaseMapJson.put("mfPrice", prov.getMfPrice() + "");//魔方价							
                                productReleaseMapJson.put("scope", "");
                                productReleaseMapJson.put("supplierId", String.valueOf(prov.getSupplierId()) + "");

                                productReleaseMapJson.put("expire", "");
                                productReleaseMapJson.put("expireMode", "");
                                productReleaseMapJson.put("expireBeginDate", "");
                                productReleaseMapJson.put("expireEndDate", "");
                                productReleaseMapJson.put("price", strategyVo.getPrice());
                                productReleaseMapJson.put("payAmount", prov.getMarketPrice());

                                /*结算价*/
                                productReleaseMapJson.put("settlementPrice", payAmount);
                                productReleaseMapJson.put("strategrId", strategrId);
                                productReleaseMapJson.put("channelId", "");

                                productReleaseMapJson.put("parentTickets", "");
                                productReleaseMapJson.put("chileTicket", "");
                                productReleaseMapJson.put("rebateAmount", "");//  返利数量
                                /*演艺部分*/
                                productReleaseMapJson.put("area", prov.getTheaterArea() + "");// 剧场区域
                                productReleaseMapJson.put("screenings", prov.getTheatercnum() + "");// 剧场场次
                                String isCandidate = "0";//0   没有 后选座
                                /*是否有后选座 0没有  1有*/
                                productReleaseMapJson.put("isCandidate", isCandidate);
                                /*演艺部分*/
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
                                    productReleaseMapJson.put("proCategory", "3");
                                    productReleaseMapJson.put("subProCategory", flag == true ? "2" : "1");
                                    if (salesType.equals("4")) {//目前是导游登录账号有后选座
                                        if (flag == true) {
                                            isCandidate = "1";//1   有 后选座
                                        }
                                        productReleaseMapJson.put("isCandidate", isCandidate);
                                    }
                                } else {
                                    String proCate = "";
                                    if (null != prov.getProCategory() && ProductCategory.scenic().equals(prov.getProCategory())) {
                                        proCate = ProductCategory.scenic();
                                        proCate = "2";
                                    } else {
                                        proCate = ProductCategory.normal();
                                    }
                                    productReleaseMapJson.put("proCategory", proCate);
                                }
                                jsonArray.add(productReleaseMapJson);
                            }
                        }
                    }
                }
            } else {
                if (list != null && list.size() > 0) {
                    double payAmount = 0;
                    for (StrategyVo strategyVo : list) {
                        if (null != strategyVo.getRebateStrategyList() && strategyVo.getRebateStrategyList().size() > 0) {
                            payAmount = strategyVo.getPrice();
                        } else {
                            payAmount = strategyVo.getSettlementPrice() == null ? 0 : strategyVo.getSettlementPrice();
                        }
                        if (strategyVo.getRebateStrategyList() != null && strategyVo.getRebateStrategyList().size() > 0) {
                            isOunterSign = "0";
                        }
                        List<Long> productIds = strategyVo.getProductList();
                        ProductPriceVO prov = null;
                        for (int i = 0; i < productIds.size(); i++) {
                            String strategrId = String.valueOf(strategyVo.getId());
                            Map<String, Object> productReleaseMapJson = Maps.newHashMap();
                            if (CheckUtils.isNotNull(productIds.get(i))) {
                                ProductComposeVO productComposeVo = new ProductComposeVO();
                                productComposeVo.setId(productIds.get(i));
                                prov = productComposeService.queryComposeById(productComposeVo);
                                if (prov.getProCategory() != null && prov.getProCategory().equals("11")) {
                                    for (int j = 0; j < prov.getProductList().size(); j++) {
                                        if (prov.getProductList().get(j).getProCategory() != null && prov.getProductList().get(j).getProCategory().equals("13")) {
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
                                List<String> scopeValueList = new ArrayList<>();
                                for (KeyValueVo keyValueVo : scope) {
                                    String value = keyValueVo.getValue();
                                    scopeValueList.add(value);
                                }
                                //查询联票里边的每张子票的 未满检金额
                                Map<String, Object> object1 = Maps.newHashMap();
                                List<Long> queryStartegyIdList = new ArrayList<>();
                                List<Long> queryParentProductIdList = new ArrayList<>(), queryProductIdList = new ArrayList<>();
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
                                                //double sonReduceMoney = ssr.getReduceSettlementMoney();
                                                long sonProductId = ssr.getProductId();
                                                ProductComposeVO pcvo = new ProductComposeVO();
                                                pcvo.setId(sonProductId);
                                                ProductPriceVO ppvo = productComposeService.queryComposeById(pcvo);
                                                object1.put(ppvo.getName(), ppvo.getReduceSettlementMoney());
                                            }
                                        }
                                    }
                                }
                                productReleaseMapJson.put("isOunterSign", isOunterSign);//身份证取票   二维码取票的标示
                                ProductVO productVO = productService.getProductVOById(prov.getProductId());
                                productVOs.put(String.valueOf(productVO.getId()), productVO);
                                productReleaseMapJson.put("productVO", productVO);
                                productReleaseMapJson.put("gainType", productVO.getGainType() == null ? 1 : productVO.getGainType());//身份证取票   二维码取票的标示
                                /*产品组部分*/
                                /*是否一票一证*/
                                boolean isUnique = false;
                                if (null != prov.getSupplierId()) {
                                    isUnique = orderUtils.getSuppilerIdIsUnique(Long.parseLong(prov.getSupplierId()));
                                }

                                productReleaseMapJson.put("isUnique", isUnique == true ? 1 : 2);
                                productReleaseMapJson.put("productInfoId", String.valueOf(prov.getProductId()) + "");
                                productReleaseMapJson.put("productReleaseId", prov.getId());
                                productReleaseMapJson.put("productReleaseName", prov.getName());
                                productReleaseMapJson.put("marketPrice", prov.getMarketPrice());
                                productReleaseMapJson.put("mfPrice", prov.getMfPrice() + "");//魔方价							
                                productReleaseMapJson.put("scope", scopeValueList);
                                productReleaseMapJson.put("supplierId", String.valueOf(prov.getSupplierId()) + "");

                                productReleaseMapJson.put("expire", expire + "");
                                productReleaseMapJson.put("expireMode", expireMode + "");
                                productReleaseMapJson.put("expireBeginDate", strategyVo.getBeginDate());
                                productReleaseMapJson.put("expireEndDate", strategyVo.getEndDate());
                                productReleaseMapJson.put("price", strategyVo.getPrice());
                                /*结算价*/
                                productReleaseMapJson.put("settlementPrice", payAmount);
                                productReleaseMapJson.put("strategrId", strategrId);
                                productReleaseMapJson.put("channelId", channelId + "");

                                productReleaseMapJson.put("parentTickets",
                                    strategyVo.getReduceSettlementMoney() == null ? "" : String.valueOf(strategyVo.getReduceSettlementMoney()));
                                productReleaseMapJson.put("chileTicket", object1);
                                productReleaseMapJson.put("rebateAmount", rebateAmount + "");//  返利数量
                                /*演艺部分*/
                                productReleaseMapJson.put("area", prov.getTheaterArea() + "");// 剧场区域
                                productReleaseMapJson.put("screenings", prov.getTheatercnum() + "");// 剧场场次
                                String isCandidate = "0";//0   没有 后选座
                                /*是否有后选座 0没有  1有*/
                                productReleaseMapJson.put("isCandidate", isCandidate);
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
                                    productReleaseMapJson.put("proCategory", "3");
                                    productReleaseMapJson.put("subProCategory", flag == true ? "2" : "1");
                                    if (salesType.equals("4")) {//目前是导游登录账号有后选座
                                        if (flag == true) {
                                            isCandidate = "1";//1   有 后选座
                                        }
                                        productReleaseMapJson.put("isCandidate", isCandidate);
                                    }
                                } else {
                                    String proCate = "";
                                    if (null != prov.getProCategory() && ProductCategory.scenic().equals(prov.getProCategory())) {
                                        proCate = ProductCategory.scenic();
                                        proCate = "2";
                                    } else {
                                        proCate = ProductCategory.normal();
                                    }
                                    productReleaseMapJson.put("proCategory", proCate);
                                }
                                jsonArray.add(productReleaseMapJson);
                            }
                        }
                    }
                }
            }
            jsonProductArray = regroupProductForQuerySenceDetail(jsonArray);
        } catch (Exception e) {
            e.printStackTrace();
            json.setCode(CodeHandle.FAILURE.getCode() + "");
            json.setMessage(CodeHandle.FAILURE.getMessage());
            json.setResponseBody(jsonObject);
            return json;
        }
        jsonObject.put("products", jsonProductArray);
        json.setCode(CodeHandle.SUCCESS.getCode() + "");
        json.setMessage(CodeHandle.SUCCESS.getMessage());
        json.setResponseBody(jsonObject);
        return json;
    }

    /**
     * 重组产品集合
     */
    private List<JsonProduct> regroupProductForQuerySenceDetail(List<Map<String, Object>> jsonArray) {
        Map<String, JsonProduct> productsMap = Maps.newHashMap();
        if (jsonArray != null && jsonArray.size() > 0) {
            ProductVO productVO = null;
            for (Map<String, Object> productObjectJson : jsonArray) {
                JsonProduct jsonProduct = null;
                if (!productsMap.containsKey(productObjectJson.get("productInfoId").toString())) {//不存在
                    jsonProduct = new JsonProduct();
                    productVO = (ProductVO) productObjectJson.get("productVO");
                    jsonProduct.setProductInfoId(String.valueOf(productVO.getId()));
                    jsonProduct.setPlaytimeValue(String.valueOf(productVO.getPlaytimeValue()));
                    jsonProduct.setPlaytimeUnit(String.valueOf(productVO.getPlaytimeUnit()));
                    jsonProduct.setPlaytimeMode(String.valueOf(productVO.getPlaytimeMode()));

                    jsonProduct.setRemarks(productVO.getReeaseInfo());
                    jsonProduct.setStartTime(productVO.getStartDate() != null ? DateUtils.formatDate(productVO.getStartDate(), "MM-dd") : "");
                    jsonProduct.setEndTime(productVO.getEndDate() != null ? DateUtils.formatDate(productVO.getEndDate(), "MM-dd") : "");
                    jsonProduct.setHourNode(String.valueOf(productVO.getHour()));
                    jsonProduct.setMinutesNode(String.valueOf(productVO.getMinutes()));
                    jsonProduct.setProductInfoName(productVO.getPopularName());
                    jsonProduct.setGainType(String.valueOf(productVO.getGainType()));
                    if (productObjectJson.get("isOunterSign").equals("1")) {
                        jsonProduct.setIsOunterSign("1");
                    } else {
                        jsonProduct.setIsOunterSign("0");
                    }
                    /*产品组中产品最低价*/
                    jsonProduct.setMinPrice(String.valueOf(productObjectJson.get("price")));
                    jsonProduct.setMinMarketPrice(String.valueOf(productObjectJson.get("marketPrice")));
                } else {
                    jsonProduct = productsMap.get(productObjectJson.get("productInfoId").toString());
                    /*产品组中产品最低价*/
                    if (Double.parseDouble(String.valueOf(productObjectJson.get("price"))) < Double.parseDouble(jsonProduct.getMinPrice())) {
                        jsonProduct.setMinPrice(String.valueOf(productObjectJson.get("price")));
                        jsonProduct.setMinMarketPrice(String.valueOf(productObjectJson.get("marketPrice")));
                    }
                }
                productObjectJson.remove("productVO");
                jsonProduct.getProductReleases().add(productObjectJson);
                productsMap.put(jsonProduct.getProductInfoId(), jsonProduct);
            }
        }
        /*map转成list返回*/
        List<JsonProduct> jsonProductList = new ArrayList<>();
        Iterator<String> it = productsMap.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next().toString();
            jsonProductList.add(productsMap.get(key));
        }
        return jsonProductList;
    }

}
