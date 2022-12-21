package com.yuchen.etl.core.java.flink;

import com.yuchen.etl.core.java.config.FlinkConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * @Author: xiaozhennan
 * @Date: 2022/12/15 10:03
 * @Package: com.yuchen.etl.core.java.flink
 * @ClassName: FlinkInitializer
 * @Description:
 **/
public interface FlinkInitializer {
    void init(FlinkConfig flinkConfig);

    StreamExecutionEnvironment streamEnvironment();
}
