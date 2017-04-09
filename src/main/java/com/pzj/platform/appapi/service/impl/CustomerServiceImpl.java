/**
 * piaozhijia.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package com.pzj.platform.appapi.service.impl;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.pzj.appapi.constants.ApiConstants;
import com.pzj.base.common.global.UserGlobalDict;
import com.pzj.base.common.global.UserGlobalDict.UserRelation;
import com.pzj.base.common.utils.PageList;
import com.pzj.customer.entity.Customer;
import com.pzj.customer.entity.CustomerRelation;
import com.pzj.customer.service.CustomerRelationService;
import com.pzj.platform.appapi.aop.ContextEntry;
import com.pzj.platform.appapi.constants.ApiCustomerConstants;
import com.pzj.platform.appapi.constants.ApiDefault;
import com.pzj.platform.appapi.dubbo.CustomerApi;
import com.pzj.platform.appapi.entity.ResellerSimpleInfo;
import com.pzj.platform.appapi.exception.BussinessException;
import com.pzj.platform.appapi.exception.Exceptions;
import com.pzj.platform.appapi.exception.UnExpectResultException;
import com.pzj.platform.appapi.exception.UploadImgException;
import com.pzj.platform.appapi.service.CustomerService;
import com.pzj.platform.appapi.service.ParamCheckService;
import com.pzj.platform.appapi.util.CheckUtils;
import com.pzj.platform.appapi.util.LogUtil;
import com.pzj.platform.appapi.util.PropertyLoader;

/**
 * 
 * @author Mark
 * @version $Id: CustomerServiceImpl.java, v 0.1 2016年7月13日 下午8:44:23 pengliqing Exp $
 */
@Service("CustomerServiceApp")
public class CustomerServiceImpl implements CustomerService {

    private static final Logger                      logger = LoggerFactory.getLogger(CustomerServiceImpl.class);
    @Resource(name = "propertyLoader")
    private PropertyLoader                           propertyLoader;
    @Autowired
    private CustomerApi                              customerApi;
    @Autowired
    CustomerRelationService                          customerRelationService;
    @Autowired
    private com.pzj.customer.service.CustomerService customerservice;

