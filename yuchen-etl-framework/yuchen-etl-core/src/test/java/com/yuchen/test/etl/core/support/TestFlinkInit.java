package com.yuchen.test.etl.core.support;

import com.yuchen.common.enums.LangType;
import com.yuchen.etl.core.java.config.ConfigFactory;
import com.yuchen.etl.core.java.config.FlinkConfig;
import com.yuchen.etl.core.java.config.FlinkJobConfig;
import com.yuchen.etl.core.java.flink.FlinkSupport;
import org.apache.flink.api.common.RuntimeExecutionMode;
import org.apache.flink.configuration.ReadableConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.junit.Test;

import java.io.IOException;

/**
 * @Author: xiaozhennan
 * @Date: 2022/12/15 10:53
 * @Package: com.yuchen.test.etl.core.support
 * @ClassName: TestFlinkInit
 * @Description:
 **/
public class TestFlinkInit {

    @Test
    public void testNewOptions() throws IOException {
        FlinkJobConfig flinkJobConfig = ConfigFactory.load("D:\\project\\YuchenDataParent\\yuchen-etl-framework\\yuchen-etl-core\\src\\test\\resources\\flink.json", FlinkJobConfig.class);
        FlinkConfig flinkConfig = flinkJobConfig.getFlinkConfig();
        flinkConfig.put("localEnableWeb", true);
        Boolean localEnableWeb = flinkConfig.getOption(FlinkConfig.LOCAL_ENABLE_WEB);
        Boolean localMode = flinkConfig.getOption(FlinkConfig.LOCAL_MODE);
        System.out.println(localEnableWeb);
        System.out.println(localMode);
        flinkConfig.put("runtimeMode", "streaming");
        RuntimeExecutionMode runtimeExecutionMode = flinkConfig.getOption(FlinkConfig.RUNTIME_MODE);
        System.out.println(runtimeExecutionMode);
        StreamExecutionEnvironment environment = FlinkSupport.createEnvironment(flinkJobConfig, LangType.JAVA);
        System.out.println(environment);
    }
}
