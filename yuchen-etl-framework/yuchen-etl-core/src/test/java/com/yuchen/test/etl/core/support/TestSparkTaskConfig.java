package com.yuchen.test.etl.core.support;

import com.yuchen.etl.core.java.config.ConfigFactory;
import com.yuchen.etl.core.java.config.TaskConfig;
import com.yuchen.etl.core.java.config.SparkConfig;
import com.yuchen.etl.core.java.config.SparkJobConfig;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.io.IOException;

/**
 * @Author: xiaozhennan
 * @Date: 2022/11/22 14:40
 * @Package: com.yuchen.test.etl.core
 * @ClassName: TestSparkTaskConfig
 * @Description:
 **/
public class TestSparkTaskConfig {

    @Test
    public void testConfig() throws IOException {
        SparkJobConfig load = ConfigFactory.load("src/test/resources/job.json", SparkJobConfig.class);
        System.out.println(load);
        SparkConfig sparkConfig = load.getSparkConfig();
        TaskConfig taskConfig = load.getTaskConfig();
        String spark123 = sparkConfig.getStringVal("123123");
        String job123 = taskConfig.getStringVal("123123j");
        assert StringUtils.isNotBlank(spark123);
        assert StringUtils.isNotBlank(job123);

        for (String key : sparkConfig.keySet()) {
            System.out.println(String.format("key: %s, value: %s", key, String.valueOf(sparkConfig.getVal(key))));
        }
    }

}
