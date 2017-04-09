package com.pzj.modules.appapi.api.order;

import java.sql.Timestamp;
import java.util.List;

import com.pzj.appapi.vo.OrderVo;
import com.pzj.modules.appapi.entity.CodeHandle;
import com.pzj.trade.order.entity.MerchResponse;

public interface OrderCheckService {
    public CodeHandle checkOrderBuy(Long resellerId, String salesType, String buyDate, Long productId, Integer num, Integer productType, Integer totalNum);

    public CodeHandle checkScenicProductStock(Long resellerId, String salesPort, String buyDate, Long productId, Integer num);

    @Deprecated
    public CodeHandle checkOrderRefund(Long resellerId, String salesPort, List<MerchResponse> refundMerchandiseList);

    public boolean findExpVerification(OrderVo orderVo, Timestamp startTime, Timestamp expireTime);

}
