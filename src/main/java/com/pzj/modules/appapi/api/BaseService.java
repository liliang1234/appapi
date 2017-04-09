package com.pzj.modules.appapi.api;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.pzj.modules.appapi.entity.CodeHandle;
import com.pzj.modules.appapi.exception.AppapiParametersException;
import com.pzj.platform.appapi.exception.ParamException;

public abstract class BaseService {

    protected Gson gson = new Gson();

    protected void checkNeededParam(JSONObject data, String param) throws AppapiParametersException {
        if (!data.containsKey(param))
            throw new ParamException(CodeHandle.ERROR10033.getCode(), param + CodeHandle.ERROR10033.getMessage());
    }
}
