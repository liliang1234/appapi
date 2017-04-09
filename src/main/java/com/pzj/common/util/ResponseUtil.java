package com.pzj.common.util;

import com.pzj.framework.context.Result;
import com.pzj.modules.appapi.entity.CodeHandle;
import com.pzj.modules.appapi.entity.JsonEntity;

public class ResponseUtil {

    public static JsonEntity handle(Result<?> result, JsonEntity rspJsonEntity) {
        if (result.getErrorCode() == new Result<>().getErrorCode()) {
            rspJsonEntity.setCode(CodeHandle.SUCCESS.getCode() + "");
            rspJsonEntity.setMessage(CodeHandle.SUCCESS.getMessage());
            rspJsonEntity.setResponseBody(result.getData() == null ? "" : result.getData());
        } else {
            rspJsonEntity.setCode(result.getErrorCode() + "");
            rspJsonEntity.setMessage(result.getErrorMsg());
        }
        return rspJsonEntity;
    }

    public static JsonEntity handleWithResult(Result<?> result, JsonEntity rspJsonEntity) {
        if (result.getErrorCode() == new Result<>().getErrorCode()) {
            rspJsonEntity.setCode(CodeHandle.SUCCESS.getCode() + "");
            rspJsonEntity.setMessage(CodeHandle.SUCCESS.getMessage());
        } else {
            rspJsonEntity.setCode(result.getErrorCode() + "");
            rspJsonEntity.setMessage(result.getErrorMsg());
        }
        rspJsonEntity.setResponseBody(result.getData());
        return rspJsonEntity;
    }

    public static JsonEntity makeSuccessJsonEntity(Object responseBody, String message) {
        JsonEntity JsonEntity = new JsonEntity();
        JsonEntity.setCode(CodeHandle.SUCCESS.getCode() + "");
        JsonEntity.setMessage(message);
        JsonEntity.setResponseBody(responseBody);
        return JsonEntity;
    }

    public static JsonEntity makeErrorJsonEntity(Object responseBody, String code, String message) {
        JsonEntity JsonEntity = new JsonEntity();
        JsonEntity.setCode(code);
        JsonEntity.setMessage(message);
        JsonEntity.setResponseBody(responseBody);
        return JsonEntity;
    }
}
