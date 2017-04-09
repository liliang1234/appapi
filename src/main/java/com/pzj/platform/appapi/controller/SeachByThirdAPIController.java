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
import com.pzj.platform.appapi.service.SeachByThirdAPIService;

/**
 * 
 * @author Administrator
 * @version $Id: AppVersionMessageController.java, v 0.1 2016年8月29日 下午6:08:07 Administrator Exp $
 */

@Controller
@RequestMapping("seachByThirdAPI")
public class SeachByThirdAPIController {
    @Autowired
    private SeachByThirdAPIService seachByThirdAPIService;

    @RequestMapping("searchAddress")
    @ResponseBody
    public JsonEntity searchAddress(HttpServletRequest request, @RequestAttribute JSONObject requestObject) throws Exception {
        return seachByThirdAPIService.searchAddress(requestObject);
    }

}
