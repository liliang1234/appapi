/**
 * piaozhijia.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package com.pzj.platform.appapi.model;

import java.io.Serializable;

/**
 * 调用第三方地图接口model
 * 
 * @author guanluguang 
 * 
 */
public class SearchAddressByAPIModel implements Serializable {

    /**  */
    private static final long serialVersionUID = -8845570176543662964L;
    //    private String            id;                                      //地址唯一标示
    private String            name;                                    //名称
    private String            address;                                 //地址
    private String            location;                                //坐标
                                                                        //    private String            type;                                    //兴趣点类型
                                                                        //    private String            typecode;                                //兴趣点类型编码
                                                                        //    private String            tel;                                     //电话
    private String            pname;                                   //   poi所在省份名称
    private String            cityname;                                //城市名
    private String            adname;                                  //区域名称

    public String getPname() {
        return pname;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }

    public String getCityname() {
        return cityname;
    }

    public void setCityname(String cityname) {
        this.cityname = cityname;
    }

    public String getAdname() {
        return adname;
    }

    public void setAdname(String adname) {
        this.adname = adname;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

}
