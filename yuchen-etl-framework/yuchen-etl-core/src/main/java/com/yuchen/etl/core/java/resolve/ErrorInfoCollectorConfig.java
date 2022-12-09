package com.yuchen.etl.core.java.resolve;

import com.yuchen.common.pub.AbstractConfig;

/**
 * @Author: xiaozhennan
 * @Date: 2022/12/9 16:39
 * @Package: com.yuchen.etl.core.java.resolve
 * @ClassName: ErrorInfoCollectorConfig
 * @Description: 错误收集配置
 **/
public class ErrorInfoCollectorConfig extends AbstractConfig {
    public long getMaxSamplingRecord() {
        return 0;
    }

    public long getSamplingInterval() {
        return 0;
    }

    public String getCollectorHandler() {
        return null;
    }
}
