package com.pzj.platform.appapi.service;

import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.pzj.modules.appapi.entity.JsonEntity;
import com.pzj.platform.appapi.entity.AppVersionMessage;

/**
 * 
 * AppVersionMessageService接口
 * 
 */
public interface AppVersionMessageService {

    /**
     * 
     * 查询（根据主键ID查询）
     * 
     */
    public AppVersionMessage selectByPrimaryKey(Long id);

    /**
     * 
     * 查询（匹配有值的字段）
     * 
     */
    public List<AppVersionMessage> selectByObject(AppVersionMessage record);

    /**
     * 
     * 删除（根据主键ID删除）
     * 
     */
    public int deleteByPrimaryKey(Long id);

    /**
     * 
     * 添加
     * 
     */
    public int insert(AppVersionMessage record);

    /**
     * 
     * 添加 （匹配有值的字段）
     * 
     */
    public int insertSelective(AppVersionMessage record);

    /**
     * 
     * 修改 （匹配有值的字段）
     * 
     */
    public int updateByPrimaryKeySelective(AppVersionMessage record);

    /**
     * 
     * 修改（根据主键ID修改）
     * 
     */
    public int updateByPrimaryKey(AppVersionMessage record);

    public JsonEntity isVersionUpdate(JSONObject data, JsonEntity json);

    public JsonEntity insertOrUpdate(JSONObject data, JsonEntity json);

}