    /**
     * 获取当前登录用户的信息
     *
     * @param token
     *            用户的token
     * @return Customer 用户
     *
     */
    @Override
    public Customer queryCustomer(String token) {
        Customer userToken = null;
        try {
            userToken = customerApi.getUserToken(token);
            if (userToken != null)
                userToken.setToken(token);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userToken;
    }

    /**
     * 根据ID查询用户
     *
     * @param customerId
     * @return Customer 用户
     *
     */
    @Override
    public Customer getCustomerById(long customerId) {
        return customerApi.getCustomerById(customerId);
    }

    /**
     * 登录
     *
     * @param username 用户名
     * @param password 密码
     * @param type 用户类型
     * @throws Exception 
     */
    @Override
    public Map<String, Object> login(String username, String password, String type) throws Exception {
        //验证用户名是否存在
        List<Customer> checkUnameList = customerApi.findCustomerByLoginName(username);
        if (CheckUtils.isEmpty(checkUnameList))
            throw new BussinessException(Exceptions.LOGINERROR_10106);
        Customer customer = customerApi.login(username, password);
        customerLoginCheck(customer, type);

        Map<String, Object> jsonObject = new HashMap<String, Object>();
        if (CheckUtils.isEmpty(customer.getLastLoginIp())) {
            jsonObject.put("bund", 0);
        } else {
            jsonObject.put("bund", 1);
            customer.setLastLoginIp("");
        }
        jsonObject.put("uid", customer.getId());
        jsonObject.put("token", customer.getToken());
        jsonObject.put("guideName", customer.getCorporater());//联系人
        jsonObject.put("mobile", customer.getCorporaterMobile());//手机号
        jsonObject.put("guideIdCard", customer.getCorporaterCredentials());//身份证号
        jsonObject.put("operChargerEmail", customer.getOperChargerEmail());//邮箱
        jsonObject.put("checkStatus", customer.getCheckStatus());//审核状态
        jsonObject.put("reasonRejection", customer.getReasonRejection());//拒绝原因
        jsonObject.put("invitationCode", customer.getInvitationCode());//邀请码
        /*        if (CheckUtils.isNotEmpty(customer.getInvitationCode())) {
                    jsonObject.put("invitationCode", customer.getInvitationCode());//邀请码
                } else {
                    jsonObject.put("invitationCode", getInvitationCode(customer.getId()));//邀请码
                }*/
        StringBuilder buffer = new StringBuilder();
        if (customer.getBusinessQualification() != null) {
            String[] business = customer.getBusinessQualification().split(",");
            for (int i = 0; i < business.length; i++) {
                if (CheckUtils.isNotEmpty(business[i])) {
                    buffer.append(business[i]).append("#");
                }
            }
            jsonObject.put("businessQualification", buffer.substring(0, buffer.length() - 1));//资质
        }

        jsonObject.put("loginName", customer.getLoginName());//用户名
        jsonObject.put("name", customer.getName());//用户名
        if (customer.getResellerType().equals(UserGlobalDict.businessUserType())) {
            jsonObject.put("identifyType", customer.getIdentifyType());//标示个人和企业
            jsonObject.put("directCompany", customer.getDirectCompany());//公司名称
            jsonObject.put("businessLicense", customer.getBusinessLicense());//营业执照号
            jsonObject.put("operChargerPhone", customer.getOperChargerPhone());//座机号码
            jsonObject.put("operChargerFax", customer.getOperChargerFax());//传真
            jsonObject.put("address", customer.getAddress());//详细地址
            jsonObject.put("guideCertificate", customer.getGuideCertificate());//经营许可证
            jsonObject.put("province", customer.getProvince());//省份
            jsonObject.put("city", customer.getCity());//城市
            jsonObject.put("county", customer.getCounty());//地区
            logger.info("用户审核状态====================" + customer.getCheckStatus());
        } else if (customer.getResellerType().equals(UserGlobalDict.guideUserType())) {
            jsonObject.put("guideCertificate", customer.getGuideCertificate());//导游证号
        }
        return jsonObject;
    }

    /**
     * 完善资料
     *
     */
    @Override
    public void updateResellerInfo(HttpServletRequest request, JSONObject data, Customer customer) throws Exception {
        String identifyType = data.containsKey("identifyType") ? data.getString("identifyType") : "";//商户类型
        String type = data.containsKey("type") ? data.getString("type") : "1";

        // 校验参数
        customerInfoCheck(data, identifyType, type, customer.getId());

        // 上传图片附件
        List<String> dataList = uploadImg(request, data.getString("number"));
        //编辑图片原来图片路径
        String imgs = data.getString("imgs");

        StringBuilder builder = new StringBuilder();
        if ((dataList != null && !dataList.isEmpty()) || (!imgs.equals("") && !imgs.isEmpty())) {
            if (dataList != null && !dataList.isEmpty()) {
                for (int i = 0; i < dataList.size(); i++) {
                    if (i == 0) {
                        builder.append(dataList.get(i));
                    } else {
                        builder.append(",").append(dataList.get(i));
                    }
                }
            }
            if (imgs != null && !imgs.isEmpty()) {
                if (builder.length() == 0) {
                    builder.append(imgs);
                } else {
                    builder.append(",").append(imgs);
                }
            }
            customer.setBusinessQualification(builder.toString());
            logger.debug("用户完善资料资质图片唯一标识码：{}", builder.toString());

        }
        if (data.getString("isUpdate") == null) {
            boolean isPersonal = false;
            if ("0".equals(type) || "p".equalsIgnoreCase(identifyType)) {
                isPersonal = true;
            }
            if (isPersonal) {
                customer.setName(data.getString("corporater"));
            } else {
                customer.setName(data.getString("directCompany"));
            }
            if (type.equals("0")) {
                customer.setGuideCertificate(data.getString("guideCardNum"));
            }

            customer.setSupplierAddress(data.getString("address"));
            customer.setAddress(data.getString("address"));
            customer.setOperChargerEmail(data.getString("email")); // 邮箱
            customer.setOperChargerFax(data.getString("operChargerFax")); // 传真
            customer.setOperChargerPhone(data.getString("operChargerPhone")); // 座机号
            customer.setCorporater(data.getString("corporater")); // 姓名
            customer.setCorporaterMobile(data.getString("mobile")); // 手机号
            customer.setCorporaterCredentials(data.getString("guideIdCard"));
            customer.setProvince(data.getString("province"));
            customer.setCity(data.getString("city"));
            customer.setCounty(data.getString("county"));
            customer.setBusinessLicense(data.getString("businessLicense"));
            customer.setInvitationCode(data.getString("invitationCode"));
            customer.setIsRoot(UserGlobalDict.mainAccount());
            customer.setIdentifyType(identifyType);
            customer.setCredentialsType(1);
        }
        customer.setAccountState(1);
        customer.setCheckStatus(UserGlobalDict.checkStatus());

        logger.debug("更新用户信息………");
        // 更新用户信息
        Integer integer = customerApi.saveCustomer(customer);
        logger.debug("更新用户信息完成！");

        if (integer < 1) {
            throw new UnExpectResultException(Exceptions.REGISTERERROR_10202);
        }
    }

    /**
     * 检查登陆用户
     *
     * @param customer 用户
     * @param type 用户类型
     *          <code>0</code> 导游
     *          <code>1</code> 商户
     */
    private void customerLoginCheck(Customer customer, String type) {
        if (customer == null) {//没有用户
            throw new BussinessException(Exceptions.LOGINERROR_10101);
        }
        if (!customer.getAccountState().equals(ApiCustomerConstants.ACCOUNT_STATE_NORMAL)) {//账户状态异常
            throw new BussinessException(Exceptions.LOGINERROR_10102);
        }

        if (!customer.getResellerType().equals(UserGlobalDict.guideUserType()) && !customer.getResellerType().equals(UserGlobalDict.businessUserType())) {
            throw new BussinessException(Exceptions.LOGINERROR_10103);
        }

        if (type.equals("0") && customer.getResellerType().equals(UserGlobalDict.businessUserType())) {
            throw new BussinessException(Exceptions.LOGINERROR_10104);
        }
        if (type.equals("1") && customer.getResellerType().equals(UserGlobalDict.guideUserType())) {
            throw new BussinessException(Exceptions.LOGINERROR_10105);
        }
    }

    /**
     * 用户资料完善，必要条件校验
     *
     * @param data 用户信息实体
     * @param identifyType 
     *          <code>q</code> 企业
     *          <code>p</code> 个人
     * @param type 用户类型
     *          <code>0</code> 导游
     *          <code>1</code> 商户
     * @param customerId
     * @throws Exception 
     */
    private void customerInfoCheck(JSONObject data, String identifyType, String type, Long customerId) throws Exception {
        logger.info("用户资料编辑数据=============" + data);
        if (data.getString("isUpdate") == null) {
            ParamCheckService.checkParam(data.getString("mobile"), "手机号不能为空");
            if ("0".equals(type)) {//导游
                ParamCheckService.checkParam(data.getString("corporater"), "联系人不能为空");
                ParamCheckService.checkParam(data.getString("guideIdCard"), "身份证号不能为空");
                ParamCheckService.checkParam(data.getString("guideCardNum"), "导游证号不能为空");
            } else {
                if ("q".equals(identifyType)) {//商户企业
                    ParamCheckService.checkParam(data.getString("directCompany"), "公司名称不能为空");
                    ParamCheckService.checkParam(data.getString("businessLicense"), "营业执照不能为空");
                } else {//商户个人
                    ParamCheckService.checkParam(data.getString("corporater"), "联系人不能为空");
                    ParamCheckService.checkParam(data.getString("guideIdCard"), "身份证号不能为空");
                }
            }
        }

        if (data.getString("businessLicense") != null && !data.getString("businessLicense").equals("")) {
            List<Customer> list = customerApi.findCustomerByBusinessLicense(data.getString("businessLicense"));
            if (list != null && list.size() > 0) {
                for (Customer cus : list) {
                    if (customerId.longValue() != cus.getId()) {
                        throw new BussinessException(Exceptions.REGISTERERROR_10204);
                    }
                }
            }
        }
        if (data.getString("directCompany") != null && !data.getString("directCompany").equals("")) {
            List<Customer> list = customerApi.findCustomerByDirectCompany(data.getString("directCompany"));
            if (list != null && list.size() > 0) {
                for (Customer cus : list) {
                    if (customerId.longValue() != cus.getId()) {
                        throw new BussinessException(Exceptions.REGISTERERROR_10205);
                    }
                }
            }
        }
        if (data.getString("guideIdCard") != null && !data.getString("guideIdCard").equals("")) {
            List<Customer> list = customerApi.findCustomerByGuideIdCard(data.getString("guideIdCard"));
            if (list != null && list.size() > 0) {
                for (Customer cus : list) {
                    if (customerId.longValue() != cus.getId()) {
                        throw new BussinessException(Exceptions.REGISTERERROR_10206);
                    }
                }
            }
        }
    }

    /**
     * 完善资料 - 上传附件
     *
     * @param request 
     * @param number 附件数量
     * @return 图片服务器生成的唯一标识码列表
     */
    private List<String> uploadImg(HttpServletRequest request, String number) {
        logger.debug("开始上传附件……");
        if (!(request instanceof MultipartHttpServletRequest))
            return null;
        Integer num = number == null ? 0 : Integer.valueOf(number);
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        List<String> dataList = new ArrayList<String>();
        JSONObject data2 = null;
        for (int i = 0; i < num; i++) {
            MultipartFile file = multipartRequest.getFile("upload" + i);
            HttpURLConnection conn = null;
            String BOUNDARY = "---------------------------123821742118716"; // boundary就是request头和上传文件内容的分隔符
            try {
                String imageServerUrl = propertyLoader.getProperty(ApiConstants.SYSTEM_FILENAME, "image.server.url", ApiDefault.DEFAULT_IMG_SERVER);
                URL url = new URL(imageServerUrl);
                conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(30000);
                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.setUseCaches(false);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 6.1; zh-CN; rv:1.9.2.6)");
                conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
                OutputStream out = new DataOutputStream(conn.getOutputStream());
                StringBuffer strBuf = new StringBuffer();
                strBuf.append("\r\n").append("--").append(BOUNDARY).append("\r\n");
                strBuf.append("Content-Disposition: form-data; name=\"" + "1" + "\"; filename=\"" + "1" + "\"\r\n");
                strBuf.append("Content-Type:" + "multipart/form-data" + "\r\n\r\n");
                out.write(strBuf.toString().getBytes());
                DataInputStream in = new DataInputStream(file.getInputStream());
                int bytes = 0;
                byte[] bufferOut = new byte[1024];
                while ((bytes = in.read(bufferOut)) != -1) {
                    out.write(bufferOut, 0, bytes);
                }
                in.close();
                byte[] endData = ("\r\n--" + BOUNDARY + "--\r\n").getBytes();
                out.write(endData);
                out.flush();
                out.close();
                // 读取返回数据
                strBuf = new StringBuffer();
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line = null;
                while ((line = reader.readLine()) != null) {
                    strBuf.append(line).append("\n");
                }
                data2 = JSON.parseObject(strBuf.toString());
                reader.close();
            } catch (Exception e) {
                logger.error(LogUtil.logFormat(ContextEntry.getMonitor().getTraceId(), e.getMessage()), e);
                throw new UploadImgException(Exceptions.REGISTERERROR_10201);
            } finally {
                if (conn != null) {
                    conn.disconnect();
                    conn = null;
                }
            }
            dataList.add(data2.getString("data"));
        }
        logger.debug("上传附件完成，上传了 {} 个附件！", dataList.size());
        return dataList;
    }

