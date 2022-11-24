package com.yuchen.data.service.utils.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 标准返回体
 * @author fan guoping
 * @version 1.0
 * @createDate 2019/6/12 14:47
 */
@ApiModel(value = "返回结构体VO")
public class ResponseResult<T> {

    /**
     * 状态码
     */
    @ApiModelProperty(value = "状态码： 0-成功；其他为失败")
    private int retcode;
    /**
     * 信息
     */
    @ApiModelProperty(value = "提示消息")
    private String msg ="success";
    /**
     * 数据
     */
    private T data;    

    public int getRetcode() {
		return retcode;
	}

	public void setRetcode(int retcode) {
		this.retcode = retcode;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public ResponseResult() {
        this.retcode = RestResultEnum.SUCCESS.getKey();
    }

    public ResponseResult(int code, String message) {
        this.retcode = code;
        this.msg = message;
    }

    public ResponseResult(RestResultEnum restResultInfoEnum) {
        this.retcode = restResultInfoEnum.getKey();
        this.msg = restResultInfoEnum.getMessage();
    }

    public ResponseResult(RestResultEnum restResultInfoEnum, T data) {
        this.retcode = restResultInfoEnum.getKey();
        this.msg = restResultInfoEnum.getMessage();
        this.data = data;
    }

    public ResponseResult(T data) {
        this.retcode = RestResultEnum.SUCCESS.getKey();
        this.data = data;
    }

    public ResponseResult(int retcode, String msg, T data) {
        this.retcode = retcode;
        this.msg = msg;
        this.data = data;
    }

    public ResponseResult(int retcode,  T data) {
        this.retcode = retcode;
        this.data = data;
    }
}
