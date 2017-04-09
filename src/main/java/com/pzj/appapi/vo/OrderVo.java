package com.pzj.appapi.vo;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import com.pzj.trade.order.vo.PurchaseProductVO;

public class OrderVo implements Serializable {

    /** 以下是字段同com.pzj.trade.order.vo.TradeOrderVO中的一致 **/

    /**  */
    private static final long serialVersionUID = 1L;

    /** 分销商ID 必填*/
    private long              resellerId;

    /**
     * 付款者ID. 必填项. 当前操作者下单时, 可以指定付款者ID, 只有具备结算能力的结算人.
     */
    private long              payerId;

    /** 操作者ID 必填*/
    private long              operatorId;

    /** 旅行社 */
    private long              travel;

    /** 旅行社部门 */
    private long              travelDepartId;

    /** 导游ID */
    private long              guideId;

    /** 分销端代理商 */
    private long              resellerAgentId;

    /** 供应端代理商 */
    private long              supplierAgentId;

    /** 销售端口（APP, OTA, 微店）必填 */
    private int               salePort;

    /** 联系人 */
    private String            contactee;

    /** 联系人电话 */
    private String            contactMobile;
    /** 联系人身份证号 */
    private String            idcard_no;

    /** 订单备注信息. */
    private String            remark;

    /** 票信息 */
    /** 以下是字段是com.pzj.trade.order.vo.TradeOrderVO中的 List<PurchaseProductVO> products **/
    private List<TicketVo>    tickets;

    /** 以下是字段是com.pzj.trade.order.vo.TradeOrderVO中的没有的 **/

    private String            orderId;

    private String            orderType;

    /** 产品组ID **/
    private Long              productInfoId;
    /** 产品所属供应商ID **/
    private Long              supplierId;

    //private String            bookMobile;

    //private String            bookName;

    private Timestamp         buyDate;

    private Timestamp         buyDateEnd;

    private Timestamp         showTime;

    private Timestamp         showTimeEnd;

    /**
     * 演出区域
     */
    private String            area;
    private String            matchs;
    private String            seat;

    /**
     * 景区ID
     */
    private Long              scenicId;
    /**
     * 景区名称
     */
    private String            scenicName;

    /**
     * 渠道ID
     */
    private Long              channelId;

    private Long              skuId;

    /**
     * 产品类型
     */
    private Integer           productType;

    /**
     * 商品团散 团:1 散：0
     *
     * 团票常量：
     * com.pzj.sale.api.order.service.OrderUtils#GROUP_ORDER
     * 散票常量：
     * com.pzj.sale.api.order.service.OrderUtils#BULK_ORDER
     */

    private String            varie;

    private String            buyCard;

    /**
     * 上车地址
     */
    private String            startAddress;
    /**
     * 下车地址
     */
    private String            endAddress;
    /**
     *  用车时间
     */
    private String            carTime;

    /**
     * 消费日期
     */
    private Timestamp         useTime;

    /**
     * 航班号
     */
    private String            flightNumber;
    /**
     * 列车号
     */
    private String            trainNumber;
    /**
     * 收货地址
     */
    private String            receiptAddress;

    /**
     * 联系人拼音
     */
    private String            contactSpelling;
    /**
     * 联系人邮箱
     */
    private String            contactEmail;

    /**
     * 填单项：预计到店时间
     * 
     * */
    private String            arrivalTime;

