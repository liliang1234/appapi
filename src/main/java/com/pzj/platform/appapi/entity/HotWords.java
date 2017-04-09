/**
 * piaozhijia.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package com.pzj.platform.appapi.entity;

import java.io.Serializable;

/**
 * 热门搜索词
 * @author fanggang
 * @version $Id: HotWords.java, v 0.1 2016年8月12日 下午3:09:53 fanggang Exp $
 */
public class HotWords extends BaseEntity implements Serializable {
    /**  */
    private static final long serialVersionUID = 2219002955262596072L;
    /** 关键词 */
    private String            keywords;
    /** 排序 */
    private int               seq;

    /**
     * Getter method for property <tt>keywords</tt>.
     * 
     * @return property value of keywords
     */
    public String getKeywords() {
        return keywords;
    }

    /**
     * Setter method for property <tt>keywords</tt>.
     * 
     * @param keywords value to be assigned to property keywords
     */
    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    /**
     * Getter method for property <tt>seq</tt>.
     * 
     * @return property value of seq
     */
    public int getSeq() {
        return seq;
    }

    /**
     * Setter method for property <tt>seq</tt>.
     * 
     * @param seq value to be assigned to property seq
     */
    public void setSeq(int seq) {
        this.seq = seq;
    }

}
