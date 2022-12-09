package com.yuchen.test.etl.core.resolve;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.yuchen.etl.core.java.config.ConfigFactory;
import com.yuchen.etl.core.java.config.SparkJobConfig;
import com.yuchen.etl.core.java.resolve.ErrorInfoCollector;
import com.yuchen.etl.core.java.resolve.ErrorInfoCollectorConfig;
import com.yuchen.etl.core.java.resolve.ErrorInfoCollectorFactory;
import com.yuchen.etl.core.java.resolve.ErrorInfoType;
import org.junit.Test;

/**
 * @Author: xiaozhennan
 * @Date: 2022/12/9 17:20
 * @Package: com.yuchen.test.etl.core.resolve
 * @ClassName: TestErrorCollector
 * @Description: 测试错误收集器
 **/
public class TestErrorCollector {


    @Test
    public void testCollector() throws JsonProcessingException {
        SparkJobConfig sparkJobConfig = ConfigFactory.loadFromJson("", SparkJobConfig.class);

        Object errorInfoCollector = sparkJobConfig.get("errorInfoCollector");

        ErrorInfoCollectorConfig errorInfoCollectorConfig = new ErrorInfoCollectorConfig();
        ErrorInfoCollector collector = ErrorInfoCollectorFactory.createCollector(null);
        collector.collect(ErrorInfoType.ILLEGAL_DATA_ERROR, "测试");
    }

}
