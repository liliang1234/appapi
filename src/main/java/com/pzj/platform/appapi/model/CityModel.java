/**
 * piaozhijia.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package com.pzj.platform.appapi.model;

import java.io.Serializable;

/**
 * 
 * @author Mark
 * @version $Id: CityModel.java, v 0.1 2016年8月2日 下午5:48:29 pengliqing Exp $
 */
public class CityModel implements Serializable {

    /**  */
    private static final long serialVersionUID = 1L;
    /**
     * 市
     */
    private String            city;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
