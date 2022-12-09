package com.yuchen.etl.core.java.resolve;

import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;

/**
 * @Author: xiaozhennan
 * @Date: 2022/12/9 16:43
 * @Package: com.yuchen.etl.core.java.resolve
 * @ClassName: ErrorInfo
 * @Description: 错误信息包装类
 **/
public class ErrorInfo implements Serializable {
    private static final String MSG_KEY = "msg";
    private Object obj;

    private String info;
    private ErrorInfoType type;
    private long errorTime;

    public ErrorInfo(Object obj, ErrorInfoType type, long timeMillis) {
        this.obj = obj;
        this.info = JSONObject.toJSONString(obj);
        this.type = type;
        this.errorTime = timeMillis;
    }

    public String getInfo() {
        return this.info;
    }

    public Object getObj() {
        return obj;
    }

    public ErrorInfoType getType() {
        return type;
    }

    public long getErrorTime() {
        return errorTime;
    }
}
