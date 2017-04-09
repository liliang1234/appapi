/**
 * piaozhijia.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package com.pzj.platform.appapi.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 
 * @author Mark
 * @version $Id: SkuCloseTimeSlotModel.java, v 0.1 2016年8月5日 上午9:54:52 pengliqing Exp $
 */
public class SkuCloseTimeSlotModel implements Serializable {

    /**  */
    private static final long serialVersionUID = 1L;

    /** 关闭时间点*/
    private List<Date>        closeDateList;

    /** 关闭开始时间*/
    private Date              startDate;

    /** 关闭结束时间*/
    private Date              endDate;

    public SkuCloseTimeSlotModel() {
        this.closeDateList = new ArrayList<>();
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    /**
     * Getter method for property <tt>closeDateList</tt>.
     * 
     * @return property value of closeDateList
     */
    public List<Date> getCloseDateList() {
        return closeDateList;
    }

    /**
     * Setter method for property <tt>closeDateList</tt>.
     * 
     * @param closeDateList value to be assigned to property closeDateList
     */
    public void setCloseDateList(List<Date> closeDateList) {
        this.closeDateList = closeDateList;
    }

}
