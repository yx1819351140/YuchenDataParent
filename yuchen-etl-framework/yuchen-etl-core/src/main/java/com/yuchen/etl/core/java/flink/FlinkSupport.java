package com.yuchen.etl.core.java.flink;

import com.yuchen.common.enums.LangType;
import com.yuchen.etl.core.java.config.FlinkJobConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: xiaozhennan
 * @Date: 2022/12/15 9:38
 * @Package: com.yuchen.etl.core.java.flink
 * @ClassName: FlinkSupport
 * @Description:
 **/
public class FlinkSupport {

    private static final Map<LangType, FlinkInitializer> initializerMap = new ConcurrentHashMap<>();

    public static StreamExecutionEnvironment createEnvironment(FlinkJobConfig flinkJobConfig, LangType langType) {
        flinkJobConfig.printInfo();
        try {
            switch (langType) {
                case JAVA:
                    return initFlinkForJava(flinkJobConfig).streamEnvironment();
                case SCALA:
                    throw new IllegalAccessException("Operation not yet supported!");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    private static FlinkInitializer initFlinkForJava(FlinkJobConfig flinkJobConfig) {
        //如果已经存在,就返回
        if (initializerMap.containsKey(LangType.JAVA)) return initializerMap.get(LangType.JAVA);
        //不存在,开始初始化
        JavaFlinkInitializer javaFlinkInitializer = new JavaFlinkInitializer();
        javaFlinkInitializer.init(flinkJobConfig.getFlinkConfig());
        initializerMap.put(LangType.JAVA, javaFlinkInitializer);
        return initializerMap.get(LangType.JAVA);
    }
}
