package com.yuchen.data.service.utils.exception;

/**
 * 业务异常基础类
 *
 * @author fan guoping
 * @version 1.0
 * @createDate 2019/6/12 14:47
 */
public class BaseException extends RuntimeException {

    private static final long serialVersionUID = -4488377121094064672L;

    public BaseException() {
    }

    public BaseException(String message) {
        super(message);
    }

    public BaseException(String message, Throwable cause) {
        super(message, cause);
    }

    public BaseException(Throwable cause) {
        super(cause);
    }

}
