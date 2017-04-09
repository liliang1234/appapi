package com.pzj.modules.appapi.api.utils;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.pzj.base.common.global.UserGlobalDict;
import com.pzj.channel.Strategy;
import com.pzj.channel.vo.resultParam.PCStrategyResult;
import com.pzj.product.vo.voParam.resultParam.SpuProductResultVO;
import com.pzj.regulation.entity.StrategyVo;

public class StrategyFilter {

    private static final Long MF_SUPPLIER_ID = 123456789L;

    public static void filterChannel(SpuProductResultVO vo, String salesType) {
        Map<Long, List<PCStrategyResult>> strategyList = vo.getStrategyList();
        if (strategyList == null || strategyList.isEmpty()) {
            return;
        }
        for (Entry<Long, List<PCStrategyResult>> entry : strategyList.entrySet()) {
            boolean isMF = false;
            boolean isDirect = false;
            List<PCStrategyResult> list = entry.getValue();
            if (list == null || list.isEmpty()) {
                continue;
            }
            //逆序排列，非魔方渠道放在前面
            //
            // 最终结果是这样的:  Strategy1(21333121312), Strategy2(2133333122212), Strategy3(123456789)
            Collections.sort(list, new Comparator<PCStrategyResult>() {

                @Override
                public int compare(PCStrategyResult a1, PCStrategyResult a2) {
                    if (a1.getChannel() == null || a1.getChannel().getSupplierId() == null) {
                        return 1;
                    }
                    if (a2.getChannel() == null || a2.getChannel().getSupplierId() == null) {
                        return -1;
                    }
                    return -1 * a1.getChannel().getSupplierId().compareTo(a2.getChannel().getSupplierId());
                }

            });
            Iterator<PCStrategyResult> iter = list.iterator();

            while (iter.hasNext()) {
                PCStrategyResult result = iter.next();
                if (MF_SUPPLIER_ID.equals(result.getChannel().getSupplierId())) {
                    isMF = true;
                } else {
                    isDirect = true;
                }
                //如果同时存在魔方渠道和直签渠道，删除魔方渠道
                if (isDirect && isMF) {
                    iter.remove();
                    isMF = false;
                }

                List<Strategy> strategies = result.getStrategyList();
                if (strategies != null && strategies.size() > 0) {
                    Iterator<Strategy> iterStra = strategies.iterator();
                    while (iterStra.hasNext()) {
                        Strategy strategy = iterStra.next();
                        if (strategy.getStatus() == 0) {
                            iterStra.remove();
                        }
                        if (strategy.getTicketVarie() != null
                            && ((String.valueOf(UserGlobalDict.StrategyGlobalDict.scatterTicketVarie()).equals(strategy.getTicketVarie())
                                 && String.valueOf(UserGlobalDict.StrategyGlobalDict.windowGuideApp()).equals(salesType))
                                || (String.valueOf(UserGlobalDict.StrategyGlobalDict.groupTicketVarie()).equals(strategy.getTicketVarie())
                                    && String.valueOf(UserGlobalDict.StrategyGlobalDict.windowTenantApp()).equals(salesType))
                                || (String.valueOf(UserGlobalDict.StrategyGlobalDict.groupTicketVarie()).equals(strategy.getTicketVarie())
                                    && String.valueOf(UserGlobalDict.StrategyGlobalDict.windowTenantMicroshop()).equals(salesType)))) {
                            iterStra.remove();
                        }
                    }
                }

            }
        }
    }

    public static void filterChannel(List<StrategyVo> lists, String salesType) {
        if (lists != null && lists.size() > 0) {
            Iterator<StrategyVo> iterStra = lists.iterator();
            while (iterStra.hasNext()) {
                StrategyVo strategy = iterStra.next();
                if (strategy.getStatus() == 0) {
                    iterStra.remove();
                }
                if (strategy.getTicketVarie() != null
                    && ((String.valueOf(UserGlobalDict.StrategyGlobalDict.scatterTicketVarie()).equals(strategy.getTicketVarie())
                         && String.valueOf(UserGlobalDict.StrategyGlobalDict.windowGuideApp()).equals(salesType))
                        || (String.valueOf(UserGlobalDict.StrategyGlobalDict.groupTicketVarie()).equals(strategy.getTicketVarie())
                            && String.valueOf(UserGlobalDict.StrategyGlobalDict.windowTenantApp()).equals(salesType))
                        || (String.valueOf(UserGlobalDict.StrategyGlobalDict.groupTicketVarie()).equals(strategy.getTicketVarie())
                            && String.valueOf(UserGlobalDict.StrategyGlobalDict.windowTenantMicroshop()).equals(salesType)))) {
                    iterStra.remove();
                }
            }
        }

    }
}
