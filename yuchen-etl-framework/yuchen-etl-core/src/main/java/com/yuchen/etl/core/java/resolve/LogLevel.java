package com.yuchen.etl.core.java.resolve;

public enum LogLevel {
    COMMON(1,"一般错误"), FATAL(2,"严重错误");

    private final Integer code;
    private final String type;

    LogLevel(Integer code, String type) {
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
