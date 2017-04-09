package com.pzj.modules.appapi.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pzj.base.common.global.GlobalDict;
import com.pzj.base.common.global.UserGlobalDict;
import com.pzj.base.entity.sale.Orders;
import com.pzj.base.entity.sale.Ticket;
import com.pzj.common.params.AppapiParams;
import com.pzj.common.util.DateUtils;
import com.pzj.customer.entity.Customer;
import com.pzj.dict.entity.Dict;
import com.pzj.dict.service.DictService;
import com.pzj.framework.context.Result;
import com.pzj.modules.appapi.api.dto.RefundMoneyDto;
import com.pzj.modules.appapi.api.order.OrderCheckService;
import com.pzj.modules.appapi.entity.CodeHandle;
import com.pzj.modules.appapi.entity.JsonEntity;
import com.pzj.modules.appapi.exception.AppapiParametersException;
import com.pzj.platform.appapi.service.impl.CommonProductRefundServiceImpl;
import com.pzj.trade.calculate.service.CalculateService;
import com.pzj.trade.calculate.vo.RefundCalcVO;
import com.pzj.trade.calculate.vo.RefundPriceVO;
import com.pzj.trade.order.entity.MerchResponse;
import com.pzj.trade.order.entity.OrderDetailResponse;
import com.pzj.trade.order.entity.OrderResponse;
import com.pzj.trade.order.service.OrderQueryService;
import com.pzj.trade.order.vo.OrderDetailVO;
import com.pzj.trade.order.vo.RefundMerchandiseVO;
import com.pzj.trade.refund.service.RefundService;

/**
 * 操作票的信息
 *
 * @author wangkai
 * @date 2015年11月6日 下午8:43:58
 */
@Component
public class TicketService {
    private static final Logger                                logger = LoggerFactory.getLogger(TicketService.class);

    @Autowired
    private com.pzj.sale.api.order.service.ICreateOrderService iCreateOrderService;
    @Autowired
    private com.pzj.sale.api.order.service.OrdersRefundService ordersRefundService;
    @Autowired
    private com.pzj.sale.api.ticket.service.TicketService      ticketService;
    @Autowired
    private RefundService                                      refundOrderMoneyService;
    @Autowired
    private CommonProductRefundServiceImpl                     commonProductRefundService;
    @Autowired
    private DictService                                        dictService;
    @Autowired
    private OrderQueryService                                  orderQueryService;
    @Autowired
    private com.pzj.platform.appapi.service.ProductService     productService;
    @Autowired
    private OrderCheckService                                  orderCheckService;
    @Autowired
    private CalculateService                                   calculateService;

    /**
     * 在未支付的订单中添加一张票
     *
     * @param data
     * @param customer
     * @param json
     * @return JsonEntity 返回类型
     * @throws
     */
    public JsonEntity addTicket(JSONObject data, Customer customer, JsonEntity json) {
        String buyName = data.containsKey("buyName") ? data.getString("buyName") : "";
        String buyCard = data.containsKey("buyCard") ? data.getString("buyCard") : "";
        String buyMobile = data.containsKey("buyMobile") ? data.getString("buyMobile") : "";

        String ticketId = data.containsKey("ticketId") ? data.getString("ticketId") : "";
        String orderId = data.containsKey("orderId") ? data.getString("orderId") : "";
        String productId = data.containsKey("productId") ? data.getString("productId") : "";
        if (ticketId.equals("") || buyCard.equals("") || buyName.equals("")) {
            json.setCode(CodeHandle.CODE_90001.getCode());
            json.setMessage(CodeHandle.CODE_90001.getMessage());
            return json;
        }
        ArrayList<Ticket> tickets = new ArrayList<>();
        String[] buyNames = buyName.split(",");
        String[] buyCards = buyCard.split(",");
        String[] buyMobiles = buyMobile.split(",");
        if (buyNames.length != buyCards.length) {
            json.setCode(CodeHandle.CODE_90001.getCode());
            json.setMessage(CodeHandle.CODE_90001.getMessage());
            return json;
        }
        for (int i = 0; i < buyMobiles.length; i++) {
            Ticket ticket = new Ticket();
            ticket.setBuyerId(buyCards[i]);
            ticket.setBuyerMobile(buyMobiles[i]);
            ticket.setBuyerName(buyNames[i]);
            ticket.setOrderId(orderId);
            ticket.setProductId(Long.parseLong(productId));
            ticket.setTicketId(ticketId);
        }
        List<Ticket> list = iCreateOrderService.addTicket(tickets);
        if (list != null && list.size() > 0) {
            List<Map<String, Object>> jsonArray = Lists.newArrayList();
            for (Ticket t : list) {
                Map<String, Object> json1 = Maps.newHashMap();
                json1.put("ticketId", t.getTicketId());
                json1.put("ticketPrice", t.getTicketPrice());
                json1.put("seat", t.getSeatNumber());
                jsonArray.add(json1);
            }
            json.setCode(CodeHandle.SUCCESS.getCode() + "");
            json.setMessage(CodeHandle.SUCCESS.getMessage());
            json.setResponseBody(jsonArray);
        } else {
            json.setCode(CodeHandle.FAILURE.getCode() + "");
            json.setMessage(CodeHandle.FAILURE.getMessage());
        }
        return json;
    }

