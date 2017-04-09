/**
 * piaozhijia.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package com.pzj.platform.appapi.service;

import com.pzj.base.entity.show.ScreeningChart;

/**
 * 
 * @author Mark
 * @version $Id: ScreeningChartService.java, v 0.1 2016年8月1日 上午10:37:22 pengliqing Exp $
 */
public interface ScreeningChartService {

    public ScreeningChart selectScreening(Long scenicId, Integer matches, String area);
}
