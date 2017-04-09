package com.pzj.modules.appapi.api;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.pzj.appapi.constants.ApiConstants;
import com.pzj.modules.appapi.entity.CodeHandle;
import com.pzj.modules.appapi.entity.JsonEntity;
import com.pzj.platform.appapi.util.PropertyLoader;

@Component
public class VersionService {
    @Resource(name = "propertyLoader")
    private PropertyLoader propertyLoader;
    private static String  TICKET_CATEGRORY_KEY = "version";
    private static String  TICKET_URL_KEY       = "url";

    private String flag = "0";
    private String yNFlag;

    public JsonEntity version(JSONObject data, JsonEntity json) {
        String name = data.containsKey("versionCode") ? data.getString("versionCode") : "";//接收版本号
        String salesType = data.containsKey("salesType") ? data.getString("salesType") : "";//销售端口
        String version = propertyLoader.getProperty(ApiConstants.SYSTEM_FILENAME, TICKET_CATEGRORY_KEY);
        String appUrl = propertyLoader.getProperty(ApiConstants.SYSTEM_FILENAME, TICKET_URL_KEY);

        String[] kinds = version.split("%");
        String[] urls = appUrl.split("%");
        String kindStr = "";
        String updateok = "0";
        String url = "";
        Map<String, Object> jsonObject = Maps.newHashMap();
        if (!salesType.equals("") && salesType.equals("4")) {
            kindStr = kinds[0];
            url = urls[0];
        } else {
            kindStr = kinds[1];
            url = urls[1];
        }
        String[] versions = kindStr.split(",");
        for (int i = 0; i < versions.length; i++) {
            String st1 = versions[i];
            String[] st3 = st1.split("=");
            String versionCode = st3[0];
            if (name.equals(versionCode)) {
                yNFlag = st3[1];
                updateok = "1";
            } else {
                yNFlag = "N";
            }
            String only = versions[versions.length - 1];
            String[] single = only.split("=");
            jsonObject.put("versionCode", single[0]);
            if (!name.equals(single[0]) && yNFlag.equals("Y")) {
                flag = "1";
            }
        }
        jsonObject.put("flag", flag);
        jsonObject.put("updateok", updateok);
        jsonObject.put("url", url);
        json.setResponseBody(jsonObject);
        json.setCode(CodeHandle.SUCCESS.getCode() + "");
        json.setMessage(CodeHandle.SUCCESS.getMessage());
        return json;
    }
}