    /**
     * 在未支付的订单中减掉一张票
     *
     * @param data
     * @param customer
     * @param json
     * @return JsonEntity 返回类型
     * @throws
     */
    public JsonEntity minusTicket(JSONObject data, Customer customer, JsonEntity json) {
        String ticketIds = data.containsKey("ticketIds") ? data.getString("ticketIds") : "";
        String orderId = data.containsKey("orderId") ? data.getString("orderId") : "";
        if (ticketIds.equals("") || orderId.equals("")) {
            json.setCode(CodeHandle.CODE_90001.getCode());
            json.setMessage(CodeHandle.CODE_90001.getMessage());
            return json;
        }
        String[] split = ticketIds.split(",");
        Long[] ticketId = new Long[split.length];
        for (int i = 0; i < split.length; i++) {
            ticketId[i] = Long.parseLong(split[i]);
        }
        Orders i = iCreateOrderService.minusTicket(split);
        if (i != null) {
            json.setCode(CodeHandle.SUCCESS.getCode() + "");
            json.setMessage(CodeHandle.SUCCESS.getMessage());
        } else {
            json.setCode(CodeHandle.FAILURE.getCode() + "");
            json.setMessage(CodeHandle.FAILURE.getMessage());
        }
        return json;
    }

    /**
     * 更新票的信息，如：身份证、姓名、手机号
     *
     * @param data
     * @param customer
     * @param json
     * @return JsonEntity 返回类型
     * @throws
     */
    public JsonEntity updateTicket(JSONObject data, Customer customer, JsonEntity json) {
        String ticketIds = data.containsKey("ticketIds") ? data.getString("ticketIds") : "";
        String buyCard = data.containsKey("buyCard") ? data.getString("buyCard") : "";
        String buyName = data.containsKey("buyName") ? data.getString("buyName") : "";
        String buyMobile = data.containsKey("buyMobile") ? data.getString("buyMobile") : null;
        if (ticketIds.equals("") || buyCard.equals("") || buyName.equals("")) {
            json.setCode(CodeHandle.CODE_90001.getCode());
            json.setMessage(CodeHandle.CODE_90001.getMessage());
            return json;
        }
        String[] buyNames = buyName.split(",");
        String[] buyCards = buyCard.split(",");
        String[] buyMobiles = buyMobile.split(",");
        String[] ticketId = ticketIds.split(",");
        if (buyNames.length != buyCards.length) {
            json.setCode(CodeHandle.CODE_90001.getCode());
            json.setMessage(CodeHandle.CODE_90001.getMessage());
            return json;
        }
        int j = 0;
        for (int i = 0; i < buyMobiles.length; i++) {
            Map<String, Object> params = Maps.newHashMap();
            params.put("buyer_id", buyCards[i]);
            params.put("buyer_name", buyNames[i]);
            params.put("buyer_mobile", buyMobiles[i]);
            params.put("ticket_id", ticketId[i]);
            j = ticketService.updateTicketInfo(params);
        }
        if (j > 0) {
            json.setCode(CodeHandle.SUCCESS.getCode() + "");
            json.setMessage(CodeHandle.SUCCESS.getMessage());
        } else {
            json.setCode(CodeHandle.FAILURE.getCode() + "");
            json.setMessage(CodeHandle.FAILURE.getMessage());
        }
        return json;
    }

