package com.pzj.modules.appapi.api.dto;

import java.io.Serializable;
import java.util.List;

import com.pzj.appapi.vo.OrderVo;

/**
 * Created by Administrator on 2016-6-24.
 */
public class RefundMoneyDto implements Serializable {
    /**  */
    private static final long  serialVersionUID = -1818710754026276348L;

    String                     orderId          = null;

    String                     confirm          = null;
    Integer                    orderStatus      = null;

    List<RefundMoneyTicketDto> tickets;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getConfirm() {
        return confirm;
    }

    public void setConfirm(String confirm) {
        this.confirm = confirm;
    }

    public Integer getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(Integer orderStatus) {
        this.orderStatus = orderStatus;
    }

    public List<RefundMoneyTicketDto> getTickets() {
        return tickets;
    }

    public void setTickets(List<RefundMoneyTicketDto> tickets) {
        this.tickets = tickets;
    }

    public static class RefundMoneyTicketDto extends OrderVo.TicketVo {

        /**  */
        private static final long serialVersionUID = -3146116625625371962L;
        /** 购买时付款的价格 */
        private Double            merchPrice;
        /** 核销凭证ID */
        private Long              voucherId;

        public Double getMerchPrice() {
            return merchPrice;
        }

        public void setMerchPrice(Double merchPrice) {
            this.merchPrice = merchPrice;
        }

        public Long getVoucherId() {
            return voucherId;
        }

        public void setVoucherId(Long voucherId) {
            this.voucherId = voucherId;
        }

        /*
        Integer timeType  = null;
        Integer days      = null;
        Double  hours     = null;
        Integer minutes   = null;
        Date    startTime = null;
        String  proCategory;
        
        public Integer getTimeType() {
            return timeType;
        }
        
        public void setTimeType(Integer timeType) {
            this.timeType = timeType;
        }
        
        public Integer getDays() {
            return days;
        }
        
        public void setDays(Integer days) {
            this.days = days;
        }
        
        public Double getHours() {
            return hours;
        }
        
        public void setHours(Double hours) {
            this.hours = hours;
        }
        
        public Integer getMinutes() {
            return minutes;
        }
        
        public void setMinutes(Integer minutes) {
            this.minutes = minutes;
        }
        
        public Date getStartTime() {
            return startTime;
        }
        
        @JSONField(format = "yyyy-MM-dd HH:mm:ss")
        public void setStartTime(Date startTime) {
            this.startTime = startTime;
        }
        
        public String getProCategory() {
            return proCategory;
        }
        
        public void setProCategory(String proCategory) {
            this.proCategory = proCategory;
        }*/
    }
}
