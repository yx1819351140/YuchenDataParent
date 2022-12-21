package com.yuchen.etl.core.java.flink;

import com.yuchen.etl.core.java.config.FlinkConfig;
import org.apache.flink.api.common.RuntimeExecutionMode;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: xiaozhennan
 * @Date: 2022/12/15 10:04
 * @Package: com.yuchen.etl.core.java.flink
 * @ClassName: JavaFlinkInitializer
 * @Description:
 **/
public class JavaFlinkInitializer implements FlinkInitializer {
    private FlinkConfig config;

    private StreamExecutionEnvironment environment;
    private Configuration flinkConf;

    @Override
    public void init(FlinkConfig flinkConfig) {
        this.config = flinkConfig;
        this.flinkConf = new Configuration();
        Map<String, Object> advancedConfig = config.getOption(FlinkConfig.ADVANCED_CONFIG);
        Boolean localMode = config.getOption(FlinkConfig.LOCAL_MODE);
        Boolean localModeWeb = config.getOption(FlinkConfig.LOCAL_ENABLE_WEB);
        Integer parallelism = config.getOption(FlinkConfig.PARALLELISM);
        Boolean enableCheckpoint = config.getOption(FlinkConfig.ENABLE_CHECKPOINT);
        Integer checkpointInterval = config.getOption(FlinkConfig.CHECKPOINT_INTERVAL);
        CheckpointingMode checkpointingMode = config.getOption(FlinkConfig.CHECKPOINT_MODE);
        RuntimeExecutionMode runtimeExecutionMode = config.getOption(FlinkConfig.RUNTIME_MODE);
        Long checkpointTimeout = config.getOption(FlinkConfig.CHECKPOINT_TIMEOUT);
        //使用高级配置覆盖默认配置
        Map<String, String> allAdv = new HashMap<>();
        advancedConfig.keySet().stream().forEach(k -> {
            allAdv.put(k, String.valueOf(advancedConfig.get(k)));
        });
        this.flinkConf.addAll(Configuration.fromMap(allAdv));
        if (localMode) {
            if (localModeWeb) {
                environment = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(flinkConf);
            } else {
                environment = StreamExecutionEnvironment.createLocalEnvironment(flinkConf);
            }

            Logger root = Logger.getRootLogger();
            root.setLevel(Level.INFO);
            root.addAppender(new ConsoleAppender(new PatternLayout("%d{yyyy-MM-dd HH:mm:ss} %-5p %-60c %x - %m%n")));
        }
        //设置并行度
        environment.setParallelism(parallelism);
        //设置运行模式
        environment.setRuntimeMode(runtimeExecutionMode);
        //开启checkpoint
        if (enableCheckpoint) {
            environment.enableCheckpointing(checkpointInterval, checkpointingMode);
            CheckpointConfig checkpointConfig = environment.getCheckpointConfig();
            checkpointConfig.setCheckpointTimeout(checkpointTimeout);
        }
    }

    @Override
    public StreamExecutionEnvironment streamEnvironment() {
        return environment;
    }


}
