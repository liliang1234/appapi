/**
 * piaozhijia.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package com.pzj.platform.appapi.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.pzj.base.common.persistence.annotation.MyBatisDao;
import com.pzj.platform.appapi.entity.HomeClassificationConfig;

/**
 * 
 * @author Mark
 * @version $Id: HomeClassificationConfigMapper.java, v 0.1 2016年7月20日 下午5:41:32 pengliqing Exp $
 */
@MyBatisDao
public interface HomeClassificationConfigMapper {

    List<HomeClassificationConfig> queryByParamMap(@Param(value = "bParam") Map<String, Object> bParam);
}
