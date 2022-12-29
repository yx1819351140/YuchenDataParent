package com.yuchen.etl.core.java.resolve;

public enum LogType {
    WARNING(1,"告警信息"), STATUS(2,"状态信息");
    private final Integer code;
    private final String type;

    LogType(Integer code, String type) {
        this.code = code;
        this.type = type;
    }

    public Integer getCode() {
        return code;
    }

    public String getType() {
        return type;
    }
}
