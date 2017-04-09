package com.pzj.modules.appapi.api;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pzj.app.service.AppEquipmentService;
import com.pzj.app.service.AppFeedbackService;
import com.pzj.app.service.AppMessageService;
import com.pzj.app.vo.AppEquipmentVO;
import com.pzj.app.vo.AppFeedbackVO;
import com.pzj.app.vo.AppMessageVO;
import com.pzj.base.common.utils.PageList;
import com.pzj.base.common.utils.PageModel;
import com.pzj.common.util.CheckUtils;
import com.pzj.common.util.DateUtils;
import com.pzj.customer.entity.Customer;
import com.pzj.modules.appapi.entity.CodeHandle;
import com.pzj.modules.appapi.entity.JsonEntity;

/**
 * 关于app自身的一些信息
 * 
 * @author wangkai
 * @date 2015年11月6日 下午5:39:13
 */
@Component
public class CommonAppInfoService {

    @Autowired
    private AppEquipmentService appEquipmentService;
    @Autowired
    private AppFeedbackService  appFeedbackService;
    @Autowired
    private AppMessageService   AppMessageService;

    /**
     * 服务器推送的消息列表
     * 
     * @param data
     * @param customer
     * @param json
     * @return JsonEntity 返回类型
     * @throws
     */
    public JsonEntity messageList(JSONObject data, Customer customer, JsonEntity json) {
        String currentPage = data.containsKey("pageSize") ? data.getString("currentPage") : "1";// 当前页数
        String pageSize = data.containsKey("pageSize") ? data.getString("pageSize") : "10";// 每页记录数
        PageModel page = new PageModel(Integer.parseInt(currentPage), Integer.parseInt(pageSize), null);
        AppMessageVO vo = new AppMessageVO();
        vo.setUserId(customer.getId());
        try {
            PageList<AppMessageVO> list = AppMessageService.queryPageByParamMap(page, vo);
            List<AppMessageVO> resultList = list.getResultList();
            List<Map<String, Object>> jsonArray = Lists.newArrayList();
            for (AppMessageVO message : resultList) {
                Map<String, Object> jsonObject = Maps.newHashMap();
                jsonObject.put("title", message.getTitle());
                jsonObject.put("news_id", message.getId());
                jsonObject.put("type", "0");
                jsonObject.put("news_date", DateUtils.formatDateTime(message.getCreateTime()));
                jsonObject.put("content", message.getContent());
                jsonArray.add(jsonObject);
                message.setStatus(message.getStatus());
                // messageService.updateByPrimaryKey(message);
            }
            Map<String, Object> object = Maps.newHashMap();
            object.put("count", list.getPageBean().getTotalCount());
            object.put("nrcount", "0");
            object.put("currentPage", currentPage);
            object.put("pageSize", pageSize);
            object.put("list", jsonArray);
            json.setCode(CodeHandle.SUCCESS.getCode() + "");
            json.setMessage(CodeHandle.SUCCESS.getMessage());
            json.setResponseBody(object);
        } catch (Exception e) {
            e.printStackTrace();
            json.setCode(CodeHandle.FAILURE.getCode() + "");
            json.setMessage(CodeHandle.FAILURE.getMessage());
        }
        return json;
    }

    /**
     * 用户对app的反馈
     * 
     * @param data
     * @param customer
     * @param json
     * @return JsonEntity 返回类型
     * @throws
     */
    public JsonEntity feedback(JSONObject data, Customer customer, JsonEntity json) {
        String content = data.containsKey("content") ? data.getString("content") : "";
        String phone = data.containsKey("phone") ? data.getString("phone") : "";
        String feedbackScenario = data.containsKey("feedbackScenario") ? data.getString("feedbackScenario") : "";
        if (CheckUtils.isNull(content) || CheckUtils.isNull(phone) || CheckUtils.isNull(feedbackScenario)) {
            json.setCode(CodeHandle.CODE_90001.getCode() + "");
            json.setMessage(CodeHandle.CODE_90001.getMessage());
        } else {
            try {
                AppFeedbackVO feedBack = new AppFeedbackVO();
                feedBack.setUserId(customer.getId());
                feedBack.setPhone(phone);
                feedBack.setContent(content);
                feedBack.setFeedbackScenario(feedbackScenario);
                feedBack.setCreateTime(new Date());
                Long i = appFeedbackService.create(feedBack);
                if (i > 0) {
                    json.setCode(CodeHandle.SUCCESS.getCode() + "");
                    json.setMessage(CodeHandle.SUCCESS.getMessage());
                } else {
                    json.setCode(CodeHandle.SERVERERROR.getCode() + "");
                    json.setMessage(CodeHandle.SERVERERROR.getMessage());
                }
            } catch (Exception e) {
                e.printStackTrace();
                json.setCode(CodeHandle.SERVERERROR.getCode() + "");
                json.setMessage(CodeHandle.SERVERERROR.getMessage());
            }
        }
        return json;
    }

