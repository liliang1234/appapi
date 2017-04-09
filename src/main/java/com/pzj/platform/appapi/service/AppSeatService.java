package com.pzj.platform.appapi.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
public class AppSeatService {
    @Autowired
    private SeatChartService   seatChartService;
    @Autowired
    private ISkuProductService productService;
    @Autowired
    private StockQueryService  stockQueryService;
    @Autowired
    private SeatService        seatService;

    public SeatArea getSeatArea(String date, String area, Long scenicId, Customer customer, String sort, String productId) {
        SeatArea seatArea = new SeatArea();
        try {
            //查询产品信息
            SkuProductResultVO findSkuProductById = productService.findSkuProductById(Long.valueOf(productId));
            System.out.println(findSkuProductById);
            //SkuProductResultVO findSkuProductById =new SkuProductResultVO();
            SkuProduct skuProduct = findSkuProductById.getSkuProduct();
            //无限库存的判断,如果是无限库存,则不继续往下进行.
            if(skuProduct.getUnlimitedInventory()){
            	return seatArea;
            }
            //加载座位图
            SeatChartModel seatChartModel = new SeatChartModel();
            seatChartModel.setScenicId(scenicId);
            seatChartModel.setAreaScreeningsId(skuProduct.getTheaterId());
            Result<ArrayList<SeatChartModel>> querySeatChartBy = seatChartService.querySeatChartByScenicAndAreaRel(seatChartModel,
                ServiceContext.getServiceContext());
            Integer seatCount = null;//座位总数
            String chartKeyA = "";//座位图1
            String chartKeyB = "";//座位图2
            ArrayList<SeatChartModel> seatChartModelList = querySeatChartBy.getData();
            //判断座位是B单还是B双,目前只针对边城,一场次包含两个座位
            for (SeatChartModel sc : seatChartModelList) {
                if (sc.getSort() == null || sc.getSort() == 1) {
                    seatCount = sc.getTotalSeats();
                    chartKeyA = sc.getSeatMaps();
                    System.out.println("-------------座位图1的数据----------------"+chartKeyA);
                } else {
                    seatCount = sc.getTotalSeats();
                    chartKeyB = sc.getSeatMaps();
                    seatArea.setMultiArea(true);
                    System.out.println("-------------座位图2的数据----------------"+chartKeyB);
                }
            }
            //查询库存规则
            StockQueryRequestModel stockModel = new StockQueryRequestModel();
            stockModel.setRuleId(skuProduct.getStockRuleId());
            stockModel.setStockTime(date);
            Result<StockModel> queryStockByDate = stockQueryService.queryStockByRule(stockModel, ServiceContext.getServiceContext());
            //查询所有已经被占用不可选的座位
            UserSeatModel userSeatModel = new UserSeatModel();
            userSeatModel.setOperateUserId(customer.getId());
            Long stockId = queryStockByDate.getData().getId();
            userSeatModel.setStockId(stockId);
            Result<UserSeatResponse> queryAlreadyOccupySeat = seatService.queryAlreadyOccupySeat(userSeatModel, ServiceContext.getServiceContext());
            //所有已经被占用的座位集合
            List<String> occupySeat = queryAlreadyOccupySeat.getData().getOccupySeat();
            if (occupySeat == null)
                occupySeat = new ArrayList<>();
            seatArea.setPlayId(skuProduct.getTheaterId().toString());

            List<RowSeats> rowSeatList = new ArrayList<>();//座位的列数
            String[] split =null;
            if(sort.equals("1")){
            	split = chartKeyA.split(";");
            }else{
            	split =chartKeyB.split(";");
            }
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

            seatArea.setAreaDesc(area);
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
}
