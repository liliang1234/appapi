package com.pzj.modules.appapi.api.order.param;

import java.util.List;

import com.pzj.channel.Strategy;

public class MerchParam {

    private Long           spuId;
    private Long           skuId;
    private List<Strategy> strategyList;

    private Integer        num = 1;     //默认为1张

    public Long getSpuId() {
        return spuId;
    }

    public void setSpuId(Long spuId) {
        this.spuId = spuId;
    }

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }

    public List<Strategy> getStrategyList() {
        return strategyList;
    }

    public void setStrategyList(List<Strategy> strategyList) {
        this.strategyList = strategyList;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

}
