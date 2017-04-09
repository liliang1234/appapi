package com.pzj.modules.appapi.seat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pzj.base.common.DateUtils;
import com.pzj.core.stock.context.UserSeatResponse;
import com.pzj.core.stock.model.SeatChartModel;
import com.pzj.core.stock.model.StockModel;
import com.pzj.core.stock.model.StockQueryRequestModel;
import com.pzj.core.stock.model.UserSeatModel;
import com.pzj.core.stock.service.SeatChartService;
import com.pzj.core.stock.service.SeatService;
import com.pzj.core.stock.service.StockQueryService;
import com.pzj.customer.entity.Customer;
import com.pzj.framework.context.Result;
import com.pzj.framework.context.ServiceContext;
import com.pzj.modules.appapi.seat.model.RowSeats;
import com.pzj.modules.appapi.seat.model.Seat;
import com.pzj.modules.appapi.seat.model.SeatArea;
import com.pzj.product.service.ISkuProductService;
import com.pzj.product.vo.product.SkuProduct;
import com.pzj.product.vo.voParam.resultParam.SkuProductResultVO;

@Component
public class SeatService1 {
    //    @Autowired
    //    private OrderUtils         orderUtils;

    @Autowired
    private ISkuProductService productService;
    @Autowired
    private StockQueryService  stockQueryService;
    @Autowired
    private SeatService        seatService;
    @Autowired
    private SeatChartService   seatChartService;

    public SeatArea getSeatArea(String date, String area, Long scenicId, Customer customer, String sort, String productId) {
        SeatArea seatArea = new SeatArea();
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
            Result<ArrayList<SeatChartModel>> querySeatChartBy = seatChartService.querySeatChartByScenicAndAreaRel(seatChartModel,
                ServiceContext.getServiceContext());
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
            //查询库存规则
            StockQueryRequestModel stockModel = new StockQueryRequestModel();
            stockModel.setRuleId(skuProduct.getStockRuleId());
            stockModel.setStockTime(date);
            Result<StockModel> queryStockByDate = stockQueryService.queryStockByDate(stockModel, ServiceContext.getServiceContext());
            //查询所有已经被占用不可选的座位
            UserSeatModel userSeatModel = new UserSeatModel();
            userSeatModel.setOperateUserId(customer.getId());
            Long stockId = queryStockByDate.getData().getId();
            userSeatModel.setStockId(stockId);
            Result<UserSeatResponse> queryAlreadyOccupySeat = seatService.queryAlreadyOccupySeat(userSeatModel, ServiceContext.getServiceContext());
            //所有已经被占用的座位集合
            List<String> occupySeat = queryAlreadyOccupySeat.getData().getOccupySeat();

            seatArea.setPlayId(skuProduct.getTheaterId().toString());

            List<RowSeats> rowSeatList = new ArrayList<>();//座位的列数

            String[] split = chartKey.split(";");
            for (int i = 0; i < split.length; i++) {
                RowSeats rowSeats = new RowSeats();
                String[] split2 = split[i].split(",");
                String row = split2[0];
                rowSeats.setIndex(i);
                rowSeats.setRowNum(row);
                List<Seat> list = rowSeats.getList();
                for (int j = 1; j < split2.length; j++) {
                    Seat seat = new Seat();
                    seat.setIndex(i);
                    String num = split2[j];
                    String seatNum = "";
                    seatNum = row + "_" + num;
                    seat.setSeatNum(seatNum);
                    seat.setStyle("active");
                    list.add(seat);
                    if (num.equals("_")) {
                        seat.setStyle("");
                    } else {
                        if (occupySeat.contains(seatNum)) {
                            seat.setStyle("disabled");
                            continue;
                        }
                    }

                }
                rowSeatList.add(rowSeats);
            }
            seatArea.setAreaDesc(area.toUpperCase() + "区");
            seatArea.setImage("");
            seatArea.setRows(split.length);
            seatArea.setSeatCount(seatCount);
            seatArea.setArea(area);
            seatArea.setRowSeatList(rowSeatList);
            return seatArea;
        } catch (Exception e) {
            e.printStackTrace();
            return seatArea;
        }
    }

    public int checkSeat(Long productId, Date playTime, String seat, Integer sort, String area, Customer customer) {
        try {
            SkuProductResultVO skuProduct = productService.findSkuProductById(productId);
            Long stockRuleIdk = skuProduct.getSkuProduct().getStockRuleId();
            String dateStr = DateUtils.getYearMonthDayString(playTime);
            StockQueryRequestModel stockQueryModel = new StockQueryRequestModel();
            stockQueryModel.setRuleId(stockRuleIdk);
            stockQueryModel.setStockTime(dateStr);
            Result<StockModel> result = stockQueryService.queryStockByDate(stockQueryModel, ServiceContext.getServiceContext());
            StockModel model = result.getData();

            UserSeatModel queryModel = new UserSeatModel();
            queryModel.setStockId(model.getId());
            queryModel.setOperateUserId(customer.getId());
            Result<UserSeatResponse> resultResponse = seatService.queryAlreadyOccupySeat(queryModel, ServiceContext.getServiceContext());
            UserSeatResponse response = resultResponse.getData();
            if (response.getOccupySeat().contains(seat)) {
                return 0;
            }

            return 1;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    /*
    public List<SeatStock> queryStock(Date date, Integer matchs, Long scenicId) {
        List<SeatStock> queryStock = null;
        try {
            queryStock = seatStockService.getStocks(date, matchs, scenicId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return queryStock;
    }
    */
}
