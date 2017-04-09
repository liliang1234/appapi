package com.pzj.modules.appapi.api;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.pzj.base.common.global.UserGlobalDict;
import com.pzj.common.util.CheckUtils;
import com.pzj.common.util.ResponseUtil;
import com.pzj.customer.entity.Customer;
import com.pzj.framework.context.Result;
import com.pzj.modules.appapi.entity.CodeHandle;
import com.pzj.modules.appapi.entity.JsonEntity;
import com.pzj.modules.appapi.exception.AppapiParametersException;
import com.pzj.trade.order.entity.OrderDetailResponse;
import com.pzj.trade.order.vo.OrderDetailVO;
import com.pzj.trade.payment.entity.PaymentResult;
import com.pzj.trade.payment.entity.WeChatResult;
import com.pzj.trade.payment.vo.PaymentVO;
import com.pzj.trade.withdraw.model.CashPostalModel;
import com.pzj.trade.withdraw.service.CashPostalService;

/**
 * APP钱包服务
 * 
 * @ClassName WalletService
 * @Description TODO
 * @author fanggang
 * @date 2016年5月27日 下午8:46:55
 *
 */
@Service
public class WalletService extends BaseService {

    @Autowired
    private com.pzj.trade.payment.service.PaymentService  commonPaymentService;

    @Autowired
    private CashPostalService                             cashPostalService;

    @Autowired
    private com.pzj.trade.order.service.OrderQueryService orderQueryService;

    public static final String                            Travel  = "travel";
    public static final String                            OrderId = "orderId";

    /**
     * 支付
     * @author fanggang
     * @date 2016年5月29日 上午10:23:45
     * @param data
     * @return
     */
    public JsonEntity pay(JSONObject data, Customer customer) {
        JsonEntity jsonEntity = null;

        try {
            jsonEntity = payDo(data, customer);
            jsonEntity.setCode(CodeHandle.SUCCESS.getCode());
            jsonEntity.setMessage(CodeHandle.SUCCESS.getMessage());
        } catch (AppapiParametersException e) {
            jsonEntity = new JsonEntity();
            jsonEntity.setCode(CodeHandle.ERROR10033.getCode());
            jsonEntity.setMessage(CodeHandle.ERROR10033.getMessage() + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonEntity;
    }

    private JsonEntity payDo(JSONObject data, Customer customer) throws AppapiParametersException {
        if (!data.containsKey(OrderId))
            throw new AppapiParametersException("订单ID（orderId）不能为null！");
        if (customer == null)
            throw new AppapiParametersException("当前用户（登录用户）信息不存在！");
        // 订单id
        String orderId = data.getString("orderId");

        if (StringUtils.isBlank(orderId))
            throw new AppapiParametersException("订单ID（orderId）不能为null！");

        // 分销商id
        String resellerId = null;
        if (UserGlobalDict.guideUserType().equals(customer.getResellerType()) || StringUtils.isNotEmpty(customer.getGuideCertificate())) {
            OrderDetailVO orderDetailVO = new OrderDetailVO();
            orderDetailVO.setOrder_id(orderId);
            Result<OrderDetailResponse> orderDetail = orderQueryService.queryOrderDetail(orderDetailVO, null);
            OrderDetailResponse result = orderDetail.getData();
            long reseller_id = result.getReseller_id();
            resellerId = String.valueOf(reseller_id);
        } else if (customer.getId() != null) {
            resellerId = customer.getId().toString();
        }

        if (StringUtils.isBlank(resellerId))
            throw new AppapiParametersException("支付订单所需的分销商ID不存在！");

        String payType = null; // 0:余额支付 1、支付宝 2、微信 3、异度（中信） 4、帐扣 5、电汇
        String appId = null;
        String appSecret = null;
        String source = data.containsKey("source") ? data.getString("source") : "2"; // 1: html页面  2: 移动应用   3、网页
        String returnUrl = data.containsKey("returnUrl") ? data.getString("returnUrl") : null; // 
        String failpayUrl = data.containsKey("failpayUrl") ? data.getString("failpayUrl") : null; //
        String rid = data.containsKey("rid") ? data.getString("rid") : null; //
        if (data.containsKey("type")) {
            String type = data.getString("type");
            if ("ALIPAYMOBILE".equals(type)) {
                payType = "1";
            } else if ("WEIXIN".equals(type)) {
                payType = "2";
                if (!source.equals("3")) {
                    appId = data.getString("appId");
                    appSecret = data.getString("appSecret");
                    if (StringUtils.isBlank(appId) || StringUtils.isBlank(appSecret))
                        throw new AppapiParametersException("微信支付所需的appId或appSecret不存在！");
                }
            } else {
                payType = "0";
            }
        }

        if (StringUtils.isBlank(payType))
            payType = "0";

        PaymentVO paymentVO = new PaymentVO();
        paymentVO.setCurrency(1); // 币种. 1: 人民币; 2: 美元
        paymentVO.setOrderId(orderId);
        paymentVO.setPayType(Integer.parseInt(payType));
        paymentVO.setResellerId(Long.parseLong(resellerId));
        paymentVO.setSource(Integer.parseInt(source)); // 请求来源. 1: html页面; 2: 移动应用
        paymentVO.setAppId(appId);
        paymentVO.setAppSecret(appSecret);
        paymentVO.setReturnUrl(returnUrl);
        paymentVO.setFailpayUrl(failpayUrl);
        paymentVO.setRid(rid);

        Result<PaymentResult> responseResult = commonPaymentService.payOrder(paymentVO, null);

        if (new Result<>().getErrorCode() != responseResult.getErrorCode())
            throw new AppapiParametersException(responseResult.getErrorMsg());
        //应app要求，不要返回""，返回{}
        if (CheckUtils.isNotNull(responseResult.getData()) && CheckUtils.isNull(responseResult.getData().getWeChatResult())) {
            responseResult.getData().setWeChatResult(new WeChatResult());
        }
        return ResponseUtil.handle(responseResult, new JsonEntity());
    }

    /**
     * 提现
     * @author Mark
     * @date 2016年6月1日 上午11:53:00
     * @param data
     * @param customer
     * @return
     * @throws AppapiParametersException 
     */
    public JsonEntity withdraw(JSONObject data, Customer customer, JsonEntity jsonEntity) throws AppapiParametersException {
        // TODO 提现方法实现
        //参数完整性校验
        checkNeededParam(data, "userType");// 帐号类型（供应商 1，分销商 2）
        checkNeededParam(data, "resellerType");// 分销商类型（ 1是二维码分销商）
        checkNeededParam(data, "cashPostalMoney");// 提现金额
        CashPostalModel cashPostalModel = new CashPostalModel();
        cashPostalModel.setAccountId(customer.getId());
        cashPostalModel.setUserType(data.getIntValue("userType"));
        cashPostalModel.setResellerType(data.getIntValue("resellerType"));
        cashPostalModel.setCashPostalMoney(data.getDoubleValue("cashPostalMoney"));

        Result<Boolean> responseResult = cashPostalService.cashPostal(cashPostalModel, null);
        JsonEntity handle = ResponseUtil.handle(responseResult, jsonEntity);
        handle.setResponseBody("");
        return handle;
    }

}
