package com.pzj.common.params;

/**
 * Created by Administrator on 2016-6-27.
 */
public interface AppapiParams {
    /**
     * 字典Key。保存的值为供应商ID，如果在字典中存在，表示这个供应商的订单，只能整单退。
     */
    String NeedRefundAllMoneySupplierIds = "appapi:NeedRefundAllMoneySupplierIds";
}
