package com.pzj.platform.appapi.service.impl;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.pzj.modules.appapi.entity.CodeHandle;
import com.pzj.modules.appapi.entity.JsonEntity;
import com.pzj.platform.appapi.dao.AppVersionMessageMapper;
import com.pzj.platform.appapi.entity.AppVersionMessage;
import com.pzj.platform.appapi.service.AppVersionMessageService;

/**
 * 
 * AppVersionMessageServiceImpl实现
 * 
 */
@Service
public class AppVersionMessageServiceImpl implements AppVersionMessageService {

    @Autowired
    private AppVersionMessageMapper appVersionMessageMapper;
    private static final Logger     logger = LoggerFactory.getLogger(AppVersionMessageServiceImpl.class);

    /**
     * 
     * 查询（根据主键ID查询）
     * 
     */
    @Override
    public AppVersionMessage selectByPrimaryKey(Long id) {
        return appVersionMessageMapper.selectByPrimaryKey(id);
    }

    /**
     * 
     * 删除（根据主键ID删除）
     * 
     */
    @Override
    public int deleteByPrimaryKey(Long id) {
        return appVersionMessageMapper.deleteByPrimaryKey(id);
    }

    /**
     * 
     * 添加
     * 
     */
    @Override
    public int insert(AppVersionMessage record) {
        return appVersionMessageMapper.insert(record);
    }

    /**
     * 
     * 添加 （匹配有值的字段）
     * 
     */
    @Override
    public int insertSelective(AppVersionMessage record) {
        return appVersionMessageMapper.insertSelective(record);
    }

    /**
     * 
     * 修改 （匹配有值的字段）
     * 
     */
    @Override
    public int updateByPrimaryKeySelective(AppVersionMessage record) {
        return appVersionMessageMapper.updateByPrimaryKeySelective(record);
    }

    /**
     * 
     * 修改（根据主键ID修改）
     * 
     */
    @Override
    public int updateByPrimaryKey(AppVersionMessage record) {
        return appVersionMessageMapper.updateByPrimaryKey(record);
    }

    /**
     * 
     * 查询（匹配有值的字段）
     * 
     */
    @Override
    public List<AppVersionMessage> selectByObject(AppVersionMessage appVersionMessage) {
        return appVersionMessageMapper.selectByObject(appVersionMessage);
    }

    /**
     * 查询是否需要强制更新
     *
     * @param data
     *            json参数包
     * @param customer
     *            当前登录用户
     * @param json
     *            返回结果包装
     * @throws
     */
    @Override
    public JsonEntity isVersionUpdate(JSONObject data, JsonEntity json) {
        logger.info("进入到是否有版本更新方法");
        String appName = data.containsKey("appName") ? data.getString("appName") : "";
        String versionCode = data.containsKey("versionCode") ? data.getString("versionCode") : "0";
        String platform = data.containsKey("platform") ? data.getString("platform") : "-1";
        String channel = data.containsKey("channel") ? data.getString("channel") : "";
        int isForceUpdate = 0;
        AppVersionMessage appVersionMessage = new AppVersionMessage();
        appVersionMessage.setAppName(appName);
        appVersionMessage.setVersionCode(Integer.valueOf(versionCode));
        appVersionMessage.setPlatform(Integer.valueOf(platform));
        appVersionMessage.setChannel(channel);
        try {
            logger.info("是否有版本更新方法传递的参数是：appName为：" + appName + "versionCode为：" + versionCode + "platform为：" + platform + "channel为：" + channel);
            List<AppVersionMessage> appVersionMessageList = selectByObject(appVersionMessage);
            if (appVersionMessageList != null && appVersionMessageList.size() != 0) {
                logger.info("appVersionMessageList有值，size是：" + appVersionMessageList.size());
                for (AppVersionMessage avm : appVersionMessageList) {
                    if (avm.getForceUpdate().intValue() == 1) {
                        isForceUpdate = 1;
                        break;
                    }
                }
                AppVersionMessage returnAppVersionMessage = appVersionMessageList.get(0);
                returnAppVersionMessage.setForceUpdate(isForceUpdate);

                Map<String, Object> object = Maps.newHashMap();
                object.put("versionName", returnAppVersionMessage.getVersionName());
                object.put("versionCode", returnAppVersionMessage.getVersionCode());
                object.put("updateMsg", returnAppVersionMessage.getUpdateMsg());
                object.put("channel", returnAppVersionMessage.getChannel());
                object.put("md5", returnAppVersionMessage.getMd5());
                object.put("downloadUrl", returnAppVersionMessage.getDownloadUrl());
                object.put("forceUpdate", returnAppVersionMessage.getForceUpdate());
                json.setCode(CodeHandle.CODE_70000.getCode() + "");
                json.setMessage(CodeHandle.CODE_70000.getMessage());
                json.setResponseBody(object);
            } else {
                json.setCode(CodeHandle.CODE_70001.getCode() + "");
                json.setMessage(CodeHandle.CODE_70001.getMessage());
                logger.info("组装json完成，返回json结果CODE_70001");
                return json;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.info("返回的json串是：" + json);
        return json;
    }

    /** 
     * @see com.pzj.platform.appapi.service.AppVersionMessageService#insertOrUpdate(com.alibaba.fastjson.JSONObject, com.pzj.platform.appapi.model.JsonEntity)
     */
    @Override
    public JsonEntity insertOrUpdate(JSONObject data, JsonEntity json) {
        String id = data.containsKey("id") ? data.getString("id") : "";
        String appName = data.containsKey("appName") ? data.getString("appName") : "";
        String versionName = data.containsKey("versionName") ? data.getString("versionName") : "";
        String versionCode = data.containsKey("versionCode") ? data.getString("versionCode") : "";
        String platform = data.containsKey("platform") ? data.getString("platform") : "";
        String channel = data.containsKey("channel") ? data.getString("channel") : "";
        String oSVersion = data.containsKey("oSVersion") ? data.getString("oSVersion") : "";
        String deviceModel = data.containsKey("deviceModel") ? data.getString("deviceModel") : "";
        String md5 = data.containsKey("md5") ? data.getString("md5") : "";
        String downloadUrl = data.containsKey("downloadUrl") ? data.getString("downloadUrl") : "";
        String forceUpdate = data.containsKey("forceUpdate") ? data.getString("forceUpdate") : "";
        String updateMsg = data.containsKey("updateMsg") ? data.getString("updateMsg") : "";

        AppVersionMessage appVersionMessage = new AppVersionMessage();
        appVersionMessage.setAppName(appName);
        appVersionMessage.setVersionCode(Integer.valueOf(versionCode));
        appVersionMessage.setPlatform(Integer.valueOf(platform));
        appVersionMessage.setChannel(channel);
        appVersionMessage.setOSVersion(oSVersion);
        appVersionMessage.setDeviceModel(deviceModel);
        appVersionMessage.setMd5(md5);
        appVersionMessage.setDownloadUrl(downloadUrl);
        appVersionMessage.setForceUpdate(Integer.valueOf(forceUpdate));
        appVersionMessage.setUpdateMsg(updateMsg);
        appVersionMessage.setVersionName(versionName);
        try {
            if (id != null && !"".equals(id)) {
                appVersionMessage.setId(Long.valueOf(id));
                updateByPrimaryKeySelective(appVersionMessage);
            } else {
                insertSelective(appVersionMessage);
            }
            json.setCode(CodeHandle.SUCCESS.getCode() + "");
            json.setMessage(CodeHandle.SUCCESS.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }
}