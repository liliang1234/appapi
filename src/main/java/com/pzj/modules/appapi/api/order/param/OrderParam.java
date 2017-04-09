package com.pzj.modules.appapi.api.order.param;

public class OrderParam {
    private Long    resellerId;   //分销商ID
    private Long    guideId;      //导游id
    private Long    secResellerId;//旅行社部门ID
    private Long    supplierId;   //供应商ID
    private Long    channelId;    //渠道id
    private String  salesPort;    //销售端口

    private Integer ticketVarie;  //团散

    public Long getResellerId() {
        return resellerId;
    }

    public void setResellerId(Long resellerId) {
        this.resellerId = resellerId;
    }

    public Long getGuideId() {
        return guideId;
    }

    public void setGuideId(Long guideId) {
        this.guideId = guideId;
    }

    public Long getSecResellerId() {
        return secResellerId;
    }

    public void setSecResellerId(Long secResellerId) {
        this.secResellerId = secResellerId;
    }

    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }

    public String getSalesPort() {
        return salesPort;
    }

    public void setSalesPort(String salesPort) {
        this.salesPort = salesPort;
    }

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    public Integer getTicketVarie() {
        return ticketVarie;
    }

    public void setTicketVarie(Integer ticketVarie) {
        this.ticketVarie = ticketVarie;
    }

}
