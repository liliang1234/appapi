package com.pzj.platform.appapi.util;

import java.text.MessageFormat;

public class LogUtil {

    private static final String ID                 = "TraceId: {0};";
    private static final String PRIFIX             = "Invoke Method {1}";
    private static final String LOG_FORMAT         = ID + "desc: {1}";
    private static final String LOG_PARAM_FORMAT   = ID + PRIFIX + " Start <-- {2}";
    private static final String LOG_RESULT_FORMAT  = ID + PRIFIX + " End   --> {2}";
    private static final String MONITOR_LOG_FORMAT = ID + PRIFIX + " TimeUsed: {2}";

    public static String logFormat(String traceId, String desc) {
        return MessageFormat.format(LOG_FORMAT, traceId, desc);
    }

    public static String invokeLogFormat(String traceId, String method, String json, boolean isParamLog) {
        if (isParamLog) {
            return MessageFormat.format(LOG_PARAM_FORMAT, traceId, method, json);
        } else {
            return MessageFormat.format(LOG_RESULT_FORMAT, traceId, method, json);
        }
    }

    public static String monitorLogFormat(String traceId, String method, long timeUsed) {
        return MessageFormat.format(MONITOR_LOG_FORMAT, traceId, method, timeUsed);
    }

    public static void main(String[] args) {
        System.out.println(invokeLogFormat("2342342344", "com.pzj.fun.Run", "{\"id\":112}", true));
    }

}