    public String getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(String arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    /**
     * Getter method for property <tt>contactSpelling</tt>.
     * 
     * @return property value of contactSpelling
     */
    public String getContactSpelling() {
        return contactSpelling;
    }

    /**
     * Setter method for property <tt>contactSpelling</tt>.
     * 
     * @param contactSpelling value to be assigned to property contactSpelling
     */
    public void setContactSpelling(String contactSpelling) {
        this.contactSpelling = contactSpelling;
    }

    /**
     * Getter method for property <tt>contactEmail</tt>.
     * 
     * @return property value of contactEmail
     */
    public String getContactEmail() {
        return contactEmail;
    }

    /**
     * Setter method for property <tt>contactEmail</tt>.
     * 
     * @param contactEmail value to be assigned to property contactEmail
     */
    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    /**
     * Getter method for property <tt>scenicId</tt>.
     * 
     * @return property value of scenicId
     */
    public Long getScenicId() {
        return scenicId;
    }

    /**
     * Getter method for property <tt>useTime</tt>.
     * 
     * @return property value of useTime
     */
    public Timestamp getUseTime() {
        return useTime;
    }

    /**
     * Setter method for property <tt>useTime</tt>.
     * 
     * @param useTime value to be assigned to property useTime
     */
    public void setUseTime(Timestamp useTime) {
        this.useTime = useTime;
    }

    /**
     * Setter method for property <tt>scenicId</tt>.
     * 
     * @param scenicId value to be assigned to property scenicId
     */
    public void setScenicId(Long scenicId) {
        this.scenicId = scenicId;
    }

    /**
     * Getter method for property <tt>startAddress</tt>.
     * 
     * @return property value of startAddress
     */
    public String getStartAddress() {
        return startAddress;
    }

    /**
     * Setter method for property <tt>startAddress</tt>.
     * 
     * @param startAddress value to be assigned to property startAddress
     */
    public void setStartAddress(String startAddress) {
        this.startAddress = startAddress;
    }

    /**
     * Getter method for property <tt>carTime</tt>.
     * 
     * @return property value of carTime
     */
    public String getCarTime() {
        return carTime;
    }

    /**
     * Setter method for property <tt>carTime</tt>.
     * 
     * @param carTime value to be assigned to property carTime
     */
    public void setCarTime(String carTime) {
        this.carTime = carTime;
    }

    /**
     * Getter method for property <tt>endAddress</tt>.
     * 
     * @return property value of endAddress
     */
    public String getEndAddress() {
        return endAddress;
    }

    /**
     * Setter method for property <tt>endAddress</tt>.
     * 
     * @param endAddress value to be assigned to property endAddress
     */
    public void setEndAddress(String endAddress) {
        this.endAddress = endAddress;
    }

    /**
     * Getter method for property <tt>flightNumber</tt>.
     * 
     * @return property value of flightNumber
     */
    public String getFlightNumber() {
        return flightNumber;
    }

    /**
     * Setter method for property <tt>flightNumber</tt>.
     * 
     * @param flightNumber value to be assigned to property flightNumber
     */
    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    /**
     * Getter method for property <tt>trainNumber</tt>.
     * 
     * @return property value of trainNumber
     */
    public String getTrainNumber() {
        return trainNumber;
    }

    /**
     * Setter method for property <tt>trainNumber</tt>.
     * 
     * @param trainNumber value to be assigned to property trainNumber
     */
    public void setTrainNumber(String trainNumber) {
        this.trainNumber = trainNumber;
    }

    /**
     * Getter method for property <tt>receiptAddress</tt>.
     * 
     * @return property value of receiptAddress
     */
    public String getReceiptAddress() {
        return receiptAddress;
    }

    /**
     * Setter method for property <tt>receiptAddress</tt>.
     * 
     * @param receiptAddress value to be assigned to property receiptAddress
     */
    public void setReceiptAddress(String receiptAddress) {
        this.receiptAddress = receiptAddress;
    }

    /**
     * Getter method for property <tt>buyCard</tt>.
     * 
     * @return property value of buyCard
     */
    public String getBuyCard() {
        return buyCard;
    }

    /**
     * Setter method for property <tt>buyCard</tt>.
     * 
     * @param buyCard value to be assigned to property buyCard
     */
    public void setBuyCard(String buyCard) {
        this.buyCard = buyCard;
    }

    public Long getProductInfoId() {
        return productInfoId;
    }

    public void setProductInfoId(Long productInfoId) {
        this.productInfoId = productInfoId;
    }

    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }

    @Deprecated
    public String getBookName() {
        return getContactee();
    }

    @Deprecated
    public void setBookName(String bookName) {
        setContactee(bookName);
    }

    @Deprecated
    public String getBookMobile() {
        return getContactMobile();
    }

