/**
 * piaozhijia.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package com.pzj.platform.appapi.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.pzj.base.common.persistence.annotation.MyBatisDao;
import com.pzj.platform.appapi.entity.HotWords;

/**
 * 
 * 
 * @author fanggang
 * @version $Id: HotWordsMapper.java, v 0.1 2016年8月12日 下午3:19:51 fanggang Exp $
 */
@MyBatisDao
public interface HotWordsMapper {

    List<HotWords> queryByParamMap(@Param(value = "bParam") Map<String, Object> bParam);
}
