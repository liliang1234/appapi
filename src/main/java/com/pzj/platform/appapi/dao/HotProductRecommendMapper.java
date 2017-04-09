/**
 * piaozhijia.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package com.pzj.platform.appapi.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.pzj.base.common.persistence.annotation.MyBatisDao;
import com.pzj.platform.appapi.entity.HotProductRecommend;

/**
 * 
 * @author Mark
 * @version $Id: HotProductRecommendMapper.java, v 0.1 2016年7月22日 上午10:44:00 pengliqing Exp $
 */
@MyBatisDao
public interface HotProductRecommendMapper {

    List<HotProductRecommend> queryByParamMap(@Param(value = "bParam") Map<String, Object> bParam);
}
