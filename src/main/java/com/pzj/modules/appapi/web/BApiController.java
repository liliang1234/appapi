package com.pzj.modules.appapi.web;

import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashMap;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bouncycastle.util.encoders.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.pzj.common.util.CheckUtils;
import com.pzj.common.util.ResponseUtil;
import com.pzj.customer.entity.Customer;
import com.pzj.framework.entity.QueryResult;
import com.pzj.modules.appapi.api.CommonAppInfoService;
import com.pzj.modules.appapi.api.CustumerService;
import com.pzj.modules.appapi.api.NewOrderService;
import com.pzj.modules.appapi.api.OrderService;
import com.pzj.modules.appapi.api.OtherService;
import com.pzj.modules.appapi.api.ProductGroupService;
import com.pzj.modules.appapi.api.ProductService;
import com.pzj.modules.appapi.api.RebateService;
import com.pzj.modules.appapi.api.RedirectService;
import com.pzj.modules.appapi.api.SceneService;
import com.pzj.modules.appapi.api.SeatService2;
import com.pzj.modules.appapi.api.SettleService;
import com.pzj.modules.appapi.api.StatisticService;
import com.pzj.modules.appapi.api.TicketService;
import com.pzj.modules.appapi.api.VersionService;
import com.pzj.modules.appapi.api.WalletService;
import com.pzj.modules.appapi.api.WebSiteService;
import com.pzj.modules.appapi.api.order.OrderCreateService;
import com.pzj.modules.appapi.entity.CodeHandle;
import com.pzj.modules.appapi.entity.JsonEntity;
import com.pzj.modules.appapi.exception.AppapiParametersException;
import com.pzj.platform.appapi.aop.ContextEntry;
import com.pzj.platform.appapi.util.MD5Util;
import com.pzj.trade.order.entity.OrderDetailResponse;
import com.pzj.trade.order.entity.OrderListResponse;

@Controller
@RequestMapping("bapi")
public class BApiController {
    private static final Logger                          logger = LoggerFactory.getLogger(BApiController.class);
    @Resource(name = "custumerService")
    private CustumerService                              custumerService;
    @Autowired
    private SceneService                                 sceneService;
    @Autowired
    private OtherService                                 otherService;
    @Autowired
    private OrderService                                 orderService;
    @Autowired
    private SeatService2                                 seatService;
    @Autowired
    private TicketService                                ticketService;
    @Autowired
    private StatisticService                             statisticService;
    @Autowired
    private WebSiteService                               webSiteService;
    @Autowired
    private RebateService                                rebateService;
    @Autowired
    private CommonAppInfoService                         commonAppInfoService;
    @Autowired
    private RedirectService                              redirectService;
    @Autowired
    private SettleService                                settleService;
    @Autowired
    private WalletService                                paymentService;
    @Autowired
    private NewOrderService                              newOrderService;
    @Autowired
    private ProductGroupService                          productService;
    @Autowired
    private ProductService                               productService1;
    @Autowired
    private VersionService                               versionService;
    @Autowired
    private com.pzj.platform.appapi.service.OrderService OrderServiceApp;
    @Autowired
    private OrderCreateService                           orderCreateService;

