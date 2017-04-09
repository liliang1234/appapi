package com.pzj.modules.appapi.api;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.pzj.common.util.PostUtils;
import com.pzj.modules.appapi.entity.JsonEntity;
import com.pzj.modules.appapi.web.BApiController;
import com.pzj.platform.appapi.util.PropertyLoader;

@Service
public class RedirectService {
    private static final Logger logger = LoggerFactory.getLogger(BApiController.class);
    @Resource(name = "propertyLoader")
    private PropertyLoader      propertyLoader;

    public JsonEntity redirect(String data, String sign, String redirect, String token, JsonEntity json) {
        try {
            String url = propertyLoader.getProperty("system", "redirect" + redirect, "");
            String string = PostUtils.getContentByPost(url + "?data=" + data + "&sign=" + sign, null, token);
            logger.info("远程数据：" + string);
            JSONObject data2 = JSON.parseObject(string);
            json.setResponseBody(data2.get("responseBody"));
            json.setCode(data2.getString("code"));
            json.setMessage(data2.getString("message"));
        } catch (Exception e) {
            json.setCode("10002");
            json.setMessage("报表远程调用失败");
        }
        return json;
    }
}