    /**
     * 更新非一证一票的票的游客数量
     *
     * @param data
     * @param customer
     * @param json
     * @return JsonEntity 返回类型
     * @throws
     */
    public JsonEntity editTicketNum(JSONObject data, Customer customer, JsonEntity json) {
        String ticketIds = data.containsKey("ticketIds") ? data.getString("ticketIds") : "";
        String nums = data.containsKey("nums") ? data.getString("nums") : "";
        if (ticketIds.equals("")) {
            json.setCode(CodeHandle.CODE_90001.getCode());
            json.setMessage(CodeHandle.CODE_90001.getMessage());
            return json;
        }
        String[] split = ticketIds.split(",");
        // Long[] ticketId = new Long[split.length];
        // for (int i = 0; i < split.length; i++) {
        // ticketId[i]=Long.parseLong(split[i]);
        // }
        String[] split1 = nums.split(",");
        int[] num = new int[split1.length];
        for (int i = 0; i < split1.length; i++) {
            num[i] = Integer.parseInt(split1[i]);
        }
        List<Ticket> list = iCreateOrderService.editTicketNum(split, num);
        if (list != null && list.size() > 0) {
            json.setCode(CodeHandle.SUCCESS.getCode() + "");
            json.setMessage(CodeHandle.SUCCESS.getMessage());
        } else {
            json.setCode(CodeHandle.FAILURE.getCode() + "");
            json.setMessage(CodeHandle.FAILURE.getMessage());
        }
        return json;
    }

