package com.pzj.platform.appapi.entity;

import java.io.Serializable;

/**
 * 
 * app版本信息表
 * 
 */
public class AppVersionMessage extends BaseEntity implements Serializable {

    /**  */
    private static final long serialVersionUID = 1L;

    /**
     * 应用名称
     */
    private String            appName;

    /**
     * 版本名字
     */
    private String            versionName;

    /**
     * 版本号
     */
    private Integer           versionCode;

    /**
     * 平台(0 android,1 ios)
     */
    private Integer           platform;

    /**
     * 渠道号（服务器根据channel 给出相应下载地址，考虑友盟统计）
     */
    private String            channel;

    /**
     * 系统版本号（扩展字段，服务器决定是否处理）
     */
    private String            oSVersion;

    /**
     * 机器型号（扩展字段，服务器决定是否处理）
     */
    private String            deviceModel;

    /**
     * url
     */
    private String            md5;

    /**
     * (apk md5)
     */
    private String            downloadUrl;

    /**
     * 更新信息
     */
    private String            updateMsg;

    /**
     * 是否强制更新：1强制，0非强制
     */
    private Integer           forceUpdate;

    /**
     * set应用名称
     */
    public void setAppName(String appName) {
        this.appName = appName;
    }

    /**
     * get应用名称
     */
    public String getAppName() {
        return this.appName;
    }

    /**
     * set版本名字
     */
    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    /**
     * get版本名字
     */
    public String getVersionName() {
        return this.versionName;
    }

    /**
     * set版本号
     */
    public void setVersionCode(Integer versionCode) {
        this.versionCode = versionCode;
    }

    /**
     * get版本号
     */
    public Integer getVersionCode() {
        return this.versionCode;
    }

    /**
     * set平台(0 android,1 ios)
     */
    public void setPlatform(Integer platform) {
        this.platform = platform;
    }

    /**
     * get平台(0 android,1 ios)
     */
    public Integer getPlatform() {
        return this.platform;
    }

    /**
     * set渠道号（服务器根据channel 给出相应下载地址，考虑友盟统计）
     */
    public void setChannel(String channel) {
        this.channel = channel;
    }

    /**
     * get渠道号（服务器根据channel 给出相应下载地址，考虑友盟统计）
     */
    public String getChannel() {
        return this.channel;
    }

    /**
     * set系统版本号（扩展字段，服务器决定是否处理）
     */
    public void setOSVersion(String oSVersion) {
        this.oSVersion = oSVersion;
    }

    /**
     * get系统版本号（扩展字段，服务器决定是否处理）
     */
    public String getOSVersion() {
        return this.oSVersion;
    }

    /**
     * set机器型号（扩展字段，服务器决定是否处理）
     */
    public void setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
    }

    /**
     * get机器型号（扩展字段，服务器决定是否处理）
     */
    public String getDeviceModel() {
        return this.deviceModel;
    }

    /**
     * seturl
     */
    public void setMd5(String md5) {
        this.md5 = md5;
    }

    /**
     * geturl
     */
    public String getMd5() {
        return this.md5;
    }

    /**
     * set(apk md5)
     */
    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    /**
     * get(apk md5)
     */
    public String getDownloadUrl() {
        return this.downloadUrl;
    }

    /**
     * set更新信息
     */
    public void setUpdateMsg(String updateMsg) {
        this.updateMsg = updateMsg;
    }

    /**
     * get更新信息
     */
    public String getUpdateMsg() {
        return this.updateMsg;
    }

    /**
     * set是否强制更新：1强制，0非强制
     */
    public void setForceUpdate(Integer forceUpdate) {
        this.forceUpdate = forceUpdate;
    }

    /**
     * get是否强制更新：1强制，0非强制
     */
    public Integer getForceUpdate() {
        return this.forceUpdate;
    }

}
