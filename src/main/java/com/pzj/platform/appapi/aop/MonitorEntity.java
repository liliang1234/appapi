package com.pzj.platform.appapi.aop;

import java.io.Serializable;

import com.pzj.platform.appapi.util.TraceUtil;

public class MonitorEntity implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    String                    traceId;

    private MonitorEntity() {
        this.traceId = TraceUtil.getTraceId();
    }

    protected static MonitorEntity getInstance() {
        return new MonitorEntity();
    }

    public String getTraceId() {
        return traceId;
    }
}