    /**
     * 根据票id集合批量退款
     *
     * @param data
     * @param customer
     * @param json
     * @return JsonEntity 返回类型
     * @throws
     */
    public JsonEntity refundTicket(JSONObject data, Customer customer, JsonEntity json) {
        String ticketIds = data.containsKey("ticketIds") ? data.getString("ticketIds") : "";
        String orderId = data.containsKey("orderId") ? data.getString("orderId") : "";
        if (ticketIds.equals("") || orderId.equals("")) {
            json.setCode(CodeHandle.CODE_90001.getCode());
            json.setMessage(CodeHandle.CODE_90001.getMessage());
            return json;
        }
        String[] split = ticketIds.split(",");
        List<Ticket> list = new ArrayList<>();
        for (int i = 0; i < split.length; i++) {
            Ticket t = new Ticket();
            t.setTicketId(split[i]);
            list.add(t);
        }
        try {
            Integer ticket = ordersRefundService.refundTicket(list, orderId);
            if (ticket > 0) {
                json.setCode(CodeHandle.SUCCESS.getCode() + "");
                json.setMessage(CodeHandle.SUCCESS.getMessage());
            } else {
                json.setCode(CodeHandle.CODE_30001.getCode() + "");
                json.setMessage(CodeHandle.CODE_30001.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
            json.setCode(CodeHandle.FAILURE.getCode() + "");
            json.setMessage(CodeHandle.FAILURE.getMessage());
        }
        return json;
    }

    /**
     * 检查身份证购票是否在有效期内
     *
     * @param data
     * @param customer
     * @param json
     * @return JsonEntity 返回类型
     * @throws
     */
    public JsonEntity findExpTicket(JSONObject data, Customer customer, JsonEntity json) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * 检查导游是否有未带团的订单
     *
     * @param data
     * @param customer
     * @param json
     * @return JsonEntity 返回类型
     * @throws
     */
    public JsonEntity checkGuideOrder(JSONObject data, Customer customer, JsonEntity json) {
        int i = iCreateOrderService.checkGuideOrder(customer.getId());
        if (i > 0) {
            json.setCode("20000");
            json.setMessage("该导游在有效期内有未激活的景点，如继续购票则只能激活最新一张门票，是否继续操作？");
        } else {
            json.setCode(CodeHandle.SUCCESS.getCode() + "");
            json.setMessage("");
        }
        return json;
    }

    /**
     * 获取最近订单的游客信息
     *
     * @param data
     * @param customer
     * @param json
     * @return JsonEntity 返回类型
     * @throws
     */
    public JsonEntity getVisitor(JSONObject data, Customer customer, JsonEntity json) {
        Date date = new Date();
        String endTime = DateUtils.formatDate(date, "yyyy-MM-dd") + " 23:59:59";
        String startTime = DateUtils.formatDate(DateUtils.addMonths(date, -1), "yyyy-MM-dd") + " 00:00:00";
        String guideId = null;
        String resellerId = null;
        Map<String, Object> param = Maps.newHashMap();
        if (customer.getResellerType().equals(UserGlobalDict.guideUserType())) {
            guideId = customer.getId() + "";
            param.put("guideId", guideId);
        }
        if (customer.getResellerType().equals(UserGlobalDict.businessUserType())) {
            resellerId = customer.getId() + "";
            param.put("resellerId", resellerId);
        }
        List<Map<String, Object>> tickets = null;
        param.put("endTime", endTime);
        param.put("startTime", startTime);
        try {
            tickets = ticketService.findByResellerIdAndTimeAndGuideId(param);
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<Map<String, Object>> jsonArray = Lists.newArrayList();
        ArrayList<Object> buyerIds = Lists.newArrayList();
        for (Map<String, Object> ticket : tickets) {

            String buyerId = ticket.containsKey("buyerId") ? ticket.get("buyerId").toString() : "";
            String buyerName = ticket.containsKey("buyerName") ? ticket.get("buyerName").toString() : "";
            String buyerMobile = ticket.containsKey("buyerMobile") ? ticket.get("buyerMobile").toString() : "";
            if (buyerId == null && buyerId.equals("")) {
                json.setCode(CodeHandle.FAILURE.getCode() + "");
                json.setMessage(CodeHandle.FAILURE.getMessage());
            } else {
                if (!buyerIds.contains(buyerId)) {
                    Map<String, Object> jsonObject = Maps.newHashMap();
                    jsonObject.put("card", buyerId);
                    jsonObject.put("name", buyerName);
                    jsonObject.put("buyMobile", buyerMobile);
                    jsonArray.add(jsonObject);
                    buyerIds.add(buyerId);
                }
            }

        }
        Map<String, Object> object = Maps.newHashMap();
        object.put("list", jsonArray);
        json.setCode(CodeHandle.SUCCESS.getCode() + "");
        json.setMessage(CodeHandle.SUCCESS.getMessage());
        json.setResponseBody(object);
        return json;
    }

    /**
     * 获取发生交易的景区
     *
     * @param data
     * @param customer
     * @param json
     * @return JsonEntity 返回类型
     * @throws
     */
    public JsonEntity buyingScene(JSONObject data, Customer customer, JsonEntity json) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * 非一证一票的退票接口
     *
     * @param data
     * @param customer
     * @param json
     * @return JsonEntity 返回类型
     * @throws
     */
    public JsonEntity notRefundTicket(JSONObject data, Customer customer, JsonEntity json) {
        String productIdsInfo = data.containsKey("productIdsInfo") ? data.getString("productIdsInfo") : "";
        String orderId = data.containsKey("orderId") ? data.getString("orderId") : "";
        if (productIdsInfo.equals("") || orderId.equals("")) {
            json.setCode(CodeHandle.CODE_90001.getCode());
            json.setMessage(CodeHandle.CODE_90001.getMessage());
            return json;
        }
        String[] split = productIdsInfo.split(",");
        List<Ticket> list = new ArrayList<>();
        Ticket ticket;
        for (int i = 0; i < split.length; i++) {
            ticket = new Ticket();
            String info = split[i];
            String[] ticketinfo = info.split("-");
            ticket.setTicketId(ticketinfo[0]);
            ticket.setProductId(Long.parseLong(ticketinfo[1]));
            int curRefundNum = (ticketinfo[2] == null || "null".equals(ticketinfo[2]) || "".equals(ticketinfo[2])) ? 0 : Integer.parseInt(ticketinfo[2]);
            ticket.setCurRefundNum(curRefundNum);
            list.add(ticket);
        }
        try {
            Integer ticklist = ordersRefundService.refundTicket(list, orderId);
            if (ticklist > 0) {
                json.setCode(CodeHandle.SUCCESS.getCode());
                json.setMessage(CodeHandle.SUCCESS.getMessage());
            } else {
                json.setCode(CodeHandle.CODE_30001.getCode());
                json.setMessage(CodeHandle.CODE_30001.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
            json.setCode(CodeHandle.CODE_30002.getCode());
            json.setMessage(CodeHandle.CODE_30002.getMessage());
        }
        return json;
    }

    /**
     * 根据票id集合批量退款(通用退款)
     *
     * @param data
     * @param customer
     * @param json
     * @return JsonEntity 返回类型
     * @throws
     */
    public JsonEntity refundTicketForCommon(JSONObject data, Customer customer, JsonEntity json) {
        String orderId = data.containsKey("orderId") ? data.getString("orderId") : "";
        /*退款单号*/
        String refundId = data.containsKey("refundId") ? data.getString("refundId") : "";

        Map<String, Object> jsonMap = Maps.newHashMap();
        jsonMap.put("refundId", refundId);
        try {
            Result<Boolean> responseResult = refundOrderMoneyService.refundMoney(orderId, null);
            printJsonResult(json, responseResult);
            json.setResponseBody(jsonMap);
        } catch (Exception e) {
            e.printStackTrace();
            json.setCode(CodeHandle.FAILURE.getCode());
            json.setMessage(CodeHandle.FAILURE.getMessage());
        }
        return json;
    }

    private void printJsonResult(JsonEntity json, Result<Boolean> result) {
        if (result.getData() != null && result.getData()) {
            json.setCode(CodeHandle.SUCCESS.getCode());
            json.setMessage(CodeHandle.SUCCESS.getMessage());
        } else {
            json.setCode(CodeHandle.CODE_30001.getCode());
            json.setMessage(CodeHandle.CODE_30001.getMessage());
        }
    }

    /**
     * 退票接口(通用退款)
     *
     * @param data
     * @param customer
     * @param json
     * @return JsonEntity 返回类型
     * @throws
     */
    public JsonEntity refundMoney(JSONObject data, Customer customer, JsonEntity json) {
        RefundMoneyDto refundMoneyDto = null;
        try {
            refundMoneyDto = toOrderVo(data);
        } catch (IOException e) {
            logger.error("Json反序列化异常", e);
            json.setCode(CodeHandle.FAILURE.getCode());
            json.setMessage("系统出错，请稍后再试。");
            return json;
        }

        // 退款
        refundMoney(refundMoneyDto, customer);

        json.setCode(CodeHandle.SUCCESS.getCode());
        json.setMessage("退款成功！");

        return json;
    }

    public void refundMoney(RefundMoneyDto refundMoneyDto, Customer customer) {
        String orderId = refundMoneyDto.getOrderId();
        List<RefundMoneyDto.RefundMoneyTicketDto> tickets = refundMoneyDto.getTickets();

        logger.info("准备退单，订单ID为 {}", orderId);

        if (StringUtils.isBlank(orderId))
            throw AppapiParametersException.createByCodeHandle(CodeHandle.ORDER_ERROE_20022);

        // 查询主订单信息
        OrderDetailVO orderDetailVO = new OrderDetailVO();
        orderDetailVO.setOrder_id(orderId);
        Result<OrderDetailResponse> mainOrderDetailResult = orderQueryService.queryOrderDetail(orderDetailVO, null);
        if (mainOrderDetailResult == null || mainOrderDetailResult.getErrorCode() != new Result<>().getErrorCode())
            throw AppapiParametersException.createByCodeHandle(CodeHandle.ORDER_ERROE_20025);
        // 从主订单中获取必要数据
        OrderDetailResponse mainOrderDetail = mainOrderDetailResult.getData();
        // 检查订单商品的退单时间是否在规定范围内
        //        boolean needCheck = false;
        //        if (!(mainOrderDetail.getConfirm() == 2 && mainOrderDetail.getOrder_status() == OrderStatusEnum.AlreadyPaid.getValue())) {
        //            needCheck = true;
        //        }

        // 查询子订单信息
        List<OrderResponse> orderResponses = orderQueryService.queryOrdersByOrderId(orderId, null);
        // 检查订单能否整单退及其数量
        boolean needRefundAllMoney = checkNeedRefundAllMoney(mainOrderDetail, orderResponses, refundMoneyDto);

        List<RefundMerchandiseVO> refundMerchandiseList = null;
        List<String> ticketIds = new ArrayList<>(tickets.size());
        if (CollectionUtils.isNotEmpty(tickets)) {
            refundMerchandiseList = new ArrayList<>(tickets.size());

            for (RefundMoneyDto.RefundMoneyTicketDto ticket : tickets) {
                String ticketId = ticket.getMerchId(); // 获取订单中的商品的ID
                int productNum = ticket.getProductNum();
                ticketIds.add(ticketId);

                // 检查是否可以退款
                checkRefund(orderId, ticket);

                RefundMerchandiseVO refundMerchandiseVO = new RefundMerchandiseVO();
                refundMerchandiseVO.setMerchandise_id(ticketId);

                if (productNum > 0)
                    refundMerchandiseVO.setMerchandise_num(productNum);

                refundMerchandiseList.add(refundMerchandiseVO);
            }
        }

        List<MerchResponse> refundMerchResponseList = mainOrderDetail.getMerchs();
        Iterator<MerchResponse> iter = refundMerchResponseList.iterator();
        while (iter.hasNext()) {
            MerchResponse merchResponse = iter.next();
            if (!ticketIds.contains(merchResponse.getMerchId())) {
                iter.remove();
            }
        }
        //        CodeHandle rr = orderCheckService.checkOrderRefund(mainOrderDetail.getReseller_id(), String.valueOf(mainOrderDetail.getSale_port()),
        //            refundMerchResponseList);
        //        if (!CodeHandle.SUCCESS.getCode().equals(rr.getCode())) {
        //            throw AppapiParametersException.createByCodeHandle(rr);
        //        }

        Result<Boolean> responseResult;
        if (refundMerchandiseList != null && !needRefundAllMoney) {
            logger.info("开始执行部分退单，订单ID为 {}", orderId);
            responseResult = refundOrderMoneyService.refundMoney(orderId, refundMerchandiseList, null);
        } else {
            logger.info("开始执行整单退单，订单ID为 {}", orderId);
            responseResult = refundOrderMoneyService.refundMoney(orderId, null);
        }

        if (new Result<>().getErrorCode() != responseResult.getErrorCode())
            throw new AppapiParametersException(responseResult.getErrorMsg());
        else {
            logger.info("退单成功！");
        }
    }

    private void checkRefund(String orderId, RefundMoneyDto.RefundMoneyTicketDto ticket) {
        logger.debug("开始调用 “计算退款运价” 接口，检查是否可以退款 => orderId: {}, productId: {}, voucherId: {}, merchPrice: {}", orderId, ticket.getProductId(),
            ticket.getVoucherId(), ticket.getMerchPrice());
        RefundCalcVO refundCalcVO = new RefundCalcVO();
        refundCalcVO.setRefundType(2); // 1: 供应商，2:分销商
        refundCalcVO.setProductId(ticket.getProductId());
        refundCalcVO.setRefundDate(new Date());
        refundCalcVO.setMerchPrice(ticket.getMerchPrice());
        refundCalcVO.setVoucherId(ticket.getVoucherId());
        Result<RefundPriceVO> refundResult = calculateService.refundSingle(refundCalcVO);
        // 如果是不可退款的话，则直接返回
        if (!refundResult.isOk()) {
            logger.error("调用是否可以退款的接口结果 => err_code: {}, err_msg: {}", refundResult.getErrorCode(), refundResult.getErrorMsg());
            throw AppapiParametersException.createByCodeHandle(CodeHandle.ORDER_ERROE_20032);
        }
        logger.debug("结束调用 “计算退款运价” 接口 => [ 可以退款 ]");
    }

    private RefundMoneyDto toOrderVo(JSONObject jsonObject) throws IOException, JsonParseException, JsonMappingException {
        String requestJson = jsonObject.toJSONString();
        return JSON.parseObject(requestJson, RefundMoneyDto.class);
    }

    /**
     * 检查订单能否整单退，如果是整退，则校验退单所请求的数量。
     * @param result
     * @param refundMoneyDto
     * @return
     * @throws AppapiParametersException
     */
    private boolean checkNeedRefundAllMoney(OrderDetailResponse mainOrder, List<OrderResponse> allSubOrderList, RefundMoneyDto refundMoneyDto)
                                                                                                                                              throws AppapiParametersException {
        logger.info("开始检查订单是否整单退");
        List<String> allSupplierIdList = new ArrayList<>(allSubOrderList.size());

        for (OrderResponse orderResponse : allSubOrderList)
            allSupplierIdList.add(String.valueOf(orderResponse.getSupplier_id()));

        List<MerchResponse> merchs = mainOrder.getMerchs();
        List<RefundMoneyDto.RefundMoneyTicketDto> tickets = refundMoneyDto.getTickets();
        boolean needRefundAllMoney = checkNeedRefundAllMoney(allSupplierIdList);

        //20160629 by jinliang
        //暂时这么写，以后会根据供应商ID来查询
        //需要交易系统给加入新接口，根据主订单接口来查询子订单
        // TODO

        if (!needRefundAllMoney) {
            for (MerchResponse merch : merchs) {
                if (checkNeedRefundAllMoney(String.valueOf(merch.getProductId()))) {
                    needRefundAllMoney = true;
                    break;
                }
            }
        }

        if (logger.isDebugEnabled()) {
            logger.debug("\t检查订单是否整单退｜订单供应商ID为 {}， 此供应商是整单退单 {}", allSupplierIdList, needRefundAllMoney);
        }

        if (needRefundAllMoney) {
            for (MerchResponse merchResponse : merchs) {
                RefundMoneyDto.RefundMoneyTicketDto hasTicket = null;
                for (RefundMoneyDto.RefundMoneyTicketDto ticket : tickets) {
                    String ticketId = ticket.getMerchId(); // 获取订单中的商品的ID
                    if (merchResponse.getMerchId().equals(ticketId)) {
                        hasTicket = ticket;
                        break;
                    }
                }

                if (hasTicket == null)
                    // 如果在退商品的列表中没找到商品
                    throw AppapiParametersException.createByCodeHandle(CodeHandle.ORDER_ERROE_20026);

                int productNum = hasTicket.getProductNum();
                int ticketNum = merchResponse.getTotalNum() - merchResponse.getRefundNum() - merchResponse.getCheckNum();
                int merchType = merchResponse.getMerchType();

                if (logger.isDebugEnabled()) {
                    logger.debug("\t检查订单是否整单退｜订单商品ID为 {}", merchResponse.getMerchId());
                    logger.debug("\t检查订单是否整单退｜产品类型为   {}", merchType);
                    logger.debug("\t检查订单是否整单退｜请求数量为   {}", productNum);
                    logger.debug("\t检查订单是否整单退｜实际数量为   {}\r\n", ticketNum);
                }

                if ((merchType == GlobalDict.ProductCategory.normal || merchType == GlobalDict.ProductCategory.scenic) && ticketNum != productNum)
                    throw AppapiParametersException.createByCodeHandle(CodeHandle.ORDER_ERROE_20026);
            }
        }

        logger.info("完成检查订单是否整单退，检查结果为 {}", needRefundAllMoney);

        return needRefundAllMoney;
    }

    /**
     * 校验一个供应商的订单能否部分退
     * <p>
     *     如果产品所属供应商在字典{@link AppapiParams#NeedRefundAllMoneySupplierIds}中，则表示订单只能是整单退。
     * </p>
     * @param supplierId
     * @return
     * @throws AppapiParametersException
     */
    private boolean checkNeedRefundAllMoney(String supplierId) throws AppapiParametersException {
        try {
            Dict byVal = dictService.getByVal(supplierId, AppapiParams.NeedRefundAllMoneySupplierIds);
            if (byVal != null)
                return true;
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
            throw AppapiParametersException.createByCodeHandle(CodeHandle.ORDER_ERROE_20027);
        }
        return false;
    }

    /**
     * 检查多个供应商的订单是否能退
     * @param supplierIdList
     * @return
     * @throws AppapiParametersException
     */
    private boolean checkNeedRefundAllMoney(List<String> supplierIdList) throws AppapiParametersException {
        for (String supplierId : supplierIdList) {
            boolean needCheck = checkNeedRefundAllMoney(supplierId);
            if (needCheck)
                return needCheck;
        }
        return false;
    }
}
