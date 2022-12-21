package com.yuchen.etl.core.java.config;

import lombok.Data;
import lombok.Getter;

/**
 * @Author: xiaozhennan
 * @Date: 2022/12/15 9:27
 * @Package: com.yuchen.etl.core.java.config
 * @ClassName: FlinkJobConfig
 * @Description: Flink任务配置对象
 **/
public class FlinkJobConfig extends JobConfig {
    private FlinkConfig flinkConfig;

    public FlinkConfig getFlinkConfig() {
        return flinkConfig;
    }

    @Override
    protected void print() {
        if (flinkConfig != null) {
            for (String key : flinkConfig.keySet()) {
                System.out.println(String.format("%s: %s", key, flinkConfig.get(key)));
            }
        }
    }
}
