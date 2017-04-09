/**
 * piaozhijia.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package com.pzj.platform.appapi.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @author Mark
 * @version $Id: BaseEntity.java, v 0.1 2016年7月20日 下午5:34:04 pengliqing Exp $
 */
public class BaseEntity implements Serializable {

    private static final long serialVersionUID = 5276154473854692878L;

    /** 主键 */
    private Long   id;
    private String createBy;
    /**
     * 创建时间
     */
    private Date   createDate;

    private String updateBy;
    /**
     * 修改时间
     */
    private Date   updateDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }
}
