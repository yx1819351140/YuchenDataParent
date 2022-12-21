package com.yuchen.test.etl.core.resolve;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.yuchen.etl.core.java.resolve.ErrorInfoCollector;
import com.yuchen.etl.core.java.resolve.ErrorInfoCollectorConfig;
import com.yuchen.etl.core.java.resolve.ErrorInfoCollectorFactory;
import com.yuchen.etl.core.java.resolve.ErrorInfoType;
import org.junit.Test;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @Author: xiaozhennan
 * @Date: 2022/12/9 17:20
 * @Package: com.yuchen.test.etl.core.resolve
 * @ClassName: TestErrorCollector
 * @Description: 测试错误收集器
 **/
public class TestErrorCollector {


    @Test
    public void testCollector() throws JsonProcessingException, InterruptedException {
//        SparkJobConfig sparkJobConfig = ConfigFactory.loadFromJson("", SparkJobConfig.class);
//
//        Object errorInfoCollector = sparkJobConfig.get("errorInfoCollector");

        ErrorInfoCollectorConfig errorInfoCollectorConfig = new ErrorInfoCollectorConfig();
        ErrorInfoCollector collector = ErrorInfoCollectorFactory.createCollector(errorInfoCollectorConfig);


        int i = -1;
        while (i < 9999999) {
            Random random = new Random(1243122);
            int is = random.nextInt(3);
            TimeUnit.SECONDS.sleep(is);
            collector.collect(ErrorInfoType.ILLEGAL_DATA_ERROR, String.format("测试错误: %s", i));
            i++;
        }
    }

}
