/**
 * piaozhijia.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package com.pzj.platform.appapi.service.impl;

import java.util.List;

/**
 * 
 * @author Mark
 * @version $Id: DataPolishingProcessService.java, v 0.1 2016年7月22日 上午11:57:54 pengliqing Exp $
 */
public abstract class DataPolishingService {

    public static final int defaultTimes = 2;

    abstract <T> List<T> getData(Object... params);

    public <T> void dataPolishing(List<T> result, int targetSize, Object... params) {
        int times = 0;
        while (times++ < defaultTimes) {
            if (result == null || result.size() < targetSize) {
                List<T> newResult = getData(params);
                if (newResult != null && newResult.size() > 0) {
                    if (newResult.size() + result.size() <= targetSize) {
                        result.addAll(newResult);
                    } else {
                        result.addAll(newResult.subList(0, targetSize - result.size()));
                    }
                }
            }
            if (result.size() == targetSize)
                return;
        }
    }
}
