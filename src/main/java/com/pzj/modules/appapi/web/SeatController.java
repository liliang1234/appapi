package com.pzj.modules.appapi.web;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pzj.common.util.CheckUtils;
import com.pzj.core.stock.model.SeatChartModel;
import com.pzj.core.stock.service.SeatChartService;
import com.pzj.customer.entity.Customer;
import com.pzj.framework.context.Result;
import com.pzj.framework.context.ServiceContext;
import com.pzj.modules.appapi.api.CustumerService;
import com.pzj.modules.appapi.seat.SeatService1;
import com.pzj.modules.appapi.seat.model.SeatArea;
import com.pzj.product.service.ISkuProductService;
import com.pzj.product.vo.product.SkuProduct;
import com.pzj.product.vo.voParam.resultParam.SkuProductResultVO;

@Controller
@RequestMapping("seat")
public class SeatController {
    private static final Logger logger = LoggerFactory.getLogger(SeatController.class);

    @Autowired
    private SeatService1        seatService1;
    @Autowired
    private CustumerService     custumerService;
    @Autowired
    private ISkuProductService  productService;
    @Autowired
    private SeatChartService    seatChartService;

    @RequestMapping("product")
    public @ResponseBody SeatArea product(@RequestParam(required = false) String supplierId, @RequestParam(required = false) Long scenicId,
                                          @RequestParam(required = false) String seatArea, @RequestParam(required = false) String productId,
                                          @RequestParam(required = false) String showStartTime, @RequestParam(required = false) String token,
                                          @RequestParam(required = false, defaultValue = "1") String sort, Model model, HttpServletRequest request,
                                          HttpSession session) {
        Customer customer = custumerService.queryCustomer(token);
        SeatArea seatArea2 = new SeatArea();
        if (customer != null) {
            seatArea2 = seatService1.getSeatArea(showStartTime, seatArea, scenicId, customer, sort, productId);
        }
        return seatArea2;
    }

    /*
    @ResponseBody
    @RequestMapping("getTheaterNum")
    public List<SeatStock> getTheaterNum(Integer matchs, Long scenicId, String showStartTime, @RequestParam(required = false) String productId,
                                         String seatArea) {
        Date playTime = DateUtils.parseDate(showStartTime);
        List<SeatStock> queryStock = seatService1.queryStock(playTime, matchs, scenicId);
        return queryStock;
    }*/

    @RequestMapping("seat")
    public String seat(String orderID, Model model, @RequestParam(required = false) String productId, @RequestParam(required = false) Long scenicId,
                       @RequestParam(required = false) String screeningId, @RequestParam(required = false) String supplierId,
                       @RequestParam(required = false) String seatArea, @RequestParam(required = false) String areaDesc,
                       @RequestParam(required = false) Integer sort, @RequestParam(required = false) String showTime,
                       @RequestParam(required = false) String checkedNum, @RequestParam(required = false) String lockSeats,
                       @RequestParam(required = false) String num, @RequestParam(required = false) String token, HttpServletRequest request) {
        try {
            //查询产品信息
            SkuProductResultVO findSkuProductById = productService.findSkuProductById(Long.valueOf(productId));
            System.out.println(findSkuProductById);
            //SkuProductResultVO findSkuProductById =new SkuProductResultVO();
            SkuProduct skuProduct = findSkuProductById.getSkuProduct();
            //加载座位图
            SeatChartModel seatChartModel = new SeatChartModel();
            seatChartModel.setScenicId(scenicId);
            seatChartModel.setAreaScreeningsId(skuProduct.getTheaterId());
            ServiceContext serviceContext = new ServiceContext();
            Result<ArrayList<SeatChartModel>> querySeatChartBy = seatChartService.querySeatChartByScenicAndAreaRel(seatChartModel,
                serviceContext.getServiceContext());
            Integer seatCount = null;//座位总数
            String chartKey = "";//座位图
            ArrayList<SeatChartModel> seatChartModelList = querySeatChartBy.getData();
            //判断座位是B单还是B双,目前只针对边城,一场次包含两个座位
            for (SeatChartModel seatChartModel2 : seatChartModelList) {
                if (seatChartModel2.getSort() == 1) {
                    seatCount = seatChartModel2.getTotalSeats();
                    chartKey = seatChartModel2.getSeatMaps();
                } else {
                    seatCount = seatChartModel2.getSort();
                    chartKey = seatChartModel2.getSeatMaps();
                }
            }
            if (sort == 1) {
                model.addAttribute("isMultiArea", false);
            } else {
                model.addAttribute("isMultiArea", true);
            }
            model.addAttribute("productId", productId);
            model.addAttribute("num", seatCount);
            model.addAttribute("showStartTime", showTime);
            model.addAttribute("screeningId", screeningId);
            model.addAttribute("seatArea", seatArea);
            if (seatChartModelList != null && seatChartModelList.size() > 0) {
                model.addAttribute("areaDesc", null);
            }
            if (CheckUtils.isNull(checkedNum)) {
                model.addAttribute("checkedNum", "0");
            } else {
                model.addAttribute("checkedNum", checkedNum + ",");
            }
            if (CheckUtils.isNotNull(checkedNum)) {
                String[] split = checkedNum.split(",");
                model.addAttribute("currentNum", split.length);
            } else {
                model.addAttribute("currentNum", 0);
            }
            model.addAttribute("sort", sort);
            model.addAttribute("supplierId", supplierId);
            model.addAttribute("scienicId", scenicId);
            model.addAttribute("token", token);
            model.addAttribute("lockSeats", lockSeats);
        } catch (Throwable throwable) {
            logger.error(throwable.getMessage(), throwable);
        }

        return "seat";
    }
}
