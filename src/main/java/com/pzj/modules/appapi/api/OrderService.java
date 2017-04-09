package com.pzj.modules.appapi.api;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pzj.base.common.global.GlobalDict.ProductCategory;
import com.pzj.base.common.global.GlobalParam;
import com.pzj.base.common.global.UserGlobalDict;
import com.pzj.base.common.utils.PageList;
import com.pzj.base.common.utils.PageModel;
import com.pzj.base.entity.rebate.RebateOrderTicketData;
import com.pzj.base.entity.sale.Orders;
import com.pzj.base.entity.sale.Ticket;
import com.pzj.base.entity.sale.TicketOrderDetail;
import com.pzj.base.entity.show.Chart;
import com.pzj.channel.entity.ChannelVo;
import com.pzj.common.util.CheckUtils;
import com.pzj.common.util.DateUtils;
import com.pzj.customer.entity.Customer;
import com.pzj.framework.context.Result;
import com.pzj.modules.appapi.entity.CodeHandle;
import com.pzj.modules.appapi.entity.JsonEntity;
import com.pzj.platform.appapi.util.PropertyLoader;
import com.pzj.product.api.enterprise.vo.ScenicVO;
import com.pzj.product.api.product.service.ProductComposeService;
import com.pzj.product.api.product.vo.ProductComposeVO;
import com.pzj.product.api.product.vo.ProductPriceVO;
import com.pzj.regulation.entity.StrategySettlementRule;
import com.pzj.regulation.entity.StrategyVo;
import com.pzj.regulation.service.StrategySettlementRuleService;
import com.pzj.regulation.service.impl.StrategySettlementRuleServiceImpl;
import com.pzj.sale.api.order.service.OrderUtils;
import com.pzj.sale.api.order.vo.BuyProductParam;
import com.pzj.sale.api.order.vo.OrdersVo;
import com.pzj.sale.api.rebate.service.IRebateService;
import com.pzj.sale.api.show.service.SeatShowService;
import com.pzj.trade.order.model.OrderCancelModel;
import com.pzj.trade.payment.entity.PaymentResult;
import com.pzj.trade.payment.vo.PaymentVO;

/**
 * app关于订单的接口
 *
 * @author wangkai
 * @date 2015年11月6日 下午6:55:29
 */
@Component
public class OrderService extends BaseService {
    private static final Logger                                  logger = LoggerFactory.getLogger(OrderService.class);
    @Autowired
    private com.pzj.sale.api.order.service.ICreateOrderService   iCreateOrderService;
    @Autowired
    private com.pzj.product.api.enterprise.service.ScenicService scenicService;
    @Resource(name = "propertyLoader")
    private PropertyLoader                                       propertyLoader;
    @Autowired
    private com.pzj.customer.service.CustomerService             customerService;
    @Autowired
    private com.pzj.sale.api.order.service.IQueryOrderService    queryOrderService;
    @Autowired
    private SeatShowService                                      showService;
    @Autowired
    private IRebateService                                       iRebateService;
    @Autowired
    private OrderUtils                                           orderUtils;
    @Autowired
    private ProductComposeService                                productComposeService;
    @Autowired
    private com.pzj.trade.order.service.CancelService            cancelService;
    @Autowired
    private com.pzj.trade.payment.service.PaymentService         commonPaymentService;

