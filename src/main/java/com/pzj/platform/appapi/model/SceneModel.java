/**
 * piaozhijia.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package com.pzj.platform.appapi.model;

import java.io.Serializable;

/**
 * 
 * @author Mark
 * @version $Id: SceneModel.java, v 0.1 2016年8月2日 下午5:48:29 pengliqing Exp $
 */
public class SceneModel implements Serializable {

    /**  */
    private static final long serialVersionUID = 1L;

    /**
     * 显示名称
     */
    private String            label;

    String                    sceneId;
    String                    sceneName;
    /**
     * 省
     */
    private String            province;
    /**
     * 市
     */
    private String            city;
    /**
     * 县
     */
    private String            county;
    String                    img_url;

    /**
     * Getter method for property <tt>sceneId</tt>.
     * 
     * @return property value of sceneId
     */
    public String getSceneId() {
        return sceneId;
    }

    /**
     * Setter method for property <tt>sceneId</tt>.
     * 
     * @param sceneId value to be assigned to property sceneId
     */
    public void setSceneId(String sceneId) {
        this.sceneId = sceneId;
    }

    /**
     * Getter method for property <tt>sceneName</tt>.
     * 
     * @return property value of sceneName
     */
    public String getSceneName() {
        return sceneName;
    }

    /**
     * Setter method for property <tt>sceneName</tt>.
     * 
     * @param sceneName value to be assigned to property sceneName
     */
    public void setSceneName(String sceneName) {
        this.sceneName = sceneName;
    }

    /**
     * Getter method for property <tt>city</tt>.
     * 
     * @return property value of city
     */
    public String getCity() {
        return city;
    }

    /**
     * Setter method for property <tt>city</tt>.
     * 
     * @param city value to be assigned to property city
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * Getter method for property <tt>county</tt>.
     * 
     * @return property value of county
     */
    public String getCounty() {
        return county;
    }

    /**
     * Setter method for property <tt>county</tt>.
     * 
     * @param county value to be assigned to property county
     */
    public void setCounty(String county) {
        this.county = county;
    }

    /**
     * Getter method for property <tt>province</tt>.
     * 
     * @return property value of province
     */
    public String getProvince() {
        return province;
    }

    /**
     * Setter method for property <tt>province</tt>.
     * 
     * @param province value to be assigned to property province
     */
    public void setProvince(String province) {
        this.province = province;
    }

    /**
     * Getter method for property <tt>img_url</tt>.
     * 
     * @return property value of img_url
     */
    public String getImg_url() {
        return img_url;
    }

    /**
     * Setter method for property <tt>img_url</tt>.
     * 
     * @param img_url value to be assigned to property img_url
     */
    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    /**
     * Getter method for property <tt>label</tt>.
     * 
     * @return property value of label
     */
    public String getLabel() {
        return label;
    }

    /**
     * Setter method for property <tt>label</tt>.
     * 
     * @param label value to be assigned to property label
     */
    public void setLabel(String label) {
        this.label = label;
    }
}
