package com.yuchen.data.service.utils.exception;

/**
 * 统一认证异常类
 *
 * @author fan guoping
 * @version 1.0
 * @createDate 2019/6/12 14:47
 */
public class AuthException extends RuntimeException {

    private static final long serialVersionUID = -4488377121094064672L;

    /**
     * 错误信息
     */
    private String errorMsg;

    /**
     * 服务器状态码
     */
    private Integer code;

    public AuthException(Integer code) {
        this.code = code;
    }

    public AuthException(Integer code, String errorMsg) {
        super(errorMsg);
        this.errorMsg = errorMsg;
        this.code = code;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public Integer getCode() {
        return code;
    }

}