    /**
     * 保存订单(导游下单时取旅行社渠道)
     *
     * @param jsonObject
     *            json参数包
     * @param customer
     *            当前登录用户
     * @param json
     *            返回结果包装
     * @key startTime 游玩时间
     * @key cPaidAmount 支付金额
     * @key groupin 是否团进团出
     * @key ticketData 游客信息集合
     * @return JsonEntity 返回类型
     */
    public JsonEntity saveOrder(JSONObject jsonObject, Customer customer, JsonEntity json) {
        OrdersVo ordersVo = new OrdersVo();
        List<BuyProductParam> list = new ArrayList<>();
        String cPaidAmount = jsonObject.containsKey("cPaidAmount") ? jsonObject.getString("cPaidAmount") : "";
        String scienicId = jsonObject.containsKey("scienicId") ? jsonObject.getString("scienicId") : "";
        Long secResellerId = jsonObject.containsKey("secResellerId") ? jsonObject.getLong("secResellerId") : 0L;
        Long resellerId = jsonObject.containsKey("resellerId") ? jsonObject.getLong("resellerId") : 0L;
        Integer groupin = jsonObject.containsKey("groupin") ? Integer.parseInt(jsonObject.getString("groupin")) : 0;
        String book_name = jsonObject.containsKey("bookName") ? jsonObject.getString("bookName") : "";
        String book_mobile = jsonObject.containsKey("bookMobile") ? jsonObject.getString("bookMobile") : "";
        String book_card = jsonObject.containsKey("bookCard") ? jsonObject.getString("bookCard") : "";
        double guideOrderAmount = 0d;
        Map<Long, Long> pidAndSids = Maps.newHashMap();
        Map<Long, String> productChannelMap = new HashMap<>();
        try {
            ordersVo.setGroupin(groupin);
            ordersVo.setOrderType(1);
            ordersVo.setCreateType(1);
            ordersVo.setOperatorId(customer.getId());
            ordersVo.setBookContactName(book_name);
            ordersVo.setBookContactPhone(book_mobile);
            ordersVo.setBookBuyerId(book_card);
            Long channelId = null;
            String isGroup = "1";
            Integer salePort = null;
            HashMap<Long, StrategyVo> strategyMap = new HashMap<>();
            Customer ctu = new Customer();
            if (customer.getResellerType().equals(UserGlobalDict.guideUserType())) {
                if (resellerId == 0) {
                    logger.info("customer.getResellerType():" + customer.getResellerType());
                    json.setCode(CodeHandle.CODE_90001.getCode() + "");
                    json.setMessage("导游购票时必须选择旅行社！");
                    return json;
                }
                isGroup = String.valueOf(UserGlobalDict.StrategyGlobalDict.groupTicketVarie());
                salePort = UserGlobalDict.StrategyGlobalDict.windowGuideApp();
            } else {
                isGroup = String.valueOf(UserGlobalDict.StrategyGlobalDict.scatterTicketVarie());
                salePort = UserGlobalDict.StrategyGlobalDict.windowTenantApp();
            }
            if (resellerId > 0) {
                ctu.setId(resellerId);
            } else {
                ctu.setId(customer.getId());
            }
            if (customer.getResellerType().equals(UserGlobalDict.guideUserType())) {
                ordersVo.setChannelId(channelId);
                ordersVo.setGuideId(customer.getId());
                ordersVo.setGuideName(customer.getName());
                ordersVo.setGuidePhone(customer.getOperChargerMobile());
                ordersVo.setGuideCardId(customer.getCorporaterCredentials());
                ordersVo.setResellerId(resellerId);
                ordersVo.setFromType(55);
                if (secResellerId != 0) {
                    ordersVo.setSecResellerId(secResellerId);
                }
                ordersVo.setSalePort(salePort);

            } else {
                ordersVo.setResellerId(customer.getId());
                ordersVo.setFromType(56);
                ordersVo.setPayerId(customer.getId());
                ordersVo.setSalePort(salePort);
            }
            ordersVo.setOperatorName(customer.getName());
            // 拼装下单具体参数
            JSONArray jsonArr = jsonObject.containsKey("ticketData") ? jsonObject.getJSONArray("ticketData") : new JSONArray();

            for (int i = 0; i < jsonArr.size(); i++) {
                BuyProductParam buyProductParam = new BuyProductParam();
                JSONObject json1 = jsonArr.getJSONObject(i);
                String buyName = json1.containsKey("buyName") ? json1.getString("buyName") : "";
                String buyCard = json1.containsKey("buyCard") ? json1.getString("buyCard") : "";
                String buyMobile = json1.containsKey("buyMobile") ? json1.getString("buyMobile") : "";
                String productId = json1.containsKey("productId") ? json1.getString("productId") : "";
                String supplierId = json1.containsKey("supplierId") ? json1.getString("supplierId") : "";
                String showTime = json1.containsKey("showTime") ? json1.getString("showTime") : "";
                String buyDate = json1.containsKey("buyDate") ? json1.getString("buyDate") : "";
                String seat = json1.containsKey("seat") ? json1.getString("seat") : "";
                String matchs = json1.containsKey("matchs") ? json1.getString("matchs") : "";
                String area = json1.containsKey("area") ? json1.getString("area") : "";
                String num = json1.containsKey("num") ? json1.getString("num") : "1";
                if (CheckUtils.isNull(supplierId) || CheckUtils.isNull(productId)) {
                    logger.info("supplierId&&productId 为空");
                    json.setCode(CodeHandle.CODE_90001.getCode() + "");
                    json.setMessage(CodeHandle.CODE_90001.getMessage());
                    return json;
                }
                pidAndSids.put(Long.parseLong(productId), Long.parseLong(supplierId));
                List<StrategyVo> list1 = orderUtils.findStrategyByScenic(ctu.getId(), Long.parseLong(scienicId), Long.parseLong(supplierId), isGroup, salePort);
                if (list1 != null && list1.size() > 0) {
                    for (StrategyVo strategyVo2 : list1) {
                        List<Long> productIds = strategyVo2.getProductList();
                        if (null != productId && null != productIds && productIds.get(0).longValue() == Long.parseLong(productId)) {
                            ChannelVo channelVo = strategyVo2.getChannelList().get(0);
                            channelId = channelVo.getId();
                            productChannelMap.put(productIds.get(0), supplierId + "," + channelId);
                        }
                    }
                }
                buyProductParam.setBuyerId(buyCard);
                buyProductParam.setBuyerMobile(buyMobile);
                buyProductParam.setBuyerName(buyName);
                buyProductParam.setNum(Integer.parseInt(num));
                buyProductParam.setProductId(Long.parseLong(productId));
                buyProductParam.setOrderTicketType(1);
                buyProductParam.setBuyDate(DateUtils.parseDate(buyDate));

                // 设置座位演艺
                if (CheckUtils.isNotNull(showTime)) {
                    Chart chart = showService.queryChartByScienic(Long.parseLong(scienicId), Integer.parseInt(matchs), area);
                    buyProductParam.setPlayId(chart.getId());
                    buyProductParam.setShowTime(DateUtils.parseDate(showTime));
                    buyProductParam.setShowSceneId(Long.parseLong(scienicId));
                    buyProductParam.setShowScreening(Integer.parseInt(matchs));
                }
                if (CheckUtils.isNotNull(seat)) {
                    buyProductParam.setSeatNumber(seat);
                    buyProductParam.setSeatArea(area);
                }
                list.add(buyProductParam);
                ordersVo.setSupplierId(Long.valueOf(supplierId));
                if (customer.getResellerType().equals(UserGlobalDict.guideUserType())) {
                    StrategyVo strategyVo = strategyMap.get(Long.valueOf(productId));
                    if (strategyVo != null) {
                        double payAmount = 0d;
                        if (null != strategyVo.getRebateStrategyList() && strategyVo.getRebateStrategyList().size() > 0) {
                            payAmount = strategyVo.getPrice();
                        } else {
                            payAmount = strategyVo.getSettlementPrice() == null ? 0 : strategyVo.getSettlementPrice();
                        }
                        guideOrderAmount += payAmount * Integer.parseInt(num);
                        cPaidAmount = guideOrderAmount + "";
                    }
                }
            }
            ordersVo.setOrdersPrice(Double.parseDouble(cPaidAmount));
            ordersVo.setPidAndSids(pidAndSids);
            ordersVo.setProductChannelMap(productChannelMap);
            Orders orders = iCreateOrderService.createOrder(ordersVo, list);
            if (orders != null) {
                json.setCode(CodeHandle.SUCCESS.getCode() + "");
                json.setMessage(CodeHandle.SUCCESS.getMessage());
                Map<String, Object> object = Maps.newHashMap();
                DecimalFormat format = new DecimalFormat("0.00");
                object.put("orderId", orders.getOrderId());
                object.put("cPaidAmount", format.format(orders.getOrderAmount()));
                json.setResponseBody(object);
                return json;
            } else {
                logger.info("iCreateOrderService.createOrder：创建订单失败");
                json.setCode(CodeHandle.ERROR10033.getCode() + "");
                json.setMessage(CodeHandle.ERROR10033.getMessage());
                return json;
            }
        } catch (Exception e) {
            e.printStackTrace();
            json.setCode(CodeHandle.ERROR10033.getCode() + "");
            json.setMessage(CodeHandle.ERROR10033.getMessage());
        }
        return json;
    }

