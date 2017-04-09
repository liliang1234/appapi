package com.pzj.platform.appapi.util;

public class CheckUtils {

    public static boolean isEmpty(Object input) {
        if (null == input) {
            return true;
        }
        if (input instanceof String) {
            if ("".equals(input.toString().trim())) {
                return true;
            }
        }
        return false;
    }

    public static boolean isNotEmpty(Object input) {
        return !isEmpty(input);
    }

}