    public JsonEntity bindEquipment(JSONObject data, Customer customer, JsonEntity json) {
        String deviceToken = data.containsKey("deviceToken") ? data.getString("deviceToken") : "";
        String deviceType = data.containsKey("deviceType") ? data.getString("deviceType") : "";
        if (CheckUtils.isNull(deviceToken) || CheckUtils.isNull(deviceType)) {
            json.setCode(CodeHandle.CODE_90001.getCode() + "");
            json.setMessage(CodeHandle.CODE_90001.getMessage());
        } else {
            try {
                AppEquipmentVO feedBack = new AppEquipmentVO();
                feedBack.setUserId(customer.getId());
                feedBack.setDeviceToken(deviceToken);
                feedBack.setDeviceType(deviceType);
                feedBack.setCreateTime(new Date());
                Long i = appEquipmentService.create(feedBack);
                if (i > 0) {
                    json.setCode(CodeHandle.SUCCESS.getCode() + "");
                    json.setMessage(CodeHandle.SUCCESS.getMessage());
                } else {
                    json.setCode(CodeHandle.SERVERERROR.getCode() + "");
                    json.setMessage(CodeHandle.SERVERERROR.getMessage());
                }
            } catch (Exception e) {
                e.printStackTrace();
                json.setCode(CodeHandle.SERVERERROR.getCode() + "");
                json.setMessage(CodeHandle.SERVERERROR.getMessage());
            }
        }
        return json;
    }

    /**
     * 获取用户的反馈列表
     * 
     * @param data
     * @param customer
     * @param json
     * @return JsonEntity 返回类型
     * @throws
     */
    public JsonEntity feedbackList(JSONObject data, Customer customer, JsonEntity json) {
        String currentPage = data.containsKey("pageSize") ? data.getString("currentPage") : "1";// 当前页数
        String pageSize = data.containsKey("pageSize") ? data.getString("pageSize") : "10";// 每页记录数
        PageModel page = new PageModel(Integer.parseInt(currentPage), Integer.parseInt(pageSize), null);
        AppFeedbackVO vo = new AppFeedbackVO();
        vo.setUserId(customer.getId());
        try {
            PageList<AppFeedbackVO> list = appFeedbackService.queryPageByParamMap(page, vo);
            List<AppFeedbackVO> resultList = list.getResultList();
            List<Map<String, Object>> jsonArray = Lists.newArrayList();
            for (AppFeedbackVO message : resultList) {
                Map<String, Object> jsonObject = Maps.newHashMap();
                jsonObject.put("content", message.getContent());
                jsonObject.put("id", message.getId());
                jsonObject.put("phone", message.getPhone());
                jsonObject.put("createTime", DateUtils.formatDateTime(message.getCreateTime()));
                jsonObject.put("feedbackScenario", message.getFeedbackScenario());
                jsonArray.add(jsonObject);
            }
            Map<String, Object> object = Maps.newHashMap();
            object.put("count", list.getPageBean().getTotalCount());
            object.put("currentPage", currentPage);
            object.put("pageSize", pageSize);
            object.put("list", jsonArray);
            json.setCode(CodeHandle.SUCCESS.getCode() + "");
            json.setMessage(CodeHandle.SUCCESS.getMessage());
            json.setResponseBody(object);
        } catch (Exception e) {
            e.printStackTrace();
            json.setCode(CodeHandle.FAILURE.getCode() + "");
            json.setMessage(CodeHandle.FAILURE.getMessage());
        }
        return json;
    }

}
