package com.yuchen.data.service.utils.exception;

/**
 * @Description :FileException
 * @author: ge haiyun
 * @Date: 2022/3/2
 */
public class FileException extends Exception{

    /**
     * 错误码
     */
    private Integer code;

    /**
     * 信息提示
     */
    private String tips;

    public FileException() {
    }

    public FileException(String msg) {
        super(msg);
    }

    public FileException(Integer code, String tips, String message) {
        super(message);
        this.code = code;
        this.tips=tips;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getTips() {
        return tips;
    }

    public void setTips(String tips) {
        this.tips = tips;
    }
}
