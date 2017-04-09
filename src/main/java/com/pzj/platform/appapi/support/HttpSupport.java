/**
 * piaozhijia.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package com.pzj.platform.appapi.support;

import java.util.Enumeration;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

/**
 * 
 * @author Mark
 * @version $Id: HttpSupport.java, v 0.1 2016年7月4日 下午1:55:02 pengliqing Exp $
 */
public class HttpSupport {

    /**
     * 获取http首部
     * 
     * @param request
     * @return
     * */
    @SuppressWarnings("unchecked")
    public static HashMap<String, String> getHeader(HttpServletRequest request) {
        HashMap<String, String> map = new HashMap<String, String>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String element = headerNames.nextElement();
            String value = request.getHeader(element);
            map.put(element.toLowerCase(), value);
        }
        return map;
    }

}
