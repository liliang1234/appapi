/**
 * piaozhijia.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package com.pzj.base.service;

/**
 * 
 * @author Mark
 * @version $Id: SMSSendService.java, v 0.1 2016年6月22日 下午9:55:12 pengliqing Exp $
 * 
 * 自定义服务契约，避免依赖很重的客户端API
 */
public interface SMSSendService {
    /**
     * 发送短信
     * 
     * @param phone
     *            手机号
     * @param message
     *            短信内容
     * @return
     */
    public Integer sendSMS(String phone, String message) throws Exception;

    /**
     * 获取短信模板
     * 
     * @param key
     *            短信模板key
     * @return String
     * @throws Exception
     */
    public String getMessage(String key) throws Exception;
}
