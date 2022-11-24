package com.yuchen.data.service.base;

import com.yuchen.data.service.utils.result.RestResultEnum;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * 描述
 *
 * @author lizhiwei
 * @version 1.0
 * @className ResponseResultVO
 * @date 2020/12/5 11:45
 */
public class ResponseResultVO<T> {
    @ApiModelProperty("状态码")
    private Integer retcode;
    @ApiModelProperty("信息")
    private String msg;
    @ApiModelProperty("数据")
    private T data;

    public Integer getRetcode() {
        return this.retcode;
    }

    public void setRetcode(Integer retcode) {
        this.retcode = retcode;
    }

    public String getMsg() {
        return this.msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return this.data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public ResponseResultVO() {
        this.retcode = RestResultEnum.SUCCESS.getKey();
    }

    public ResponseResultVO(int code, String message) {
        this.retcode = code;
        this.msg = message;
    }

    public ResponseResultVO(RestResultEnum restResultInfoEnum) {
        this.retcode = restResultInfoEnum.getKey();
        this.msg = restResultInfoEnum.getMessage();
    }

    public ResponseResultVO(RestResultEnum restResultInfoEnum, T data) {
        this.retcode = restResultInfoEnum.getKey();
        this.msg = restResultInfoEnum.getMessage();
        this.data = data;
    }

    public ResponseResultVO(T data) {
        this.retcode = RestResultEnum.SUCCESS.getKey();
        this.data = data;
    }

    public ResponseResultVO(int retcode, String msg, T data) {
        this.retcode = retcode;
        this.msg = msg;
        this.data = data;
    }

    public String toString() {
        return (new ToStringBuilder(this)).append("retcode", this.retcode).append("msg", this.msg).append("data", this.data).toString();
    }
}