    /** 
     * @see com.pzj.platform.appapi.service.CustomerService#register(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public Customer register(JSONObject data) throws Exception {
        String type = data.containsKey("type") ? data.getString("type") : "1";
        ParamCheckService.checkParam(data.getString("username"), "用户名不能为空");
        ParamCheckService.checkParam(data.getString("password"), "密码不能为空");
        ParamCheckService.checkParam(data.getString("mobile"), "手机号不能为空");
        validateLoginName(data.getString("username"));

        Customer customer = new Customer();
        customer.setLoginName(data.getString("username"));
        customer.setLoginPasswd(data.getString("password"));
        customer.setCorporaterMobile(data.getString("mobile"));
        //customer.setOperChargerMobile(data.getString("mobile"));
        customer.setIdentifyType(data.getString("identifyType"));//商户类型
        customer.setInvitationCode(data.getString("invitationCode"));
        customer.setCredentialsType(1);
        customer.setAccountState(1);
        customer.setIsRoot(UserGlobalDict.mainAccount());
        customer.setCheckStatus(UserGlobalDict.registeredStatus());
        if (type.equals("1")) {
            customer.setUserType(UserGlobalDict.resellUserType());
            customer.setResellerType(UserGlobalDict.businessUserType());
        } else if (type.equals("0")) {
            customer.setUserType(UserGlobalDict.resellUserType());
            customer.setResellerType(UserGlobalDict.guideUserType());
        }
        Long result = customerApi.create(customer);
        if (result == null) {
            throw new UnExpectResultException(Exceptions.REGISTERERROR_10207);
        } else {
            Customer fCustomer = new Customer();
            fCustomer.setUserType(UserGlobalDict.salespersonType());
            fCustomer.setInvitationCode(data.getString("invitationCode"));
            List<Customer> findCustomerByParams = customerservice.findCustomerByParams(fCustomer);
            if (CollectionUtils.isNotEmpty(findCustomerByParams)) {
                Long userId = findCustomerByParams.get(0).getId();
                customerRelationService.insertBatch(userId, Arrays.asList(result), UserRelation.SALESPESON_RESELLER);
            }
        }
        return customer;
    }

    /**
     * 校验用户名是否重复
     *
     * @param data 用户注册信息
     * 
     * @throws Exception 
     */
    @Override
    public void validateLoginName(String loginName) throws Exception {
        List<Customer> list = customerApi.findCustomerByLoginName(loginName);
        if (list != null && list.size() > 0) {
            throw new BussinessException(Exceptions.REGISTERERROR_10203);
        }
    }

