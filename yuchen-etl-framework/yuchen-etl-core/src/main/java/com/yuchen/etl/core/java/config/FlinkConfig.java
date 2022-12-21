package com.yuchen.etl.core.java.config;

import com.yuchen.common.pub.AbstractConfig;
import com.yuchen.common.pub.Options;
import org.apache.flink.api.common.RuntimeExecutionMode;
import org.apache.flink.streaming.api.CheckpointingMode;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: xiaozhennan
 * @Date: 2022/12/15 9:59
 * @Package: com.yuchen.etl.core.java.config
 * @ClassName: FlinkConfig
 * @Description:
 **/
public class  FlinkConfig extends AbstractConfig {

    public static final Options<Boolean> LOCAL_MODE = Options.<Boolean>builder()
            .key("localMode")
            .defaultVar(false)
            .required(false)
            .description("是否开启本地模式")
            .build();
    public static final Options<Boolean> LOCAL_ENABLE_WEB = Options.<Boolean>builder()
            .key("localEnableWeb")
            .defaultVar(false)
            .required(false)
            .description("本地模式是否开启web")
            .build();

    public static final Options<Integer> PARALLELISM = Options.<Integer>builder()
            .key("parallelism")
            .defaultVar(1)
            .required(false)
            .description("作业的并行度")
            .build();

    public static final Options<RuntimeExecutionMode> RUNTIME_MODE = Options.<RuntimeExecutionMode>builder()
            .key("runtimeMode")
            .defaultVar(RuntimeExecutionMode.STREAMING)
            .required(false)
            .description("执行模式（流/批）支持: STREAMING|BATCH|AUTOMATIC")
            .build();


    public static final Options<Boolean> ENABLE_CHECKPOINT = Options.<Boolean>builder()
            .key("enableCheckpoint")
            .defaultVar(false)
            .required(false)
            .description("是否开启checkpoint")
            .build();
    public static final Options<Integer> CHECKPOINT_INTERVAL = Options.<Integer>builder()
            .key("checkpointInterval")
            .defaultVar(10000)
            .required(false)
            .description("checkpoint执行的间隔")
            .build();


    public static final Options<CheckpointingMode> CHECKPOINT_MODE = Options.<CheckpointingMode>builder()
            .key("checkpointMode")
            .defaultVar(CheckpointingMode.EXACTLY_ONCE)
            .required(false)
            .description("checkpoint模式,可选(EXACTLY_ONCE|AT_LEAST_ONCE)")
            .build();

    public static final Options<Long> CHECKPOINT_TIMEOUT = Options.<Long>builder()
            .key("checkpointTimeout")
            .defaultVar(60000L)
            .required(false)
            .description("checkpoint超时时间, 默认1分钟")
            .build();

    public static final Options<Map<String, Object>> ADVANCED_CONFIG = Options.<Map<String, Object>>builder()
            .key("advancedConfig")
            .defaultVar(new HashMap<>())
            .required(false)
            .description("其它进阶配置, 具体请查看官网相关配置,会覆盖flink默认配置")
            .build();
}
