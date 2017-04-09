/**
 * piaozhijia.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package com.pzj.platform.appapi.util;

import java.text.DecimalFormat;

/**
 * 
 * @author daixuewei
 * @version $Id: StringUtils.java, v 0.1 2016年9月7日 下午2:48:19 daixuewei Exp $
 */
public class StringUtils {

    public static String doubleToString(Double d) {
        if (d == null) {
            return "0";
        }
        String temp = DecimalFormat.getInstance().format(d);
        String result = temp.replaceAll(",", "");
        String[] splits = result.split("\\.");
        if (splits.length > 1) {//存在小数位
            int ddInteger = Integer.parseInt(splits[1]);
            if (ddInteger > 0) {
                return result;
            } else {
                return splits[0];
            }
        } else
            return splits[0];
    }

    public static void main(String[] args) {
        System.out.println(doubleToString(1d));
        System.out.println(doubleToString(1000d));
        System.out.println(doubleToString(1000.0));
        System.out.println(doubleToString(1000.1));
        System.out.println(doubleToString(0.1));
    }
}
