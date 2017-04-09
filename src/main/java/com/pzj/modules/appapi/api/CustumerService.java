package com.pzj.modules.appapi.api;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pzj.appapi.constants.ApiConstants;
import com.pzj.appapi.constants.LiteralConstants;
import com.pzj.base.common.global.UserGlobalDict;
import com.pzj.common.util.CheckUtils;
import com.pzj.customer.entity.Customer;
import com.pzj.modules.appapi.entity.CodeHandle;
import com.pzj.modules.appapi.entity.JsonEntity;
import com.pzj.platform.appapi.constants.ApiDefault;
import com.pzj.platform.appapi.util.PropertyLoader;

/**
 * 用户的基本信息
 *
 * @author wangkai
 * @date 2015年11月6日 下午5:41:03
 */
@Component
public class CustumerService {

    private static final Logger                        logger = LoggerFactory.getLogger(CustumerService.class);

    @Autowired
    private com.pzj.customer.service.CustomerService   customerService;
    @Resource(name = "propertyLoader")
    private PropertyLoader                             propertyLoader;
    @Autowired
    private com.pzj.cache.UserCacheService             userCacheService;
    @Autowired
    private com.pzj.message.sms.service.SmsSendService sMSSendService;

    /**
     * 验证当前用户的token是否有效
     *
     * @param token
     * @return boolean 返回类型
     *
     */
    public boolean isTokenValid(String token) {
        try {
            return customerService.isTokenValid(token.trim());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Customer getCustomerById(Long id) {
        try {

            return customerService.getCustomerById(id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取当前登录用户的信息
     *
     * @param token
     *            用户的token
     * @return Customer 用户
     *
     */
    public Customer queryCustomer(String token) {
        Customer userToken = null;
        try {
            userToken = userCacheService.getUserToken(token);
            if (userToken != null)
                userToken.setToken(token);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userToken;
    }

    /**
     * 登录
     *
     * @param data
     *            json参数包
     * @param customer
     *            当前登录用户
     * @param json
     *            返回结果包装
     * @key password 密码
     * @key username 用户名
     * @return JsonEntity 返回类型
     * @key bund 是否是首次登陆
     * @key token 用户登录凭证
     * @key guideName 名称
     * @key mobile 手机号
     */
    public JsonEntity login(JSONObject data, JsonEntity json) {
        String password = data.containsKey("password") ? data.getString("password") : "";
        String loginName = data.containsKey("username") ? data.getString("username") : "";
        String type = data.containsKey("type") ? data.getString("type") : "";
        if (!CheckUtils.isNotNull(loginName) && !CheckUtils.isNotNull(password)) {
            json.setCode(CodeHandle.LOGINERROR.getCode() + "");
            json.setMessage(CodeHandle.LOGINERROR.getMessage());
            return json;
        }
        return login(json, password, loginName, type);
    }

    public JsonEntity login(JsonEntity json, String password, String loginName, String type) {
        try {
            Customer customer = customerService.login(loginName, password);
            if (customer != null && customer.getAccountState().equals(1)) {
                //如果账号既不是商户又不是导游的判断
                if (!customer.getResellerType().equals(UserGlobalDict.guideUserType())
                    && !customer.getResellerType().equals(UserGlobalDict.businessUserType())) {
                    json.setCode(CodeHandle.ERROR10024.getCode() + "");
                    json.setMessage(CodeHandle.ERROR10024.getMessage());
                    return json;
                } else if (type.equals("0")) {//type为0   表示前端传过来的是   登录的  导游app
                    if (customer.getResellerType().equals(UserGlobalDict.guideUserType())) {
                        json.setCode(CodeHandle.SUCCESS.getCode() + "");
                        json.setMessage(CodeHandle.SUCCESS.getMessage());
                    } else if (customer.getResellerType().equals(UserGlobalDict.businessUserType())) {
                        json.setCode(CodeHandle.ERROR10025.getCode() + "");
                        json.setMessage(CodeHandle.ERROR10025.getMessage());
                        return json;
                    }
                } else if (type.equals("1")) {//type为1  表示前端传过来的是   登录的  商户app
                    if (customer.getResellerType().equals(UserGlobalDict.businessUserType())) {
                        json.setCode(CodeHandle.SUCCESS.getCode() + "");
                        json.setMessage(CodeHandle.SUCCESS.getMessage());
                    } else if (customer.getResellerType().equals(UserGlobalDict.guideUserType())) {
                        json.setCode(CodeHandle.ERROR10026.getCode() + "");
                        json.setMessage(CodeHandle.ERROR10026.getMessage());
                        return json;
                    }
                }
                //                if (CheckUtils.isNull(customer.getCheckStatus()) || customer.getCheckStatus().equals(UserGlobalDict.rejustStatus())) {
                //                    json.setCode(CodeHandle.ERROR10022.getCode() + "");
                //                    json.setMessage(CodeHandle.ERROR10022.getMessage());
                //                    return json;
                //                } else if (CheckUtils.isNull(customer.getCheckStatus()) || customer.getCheckStatus().equals(UserGlobalDict.checkStatus())) {
                //                    json.setCode(CodeHandle.ERROR10023.getCode() + "");
                //                    json.setMessage(CodeHandle.ERROR10023.getMessage());
                //                    return json;
                //                }
                //                if (!CheckUtils.isNull(customer.getCheckStatus()) && customer.getCheckStatus().equals("3")) {
                //                    json.setCode(CodeHandle.ERROR10029.getCode() + "");
                //                    json.setMessage(customer.getReasonRejection() == null ? "" : customer.getReasonRejection());
                //                    return json;
                //                }

                Map<String, Object> jsonObject = Maps.newHashMap();
                if (CheckUtils.isNull(customer.getLastLoginIp())) {
                    jsonObject.put("bund", 0);
                } else {
                    jsonObject.put("bund", 1);
                    customer.setLastLoginIp("");
                }
                jsonObject.put("token", customer.getToken());
                String name = customer.getName();
                String mobile = customer.getCorporaterMobile();
                String guideIdCard = customer.getCorporaterCredentials();
                String guideCardNum = customer.getGuideCertificate();
                String resellerType = customer.getResellerType();
                String logName = customer.getLoginName();
                String oper_charger_email = customer.getOperChargerEmail();
                String business_qualification = customer.getBusinessQualification();
                String guideCertificate = customer.getGuideCertificate();
                String directCompany = customer.getDirectCompany();
                String identifyType = customer.getIdentifyType();
                String businessLicense = customer.getBusinessLicense();
                String oper_charger_fax = customer.getOperChargerFax();
                String oper_charger_phone = customer.getOperChargerPhone();
                String corporater = customer.getCorporater();
                String address = customer.getAddress();
                StringBuffer buffer = new StringBuffer();
                if (business_qualification != null) {
                    String[] business = business_qualification.split(",");
                    for (int i = 0; i < business.length; i++) {
                        buffer.append(business[i]).append("#");
                    }
                }
                String qualification = buffer.toString();
                if (qualification != null && !qualification.equals("")) {
                    qualification = qualification.substring(0, (buffer.length()) - 1);
                }
                jsonObject.put("guideName", corporater);//联系人
                jsonObject.put("mobile", mobile);//手机号
                jsonObject.put("guideIdCard", guideIdCard);//身份证号
                jsonObject.put("oper_charger_email", oper_charger_email);//邮箱
                jsonObject.put("business_qualification", qualification);//资质
                jsonObject.put("logName", logName);//用户名
                jsonObject.put("name", name);//用户名
                jsonObject.put("checkStatus", customer.getCheckStatus());//审核状态
                jsonObject.put("invitationCode", customer.getInvitationCode());//邀请码
                if (resellerType.equals(UserGlobalDict.businessUserType())) {
                    jsonObject.put("identifyType", identifyType);//标示个人和企业
                    jsonObject.put("directCompany", directCompany);//公司名称
                    jsonObject.put("businessLicense", businessLicense);//营业执照号
                    jsonObject.put("oper_charger_phone", oper_charger_phone);//座机号码
                    jsonObject.put("oper_charger_fax", oper_charger_fax);//传真
                    jsonObject.put("address", address);//详细地址
                    jsonObject.put("guideCardNum", guideCardNum);//经营许可证
                    jsonObject.put("province", customer.getProvince());//省份
                    jsonObject.put("city", customer.getCity());//城市
                    jsonObject.put("county", customer.getCounty());//地区
                } else if (resellerType.equals(UserGlobalDict.guideUserType())) {
                    jsonObject.put("guideCertificate", guideCertificate);//导游证号
                }
                json.setResponseBody(jsonObject);
                json.setCode(CodeHandle.SUCCESS.getCode() + "");
                json.setMessage(CodeHandle.SUCCESS.getMessage());
            } else {
                List<Customer> list = customerService.findCustomerByLoginName(loginName, null);
                if (list == null) {
                    json.setCode(CodeHandle.ExistERROR.getCode() + "");
                    json.setMessage(CodeHandle.ExistERROR.getMessage());
                } else {
                    json.setCode(CodeHandle.LOGINERROR.getCode() + "");
                    json.setMessage(CodeHandle.LOGINERROR.getMessage());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            json.setCode(CodeHandle.SERVERERROR.getCode() + "");
            json.setMessage(CodeHandle.SERVERERROR.getMessage());
        }
        return json;
    }

    /**
     * 验证码是否正确
     *
     * @param data
     *            json参数包
     * @param customer
     *            当前登录用户
     * @param json
     *            返回结果包装
     * @key mobile 电话号码
     * @key code 验证码
     * @return JsonEntity 返回类型
     * @throws Exception 
     *
     */
    public JsonEntity checkCode(JSONObject data, JsonEntity json) throws Exception {
        String mobile = data.containsKey("mobile") ? data.getString("mobile") : "";
        String code = data.containsKey("code") ? data.getString("code") : "";

        String key = "mob_" + mobile;
        // 验证码是否正确
        String userCode = userCacheService.getUserCode(key);
        if (!code.equals(userCode)) {
            json.setCode(CodeHandle.ERROR10017.getCode() + "");
            json.setMessage(CodeHandle.ERROR10017.getMessage());
        } else {
            json.setCode(CodeHandle.SUCCESS.getCode() + "");
        }

        return json;
    }

    /**
     * 注册分销商或者导游
     *
     * @param data
     *            json参数包
     * @param customer
     *            当前登录用户
     * @param json
     *            返回结果包装
     * @key guideName 姓名
     * @key guideCardNum 导游证号
     * @key guideIdCard 身份证号（后六位为默认密码）
     * @key mobile 手机号（作为用户名）
     * @key email 邮箱地址
     * @key corporater 管理员姓名
     * @key address 地址
     * @key password 密码
     * @key type 1:商户 2：导游
     * @return JsonEntity 返回类型
     * @throws IOException
     *
     */
    public JsonEntity registerReseller(HttpServletRequest request, JSONObject data, JsonEntity json) throws IOException {
        String name = data.containsKey("guideName") ? data.getString("guideName") : "";
        String number = data.containsKey("number") ? data.getString("number") : "";
        Integer num = Integer.valueOf(number);
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        InputStream input = null;
        List<String> dataList = new ArrayList<>();
        StringBuffer buffer = new StringBuffer();
        JSONObject data2 = null;
        for (int i = 0; i < num; i++) {
            MultipartFile file = multipartRequest.getFile("upload" + i);
            input = file.getInputStream();
            URL url = null;
            String res = "";
            HttpURLConnection conn = null;
            String BOUNDARY = "---------------------------123821742118716"; // boundary就是request头和上传文件内容的分隔符
            try {
                String imageServerUrl = propertyLoader.getProperty(ApiConstants.SYSTEM_FILENAME, "image.server.url", ApiDefault.DEFAULT_IMG_SERVER);
                url = new URL(imageServerUrl);
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
                String filename = "1";
                String contentType = "";
                if (contentType == null || contentType.equals("")) {
                    contentType = "multipart/form-data";
                }
                StringBuffer strBuf = new StringBuffer();
                strBuf.append("\r\n").append("--").append(BOUNDARY).append("\r\n");
                strBuf.append("Content-Disposition: form-data; name=\"" + filename + "\"; filename=\"" + filename + "\"\r\n");
                strBuf.append("Content-Type:" + contentType + "\r\n\r\n");
                out.write(strBuf.toString().getBytes());
                DataInputStream in = new DataInputStream(input);
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
                res = strBuf.toString();
                data2 = JSON.parseObject(res);
                reader.close();
                json.setCode(CodeHandle.SUCCESS.getCode());
                json.setMessage(CodeHandle.SUCCESS.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
                json.setCode(CodeHandle.SERVERERROR.getCode() + "");
                json.setMessage(CodeHandle.SERVERERROR.getMessage());
            } finally {
                if (conn != null) {
                    conn.disconnect();
                    conn = null;
                }
                if (input != null) {
                    input.close();
                }
            }
            if (!json.getCode().equals("10000")) {
                json.setCode(CodeHandle.ERROR10032.getCode() + "");
                json.setMessage(CodeHandle.ERROR10032.getMessage());
                return json;
            }
            dataList.add(data2.getString("data"));
        }
        String idCard = data.containsKey("guideIdCard") ? data.getString("guideIdCard") : "";
        String mobile = data.containsKey("mobile") ? data.getString("mobile") : "";
        String guideCardNum = data.containsKey("guideCardNum") ? data.getString("guideCardNum") : "";
        String corporater = data.containsKey("corporater") ? data.getString("corporater") : "";
        String email = data.containsKey("email") ? data.getString("email") : "";
        String province = data.containsKey("province") ? data.getString("province") : "";
        String address = data.containsKey("address") ? data.getString("address") : "";
        String password = data.containsKey("password") ? data.getString("password") : "";
        String city = data.containsKey("city") ? data.getString("city") : "";
        String county = data.containsKey("county") ? data.getString("county") : "";
        String operChargerPhone = data.containsKey("operChargerPhone") ? data.getString("operChargerPhone") : "";
        String directCompany = data.containsKey("directCompany") ? data.getString("directCompany") : "";//公司名称
        String businessLicense = data.containsKey("businessLicense") ? data.getString("businessLicense") : ""; //营业执照号
        String operChargerFax = data.containsKey("operChargerFax") ? data.getString("operChargerFax") : ""; //传真号码
        String identifyType = data.containsKey("identifyType") ? data.getString("identifyType") : "";//商户类型

        String type = data.containsKey("type") ? data.getString("type") : "1";
        boolean isPersonal = false;
        if ("0".equals(type) || "p".equalsIgnoreCase(identifyType)) {
            isPersonal = true;
        }
        if (isPersonal) {
            if (CheckUtils.isNull(name) || CheckUtils.isNull(mobile) || CheckUtils.isNull(idCard) || CheckUtils.isNull(password)) {
                json.setCode(CodeHandle.CODE_90001.getCode() + "");
                json.setMessage(CodeHandle.CODE_90001.getMessage());
                return json;
            }
        } else {
            if (CheckUtils.isNull(name) || CheckUtils.isNull(mobile) || CheckUtils.isNull(password)) {
                json.setCode(CodeHandle.CODE_90001.getCode() + "");
                json.setMessage(CodeHandle.CODE_90001.getMessage());
                return json;
            }
        }
        Long integer = null;
        try {
            Customer customer = new Customer();
            customer.setLoginName(name);
            if (isPersonal) {
                customer.setName(corporater);
            } else {
                customer.setName(directCompany);
            }
            customer.setLoginPasswd(password);
            customer.setCredentialsType(1);
            customer.setSupplierAddress(address);
            customer.setAddress(address);
            customer.setCorporaterEmail(email);
            customer.setCorporaterCredentials(idCard);
            customer.setCorporater(corporater);
            customer.setCorporaterMobile(mobile);

            customer.setCheckStatus(UserGlobalDict.checkStatus());
            customer.setOperChargerEmail(email);
            customer.setOperChargerMobile(mobile);
            customer.setIsRoot(UserGlobalDict.mainAccount());
            customer.setAccountState(1);
            customer.setProvince(province);
            customer.setCity(city);
            customer.setCounty(county);
            customer.setCorporaterPhone(operChargerPhone);
            customer.setOperChargerPhone(operChargerPhone);
            customer.setOperChargerEmail(email);
            customer.setBusinessLicense(businessLicense);
            customer.setOperChargerFax(operChargerFax);
            customer.setIdentifyType(identifyType);

            for (int i = 0; i < dataList.size(); i++) {
                if (customer.getBusinessQualification() != null) {
                    customer.setBusinessQualification(buffer.append(",").append(dataList.get(i)).toString());
                } else {
                    customer.setBusinessQualification(buffer.append(dataList.get(i).toString()).toString());
                }
            }
            if (type.equals("1")) {
                customer.setUserType(UserGlobalDict.resellUserType());
                customer.setResellerType(UserGlobalDict.businessUserType());

            } else if (type.equals("0")) {
                customer.setGuideCertificate(guideCardNum);
                customer.setUserType(UserGlobalDict.resellUserType());
                customer.setResellerType(UserGlobalDict.guideUserType());
            }

            // 验证手机号、身份证是否重复
            List<Customer> list = customerService.findCustomerByLoginName(customer.getLoginName(), null);
            if (list != null && list.size() > 0) {
                json.setCode("-1");
                json.setMessage("该用户名已被注册");
                return json;
            }
            Customer co = new Customer();
            if (businessLicense != null && !businessLicense.equals("")) {
                co.setBusinessLicense(businessLicense);
                List<Customer> list1 = customerService.findCustomerByParams(co);
                if (list1 != null && list1.size() > 0) {
                    json.setCode("-1");
                    json.setMessage("营业执照号重复");
                    return json;
                }
            }

            Customer omer = new Customer();
            if (directCompany != null && !directCompany.equals("")) {
                omer.setName(directCompany);
                List<Customer> list2 = customerService.findCustomerByParams(omer);
                if (list2 != null && list2.size() > 0) {
                    json.setCode("-1");
                    json.setMessage("公司名称重复");
                    return json;
                }
            }
            Customer ome = new Customer();
            if (idCard != null && !idCard.equals("")) {
                ome.setCorporaterCredentials(idCard);
                List<Customer> list3 = customerService.findCustomerByParams(ome);
                if (list3 != null && list3.size() > 0) {
                    json.setCode("-1");
                    json.setMessage("身份证号重复");
                    return json;
                }
            }
            integer = customerService.createCustomer(customer);
            // balanceService.insertSelective(customer);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (integer != null) {
            json.setCode("10000");
            json.setMessage("用户注册成功");
            return json;
        } else {
            json.setCode("-1");
            json.setMessage("用户注册失败");
            return json;
        }
    }

    /**
     * 获取当前登录用户的信息，附带一些统计信息
     *
     * @param data
     *            json参数包
     * @param customer
     *            当前登录用户
     * @param json
     *            返回结果包装
     * @return JsonEntity 返回类型
     * @key guideName 姓名
     * @key guideIdCard 身份证号
     * @key guideCardNum 导游证号
     * @key mobile 手机号
     * @key address 收货地址
     * @key guideCardNum 导游证号
     * @key teamCount 带团数量
     * @key teamUserCount 带游客数量
     * @key total 总订单数
     * @key amount 总金额
     * @key checktotal 已检订单数
     * @key checkednum 已检游客数
     * @key checkamount 已检金额
     */
    public JsonEntity getReseller(JSONObject data, Customer customer, JsonEntity json) {

        Map<String, Object> jsonObject = Maps.newHashMap();

        jsonObject.put("guideName", customer.getName());
        jsonObject.put("mobile", customer.getOperChargerMobile());
        jsonObject.put("guideIdCard", customer.getCorporaterCredentials());
        jsonObject.put("guideCardNum", customer.getBusinessCertificate());
        jsonObject.put("address", customer.getAddress());

        jsonObject.put("teamCount", LiteralConstants.ZERO_AMOUNT);
        jsonObject.put("teamUserCount", LiteralConstants.ZERO_AMOUNT);
        jsonObject.put("total", LiteralConstants.ZERO_AMOUNT);
        jsonObject.put("amount", LiteralConstants.ZERO_AMOUNT);
        jsonObject.put("checktotal", LiteralConstants.ZERO_AMOUNT);
        jsonObject.put("checkednum", LiteralConstants.ZERO_AMOUNT);
        jsonObject.put("checkamount", LiteralConstants.ZERO_AMOUNT);

        json.setResponseBody(jsonObject);
        json.setCode(CodeHandle.SUCCESS.getCode());
        json.setMessage(CodeHandle.SUCCESS.getMessage());
        return json;
    }

    /**
     * 修改用户的信息
     *
     * @param data
     *            json参数包
     * @param customer
     *            当前登录用户
     * @param json
     *            返回结果包装
     * @key guideName 姓名
     * @key password 密码
     * @key guideIdCard 身份证号
     * @key guideCardNum 导游证号
     * @key code 验证码 （code不为空时,修改手机号）
     * @key mobile 手机号
     * @return JsonEntity 返回类型
     *
     */
    public JsonEntity updateReseller(JSONObject data, Customer customer, JsonEntity json) {
        String name = data.getString("guideName");
        String password = data.getString("password");
        String idCard = data.getString("guideIdCard");
        String guideCardNum = data.getString("guideCardNum");

        String code = data.getString("code");
        String mobile = data.getString("mobile");
        if (CheckUtils.isNotNull(mobile) && CheckUtils.isNotNull(code)) {
            if (CheckUtils.isNull(mobile)) {
                json.setCode(CodeHandle.CODE_90001.getCode());
                json.setMessage(CodeHandle.CODE_90001.getMessage());
                return json;
            }
            customer.setOperChargerMobile(mobile);
        } else {
            if (CheckUtils.isNull(name)) {
                json.setCode(CodeHandle.CODE_90001.getCode());
                json.setMessage(CodeHandle.CODE_90001.getMessage());
                return json;
            }
            customer.setName(name);
            customer.setLastLoginIp(LiteralConstants.LOCAL_HOST_IP);
            customer.setLoginPasswd(password);
            customer.setCorporaterCredentials(idCard);
            customer.setOperChargerMobile(mobile);
            if (customer.getResellerType().equals(UserGlobalDict.guideUserType())) {
                customer.setBusinessCertificate(guideCardNum);
            }
        }
        Integer integer = 0;
        try {
            integer = customerService.saveCustomer(customer);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (integer > 0) {
            json.setCode(CodeHandle.SUCCESS.getCode() + "");
            json.setMessage(CodeHandle.SUCCESS.getMessage());
        } else {
            json.setCode(CodeHandle.UPDATEERROR.getCode() + "");
            json.setMessage(CodeHandle.UPDATEERROR.getMessage());
        }
        return json;
    }

    /**
     * 通过手机号获取的验证码，强行修改密码
     *
     * @param data
     *            json参数包
     * @param customer
     *            当前登录用户
     * @param json
     *            返回结果包装
     * @key password 新密码
     * @key code 手机验证码
     * @key tel 手机号
     * @return JsonEntity 返回类型
     *
     */
    public JsonEntity updatePasswordWithCode(JSONObject data, JsonEntity json) {
        String password = data.containsKey("password") ? data.getString("password") : "";//接收密码
        String code = data.containsKey("code") ? data.getString("code") : "";
        String mobile = data.containsKey("mobile") ? data.getString("mobile") : "";//接收手机号
        String loginName = data.containsKey("loginName") ? data.getString("loginName") : "";//接收用户名
        String isOnly = data.containsKey("isOnly") ? data.getString("isOnly") : "";//判断这个手机号是否是唯一的
        Customer customer = new Customer();
        List<Customer> list = null;
        if (mobile.equals("") || code.equals("")) {
            json.setCode(CodeHandle.CODE_90001.getCode() + "");
            json.setMessage(CodeHandle.CODE_90001.getMessage());
            return json;
        }
        try {
            if (isOnly.equals("1")) {//如果手机号不是唯一的，就用用户名去查询
                customer.setLoginName(loginName);
                list = customerService.findCustomerByLoginName(customer.getLoginName(), null);
            } else {
                //如果手机号是唯一的，就用手机号去查询
                customer.setOperChargerMobile(mobile);
                list = customerService.findCustomerByParams(customer, false);
            }
            if (list != null && list.size() > 0) {
                Customer customer2 = list.get(0);
                customer2.setLoginPasswd(password);
                Integer integer = customerService.saveCustomerPassword(customer2);
                if (integer > 0) {
                    json.setCode(CodeHandle.SUCCESS.getCode() + "");
                    json.setMessage(CodeHandle.SUCCESS.getMessage());
                } else {
                    json.setCode(CodeHandle.UPDATEERROR.getCode() + "");
                    json.setMessage(CodeHandle.UPDATEERROR.getMessage());
                }
            } else {
                json.setCode(CodeHandle.ERROR10018.getCode() + "");
                json.setMessage(CodeHandle.ERROR10018.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
            json.setCode(CodeHandle.SERVERERROR.getCode() + "");
            json.setMessage(CodeHandle.SERVERERROR.getMessage());
        }
        return json;
    }

    /**
     * 根据旧密码修改密码
     *
     * @param data
     *            json参数包
     * @param customer
     *            当前登录用户
     * @param json
     *            返回结果包装
     * @key oldPasswd 旧密码
     * @key password 新密码
     * @return JsonEntity 返回类型
     *
     */
    public JsonEntity updatePassword(JSONObject data, Customer customer, JsonEntity json) {
        String oldPasswd = data.containsKey("oldPasswd") ? data.getString("oldPasswd") : "";
        String password = data.containsKey("password") ? data.getString("password") : "";
        if (oldPasswd.equals("") || password.equals("")) {
            json.setCode(CodeHandle.CODE_90001.getCode() + "");
            json.setMessage(CodeHandle.CODE_90001.getMessage());
            return json;
        }
        boolean compilePasswordMd5 = customerService.compilePasswordMd5(customer.getId(), customer.getLoginName(), oldPasswd, customer.getLoginPasswd());
        // 验证老旧密码是否相等
        try {
            if (compilePasswordMd5) {
                customer.setLoginPasswd(password);
                Integer integer = customerService.saveCustomerPassword(customer);
                if (integer > 0) {
                    json.setCode(CodeHandle.SUCCESS.getCode() + "");
                    json.setMessage(CodeHandle.SUCCESS.getMessage());
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("token", customer.getToken());
                    json.setResponseBody(jsonObject);
                } else {
                    json.setCode(CodeHandle.UPDATEERROR.getCode() + "");
                    json.setMessage(CodeHandle.UPDATEERROR.getMessage());
                }
            } else {
                json.setCode(CodeHandle.PASSWORDERROR.getCode() + "");
                json.setMessage(CodeHandle.PASSWORDERROR.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
            json.setCode(CodeHandle.SERVERERROR.getCode() + "");
            json.setMessage(CodeHandle.SERVERERROR.getMessage());
        }
        return json;
    }

    /**
     * 发送验证码，并判断用户是否存在
     *
     * @param data
     *            json参数包
     * @param json
     *            返回结果包装
     * @key tel 接受验证码的手机
     * @return JsonEntity 返回类型
     *
     */
    public JsonEntity sendCodeWithMobile(JSONObject data, JsonEntity json) {
        String tel = data.containsKey("tel") ? data.getString("tel") : "";//手机号
        String type = data.containsKey("type") ? data.getString("type") : "";// 1:修改密码 2:验证手机号
        String loginName = data.containsKey("loginName") ? data.getString("loginName") : "";// 用户名
        List<Customer> list = null;
        if (CheckUtils.isNull(tel)) {
            json.setCode(CodeHandle.CODE_90001.getCode() + "");
            json.setMessage(CodeHandle.CODE_90001.getMessage() + "tel为空");
            return json;
        }
        try {
            String isOnly = "0";
            if (type.equals("1")) {//如果
                Customer customer = new Customer();
                customer.setCorporaterMobile(tel);
                list = customerService.findCustomerByParams(customer, false);
                if (list == null) {
                    json.setCode(CodeHandle.ERROR10030.getCode() + "");
                    json.setMessage(CodeHandle.ERROR10030.getMessage());
                    return json;
                } else if (list.size() > 1) {
                    isOnly = "1";
                    json.setCode(CodeHandle.ERROR10031.getCode() + "");
                    json.setMessage(CodeHandle.ERROR10031.getMessage());
                }
            } else {
                Customer customer = new Customer();
                customer.setLoginName(loginName);
                list = customerService.findCustomerByLoginName(customer.getLoginName(), null);
                if (list != null && list.size() > 0) {
                    json.setCode(CodeHandle.ERROR10020.getCode() + "");
                    json.setMessage(CodeHandle.ERROR10020.getMessage());
                    return json;
                }
            }
            String randomNum = getRandomNum(6);
            sendMessage("mob", tel, randomNum);
            logger.info(randomNum);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("isOnly", isOnly);
            json.setResponseBody(jsonObject);
            json.setCode(CodeHandle.SUCCESS.getCode() + "");
            json.setMessage(CodeHandle.SUCCESS.getMessage());
            return json;
        } catch (Exception e) {
            e.printStackTrace();
            json.setCode(CodeHandle.SERVERERROR.getCode() + "");
            json.setMessage(CodeHandle.SERVERERROR.getMessage());
            return json;
        }
    }

    /**
     * 给当前登录用户发送验证码
     *
     * @param data
     *            json参数包
     * @param customer
     *            当前登录用户
     * @param json
     *            返回结果包装
     * @return JsonEntity 返回类型
     * @throws Exception
     *
     */
    public JsonEntity sendCode(JSONObject data, Customer customer, JsonEntity json) throws Exception {
        String mobile = customer.getOperChargerMobile();
        String randomNum = getRandomNum(6);
        sendMessage(customer.getId() + "", mobile, randomNum);
        json.setCode(CodeHandle.SUCCESS.getCode() + "");
        json.setMessage(CodeHandle.SUCCESS.getMessage());
        return json;
    }

    /**
     * 获取num位的重复随机码 纯数字
     *
     * @param num
     * @return String 返回类型
     *
     */
    public static String getRandomNum(int num) {
        Random random = new Random();
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < num; i++) {
            buf.append(random.nextInt(9));
        }
        return buf.toString();
    }

    private void sendMessage(String sub, String mobile, String num) throws Exception {
        userCacheService.putUserCode(sub + "_" + mobile, num, 300);
        String messageTemplateContent = propertyLoader.getProperty(ApiConstants.SYSTEM_FILENAME, "send.SMS.fh.login");
        String sms = messageTemplateContent.replaceAll("%code%", num);
        sMSSendService.sendSMS(mobile, sms);
    }

    /**
     * TODO(上传图片，暂时不做)
     *
     * @key json
     * @key request
     * @return JsonEntity 返回类型
     *
     */
    public JsonEntity uploadPhoto(JSONObject data, Customer customer, JsonEntity json, HttpServletRequest request) {
        // TODO 上传图片
        return null;
    }

    /**
     * 获取用户的收货地址
     *
     * @key json
     * @return JsonEntity 返回类型
     *
     */
    public JsonEntity getAddress(JSONObject data, Customer customer, JsonEntity json) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * 动态搜索地接社、地接社部门列表
     *
     * @param data
     *            json参数包
     * @param customer
     *            当前登录用户
     * @param json
     *            返回结果包装
     * @return JsonEntity 返回类型
     *
     */
    public JsonEntity queryResellerComplete(JSONObject data, Customer customer, JsonEntity json) {
        String name = data.containsKey("name") ? data.getString("name") : "";
        String scale = data.containsKey("scale") ? data.getString("scale") : "1";
        name = name.toLowerCase();
        Customer customer2 = new Customer();
        customer2.setName(name);
        customer2.setUserType(UserGlobalDict.resellUserType());
        customer2.setCheckStatus(UserGlobalDict.passStatus());
        if (scale.equals("1")) {
            customer2.setResellerType(UserGlobalDict.travelUserType());
        } else {
            customer2.setResellerType(UserGlobalDict.travelDeptUserType());
        }
        try {
            List<Customer> findCustomerByParams = customerService.findCustomerByParams(customer2);
            List<Map<String, Object>> jsonArray = Lists.newArrayList();
            if (findCustomerByParams != null && findCustomerByParams.size() > 0) {
                for (Customer reseller : findCustomerByParams) {
                    Map<String, Object> jsonObject = Maps.newHashMap();
                    jsonObject.put("name", reseller.getName());
                    jsonObject.put("id", String.valueOf(reseller.getId()));
                    jsonArray.add(jsonObject);
                }
            }

            Map<String, Object> object = Maps.newHashMap();
            object.put("list", jsonArray);
            json.setCode(CodeHandle.SUCCESS.getCode() + "");
            json.setMessage(CodeHandle.SUCCESS.getMessage());
            json.setResponseBody(object);
        } catch (Exception e) {
            e.printStackTrace();
            json.setCode(CodeHandle.SERVERERROR.getCode() + "");
            json.setMessage(CodeHandle.SERVERERROR.getMessage());
        }
        return json;
    }

    /**
     * 验证用户名是否重复
     *
     * @param data
     *            json参数包
     * @param json
     *            返回结果包装
     * @return JsonEntity 返回类型
     *
     */
    public JsonEntity sentenced(JSONObject data, JsonEntity json) {
        String name = data.containsKey("loginName") ? data.getString("loginName") : "";//接收用户名
        try {
            List<Customer> list = customerService.findCustomerByLoginName(name, null);
            if (list != null && list.size() > 0) {
                json.setCode("-1");
                json.setMessage("该用户名已被注册");
                return json;
            }
            json.setCode(CodeHandle.SUCCESS.getCode() + "");
            json.setMessage(CodeHandle.SUCCESS.getMessage());
            return json;
        } catch (Exception e) {
            e.printStackTrace();
            json.setCode(CodeHandle.SERVERERROR.getCode() + "");
            json.setMessage(CodeHandle.SERVERERROR.getMessage());
            return json;
        }

    }

    /**
     * 验证用户名和手机号是否匹配
     *
     * @param data
     *            json参数包
     * @param json
     *            返回结果包装
     * @return JsonEntity 返回类型
     *
     */
    public JsonEntity isMatching(JSONObject data, JsonEntity json) {
        String name = data.containsKey("loginName") ? data.getString("loginName") : "";//接收用户名
        String tel = data.containsKey("tel") ? data.getString("tel") : "";
        try {
            List<Customer> list = customerService.findCustomerByLoginName(name, null);
            if (list == null) {
                json.setCode(CodeHandle.ERROR10030.getCode() + "");
                json.setMessage(CodeHandle.ERROR10030.getMessage());
                return json;
            } else if (!list.get(0).getCorporaterMobile().equals(tel)) {
                json.setCode(CodeHandle.ERROR10028.getCode() + "");
                json.setMessage(CodeHandle.ERROR10028.getMessage());
                return json;
            }
            json.setCode(CodeHandle.SUCCESS.getCode() + "");
            json.setMessage(CodeHandle.SUCCESS.getMessage());
            return json;
        } catch (Exception e) {
            e.printStackTrace();
            json.setCode(CodeHandle.SERVERERROR.getCode() + "");
            json.setMessage(CodeHandle.SERVERERROR.getMessage());
            return json;
        }
    }

}
