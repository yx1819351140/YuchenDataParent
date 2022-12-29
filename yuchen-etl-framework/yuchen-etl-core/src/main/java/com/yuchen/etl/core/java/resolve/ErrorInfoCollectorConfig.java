package com.yuchen.etl.core.java.resolve;

import com.yuchen.common.pub.AbstractConfig;

import java.util.Map;

/**
 * @Author: xiaozhennan
 * @Date: 2022/12/9 16:39
 * @Package: com.yuchen.etl.core.java.resolve
 * @ClassName: ErrorInfoCollectorConfig
 * @Description: 错误收集配置
 **/
public class ErrorInfoCollectorConfig extends AbstractConfig {
    private static final String KAFKA_ERROR_INFO_HANDLER = "com.yuchen.etl.core.java.resolve.KafkaErrorInfoHandler";
    private static final long MAX_SAMPLING_RECORD = 200;
    private static final long SAMPLING_INTERVAL = 60;

    public ErrorInfoCollectorConfig(Map m) {
        super(m);
    }

    public long getMaxSamplingRecord() {
        return MAX_SAMPLING_RECORD;
    }

    public long getSamplingInterval() {
        return SAMPLING_INTERVAL;
    }

    public String getCollectorHandler() {
        return KAFKA_ERROR_INFO_HANDLER;
    }
}
