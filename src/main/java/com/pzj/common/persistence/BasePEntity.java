package com.pzj.common.persistence;

import java.io.Serializable;

/**
 * 实体父类
 * @author apple
 *
 */
public class BasePEntity implements Serializable {

    private static final long serialVersionUID = 3121418431571658519L;

    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
