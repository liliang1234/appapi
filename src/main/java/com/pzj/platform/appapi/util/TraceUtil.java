package com.pzj.platform.appapi.util;

import java.util.UUID;

public class TraceUtil {

    public static String getTraceId() {
        return getUUID();
    }

    public static String getUUID() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }
}
