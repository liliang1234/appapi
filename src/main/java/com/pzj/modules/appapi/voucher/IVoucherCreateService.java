/**
 * piaozhijia.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package com.pzj.modules.appapi.voucher;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import com.pzj.appapi.vo.OrderVo;
import com.pzj.product.vo.product.SpuProduct;
import com.pzj.voucher.entity.VoucherEntity;

/**
 *
 * @author jinliang
 * @version $Id: IAppVoucherService.java, v 0.1 2016年6月8日 下午6:07:50 jinliang Exp $
 */
public interface IVoucherCreateService {

    VoucherEntity createCommonVoucher(Map<Long, Integer> productInfoMap, Integer voucherCategory, Long supplierId, String contactMobile, String contactName,
                                      Timestamp startTime, Timestamp expireTime, String address, Integer consumerCardType, SpuProduct spu) throws Exception;

    /**
     *
     * 创建非一证一票的voucher
     * @param productInfoMap 传入参数为产品id, 个数
     * @param voucherCategory 本次购买的产品类别
     * @param supplierId
     * @param contactMobile
     * @param contactName
     * @param startTime
     * @param expireTime
     * @param showStartTime
     * @param showEndTime
     * @param address
     * @return
     * @throws Exception
     */
    VoucherEntity createCommonVoucher(Map<Long, OrderVo.TicketVo> productInfoMap, Integer voucherCategory, Long supplierId, String contactMobile,
                                      String contactName, Timestamp startTime, Timestamp expireTime, Timestamp showStartTime, Timestamp showEndTime,
                                      String address, String scenic, String scenicId, Integer ticketVarie, SpuProduct spu, Integer consumerCardType)
                                                                                                                                                    throws Exception;

    /**
     * 创建一证一票的voucher
     *
     * @param productInfoMap 传入参数为产品id, 身份证号
     * @param voucherCategory
     * @param supplierId
     * @param contactMobile
     * @param startTime
     * @param expireTime
     * @param showStartTime
     * @param showEndTime
     * @param address
     * @return
     * @throws Exception
     */
    List<VoucherEntity> createOneCardVoucher(Map<String, OrderVo.TicketVo> productInfoMap, Integer voucherCategory, Long supplierId, String contactMobile,
                                             Timestamp startTime, Timestamp expireTime, Timestamp showStartTime, Timestamp showEndTime, String address,
                                             String scenic, String scenicId, Integer ticketVarie, SpuProduct spu, Integer consumerCardType) throws Exception;

}