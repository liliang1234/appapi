/**
 * piaozhijia.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package com.pzj.platform.appapi.aop;

/**
 * 
 * @author Mark
 * @version $Id: ContextEntry.java, v 0.1 2016年6月10日 下午5:28:52 pengliqing Exp $
 */
public class ContextEntry {

    private static ThreadLocal<MonitorEntity> monitor = new ThreadLocal<MonitorEntity>();

    public static MonitorEntity getMonitor() {
        MonitorEntity me = monitor.get();
        if (me == null)
            me = MonitorEntity.getInstance();
        monitor.set(me);
        return me;
    }

    public static void removeMonitor() {
        monitor.remove();
    }
}
