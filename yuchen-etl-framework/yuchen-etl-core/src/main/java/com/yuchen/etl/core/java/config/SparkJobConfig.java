package com.yuchen.etl.core.java.config;

/**
 * @Author: xiaozhennan
 * @Date: 2022/11/22 14:16
 * @Package: com.yuchen.etl.core.java.config
 * @ClassName: SparkJobConfig
 * @Description: Spark程序运行的配置类
 **/
public class SparkJobConfig extends JobConfig {
    private SparkConfig sparkConfig;

    public SparkJobConfig() {
    }

    public SparkConfig getSparkConfig() {
        return this.sparkConfig;
    }


    @Override
    protected void print() {
        if (sparkConfig != null) {
            for (String key : sparkConfig.keySet()) {
                System.out.println(String.format("%s: %s", key, sparkConfig.get(key)));
            }
        }
    }
}