    @Deprecated
    public void setBookMobile(String bookMobile) {
        setContactMobile(bookMobile);
    }

    public Timestamp getBuyDate() {
        return buyDate;
    }

    public void setBuyDate(Timestamp buyDate) {
        this.buyDate = buyDate;
    }

    public Timestamp getShowTime() {
        return showTime;
    }

    public void setShowTime(Timestamp showTime) {
        this.showTime = showTime;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Long getScienicId() {
        return scenicId;
    }

    public void setScienicId(Long scenicId) {
        this.scenicId = scenicId;
    }

    public String getMatchs() {
        return matchs;
    }

    public void setMatchs(String matchs) {
        this.matchs = matchs;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getSeat() {
        return seat;
    }

    public void setSeat(String seat) {
        this.seat = seat;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }

    public Timestamp getBuyDateEnd() {
        return buyDateEnd;
    }

    public void setBuyDateEnd(Timestamp buyDateEnd) {
        this.buyDateEnd = buyDateEnd;
    }

    public Integer getProType() {
        return getProductType();
    }

    public void setProType(Integer proType) {
        setProductType(proType);
    }

    public String getIdcard_no() {
        return idcard_no;
    }

    public void setIdcard_no(String idcard_no) {
        this.idcard_no = idcard_no;
    }

    public String getVarie() {
        return varie;
    }

    public void setVarie(String varie) {
        this.varie = varie;
    }

    public int getSalePort() {
        return salePort;
    }

    public void setSalePort(int salePort) {
        this.salePort = salePort;
    }

    public Integer getProductType() {
        return productType;
    }

    public void setProductType(Integer productType) {
        this.productType = productType;
    }

    public Timestamp getShowTimeEnd() {
        return showTimeEnd;
    }

    public void setShowTimeEnd(Timestamp showTimeEnd) {
        this.showTimeEnd = showTimeEnd;
    }

    public String getScenicName() {
        return scenicName;
    }

    public void setScenicName(String scenicName) {
        this.scenicName = scenicName;
    }

    public static class TicketVo implements Serializable {

        private static final long serialVersionUID = -9193885408105139272L;

        /** 商品ID */
        private String            merchId;

        /** 产品ID */
        private Long              productId;

        /** 商品数量 */
        private int               productNum;

        /** 渠道ID */
        private long              channelId;

        /** 身份证号 **/
        private String            buyCard;
        /** 游客姓名 **/
        private String            buyName;
        private String            contactee;                               //兼容旧版

        private PurchaseProductVO purchaseProductVO;

        private String            area;
        private String            matchs;                                  // 场次
        private String            seat;

        private Timestamp         showTime;

        /**
         * 库存id，创建voucher需要
         */
        private Long              stockId;

        /**
         * Getter method for property <tt>stockId</tt>.
         * 
         * @return property value of stockId
         */
        public Long getStockId() {
            return stockId;
        }

        /**
         * Setter method for property <tt>stockId</tt>.
         * 
         * @param stockId value to be assigned to property stockId
         */
        public void setStockId(Long stockId) {
            this.stockId = stockId;
        }

        /**
         * 政策ID
         */
        private Long strategyId;

        /**
         * @return the productId
         */
        public Long getProductId() {
            return productId;
        }

        /**
         * @param productId the productId to set
         */
        public void setProductId(Long productId) {
            this.productId = productId;
        }

        /**
         * @return the productNum
         */
        public int getProductNum() {
            return productNum;
        }

        /**
         * @param productNum the productNum to set
         */
        public void setProductNum(int productNum) {
            this.productNum = productNum;
        }

        /**
         * @return the channelId
         */
        public long getChannelId() {
            return channelId;
        }

        /**
         * @param channelId the channelId to set
         */
        public void setChannelId(long channelId) {
            this.channelId = channelId;
        }

        public String getBuyCard() {
            return buyCard;
        }

        public String getBuyName() {
            return buyName;
        }

        public void setBuyName(String buyName) {
            this.buyName = buyName;
        }

        public String getContactee() {
            return contactee;
        }

        public void setContactee(String contactee) {
            this.contactee = contactee;
        }

        public void setBuyCard(String buyCard) {
            this.buyCard = buyCard;
        }

        public PurchaseProductVO getPurchaseProductVO() {
            return purchaseProductVO;
        }

        public void setPurchaseProductVO(PurchaseProductVO purchaseProductVO) {
            this.purchaseProductVO = purchaseProductVO;
        }

        public String getMerchId() {
            return merchId;
        }

        public void setMerchId(String merchId) {
            this.merchId = merchId;
        }

        public String getArea() {
            return area;
        }

        public void setArea(String area) {
            this.area = area;
        }

        public String getMatchs() {
            return matchs;
        }

        public void setMatchs(String matchs) {
            this.matchs = matchs;
        }

        public String getSeat() {
            return seat;
        }

        public void setSeat(String seat) {
            this.seat = seat;
        }

        public Long getStrategyId() {
            return strategyId;
        }

        public void setStrategyId(Long strategyId) {
            this.strategyId = strategyId;
        }

        public Timestamp getShowTime() {
            return showTime;
        }

        public void setShowTime(Timestamp showTime) {
            this.showTime = showTime;
        }
    }

    /**
     * @return the resellerId
     */
    public long getResellerId() {
        return resellerId;
    }

    /**
     * @param resellerId the resellerId to set
     */
    public void setResellerId(long resellerId) {
        this.resellerId = resellerId;
    }

    /**
     * @return the payerId
     */
    public long getPayerId() {
        return payerId;
    }

    /**
     * @param payerId the payerId to set
     */
    public void setPayerId(long payerId) {
        this.payerId = payerId;
    }

    /**
     * @return the operatorId
     */
    public long getOperatorId() {
        return operatorId;
    }

    /**
     * @param operatorId the operatorId to set
     */
    public void setOperatorId(long operatorId) {
        this.operatorId = operatorId;
    }

    /**
     * @return the travel
     */
    public long getTravel() {
        return travel;
    }

    /**
     * @param travel the travel to set
     */
    public void setTravel(long travel) {
        this.travel = travel;
    }

    /**
     * @return the travelDepartId
     */
    public long getTravelDepartId() {
        return travelDepartId;
    }

    /**
     * @param travelDepartId the travelDepartId to set
     */
    public void setTravelDepartId(long travelDepartId) {
        this.travelDepartId = travelDepartId;
    }

    /**
     * @return the guideId
     */
    public long getGuideId() {
        return guideId;
    }

    /**
     * @param guideId the guideId to set
     */
    public void setGuideId(long guideId) {
        this.guideId = guideId;
    }

    /**
     * @return the resellerAgentId
     */
    public long getResellerAgentId() {
        return resellerAgentId;
    }

    /**
     * @param resellerAgentId the resellerAgentId to set
     */
    public void setResellerAgentId(long resellerAgentId) {
        this.resellerAgentId = resellerAgentId;
    }

    /**
     * @return the supplierAgentId
     */
    public long getSupplierAgentId() {
        return supplierAgentId;
    }

    /**
     * @param supplierAgentId the supplierAgentId to set
     */
    public void setSupplierAgentId(long supplierAgentId) {
        this.supplierAgentId = supplierAgentId;
    }

    /**
     * @return the contactee
     */
    public String getContactee() {
        return contactee;
    }

    /**
     * @param contactee the contactee to set
     */
    public void setContactee(String contactee) {
        this.contactee = contactee;
    }

    /**
     * @return the contactMobile
     */
    public String getContactMobile() {
        return contactMobile;
    }

    /**
     * @param contactMobile the contactMobile to set
     */
    public void setContactMobile(String contactMobile) {
        this.contactMobile = contactMobile;
    }

    /**
     * @return the remark
     */
    public String getRemark() {
        return remark;
    }

    /**
     * @param remark the remark to set
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }

    /**
     * @return the tickets
     */
    public List<TicketVo> getTickets() {
        return tickets;
    }

    /**
     * @param tickets the tickets to set
     */
    public void setTickets(List<TicketVo> tickets) {
        this.tickets = tickets;
    }

}
