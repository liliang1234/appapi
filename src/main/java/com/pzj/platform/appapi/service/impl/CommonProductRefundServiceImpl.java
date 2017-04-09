/**
 * piaozhijia.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package com.pzj.platform.appapi.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pzj.base.common.utils.PageList;
import com.pzj.base.entity.product.TicketRule;
import com.pzj.base.service.product.ITicketRuleService;
import com.pzj.common.util.DateUtils;
import com.pzj.platform.appapi.aop.ContextEntry;
import com.pzj.platform.appapi.service.CommonProductRefundService;
import com.pzj.platform.appapi.util.LogUtil;

/**
 * 
 * @author Mark
 * @version $Id: CommonProductRefundService.java, v 0.1 2016年6月24日 下午2:30:59 pengliqing Exp $
 */
@Service
public class CommonProductRefundServiceImpl implements CommonProductRefundService {

    private static final Logger logger = LoggerFactory.getLogger(CommonProductRefundServiceImpl.class);

    @Autowired
    private ITicketRuleService ticketRuleService;

    /**
     * 根据游玩开始时间计算是否允许退票(规则为离游玩开始时间<code>days</code>天可以退票
     *      退票时间必须早于<code>hours</code>点<code>minutes</code>分)
     * 
     * @param timeType 暂时不处理
     * @param days 天
     * @param hours 小时
     * @param minutes 分钟
     * @param startTime 游玩开始时间
     * @return <code>true</code> 允许退票  
     *         <code>false</code> 不允许退票
     * 
     * @see TicketRule
     * */
    @Override
    public boolean isRefund(Integer timeType, Integer days, Double hours, Integer minutes, Date startTime) {
        logger.info(LogUtil.logFormat(ContextEntry.getMonitor().getTraceId(),
            "check ticket can be refund  by request : days=" + days + ",hours=" + hours + ",minutes=" + minutes + ",startTime=" + startTime));
        Calendar now = Calendar.getInstance();
        long daysBetween = DateUtils.daysBetween(DateUtils.parseDate(DateUtils.formatDate(now.getTime())),
            DateUtils.parseDate(DateUtils.formatDate(startTime)));
        if (days == null) {
            days = 0;
        }
        if (daysBetween > days) { //天数
            return true;
        } else if (daysBetween == days) {
            if (hours == null || hours == 0.0) {
                if (minutes == null || minutes == 0) {
                    return true;
                } else if (now.get(Calendar.MINUTE) < minutes) {
                    return true;
                }
            }
            int nowHour = now.get(Calendar.HOUR_OF_DAY);
            int refundHourLimit = new Double(hours).intValue();
            if (refundHourLimit > nowHour) {//小时
                return true;
            } else if (refundHourLimit == nowHour) {
                if (minutes != null && now.get(Calendar.MINUTE) < minutes) // 分钟
                    return true;
            }
        }
        return false;
    }

    /**
     * 查询有效退票/换票规则
     * 
     * @param productId
     * @return 退票/换票 规则
     * */
    @Override
    public TicketRule findActiveRefundRule(Long productId) {
        TicketRule request = new TicketRule();
        List<Long> proIds = new ArrayList<Long>();
        proIds.add(productId);
        request.setProIds(proIds);
        PageList<TicketRule> ticketPageList = ticketRuleService.queryPageByParamMap(null, request);
        if (ticketPageList != null && ticketPageList.getResultList() != null && ticketPageList.getResultList().size() > 0) {
            for (TicketRule ticRule : ticketPageList.getResultList()) {
                //                if(ticRule.getStatus() != null && ticRule.getStatus() == 1){
                //                    return ticRule;
                //                }
                return ticRule;
            }

        }
        return null;
    }
}