    private String getInvitationCode(Long relUserId) throws Exception {
        CustomerRelation relation = new CustomerRelation();
        relation.setRelType(UserRelation.SALESPESON_RESELLER);
        relation.setRelUserId(relUserId);
        PageList<CustomerRelation> page = customerApi.findCustomerRelation(relation);
        if (page != null && !page.isEmpty()) {
            CustomerRelation customerRelation = page.getResultList().get(0);
            Customer customer = customerApi.getCustomerById(customerRelation.getUserId());
            return customer.getInvitationCode();
        }
        return null;
    }

    /** 
     * @see com.pzj.platform.appapi.service.CustomerService#findResellerInfo(com.pzj.customer.entity.Customer)
     */
    @Override
    public ResellerSimpleInfo findResellerInfo(Customer customer) {
        ResellerSimpleInfo r = new ResellerSimpleInfo();
        r.setResellerId(customer.getId());
        r.setResellerType(customer.getResellerType());
        r.setResellerIdentifyType(customer.getIdentifyType());
        //如果身份属性为null或者个人，那么使用联系人姓名
        if (CheckUtils.isEmpty(customer.getIdentifyType()) || customer.getIdentifyType().equals(UserGlobalDict.personal)) {
            r.setName(customer.getCorporater());
        } else {
            //否则使用企业名称
            r.setName(customer.getName());
        }
        r.setMobile(customer.getCorporaterMobile());
        r.setIdCard(customer.getCorporaterCredentials());
        return r;
    }

}