    @RequestMapping
    @ResponseBody
    public JsonEntity api(String data, HttpServletRequest request, String sign, HttpServletResponse response) {
        JsonEntity json = new JsonEntity();
        HashMap<String, String> headers = getHeaders(request);
        String token = headers.get("token") == null ? "" : headers.get("token");
        Long resellerId = headers.get("rid") == null ? null : Long.parseLong(headers.get("rid"));

        try {
            if (CheckUtils.isNull(data)) {
                json.setCode(CodeHandle.CODE_90001.getCode() + "");
                json.setMessage(CodeHandle.CODE_90001.getMessage());
                return json;
            }
            logger.info("[old api] request param: data={}&sign={}", data, sign);
            String value = new String(Base64.decode(URLDecoder.decode(data, "UTF-8")));
            JSONObject data2 = JSON.parseObject(value);
            String method = data2.containsKey("method") ? data2.getString("method") : "";
            logger.info("[old api] request ==> {}", value);

            json.setResponseTime(String.valueOf(System.currentTimeMillis()));
            if (method.equals("login")) {
                return custumerService.login(data2, json);
            } else if (method.equals("sendCodeWithMobile")) { // 发送验证码
                return custumerService.sendCodeWithMobile(data2, json);
            } else if (method.equals("updatePasswordWithCode")) { // 通过手机验证码修改密码
                return custumerService.updatePasswordWithCode(data2, json);
            } else if (method.equals("register")) { // 注册用户
                return custumerService.registerReseller(request, data2, json);
            } else if (method.equals("checkCode")) { // 校验验证码是否正确
                return custumerService.checkCode(data2, json);
            } else if (method.equals("Sentenced")) {// 判断用户名是否重复
                return custumerService.sentenced(data2, json);
            } else if (method.equals("isMatching")) {// 判断手机号和用户名是否匹配
                return custumerService.isMatching(data2, json);
            } else if (method.equals("version")) {// 判断app版本是否需要升级
                return versionService.version(data2, json);
            }

            Customer customer = null;
            if (CheckUtils.isNotNull(token)) {
                logger.info("[old api] 当前用户的Token为：{}", token);
                customer = custumerService.queryCustomer(token.trim());
            } else if (CheckUtils.isNotNull(resellerId)) {
                logger.info("[old api] 当前用户的rid为：{}", resellerId);
                customer = custumerService.getCustomerById(resellerId);
            }

            if (customer == null || !customer.getAccountState().equals(1)) {
                json.setCode(CodeHandle.TOKENRERROR.getCode() + "");
                json.setMessage(CodeHandle.TOKENRERROR.getMessage());
                return json;
            }

            // 校验签名
            String sign1 = MD5Util.md5Hex(URLDecoder.decode(data, "UTF-8") + (CheckUtils.isNull(token) ? resellerId.toString() : token));
            if (CheckUtils.isNull(sign) || !sign.equals(sign1)) {
                json.setCode(CodeHandle.SIGNERROR.getCode() + "");
                json.setMessage(CodeHandle.SIGNERROR.getMessage());
                return json;
            }
            String redirect = data2.containsKey("redirect") ? data2.getString("redirect") : "";
            if (CheckUtils.isNotNull(redirect)) {
                json = redirectService.redirect(data, sign, redirect, token, json);
            } else {
                if (method.equals("getReseller")) { // 获取店主信息
                    json = custumerService.getReseller(data2, customer, json);
                } else if (method.equals("updateReseller")) {// 更新店主信息
                    json = custumerService.updateReseller(data2, customer, json);
                } else if (method.equals("updatePassword")) { // 修改店主密码
                    json = custumerService.updatePassword(data2, customer, json);
                } else if (method.equals("uploadPhoto")) { // 上传图片
                    json = custumerService.uploadPhoto(data2, customer, json, request);
                } else if (method.equals("sendCode")) {// 发送验证码
                    json = custumerService.sendCode(data2, customer, json);
                } else if (method.equals("getAddress")) {// 获取 收货地址
                    json = custumerService.getAddress(data2, customer, json);
                } else if (method.equals("queryResellerComplete")) {// 动态搜索地接社、地接社部门列表
                    json = custumerService.queryResellerComplete(data2, customer, json);
                } else if (method.equals("getHelp")) { // 获取帮助连接
                    json = otherService.getHelp();
                } else if (method.equals("queryOrderStatus")) {// 获取状态列表
                    json = otherService.queryOrderStatus(data2, customer, json);
                } else if (method.equals("queryDealTypes")) {// 获取流水是由列表
                    json = otherService.queryDealTypes(data2, customer, json);
                } else if (method.equals("saveGuideOrder")) { // 保存订单
                    json = orderService.saveOrder(data2, customer, json);
                } else if (method.equals("lastSaveGuideOrder")) { // 修改订单信息
                    json = orderService.lastSaveOrder(data2, customer, json);
                } else if (method.equals("pay")) {// 通用支付
                    json = paymentService.pay(data2, customer);
                } else if (method.equals("cancelOrder")) {// 取消订单
                    json = orderService.cancelOrder(data2, customer, json);
                } else if (method.equals("queryOrders")) { // 查询订单列表
                    json = orderService.queryOrders(data2, customer, json);
                } else if (method.equals("queryOrder")) { // 查询单个订单
                    json = orderService.queryOrder(data2, customer, json);
                } else if (method.equals("addTicket")) {// 添加票 一证一票未支付
                    json = ticketService.addTicket(data2, customer, json);
                } else if (method.equals("minusTicket")) {// 删除票 一证一票未支付
                    json = ticketService.minusTicket(data2, customer, json);
                } else if (method.equals("updateTicket")) {// 修改票的信息 一证一票已支付
                    json = ticketService.updateTicket(data2, customer, json);
                } else if (method.equals("refundTicket")) {// 退款一证一票
                    json = ticketService.refundTicket(data2, customer, json);
                } else if (method.equals("notRefundTicket")) {// 退款非一证一票
                    json = ticketService.notRefundTicket(data2, customer, json);
                } else if (method.equals("editTicketNum")) {// 修改 票的数量（非一证一票）
                    json = ticketService.editTicketNum(data2, customer, json);
                } else if (method.equals("findExpTicket")) {// 检查身份证购票是否在有效期内
                    json = ticketService.findExpTicket(data2, customer, json);
                } else if (method.equals("checkGuideOrder")) {// 检测导游购票
                    json = ticketService.checkGuideOrder(data2, customer, json);
                } else if (method.equals("getVisitor")) {// 批量导入
                    json = ticketService.getVisitor(data2, customer, json);
                } else if (method.equals("buyingScene")) {// 联票非一证一票退款
                    json = ticketService.buyingScene(data2, customer, json);
                }
                //                else if (method.equals("removeSeats")) {// 删除座位
                //                    json = seatService.removeSeats(data2, customer, json);
                //                } 
                //                else if (method.equals("autoTicketSeat")) {// 自动添加座位
                //                    // json = seatService.autoTicketSeat(data2, customer, json);
                //                } 

                else if (method.equals("confirmOrder")) {// 开始对账
                    json = orderService.confirmOrder(data2, customer, json);
                } else if (method.equals("payAnother")) {// 自动添加座位
                    json = orderService.payAnother(data2, customer, json);
                } else if (method.equals("queryFlows")) { // 查询流水
                    json = statisticService.walletRecord(data2, customer, json);
                } else if (method.equals("queryFlowOrders")) {// 查询流水订单
                    json = statisticService.queryFlowOrders(data2, customer, json);
                } else if (method.equals("bills")) {// 对账单
                    json = statisticService.bills(data2, customer, json);
                } else if (method.equals("statement")) {// 对账单
                    json = statisticService.statement(data2, customer, json);
                } else if (method.equals("updateStatement")) {// 更新对账单
                    json = statisticService.updateStatement(data2, customer, json);
                } else if (method.equals("getStatistics")) {// 获取店铺统计
                    json = statisticService.getStatistics(data2, customer, json);
                } else if (method.equals("getSale")) {// 获销售统计总表
                    json = statisticService.getSale(data2, customer, json);
                } else if (method.equals("getSaleStatistics")) {// 销售统计
                    json = statisticService.getSaleStatistics(data2, customer, json);
                } else if (method.equals("getAccountInfo")) { // 钱包A面，账户余额。
                    json = settleService.getAccountInfo(data2, customer, json);
                } else if (method.equals("getAccountDetailFlow")) {
                    json = settleService.getAccountDetailFlow(data2, customer, json);
                } else if (method.equals("querySceneByProvince")) { // 查询登录用户能看到的省份下的所有景区
                    json = sceneService.querySceneByProvince(data2, customer, json);
                } else if (method.equals("queryAllScenes")) { // 查询登录用户能看到的所有景区,并以首字母分组
                    json = sceneService.queryAllScenes(data2, customer, json);
                } else if (method.equals("queryProductBySceneId")) { // 查询单个景区的产品列表，这些产品必须是用户能看到的
                    json = sceneService.queryProductBySceneId(data2, customer, json);
                } else if (method.equals("checkTimeCount")) { //计算检票时长
                    json = sceneService.checkTimeCount(data2, customer, json);
                } else if (method.equals("queryProductDetail")) {// 查询产品介绍及退票规则
                    json = sceneService.queryProductDetail(data2, json);
                } else if (method.equals("queryProductComplete")) {//
                    json = sceneService.queryProductComplete(data2, customer, json);
                } else if (method.equals("getWebsite")) {// 查询微店信息
                    json = webSiteService.getWebsite(data2, customer, json);
                } else if (method.equals("updateWebsite")) {// 更新微店信息
                    json = webSiteService.updateWebsite(data2, customer, json);
                } else if (method.equals("queryCustomers")) {// 获取客户列表
                    json = webSiteService.queryCustomers(data2, customer, json);
                } else if (method.equals("updateCustomers")) {// 更新微店信息
                    json = webSiteService.updateCustomers(data2, customer, json);
                } else if (method.equals("queryMessages")) { // 查询系统消息
                    json = commonAppInfoService.messageList(data2, customer, json);
                } else if (method.equals("feedback")) { // 反馈
                    json = commonAppInfoService.feedback(data2, customer, json);
                } else if (method.equals("feedbackList")) {// 意见反馈列表
                    json = commonAppInfoService.feedbackList(data2, customer, json);
                } else if (method.equals("rebateProduct")) {// 获取返利积分列表
                    json = rebateService.rebateProduct(data2, customer, json);
                } else if (method.equals("getProductInfo")) {// 获取产品组的产品类型
                    json = productService.getProductInfo(data2, customer, json);
                } else if (method.equals("queryForAppIndex")) {//首页数据查询
                    json = sceneService.queryForAppIndexNew(data2, customer, json);
                } else if (method.equals("querySenceDetail")) {//景区详情接口
                    json = sceneService.querySenceDetail(data2, customer, json);
                } else if (method.equals("queryFortuneCatBySpuId")) { // 招财猫
                    json = sceneService.queryFortuneCatBySpuId(data2, customer, json);
                }
                /***************************************/
                else if (method.equals("refundMoney")) {// 退款(通用)
                    json = ticketService.refundMoney(data2, customer, json);
                } else if (method.equals("cancelOrderForCommon")) {// 取消订单(通用)
                    json = newOrderService.cancelOrder(data2);
                } else if (method.equals("saveOrder")) {//通用保存订单
                    logger.info("[old api] ========================> Save Order Begin");
                    //                    json = newOrderService.saveOrder(data2, customer, json);
                    //20160821 使用orderCreateService中的saveOrder方法
                    json = orderCreateService.saveOrder(data2, customer);
                    logger.info("[old api] <======================== Save Order End");
                } else if (method.equals("queryOrdersByReseller")) {// 查询订单列表(分销商)
                    QueryResult<OrderListResponse> result = OrderServiceApp.queryOrdersByReseller(data2, customer);
                    json = ResponseUtil.makeSuccessJsonEntity(result, "查询订单列表成功");
                } else if (method.equals("queryOrderDetailByReseller")) {// 查询订单详情(分销商)
                    OrderDetailResponse result = OrderServiceApp.queryOrderDetailByReseller(data2, customer.getResellerType());
                    json = ResponseUtil.makeSuccessJsonEntity(result, "查询订单详情成功");
                }
                /***************************************/
                else if (method.equals("querySellCheck")) {// 查询销售对账列表
                    json = settleService.querySellCheck(data2, customer, json);
                } else if (method.equals("querySellCheckById")) {// 查询销售对账单
                    json = settleService.querySellCheckById(data2, customer, json);
                } else if (method.equals("querySettlementByProductId")) {// 查询销售对账单-产品详情
                    json = settleService.querySettlementByProductId(data2, customer, json);
                } else if (method.equals("querySettlementBySettleId")) {// 查询销售对账单-结算单-订单详情
                    json = settleService.querySettlementBySettleId(data2, customer, json);
                } else if (method.equals("confirmSellCheck")) {// 分销商确认
                    json = settleService.confirmSellCheck(data2, customer, json);
                } else if (method.equals("refuseSellCheck")) {// 分销商拒绝
                    json = settleService.refuseSellCheck(data2, customer, json);
                } else if (method.equals("withdraw")) {// 提现
                    json = paymentService.withdraw(data2, customer, json);
                }
                /*****************************************************************************/
                else if (method.equals("getSPUList")) {// 查询产品列表
                    json = productService1.getSPUList(data2, customer, json);
                } else if (method.equals("getSPUDetail")) {// 查询产品详情
                    json = productService1.getSPUDetail(data2, customer, json);
                } else if (method.equals("getScenicType")) {// 查询产品类别
                    json = productService1.getScenicType(data2, customer, json);
                }
                //                else if (method.equals("selectedSeatNum")) {// 查询当前用户选中的座位
                //                    json = seatService.selectedSeatNum(data2, customer, json);
                //                } 
                else {
                    json.setCode(CodeHandle.CODE_90001.getCode() + "");
                    json.setMessage(CodeHandle.CODE_90001.getMessage());
                }
            }
            logger.info("[old api] response <== " + JSON.toJSONString(json));
            json.setResponseTime(System.currentTimeMillis() + "");
        } catch (AppapiParametersException e) {
            logger.error(e.getMessage(), e);
            json.setCode(e.getCode());
            json.setMessage(e.getMessage());
        } catch (JSONException e) {
            logger.error(e.getMessage(), e);
            json.setCode(CodeHandle.SERVERERROR.getCode());
            json.setMessage(CodeHandle.SERVERERROR.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            json.setCode(CodeHandle.SERVERERROR.getCode());
            json.setMessage(CodeHandle.SERVERERROR.getMessage());
        } catch (Throwable t) {
            logger.error(t.getMessage(), t);
            json.setCode(CodeHandle.SERVERERROR.getCode());
            json.setMessage(CodeHandle.SERVERERROR.getMessage());
        } finally {
            ContextEntry.removeMonitor();
        }

        return json;
    }

    @SuppressWarnings("unchecked")
    private HashMap<String, String> getHeaders(HttpServletRequest request) {
        HashMap<String, String> map = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String element = headerNames.nextElement();
            String value = request.getHeader(element);
            map.put(element.toLowerCase(), value);
        }
        return map;
    }
}
