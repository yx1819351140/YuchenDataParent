package com.yuchen.data.api.enums;

import java.io.Serializable;

/**
 * @Author: xiaozhennan
 * @Date: 2022/12/12 9:57
 * @Package: com.yuchen.data.api.enums
 * @ClassName: ResponseStatus
 * @Description: 返回状态枚举,
 * 1.正向为成功 如 1000 1001 1002 0
 * 2.反向为失败 如 9999 2999 1999 999
 * 3.状态范围
 * 基础: 0-999
 * ES: 1000-1999
 * Hbase: 2000-2999
 * Mongo: 3000-3999
 * MySql: 4000-4999
 **/
public enum ResponseStatus implements Serializable {
    //基本状态 0 - 999
    SUCCESS(0, "成功", true),
    OPERATION_SUCCESS(1, "操作成功", true),
    INSERT_SUCCESS(2, "插入成功", true),
    UPDATE_SUCCESS(3, "更新成功", true),
    DELETE_SUCCESS(4, "删除成功", true),

    FAILURE(999, "失败", false),
    UNKNOWN_EXCEPTION(997, "未知异常", false),
    UNSUPPORTED_OPERATION(996, "不支持的操作", false),
    ILLEGAL_PARAMETER(995, "参数非法", false);


    //ES状态 1000 - 1999


    //Hbase状态 2000 - 2999


    //mongo状态 3000 - 3999

    private int code;
    private String message;
    private boolean successful;

    ResponseStatus(int code, String message, boolean successful) {
        this.code = code;
        this.message = message;
        this.successful = successful;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public boolean isSuccessful() {
        return successful;
    }
}
