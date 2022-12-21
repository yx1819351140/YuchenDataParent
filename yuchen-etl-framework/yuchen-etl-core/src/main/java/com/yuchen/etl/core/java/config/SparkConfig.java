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

    public static final Options<String> SPARK_MASTER = Options.<String>builder()
            .key("master")
            .defaultVar("local")
            .required(true)
            .description("Spark Master URL. 默认值(Local). 可选: \n" +
                    "[local 本地单线程\n" +
                    "local[K] 本地多线程（指定K个内核）\n" +
                    "local[*] 本地多线程（指定所有可用内核）\n" +
                    "spark://HOST:PORT 连接到指定的  Spark standalone cluster master，需要指定端口。\n" +
                    "mesos://HOST:PORT 连接到指定的  Mesos 集群，需要指定端口。\n" +
                    "yarn-client客户端模式 连接到  YARN 集群。需要配置 HADOOP_CONF_DIR。\n" +
                    "yarn-cluster集群模式 连接到 YARN 集群。需要配置 HADOOP_CONF_DIR。]")
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
    public String getSparkMaster() {
        return this.getOption(SPARK_MASTER);
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
