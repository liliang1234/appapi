package com.pzj.modules.appapi.exception;

import org.springframework.util.StringUtils;

import com.pzj.modules.appapi.entity.CodeHandle;

/**
 * Created by Administrator on 2016-6-2.
 */
public class AppapiParametersException extends RuntimeException {
    /**  */
    private static final long serialVersionUID = 1L;
    private String            code;

    public AppapiParametersException(String message) {
        super(message);
        this.code = CodeHandle.SERVERERROR.getCode();
    }

    public AppapiParametersException(String code, String message) {
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static AppapiParametersException createByCodeHandle(CodeHandle codeHandle) {
        return new AppapiParametersException(codeHandle.getCode(), codeHandle.getMessage());
    }

    public static AppapiParametersException createByCodeHandle(CodeHandle codeHandle, String msg) {
        if (StringUtils.isEmpty(msg)) {
            return new AppapiParametersException(codeHandle.getCode(), codeHandle.getMessage());
        }
        return new AppapiParametersException(codeHandle.getCode(), msg);
    }
}
