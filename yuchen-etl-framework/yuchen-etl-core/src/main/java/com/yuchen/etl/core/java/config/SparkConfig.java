package com.yuchen.etl.core.java.config;

import com.yuchen.common.pub.AbstractConfig;
import com.yuchen.common.pub.Options;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: xiaozhennan
 * @Date: 2022/11/22 16:50
 * @Package: com.yuchen.etl.core.java.config
 * @ClassName: SparkConfig
 * @Description: Spark任务配置
 **/
public class SparkConfig extends AbstractConfig {

    public static final Options<Long> STREAM_DURATION = Options.<Long>builder()
            .key("streamDuration")
            .defaultVar(10L)
            .required(false)
            .description("stream程序执行间隔")
            .build();
    public static final Options<Boolean> ENABLE_HIVE_SUPPORT = Options.<Boolean>builder()
            .key("enableHiveSupport")
            .defaultVar(false)
            .required(false)
            .description("是否开启Hive支持")
            .build();

    public static final Options<Boolean> ENABLE_DEBUG = Options.<Boolean>builder()
            .key("enableDebug")
            .defaultVar(false)
            .required(false)
            .description("是否开启Debug模式")
            .build();
    public static final Options<Boolean> IS_LOCAL = Options.<Boolean>builder()
            .key("isLocal")
            .defaultVar(false)
            .required(false)
            .description("是否是本地模式")
            .build();

    public static final Options<Map<String,Object>> ADVANCED_CONFIG = Options.<Map<String,Object>>builder()
            .key("advancedConfig")
            .defaultVar(new HashMap<>())
            .required(false)
            .description("高级配置")
            .build();

    public SparkConfig() {
    }

    public SparkConfig(Map m) {
        super(m);
    }

    public boolean isEnableHiveSupport() {
        return this.getOption(ENABLE_HIVE_SUPPORT);
    }

    public boolean isLocal() {
        return this.getOption(IS_LOCAL);
    }

    public Long getStreamDuration(){
        return this.getOption(STREAM_DURATION);
    }
    public boolean isEnableDebug(){
        return this.getOption(ENABLE_DEBUG);
    }

    public Map<String, Object> getAdvancedConfig(){
        return this.getOption(ADVANCED_CONFIG);
    }
}
