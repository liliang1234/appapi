package com.pzj.modules.appapi.api;

import java.util.Calendar;
import java.util.Date;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.pzj.base.service.product.IProductScenicService;
import com.pzj.base.service.product.IProductSiteDataService;
import com.pzj.base.service.show.IScreeningChartService;
import com.pzj.base.service.sys.ILabelRelationService;
import com.pzj.common.util.ResponseUtil;
import com.pzj.framework.context.Result;
import com.pzj.modules.appapi.api.order.OrderCheckService;
import com.pzj.modules.appapi.entity.JsonEntity;
import com.pzj.modules.appapi.voucher.IVoucherCreateService;
import com.pzj.platform.appapi.util.PropertyLoader;
import com.pzj.product.api.enterprise.service.ScenicService;
import com.pzj.product.api.product.service.ProductComposeService;
import com.pzj.product.api.product.service.ProductService;
import com.pzj.regulation.service.StrategyService;
import com.pzj.sale.api.order.service.OrderUtils;
import com.pzj.sale.api.show.service.IShowService;
import com.pzj.trade.order.model.OrderCancelModel;
import com.pzj.voucher.service.VoucherService;

/**
 * 订单服务
 * @ClassName NewOrderService
 * @Description TODO
 * @author fanggang
 * @date 2016年5月29日 下午12:01:05
 *
 */
@Component
public class NewOrderService {
    @Resource(name = "propertyLoader")
    private PropertyLoader                                 propertyLoader;

    @Autowired
    private com.pzj.customer.service.CustomerService       customerService;

    @Autowired
    private OrderUtils                                     orderUtils;

    @Autowired
    private ProductComposeService                          productComposeService;

    @Autowired
    private com.pzj.trade.order.service.OrderService       orderService;

    @Autowired
    private com.pzj.trade.order.service.CancelService      cancelService;

    @Autowired
    private ProductService                                 productService;

    @Autowired
    private IVoucherCreateService                          voucherCreateService;

    @Autowired
    private ScenicService                                  scenicService;

    @Autowired
    private IScreeningChartService                         screeningChartService;
    @Autowired
    private BizAccountService                              bizAccountService;

    @Autowired
    private ILabelRelationService                          labelRelationService;
    @Autowired
    private StrategyService                                strategyService;
    @Autowired
    private IShowService                                   showService;
    @Autowired
    private IProductScenicService                          iProductScenicService;
    @Autowired
    private com.pzj.platform.appapi.service.ProductService productServicePlatform;
    @Resource(name = "voucherService")
    private VoucherService                                 voucherService;
    @Autowired
    private IProductSiteDataService                        productSiteDataService;

    @Autowired
    private OrderCheckService                              orderCheckService;

    /**
     * 取消订单(通用的)
     *
     * @param data
     * @throws
     */
    public JsonEntity cancelOrder(JSONObject data) {
        String orderId = data.containsKey("orderId") ? data.getString("orderId") : "";
        OrderCancelModel orderCancelModel = new OrderCancelModel();
        orderCancelModel.setOrderId(orderId);
        // TODO 添加上下文参数
        Result<Boolean> responseResult = cancelService.cancelOrder(orderCancelModel, null);

        return ResponseUtil.handle(responseResult, new JsonEntity());
    }

    /**
     * 取指定时间 提前时间
     *
     * @param date
     * @param daysum
     * @return
     */
    public Date getAppointHour(Date date, int daysum) {
        java.util.Calendar calstart = java.util.Calendar.getInstance();
        calstart.setTime(date);
        calstart.add(Calendar.HOUR_OF_DAY, daysum);
        // calstart.add(java.util.Calendar.DAY_OF_WEEK, daysum);
        return calstart.getTime();
    }
}
