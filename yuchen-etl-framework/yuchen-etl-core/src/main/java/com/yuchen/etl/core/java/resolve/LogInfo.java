package com.yuchen.etl.core.java.resolve;

import com.alibaba.fastjson.JSONObject;

public class LogInfo {
    private static final String MSG_KEY = "msg";
    private final LogType type;

    private final LogLevel level;

    private final LogSource source;

    private String model;

    private String content;

    private Throwable error;

    private long logTimestamp;


    public LogInfo(LogType type, LogLevel level, LogSource source, String model, String content, Throwable error, long logTimestamp) {
        this.type = type;
        this.level = level;
        this.source = source;
        this.model = model;
        this.content = content;
        this.error = error;
        this.logTimestamp = logTimestamp;
    }

    public LogType getType() {
        return type;
    }

    public LogLevel getLevel() {
        return level;
    }

    public LogSource getSource() {
        return source;
    }

    public String getModel() {
        return model;
    }

    public String getContent() {
        return content;
    }

    public Throwable getError() {
        return error;
    }

    public long getLogTimestamp() {
        return logTimestamp;
    }
}
