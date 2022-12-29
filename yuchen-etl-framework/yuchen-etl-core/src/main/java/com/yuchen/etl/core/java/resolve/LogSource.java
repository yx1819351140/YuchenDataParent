package com.yuchen.etl.core.java.resolve;

public enum LogSource {
    JAVA(1,"后端"), BIGDATA(2,"大数据端"), ALGORITHM(3,"算法端");
    private final Integer code;
    private final String type;

    LogSource(Integer code, String type) {
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
