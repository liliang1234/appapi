/**
 * piaozhijia.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package com.pzj.platform.appapi.exception;

/**
 * 
 * @author Mark
 * @version $Id: Exceptions.java, v 0.1 2016年7月13日 下午9:53:42 pengliqing Exp $
 */
public enum Exceptions {

    SUCCESS         ("10000", "成功"),
    SERVERERROR     ("10001", "系统错误,请联系管理员"),
    PARAMEMPTYERROR ("10002", "参数缺失"),
    DATAERROR       ("10003", "数据异常"),
    TOKENRERROR     ("10004", "token失效请登录"),
    SIGNERROR       ("10008", "验签失败"),
    
    SERVICEERROR    ("10005", "远程服务异常"),
    TIMEOUTEERROR   ("10006", "服务超时"),
    
    /* 登陆异常 */
    LOGINERROR_10101("10101", "密码错误"),
    LOGINERROR_10102("10102", "账户异常"),
    LOGINERROR_10103("10103", "账号非导游、商户权限"),
    LOGINERROR_10104("10104", "账号为商户权限（导游APP）"),
    LOGINERROR_10105("10105", "账号为导游权限（商户APP）"),
    LOGINERROR_10106("10106", "请核实用户名"),
    /* 用户注册/资料完善相关异常 */
    REGISTERERROR_10201("10201", "图片上传失败"),
    REGISTERERROR_10202("10202", "保存用户资料失败"),
    REGISTERERROR_10203("10203", "用户名重复，请重新输入"),
    REGISTERERROR_10204("10204", "营业执照号重复"),
    REGISTERERROR_10205("10205", "公司名称重复"),
    REGISTERERROR_10206("10206", "身份证号重复"),
    REGISTERERROR_10207("10207", "用户注册失败"),
    
    /* 对帐单相关异常 */
    CHECKING_NOT_FOUND      ("10301", "对账单不存在"),
    CHECKING_REFUSE_ERROE   ("10302", "分销商对账单拒绝失败"),
    CHECKING_CONFIRM_ERROE  ("10303", "分销商对帐单确认失败"),
    CHECKING_STATUS_ILLEGAL ("10304", "对帐单状态非法"),
    
    /* 财务账户异常 */
    ACCOUNT_BALANCE_NOT_FOUND   ("10401", "未找到该用户的账户余额项"),
    ACCOUNT_BALANCE_ERROE       ("10402", "账户余额查询失败"),
    ACCOUNT_SALES_ERROE         ("10403", "月销售额查询接口出错");
    
    private String code;
    private String message;
    
    private Exceptions(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }
    public String getMessage() {
        return message;
    }

}
