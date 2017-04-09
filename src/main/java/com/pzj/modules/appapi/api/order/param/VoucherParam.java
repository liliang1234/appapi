package com.pzj.modules.appapi.api.order.param;

import java.util.List;

public class VoucherParam {

    private Integer      isUnique = 0;      //一证一票或非一证一票，一证一票为1

    private Long         skuId;
    private String       contactName;
    private String       contactMobile;
    private String       contactIdCard;
    private Integer      voucherContentType;

    private String       address;           //特产的座位

    private String       playDate;          //游玩时间
    private Integer      num      = 1;      //游客数量，特产份数，房间件数等等，默认最小为1
    private String       showDate;          //演出时间
    private Long         showId;            //演出id，根据这个可以查出来座位图
    private List<String> seatList;          //演出座位，根据这个可以判断座位是否可以下

    public Integer getIsUnique() {
        return isUnique;
    }

    public void setIsUnique(Integer isUnique) {
        this.isUnique = isUnique;
    }

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactMobile() {
        return contactMobile;
    }

    public void setContactMobile(String contactMobile) {
        this.contactMobile = contactMobile;
    }

    public String getContactIdCard() {
        return contactIdCard;
    }

    public void setContactIdCard(String contactIdCard) {
        this.contactIdCard = contactIdCard;
    }

    public Integer getVoucherContentType() {
        return voucherContentType;
    }

    public void setVoucherContentType(Integer voucherContentType) {
        this.voucherContentType = voucherContentType;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPlayDate() {
        return playDate;
    }

    public void setPlayDate(String playDate) {
        this.playDate = playDate;
    }

    public String getShowDate() {
        return showDate;
    }

    public void setShowDate(String showDate) {
        this.showDate = showDate;
    }

    public Long getShowId() {
        return showId;
    }

    public void setShowId(Long showId) {
        this.showId = showId;
    }

    public List<String> getSeatList() {
        return seatList;
    }

    public void setSeatList(List<String> seatList) {
        this.seatList = seatList;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

}
