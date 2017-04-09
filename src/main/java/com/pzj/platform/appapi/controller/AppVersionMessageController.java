/**
 * piaozhijia.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package com.pzj.platform.appapi.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.pzj.modules.appapi.entity.JsonEntity;
import com.pzj.platform.appapi.service.AppVersionMessageService;

/**
 * 
 * @author Administrator
 * @version $Id: AppVersionMessageController.java, v 0.1 2016年8月29日 下午6:08:07 Administrator Exp $
 */

@Controller
@RequestMapping("versionMessage")
public class AppVersionMessageController {
    @Autowired
    private AppVersionMessageService appVersionMessageService;

    @RequestMapping("isversionUpdate")
    @ResponseBody
    public JsonEntity isversionUpdate(HttpServletRequest request, @RequestAttribute JSONObject requestObject) throws Exception {
        JsonEntity json = new JsonEntity();
        json = appVersionMessageService.isVersionUpdate(requestObject, json);
        return json;
    }

    @RequestMapping("insertVersion")
    @ResponseBody
    public JsonEntity insertVersion(HttpServletRequest request, @RequestAttribute JSONObject requestObject) throws Exception {
        JsonEntity json = new JsonEntity();
        json = appVersionMessageService.insertOrUpdate(requestObject, json);
        return json;
    }
}