    /**
     * 查询订单列表
     *
     * @param data
     *            json参数包
     * @param customer
     *            当前登录用户
     * @param json
     *            返回结果包装
     * @key currentPage
     * @key pageSize
     * @key orderState 状态集合 1,2,3
     * @key search 搜索条件:游客姓名、身份证
     * @return JsonEntity 返回类型
     * @throws
     */
    @SuppressWarnings("unused")
    public JsonEntity queryOrders(JSONObject data, Customer customer, JsonEntity json) {
        String currentPage = data.containsKey("currentPage") ? data.getString("currentPage") : "1";
        String pageSize = data.containsKey("pageSize") ? data.getString("pageSize") : "10";
        String orderState = data.containsKey("orderState") ? data.getString("orderState") : "";
        if (orderState.equals("")) {
            //			String string = propertyLoader.getProperty("system", "orderStatus",
            //					"");
            //			String[] split = string.split(";");
            //			for (String string2 : split) {
            //				String[] split2 = string2.split(":");
            //				orderState = orderState + split2[0] + ",";
            //			}
        }
        Map<String, Object> params = new HashMap<>();
        List<Map<String, Object>> jsonArray = Lists.newArrayList();
        try {
            PageModel page = new PageModel(Integer.parseInt(currentPage), Integer.parseInt(pageSize), null);
            params.put("pParam", page);
            params.put("orderState", orderState);
            params.put("resellerIds", customer.getId());
            params.put("is_online", "1");
            PageList<Map<String, Object>> list = queryOrderService.queryOrderList(params);

            List<Map<String, Object>> resultList = list.getResultList();
            // boolean guide = true;
            for (Map<String, Object> orders : resultList) {
                Map<String, Object> order = queryOrderService.queryOrderById(orders.get("order_id").toString());
                Map<String, Object> object = Maps.newHashMap();
                object.put("createTime", DateUtils.toStringTime(orders.get("buyTicketTime")));
                object.put("orderId", orders.get("order_id"));
                object.put("orderState", orders.get("orderState"));
                object.put("paidAmount", orders.get("sumAmount"));
                object.put("refundAmount", orders.get("refundMoney"));
                object.put("total_num", orders.get("count"));
                object.put("sceneName", orders.get("scene_name"));
                object.put("combined_order_id", orders.get("combined_order_id"));
                String sec = orders.containsKey("sec_reseller_id") ? orders.get("sec_reseller_id") + "" : "";
                object.put("secResellerId", sec);
                if (CheckUtils.isNotNull(sec)) {
                    Customer customer1 = new Customer();
                    customer1.setId(Long.parseLong(sec));
                    List<Customer> list2 = customerService.findCustomerByParams(customer1);
                    object.put("secResellerName", list2.get(0).getName());
                }
                String rec = orders.containsKey("reseller_id") ? orders.get("reseller_id") + "" : "";
                object.put("resellerId", rec);
                if (CheckUtils.isNotNull(rec)) {
                    Customer customer1 = new Customer();
                    customer1.setId(Long.parseLong(rec));
                    List<Customer> list2 = customerService.findCustomerByParams(customer1);
                    object.put("resellerName", list2.get(0).getName());
                }
                JSONArray tickets = new JSONArray();
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> ticketList = (List<Map<String, Object>>) order.get("ticketList");
                double bmAmount = orders.get("rebate_bm_amount") == null ? 0 : Double.parseDouble(orders.get("rebate_bm_amount") + "");
                double payerAmount = orders.get("rebate_payer_amount") == null ? 0 : Double.parseDouble(orders.get("rebate_payer_amount") + "");
                double dyAmount = orders.get("rebate_dy_amount") == null ? 0 : Double.parseDouble(orders.get("rebate_dy_amount") + "");
                double lxsAmount = orders.get("rebate_lxs_amount") == null ? 0 : Double.parseDouble(orders.get("rebate_lxs_amount") + "");
                List<RebateOrderTicketData> orderTicketDataList = iRebateService.getRebateOrderTicketDataListByOrderId(orders.get("order_id").toString());
                for (Map<String, Object> map : ticketList) {
                    Map<String, Object> ticket = Maps.newHashMap();
                    TicketOrderDetail ticketOrderDetail = queryOrderService.selectTicketDetailByOrderIdAndTicketId(map.get("ticket_id").toString(),
                        orders.get("order_id").toString());
                    if (UserGlobalDict.dataSourceGuide().equals(customer.getUserSource())) {
                        ticket.put("rebateAmount", dyAmount);
                    } else if (UserGlobalDict.dataSourceBusiness().equals(customer.getUserSource())) {
                        ticket.put("rebateAmount", payerAmount);
                    } else if (UserGlobalDict.dataSourceTravel().equals(customer.getUserSource())) {
                        ticket.put("rebateAmount", lxsAmount);
                    } else if (UserGlobalDict.dataSourceDept().equals(customer.getUserSource())) {
                        ticket.put("rebateAmount", bmAmount);
                    } else if (UserGlobalDict.dataSourceSingle().equals(customer.getUserSource())) {
                        ticket.put("rebateAmount", payerAmount);
                    }

                    //查询联票里边的每张子票的 未满检金额
                    Map<String, Object> object1 = Maps.newHashMap();
                    ProductComposeVO productComposeVo = new ProductComposeVO();
                    ProductPriceVO pricevo = null;
                    long productId = map.get("product_id") == null ? 0 : Long.parseLong(map.get("product_id") + "");
                    productComposeVo.setId(productId);
                    productComposeVo.setNeedSubScenciList(true);
                    productComposeVo.setFindScenicSpot(GlobalParam.ONE);
                    pricevo = productComposeService.queryComposeById(productComposeVo);
                    pricevo.setNeedSubScenciList(true);
                    List<Long> queryStartegyIdList = new ArrayList<>();
                    List<Long> queryParentProductIdList = new ArrayList<>(), queryProductIdList = new ArrayList<>();
                    StrategySettlementRule vo = new StrategySettlementRule();
                    if (null != pricevo && null != pricevo.getProductList() && pricevo.getProductList().size() > 0) {
                        queryParentProductIdList.add(pricevo.getProductId());
                        queryStartegyIdList.add(ticketOrderDetail.getStrategyId());
                        vo.setQueryParentProductIdList(queryProductIdList);
                        vo.setQueryStartegyIdList(queryStartegyIdList);
                        StrategySettlementRuleService strategySettlementRuleService = new StrategySettlementRuleServiceImpl();
                        List<StrategySettlementRule> strategySettRuleList = strategySettlementRuleService.queryByParamMap(vo);
                        if (null != strategySettRuleList && strategySettRuleList.size() > 0) {
                            for (StrategySettlementRule ssr : strategySettRuleList) {
                                if (ssr.getProductId().longValue() != productId) {
                                    double sonReduceMoney = ssr.getReduceSettlementMoney();
                                    long sonProductId = ssr.getProductId();
                                    ProductComposeVO pcvo = new ProductComposeVO();
                                    pcvo.setId(sonProductId);
                                    ProductPriceVO ppvo = productComposeService.queryComposeById(pcvo);
                                    object1.put(ppvo.getName(), ppvo.getReduceSettlementMoney());

                                }

                            }
                        }
                    }
                    //ticket.put("ParentTickets", ticketOrderDetail.getReduceAmount());
                    boolean falg = false;
                    String perform = "0";
                    ticket.put("perform", perform);
                    String sceneId = "0";
                    //判断产品类型   如果是演艺票或者联合带有演艺票的  就可以去选选座
                    if (null != pricevo) {
                        if (null != pricevo.getProductList() && pricevo.getProCategory().equals("11")) {
                            for (int i = 0; i < pricevo.getProductList().size(); i++) {
                                //ticket.put(pricevo.getProductList().get(i).getName(),pricevo.getProductList().get(i).getReduceSettlementMoney());
                                if (ProductCategory.performingPack().equals(pricevo.getProductList().get(i).getProCategory())
                                    && pricevo.getProductList().get(i).getProCategory() != null) {
                                    sceneId = String.valueOf(pricevo.getProductList().get(i).getScenicList().get(0).getId());
                                    falg = true;
                                    perform = "1";
                                    ticket.put("perform", perform);
                                    ticket.put("showScreening", pricevo.getProductList().get(i).getTheatercnum());//如果子产品为演艺票，场次就从子票取值
                                    ticket.put("seatSrea", pricevo.getProductList().get(i).getTheaterArea());//如果子产品为演艺票，区域就从子票取值
                                    ticket.put("secenid", sceneId);//如果子产品为演艺票，景区id就从子票取值
                                    break;
                                }
                            }
                        }
                    } else {
                        ticket.put("showScreening", map.get("show_screening"));//如果产品不为联票，场次就在票里边取值
                        ticket.put("seatSrea", map.get("seat_area"));////如果产品不为联票，区域就在票里边取值
                    }
                    ticket.put("thumnail", pricevo.getProduct().getThumbnail() == null ? "" : pricevo.getProduct().getThumbnail());
                    ticket.put("childTicket", object1);
                    ticket.put("ticketId", map.get("ticket_id"));
                    ticket.put("status", map.get("ticket_state"));
                    ticket.put("ticketNum", map.get("ticket_num"));
                    ticket.put("productName", map.get("product_name"));
                    ticket.put("productId", String.valueOf(map.get("product_id")));
                    ticket.put("ticketCategory", String.valueOf(map.get("ticket_category")));
                    ticket.put("group", order.get("groupin"));
                    ticket.put("confirmTime", DateUtils.toStringTime(map.get("confirm_time")));
                    ticket.put("refundTime", DateUtils.toStringTime(map.get("refund_time")));
                    if (null != ticketOrderDetail) {
                        ticket.put("ticketPrice", ticketOrderDetail.getTicketPrice());
                        ticket.put("refundAmount", ticketOrderDetail.getRefundAmount());
                    } else {
                        ticket.put("ticketPrice", 0);
                        ticket.put("refundAmount", 0);
                    }
                    ticket.put("buyerId", map.get("buyer_id"));
                    ticket.put("buyerMobile", map.get("buyer_mobile"));
                    ticket.put("buyerName", map.get("buyer_name"));
                    ticket.put("startTime", map.get("start_time"));
                    ticket.put("expTime", map.get("exp_time"));
                    ticket.put("playTime", map.get("play_time"));
                    // ticket.put("rebateAmount", map.get("rebateAmount"));
                    ticket.put("startTime", DateUtils.toStringTime(map.get("start_time")));
                    ticket.put("expTime", DateUtils.toStringTime(map.get("exp_time")));
                    ticket.put("showStartTime", DateUtils.toStringTime(map.get("show_start_time")));
                    ticket.put("showEndTime", DateUtils.toStringTime(map.get("show_end_time")));
                    ticket.put("seatNumber", map.get("seat_number"));
                    //					ticket.put("showScreening", map.get("show_screening"));
                    //					ticket.put("seatSrea", map.get("seat_area"));
                    ticket.put("sceneName", map.get("scene_name"));
                    Long supplierId = (Long) order.get("supplier_id");
                    Integer total = map.get("ticket_num") == null ? 0 : (Integer) map.get("ticket_num");//票总数
                    Integer refundNum = map.get("refund_num") == null ? 0 : (Integer) map.get("refund_num");//已经退票数
                    Integer checkNum = map.get("check_num") == null ? 0 : (Integer) map.get("check_num");//已经检票数
                    ticket.put("checkNum", checkNum);//已经检票数
                    ticket.put("refundNum", refundNum);////已经退票数
                    if (total != null && refundNum != null && checkNum != null) {
                        Integer OkNum = total - refundNum - checkNum;
                        ticket.put("refund", OkNum);
                    }
                    String scene_id = (String) map.get("scene_id");//获取景区id，以逗号分隔，取第一个
                    String[] scenes = scene_id.split(",");
                    String scienicId = scenes[0];
                    Integer showscreening = (Integer) (map.get("show_screening") == null ? 0 : map.get("show_screening"));
                    String seatarea = (String) (map.get("seat_area") == null ? "0" : (map.get("seat_area")));//获取座位区域

                    Chart chart = showService.queryChartByScienic(scienicId == null ? 0 : Long.parseLong(scienicId), showscreening, seatarea);
                    if (chart != null) {
                        ticket.put("areaDesc", chart.getAreaDesc());
                    }
                    Integer isonevote = (Integer) (orders.get("is_one_vote") == null ? 0 : orders.get("is_one_vote"));//获取标示：一证一票和非一证一票的标示
                    if (isonevote != null && isonevote == 2) {//是非一证一票，isUnique=0
                        ticket.put("isUnique", "0");
                        ticket.put("qr_code", map.get("qr_code"));
                    } else {
                        ticket.put("isUnique", "1");//是一证一票，isUnique=1
                    }
                    tickets.add(ticket);
                }
                object.put("tickets", tickets);
                jsonArray.add(object);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Map<String, Object> object = Maps.newHashMap();
        object.put("currentPage", currentPage);
        object.put("pageSize", pageSize);
        object.put("list", jsonArray);
        json.setCode(CodeHandle.SUCCESS.getCode() + "");
        json.setMessage(CodeHandle.SUCCESS.getMessage());
        json.setResponseBody(object);
        return json;
    }

    /**
     * 查询订单详情
     *
     * @param data
     *            json参数包
     * @param customer
     *            当前登录用户
     * @param json
     *            返回结果包装
     * @key orderId 订单号
     * @return JsonEntity 返回类型
     * @throws
     */
    public JsonEntity queryOrder(JSONObject data, Customer customer, JsonEntity json) {
        String orderId = data.containsKey("orderId") ? data.getString("orderId") : "";
        if (orderId.equals("")) {
            json.setCode(CodeHandle.CODE_90001.getCode() + "");
            json.setMessage(CodeHandle.CODE_90001.getMessage() + "orderId为空");
            return json;
        }
        // Orders orders = ordersService.findOrderById(orderId);
        Map<String, Object> orders = queryOrderService.queryOrderById(orderId);
        Map<String, Object> jsonObject = Maps.newHashMap();
        jsonObject.put("createTime", orders.get("buyTicketTime"));
        jsonObject.put("cPaidAmount", orders.get("order_amount"));
        jsonObject.put("orderAmount", orders.get("order_amount"));
        jsonObject.put("orderID", orders.get("order_id"));
        jsonObject.put("total_num", orders.get("total_num"));
        jsonObject.put("sceneName", orders.get("scene_name"));
        jsonObject.put("orderState", orders.get("orderState"));
        jsonObject.put("backmoney", orders.get("refundMoney"));
        jsonObject.put("backamount", orders.get("refund_num") == null ? "0" : orders.get("refund_num"));
        // List<Ticket> list = ticketservice.selectTicketByOrderid(orderId);
        List<Ticket> list = new ArrayList<>();
        HashMap<Long, JSONArray> map = new HashMap<>();
        JSONArray jsonArray1 = new JSONArray();
        boolean i = true;
        Integer isGroup = 0;
        for (Ticket ticket : list) {
            Long productId = ticket.getProductId();
            isGroup = ticket.getIsGroup();
            JSONArray jsonArray = null;
            int isUnique = 1;
            if (isUnique != 1) {
                i = false;
                Map<String, Object> object = Maps.newHashMap();
                object.put("ticketId", ticket.getTicketId());
                object.put("status", ticket.getTicketState());
                object.put("productName", ticket.getProductName());
                object.put("count", ticket.getTicketNum());
                object.put("retailPrice", ticket.getTicketPrice());
                object.put("refundAmount", ticket.getRefundAmount());
                if (!jsonObject.containsKey("startTime"))
                    jsonObject.put("startTime", DateUtils.formatDateTime(ticket.getStartTime()));
                jsonObject.put("expTime", DateUtils.formatDateTime(ticket.getExpTime()));
                Date showStartTime = ticket.getShowStartTime();
                if (showStartTime != null) {
                    object.put("showStartTime", DateUtils.formatDateTime(showStartTime));
                    object.put("showEndTime", DateUtils.formatDateTime(ticket.getShowEndTime()));
                    object.put("seat", ticket.getSeatNumber());
                }
                jsonArray1.add(object);
            } else {
                if (map.containsKey(productId)) {
                    jsonArray = map.get(productId);
                } else {
                    jsonArray = new JSONArray();
                    map.put(productId, jsonArray);
                }
                Map<String, Object> object = Maps.newHashMap();
                object.put("buyerICard", ticket.getBuyerId());
                object.put("buyerMobile", ticket.getBuyerMobile());
                object.put("buyerName", ticket.getBuyerName());
                object.put("seat", ticket.getSeatNumber());
                object.put("ticketId", ticket.getTicketId());
                object.put("status", ticket.getTicketState());
                object.put("productName", ticket.getProductName());
                object.put("retailPrice", ticket.getTicketPrice());
                if (!jsonObject.containsKey("startTime"))
                    jsonObject.put("startTime", DateUtils.formatDateTime(ticket.getStartTime()));
                jsonObject.put("expTime", DateUtils.formatDateTime(ticket.getExpTime()));

                Date showStartTime = ticket.getShowStartTime();
                if (showStartTime != null) {
                    object.put("showStartTime", DateUtils.formatDateTime(showStartTime));
                    object.put("showEndTime", DateUtils.formatDateTime(ticket.getShowEndTime()));

                    if (new Date().getTime() >= showStartTime.getTime()) {
                        object.put("status", "6");
                    }
                }
                jsonArray.add(object);
            }

        }
        if (i) {
            for (Entry<Long, JSONArray> entry : map.entrySet()) {
                Long key = entry.getKey();
                JSONArray value = entry.getValue();
                Map<String, Object> object = Maps.newHashMap();
                object.put("productName", value.getJSONObject(0).getString("productName"));
                object.put("productId", key + "");
                if (value.getJSONObject(0).containsKey("showStartTime")) {
                    object.put("showStartTime", value.getJSONObject(0).getString("showStartTime"));
                }
                object.put("count", value.size());
                object.put("retailPrice", value.getJSONObject(0).getString("retailPrice"));
                object.put("data", value);
                jsonArray1.add(object);
            }
            jsonObject.put("isUnique", "1");
        } else {
            jsonObject.put("isUnique", "0");
        }
        // if (!jsonObject.containsKey("refundTicketType") && product != null) {
        // jsonObject.put("refundTicketType", product.getRefundTicketType());
        // }
        try {
            ScenicVO scenicVO = scenicService.getById(Long.parseLong(orderId));
            jsonObject.put("sceneAddress", scenicVO.getAddress());
            jsonObject.put("provice", scenicVO.getProvince());
            jsonObject.put("city", scenicVO.getCity());
            jsonObject.put("county", scenicVO.getCounty());
        } catch (Exception e) {
            e.printStackTrace();
        }
        jsonObject.put("isGroup", isGroup);
        jsonObject.put("list", jsonArray1);
        json.setCode(CodeHandle.SUCCESS.getCode() + "");
        json.setMessage(CodeHandle.SUCCESS.getMessage());
        json.setResponseBody(jsonObject);
        return json;
    }

    /**
     * 退掉已支付的整单
     *
     * @param data
     * @param customer
     * @param json
     * @return JsonEntity 返回类型
     * @throws
     */
    public JsonEntity cancelOrder(JSONObject data, Customer customer, JsonEntity json) {

        return null;
    }

    public JsonEntity confirmOrder(JSONObject data, Customer customer, JsonEntity json) {
        String orderId = data.containsKey("orderId") ? data.getString("orderId") : "";
        if (orderId.equals("")) {
            json.setCode(CodeHandle.CODE_90001.getCode() + "");
            json.setMessage(CodeHandle.CODE_90001.getMessage() + "orderId为空");
            return json;
        }
        Integer integer = 0;
        String[] split = orderId.split(",");
        for (String string : split) {
            Map<String, Object> params = Maps.newHashMap();
            params.put("sureOrNot", 1);
            params.put("order_id", string);
            integer += queryOrderService.updateOrder(params);
        }
        if (integer != null && integer != 0) {
            json.setCode(CodeHandle.SUCCESS.getCode());
            json.setMessage(CodeHandle.SUCCESS.getMessage());
            return json;
        } else {
            json.setCode("-1");
            json.setMessage("失败");
            return json;
        }
    }

    public JsonEntity payAnother(JSONObject data, Customer customer, JsonEntity json) {
        String orderId = data.containsKey("orderId") ? data.getString("orderId") : "";
        String payerId = data.containsKey("payerId") ? data.getString("payerId") : "";
        if (orderId.equals("")) {
            json.setCode(CodeHandle.CODE_90001.getCode() + "");
            json.setMessage(CodeHandle.CODE_90001.getMessage() + "orderId为空");
            return json;
        }
        if (!customer.getResellerType().equals(UserGlobalDict.guideUserType())) {
            json.setCode(CodeHandle.CODE_90001.getCode() + "");
            json.setMessage("非导游不支持代付");
            return json;
        }
        Orders order = new Orders();
        order.setOrderId(orderId);
        order.setPayerId(Long.parseLong(payerId));
        Integer integer = queryOrderService.updateByPrimaryKeySelective(order);
        if (integer != null && integer != 0) {
            json.setCode(CodeHandle.SUCCESS.getCode());
            json.setMessage(CodeHandle.SUCCESS.getMessage());
            return json;
        } else {
            json.setCode("-1");
            json.setMessage("失败");
            return json;
        }
    }

    /**
     * 后选座保存订单(导游下单时取旅行社渠道)
     *
     * @param data
     *            json参数包
     * @param customer
     *            当前登录用户
     * @param json
     *            返回结果包装
     * @key startTime 游玩时间
     * @key cPaidAmount 支付金额
     * @key groupin 是否团进团出
     * @key ticketData 游客信息集合
     * @return JsonEntity 返回类型
     */
    public JsonEntity lastSaveOrder(JSONObject jsonObject, Customer customer, JsonEntity json) {
        OrdersVo ordersVo = new OrdersVo();
        List<BuyProductParam> list = new ArrayList<>();
        String scienicId = jsonObject.containsKey("scienicId") ? jsonObject.getString("scienicId") : "";
        String orderId = jsonObject.containsKey("orderId") ? jsonObject.getString("orderId") : "";
        Map<Long, Long> pidAndSids = Maps.newHashMap();
        Map<Long, String> productChannelMap = new HashMap<>();
        try {
            ordersVo.setOrderType(1);
            ordersVo.setCreateType(1);
            ordersVo.setOperatorId(customer.getId());
            ordersVo.setOrderId(orderId);
            if (customer.getResellerType().equals(UserGlobalDict.guideUserType())) {
                ordersVo.setGuideId(customer.getId());
                ordersVo.setGuideName(customer.getName());
                ordersVo.setGuidePhone(customer.getOperChargerMobile());
                ordersVo.setGuideCardId(customer.getCorporaterCredentials());
                ordersVo.setFromType(55);
                ordersVo.setSalePort(OrderUtils.OrderSalePortType.TOUR_GUIDE_APP.getKey());
            } else {
                ordersVo.setResellerId(customer.getId());
                ordersVo.setFromType(56);
                ordersVo.setPayerId(customer.getId());
                ordersVo.setSalePort(OrderUtils.OrderSalePortType.MERCHANT_APP.getKey());
            }
            // 拼装下单具体参数
            JSONArray jsonArr = jsonObject.containsKey("ticketDate") ? jsonObject.getJSONArray("ticketDate") : new JSONArray();
            for (int i = 0; i < jsonArr.size(); i++) {

                BuyProductParam buyProductParam = new BuyProductParam();
                JSONObject json1 = jsonArr.getJSONObject(i);
                String buyName = json1.containsKey("buyName") ? json1.getString("buyName") : "";
                String buyCard = json1.containsKey("buyCard") ? json1.getString("buyCard") : "";
                String buyMobile = json1.containsKey("buyMobile") ? json1.getString("buyMobile") : "";
                String productId = json1.containsKey("productId") ? json1.getString("productId") : "";
                String showTime = json1.containsKey("showTime") ? json1.getString("showTime") : "";
                String buyDate = json1.containsKey("buyDate") ? json1.getString("buyDate") : "";
                String seat = json1.containsKey("seat") ? json1.getString("seat") : "";
                String matchs = json1.containsKey("matchs") ? json1.getString("matchs") : "";
                String area = json1.containsKey("area") ? json1.getString("area") : "";
                String num = json1.containsKey("num") ? json1.getString("num") : "1";
                String ticketid = json1.containsKey("ticketid") ? json1.getString("ticketid") : "";//票id
                buyProductParam.setBuyerId(buyCard);
                buyProductParam.setBuyerMobile(buyMobile);
                buyProductParam.setBuyerName(buyName);
                buyProductParam.setNum(Integer.parseInt(num));
                buyProductParam.setProductId(Long.parseLong(productId));
                buyProductParam.setOrderTicketType(1);
                buyProductParam.setBuyDate(DateUtils.parseDate(buyDate));
                buyProductParam.setTicketId(ticketid);
                // 设置座位演艺
                if (CheckUtils.isNotNull(showTime)) {
                    Chart chart = showService.queryChartByScienic(Long.parseLong(scienicId), Integer.parseInt(matchs), area);
                    buyProductParam.setPlayId(chart.getId());
                    buyProductParam.setShowTime(DateUtils.parseDate(showTime));
                    buyProductParam.setShowSceneId(Long.parseLong(scienicId));
                    buyProductParam.setShowScreening(Integer.parseInt(matchs));
                }
                if (CheckUtils.isNotNull(seat)) {
                    buyProductParam.setSeatNumber(seat);
                    buyProductParam.setSeatArea(area);
                }
                list.add(buyProductParam);
            }
            ordersVo.setBuyProductParam(list);
            ordersVo.setPidAndSids(pidAndSids);
            ordersVo.setProductChannelMap(productChannelMap);
            Orders orders = iCreateOrderService.updateOrder(ordersVo);
            if (orders != null) {
                json.setCode(CodeHandle.SUCCESS.getCode() + "");
                json.setMessage(CodeHandle.SUCCESS.getMessage());
                return json;
            } else {
                logger.info("iCreateOrderService.createOrder：创建订单失败");
                json.setCode(CodeHandle.CODE_90001.getCode() + "");
                json.setMessage(CodeHandle.CODE_90001.getMessage());
                return json;
            }
        } catch (Exception e) {
            e.printStackTrace();
            json.setCode(CodeHandle.CODE_90001.getCode() + "");
            json.setMessage(e.getMessage());
        }
        return json;
    }

    /**
     * 通用支付
     *
     * @param data
     *            json参数包
     * @param customer
     *            当前登录用户
     * @param json
     *            返回结果包装
     * @throws
     */
    public JsonEntity payOrderForCommon(JSONObject data, Customer customer, JsonEntity json) {
        String orderId = data.containsKey("orderId") ? data.getString("orderId") : "";//订单id
        String payType = data.containsKey("payType") ? data.getString("payType") : "0";//0:余额支付  1、支付宝  2、微信   3、异度（中信） 4、帐扣  5、电汇
        //请求来源
        String source = data.containsKey("source") ? data.getString("source") : "0"; //1
        String resellerId = data.containsKey("resellerId") ? data.getString("resellerId") : "0"; /*分销商id*/

        PaymentVO paymentVO = new PaymentVO();
        /*币种. 1: 人民币; 2: 美元*/
        paymentVO.setCurrency(1);
        paymentVO.setOrderId(orderId);

        paymentVO.setPayType(Integer.parseInt(payType));
        paymentVO.setResellerId(Long.parseLong(resellerId));
        /*请求来源. 1: html页面; 2: 移动应用*/
        paymentVO.setSource(Integer.parseInt(source));
        Result<PaymentResult> responseResult = commonPaymentService.payOrder(paymentVO, null);
        if (responseResult.getErrorCode() == new Result<>().getErrorCode()) {
            json.setCode(CodeHandle.SUCCESS.getCode() + "");
            json.setMessage(CodeHandle.SUCCESS.getMessage());
        } else {
            json.setCode(responseResult.getErrorCode() + "");
            json.setMessage(responseResult.getErrorMsg());
        }
        return json;
    }

    /**
     * 取消订单(通用的)
     *
     * @param data
     * @param customer
     * @param json
     * @return JsonEntity 返回类型
     * @throws
     */
    public JsonEntity cancelOrderForCommon(JSONObject data, Customer customer, JsonEntity json) {
        String orderId = data.containsKey("orderId") ? data.getString("orderId") : "";//订单id
        OrderCancelModel orderCancelModel = new OrderCancelModel();
        orderCancelModel.setOrderId(orderId);
        Result<Boolean> responseResult = cancelService.cancelOrder(orderCancelModel, null);
        if (responseResult.getErrorCode() == new Result<>().getErrorCode()) {
            json.setCode(CodeHandle.SUCCESS.getCode() + "");
            json.setMessage(CodeHandle.SUCCESS.getMessage());
        } else {
            json.setCode(responseResult.getErrorCode() + "");
            json.setMessage(responseResult.getErrorMsg());
        }
        return json;
    }

}
