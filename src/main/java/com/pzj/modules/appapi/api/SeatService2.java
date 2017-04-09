package com.pzj.modules.appapi.api;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.pzj.common.util.CheckUtils;
import com.pzj.common.util.DateUtils;
import com.pzj.customer.entity.Customer;
import com.pzj.modules.appapi.entity.CodeHandle;
import com.pzj.modules.appapi.entity.JsonEntity;
import com.pzj.modules.appapi.seat.SeatService1;
import com.pzj.sale.api.order.service.OrderUtils;
import com.pzj.sale.api.show.vo.SeatNode;

@Component
public class SeatService2 {

    @Autowired
    private SeatService1 seatService1;

    @Autowired
    private OrderUtils   orderUtils;

    @RenderMapping("toSeat.do")
    public String toSeat() {
        return "seat";
    }

    public JsonEntity addTicketSeat(JSONObject data, Customer customer, JsonEntity json) {
        return null;
    }

    public JsonEntity delCacheSeat(JSONObject data, Customer customer, JsonEntity json) {
        return null;
    }

    public JsonEntity addCacheSeat(JSONObject data, Customer customer, JsonEntity json) throws ParseException {
        String scienicId = (data.containsKey("scienicId")) ? data.getString("scienicId") : "";
        String playId = (data.containsKey("playId")) ? data.getString("playId") : "";
        String playTime = (data.containsKey("playTime")) ? data.getString("playTime") : "";
        String seat = (data.containsKey("seat")) ? data.getString("seat") : "";
        String showScreening = (data.containsKey("showScreening")) ? data.getString("showScreening") : "";
        String area = (data.containsKey("area")) ? data.getString("area") : "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date date = sdf.parse(playTime);
        //        int cacheSeat = this.seatService1.addUserCacheSeat(scienicId, playId, date, seat, Integer.valueOf(Integer.parseInt(showScreening)), area, customer);
        //        if (cacheSeat == 1) {
        //            json.setCode(CodeHandle.SUCCESS.getCode());
        //            json.setCode(CodeHandle.SUCCESS.getMessage());
        //        } else {
        //            json.setCode(CodeHandle.FAILURE.getCode());
        //            json.setCode(CodeHandle.FAILURE.getMessage());
        //        }

        return json;
    }

    public JsonEntity getTheaterNum(JSONObject data, Customer customer, JsonEntity json) {
        return null;
    }

    public JsonEntity getT(JSONObject data, Customer customer, JsonEntity json) {
        return null;
    }

    public JsonEntity removeSeats(JSONObject data, Customer customer, JsonEntity json) {
        String matchs = (data.containsKey("matchs")) ? data.getString("matchs") : "";
        String date = (data.containsKey("date")) ? data.getString("date") : "";
        String area = (data.containsKey("area")) ? data.getString("area") : "";
        String scienicId = (data.containsKey("scienicId")) ? data.getString("scienicId") : "";
        String seats = (data.containsKey("seats")) ? data.getString("seats") : "";

        if ((CheckUtils.isNull(matchs)) || (CheckUtils.isNull(date)) || (CheckUtils.isNull(area)) || (CheckUtils.isNull(scienicId))
            || (CheckUtils.isNull(seats))) {
            json.setCode(CodeHandle.CODE_90001.getCode());
            json.setMessage(CodeHandle.CODE_90001.getMessage());
            return json;
        }
        //        this.seatService1.delAllSeat(Integer.valueOf(Integer.parseInt(matchs)), date, area, Long.valueOf(Long.parseLong(scienicId)), customer, seats);
        json.setCode(CodeHandle.SUCCESS.getCode());
        json.setMessage(CodeHandle.SUCCESS.getMessage());
        return json;
    }

    /**
     * 查询当前登录用户选中的座位
     * @param data
     * @param customer
     * @param json
     * @return
     */
    public JsonEntity selectedSeatNum(JSONObject data, Customer customer, JsonEntity json) {
        String scienicId = (data.containsKey("scienicId")) ? data.getString("scienicId") : "";
        String showStartTime = (data.containsKey("showStartTime")) ? data.getString("showStartTime") : "";
        String matchs = (data.containsKey("matchs")) ? data.getString("matchs") : "";
        String area = (data.containsKey("area")) ? data.getString("area") : "";
        String userId = customer.getId() + "";
        Map<String, Object> jsonObject = Maps.newHashMap();
        StringBuffer bufferStr = new StringBuffer();
        try {
            List<SeatNode> userSeats = orderUtils.getUserSeats(scienicId, DateUtils.parseDate(showStartTime), matchs, area, userId);
            for (SeatNode seatNode : userSeats) {
                String seatNum = seatNode.getSeat();
                bufferStr.append(seatNum).append(",");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        jsonObject.put("checkNumber", bufferStr.toString());
        json.setResponseBody(jsonObject);
        json.setCode(CodeHandle.SUCCESS.getCode());
        json.setMessage(CodeHandle.SUCCESS.getMessage());
        return json;
    }
}