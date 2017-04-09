/**
 * piaozhijia.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package com.pzj.platform.appapi.model;

import java.io.Serializable;
import java.util.List;

/**
 * 
 * @author Mark
 * @version $Id: ProvinceModel.java, v 0.1 2016年8月2日 下午5:47:54 pengliqing Exp $
 */
public class ProvinceModel implements Serializable {

    /**  */
    private static final long serialVersionUID = 1L;
    /**
     * 省
     */
    private String            province;
    private List<CityModel>   citys;

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public List<CityModel> getCitys() {
        return citys;
    }

    public void setCitys(List<CityModel> citys) {
        this.citys = citys;
    }
}
