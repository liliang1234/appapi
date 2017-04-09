/**
 * piaozhijia.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package com.pzj.platform.appapi.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pzj.base.entity.show.ScreeningChart;
import com.pzj.base.service.show.IScreeningChartService;
import com.pzj.platform.appapi.service.ScreeningChartService;

/**
 * 
 * @author Mark
 * @version $Id: ScreeningChartService.java, v 0.1 2016年7月7日 上午11:05:44 pengliqing Exp $
 */
@Component("screeningChartServiceApp")
public class ScreeningChartServiceImpl implements ScreeningChartService {

    @Autowired
    private IScreeningChartService iScreeningChartService;

    /**
     * 场次区域信息查询
     * */
    @Override
    public ScreeningChart selectScreening(Long scenicId, Integer matches, String area) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("scenicId", scenicId);
        params.put("matches", matches);
        params.put("area", area);
        List<ScreeningChart> charts = iScreeningChartService.selectScreenings(params);
        return (charts == null || charts.size() == 0) ? null : charts.get(0);
    }

}
