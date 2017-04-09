/**
 * piaozhijia.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package com.pzj.platform.appapi.model;

import java.io.Serializable;
import java.util.List;

import com.pzj.channel.Channel;

/**
 * 
 * @author daixuewei
 * @version $Id: StrategyModel.java, v 0.1 2016年9月8日 下午3:48:48 daixuewei Exp $
 */
public class StrategyResultModel implements Serializable {
    /**
     * 
     */
    private static final long   serialVersionUID = 1952901096614760127L;

    private Long                productId;

    /**
     * 渠道信息
     */
    private Channel             channel;

    /**
     * 政策信息
     */
    private List<StrategyModel> strategyList;

    /**
     * Getter method for property <tt>productId</tt>.
     * 
     * @return property value of productId
     */
    public Long getProductId() {
        return productId;
    }

    /**
     * Setter method for property <tt>productId</tt>.
     * 
     * @param productId value to be assigned to property productId
     */
    public void setProductId(Long productId) {
        this.productId = productId;
    }

    /**
     * Getter method for property <tt>channel</tt>.
     * 
     * @return property value of channel
     */
    public Channel getChannel() {
        return channel;
    }

    /**
     * Setter method for property <tt>channel</tt>.
     * 
     * @param channel value to be assigned to property channel
     */
    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    /**
     * Getter method for property <tt>strategyList</tt>.
     * 
     * @return property value of strategyList
     */
    public List<StrategyModel> getStrategyList() {
        return strategyList;
    }

    /**
     * Setter method for property <tt>strategyList</tt>.
     * 
     * @param strategyList value to be assigned to property strategyList
     */
    public void setStrategyList(List<StrategyModel> strategyList) {
        this.strategyList = strategyList;
    }
}
