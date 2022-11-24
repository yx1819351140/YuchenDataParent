package com.yuchen.data.service.utils.exception;

/**
 * 参数异常类
 *
 * @author fan guoping
 * @version 1.0
 * @createDate 2019/6/12 14:47
 */
public class ArgumentException extends BaseException {

    /**
	 * 
	 */
	private static final long serialVersionUID = -1267024469580775945L;

	public ArgumentException() {
    }

    public ArgumentException(String message) {
        super(message,new IllegalArgumentException(message));
    }

    public ArgumentException(Throwable cause) {
        super(cause);
    }
}
