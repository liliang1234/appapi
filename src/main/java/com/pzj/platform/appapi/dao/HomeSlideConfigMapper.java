/**
 * piaozhijia.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package com.pzj.platform.appapi.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.pzj.base.common.persistence.annotation.MyBatisDao;
import com.pzj.platform.appapi.entity.HomeSlideConfig;

/**
 * 
 * @author Mark
 * @version $Id: HomeSlideConfigMapper.java, v 0.1 2016年7月20日 下午6:14:38 pengliqing Exp $
 */
@MyBatisDao
public interface HomeSlideConfigMapper {

    List<HomeSlideConfig> queryByParamMap(@Param(value = "bParam") Map<String, Object> bParam);
}
