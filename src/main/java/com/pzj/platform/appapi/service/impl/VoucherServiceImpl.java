package com.pzj.platform.appapi.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pzj.platform.appapi.dubbo.VoucherApi;
import com.pzj.platform.appapi.service.VoucherService;
import com.pzj.voucher.common.ExecuteResult;
import com.pzj.voucher.entity.VoucherEntity;

/**
 * app关于Voucher的接口
 * 
 * @author Mark
 * @date 2016年6月3日 下午11:32:29
 */
@Service
public class VoucherServiceImpl implements VoucherService {

    @Autowired
    private VoucherApi voucherApi;

    /**
    * 根据ID查询商品核销信息
    * 
    * @param voucherId
    * @return 
    */
    @Override
    public VoucherEntity getVoucherById(Long voucherId) {
        VoucherEntity baseVoucher = new VoucherEntity();
        baseVoucher.setVoucherId(voucherId);
        ExecuteResult<List<VoucherEntity>> responseResult = voucherApi.queryVoucherByParam(baseVoucher);
        if (responseResult.getStateCode() != null && responseResult.getStateCode().equals(ExecuteResult.SUCCESS)) {
            return responseResult.getData() == null ? null : (responseResult.getData().size() == 0 ? null : responseResult.getData().get(0));
        }
        return null;
    }

}
