package com.pzj.platform.appapi.model;

import java.io.Serializable;

public class VoucherModel implements Serializable {

    private static final long serialVersionUID = 1L;
    private long              voucherId;
    /** 身份证号或二维码  */
    private String            voucherContent;
    /** 凭证介质类型：（ 1 身份证号   2 二维码或条码及其辅助码  3 手机号) */
    private Integer           voucherContentType;
    /** 凭证图片地址 */
    private String            voucherContentImageUrl;
    /** 凭证人手机号 */
    private String            contactMobile;
    /** 凭证人姓名 */
    private String            contactName;

    /** 凭证状态(-1:不可用；0：可以使用；1核销完毕；2：退单) 参照 VoucherState枚举对象 */
    private Integer           voucherState;

    private String            merchId;
    private int               merchState;             //商品状态

    private String            screenings;            //演出场次
    private String            region;                //演出区域
    private String            areaDesc;              //区域描述
    private boolean           needSeat;              //是否需要选座
    private String            seatNumbers;            //座位号、中间用,隔开 
    /**  商品是否存在退款中的 0否  1是*/
    private int               is_refunding;

    public int getIs_refunding() {
        return is_refunding;
    }

    public void setIs_refunding(int is_refunding) {
        this.is_refunding = is_refunding;
    }

    public long getVoucherId() {
        return voucherId;
    }

    public void setVoucherId(long voucherId) {
        this.voucherId = voucherId;
    }

    public String getVoucherContent() {
        return voucherContent;
    }

    public void setVoucherContent(String voucherContent) {
        this.voucherContent = voucherContent;
    }

    public Integer getVoucherContentType() {
        return voucherContentType;
    }

    public void setVoucherContentType(Integer voucherContentType) {
        this.voucherContentType = voucherContentType;
    }

    public String getVoucherContentImageUrl() {
        return voucherContentImageUrl;
    }

    public void setVoucherContentImageUrl(String voucherContentImageUrl) {
        this.voucherContentImageUrl = voucherContentImageUrl;
    }

    public String getContactMobile() {
        return contactMobile;
    }

    public void setContactMobile(String contactMobile) {
        this.contactMobile = contactMobile;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public Integer getVoucherState() {
        return voucherState;
    }

    public void setVoucherState(Integer voucherState) {
        this.voucherState = voucherState;
    }

    public String getMerchId() {
        return merchId;
    }

    public void setMerchId(String merchId) {
        this.merchId = merchId;
    }

    public int getMerchState() {
        return merchState;
    }

    public void setMerchState(int merchState) {
        this.merchState = merchState;
    }

    public String getScreenings() {
        return screenings;
    }

    public void setScreenings(String screenings) {
        this.screenings = screenings;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getSeatNumbers() {
        return seatNumbers;
    }

    public void setSeatNumbers(String seatNumbers) {
        this.seatNumbers = seatNumbers;
    }

    public boolean isNeedSeat() {
        return needSeat;
    }

    public void setNeedSeat(boolean needSeat) {
        this.needSeat = needSeat;
    }

    public String getAreaDesc() {
        return areaDesc;
    }

    public void setAreaDesc(String areaDesc) {
        this.areaDesc = areaDesc;
    }

}
