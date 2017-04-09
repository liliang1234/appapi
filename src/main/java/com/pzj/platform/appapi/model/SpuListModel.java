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
 * @version $Id: ScenicModel.java, v 0.1 2016年8月2日 下午5:48:29 pengliqing Exp $
 */
public class SpuListModel implements Serializable {

    /**  */
    private static final long serialVersionUID = 1L;

    /**产品组列表*/
    List<SpuProductModel>     spuLists;

    private Integer           pageNo;

    private Integer           pageSize;

    private long              totalCount;

    /**
     * Setter method for property <tt>spuLists</tt>.
     * 
     * @param spuLists value to be assigned to property spuLists
     */
    public void setSpuLists(List<SpuProductModel> spuLists) {
        this.spuLists = spuLists;
    }

    /**
     * Getter method for property <tt>spuLists</tt>.
     * 
     * @return property value of spuLists
     */
    public List<SpuProductModel> getSpuLists() {
        return spuLists;
    }

    /**
     * Getter method for property <tt>pageNo</tt>.
     * 
     * @return property value of pageNo
     */
    public Integer getPageNo() {
        return pageNo;
    }

    /**
     * Setter method for property <tt>pageNo</tt>.
     * 
     * @param pageNo value to be assigned to property pageNo
     */
    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }

    /**
     * Getter method for property <tt>pageSize</tt>.
     * 
     * @return property value of pageSize
     */
    public Integer getPageSize() {
        return pageSize;
    }

    /**
     * Setter method for property <tt>pageSize</tt>.
     * 
     * @param pageSize value to be assigned to property pageSize
     */
    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    /**
     * Getter method for property <tt>totalCount</tt>.
     * 
     * @return property value of totalCount
     */
    public long getTotalCount() {
        return totalCount;
    }

    /**
     * Setter method for property <tt>totalCount</tt>.
     * 
     * @param totalCount value to be assigned to property totalCount
     */
    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }
}
