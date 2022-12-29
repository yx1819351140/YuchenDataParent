package com.yuchen.test.etl.core.resolve;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.yuchen.etl.core.java.resolve.*;
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

        ErrorInfoCollectorConfig errorInfoCollectorConfig = new ErrorInfoCollectorConfig(null);
        errorInfoCollectorConfig.setStringVal("kafkaTopic","test_log_collector");
        errorInfoCollectorConfig.setStringVal("bootstrap.servers","datanode01:19092,datanode02:19092,datanode03:19092");
        errorInfoCollectorConfig.setStringVal("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        errorInfoCollectorConfig.setStringVal("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        ErrorInfoCollector collector = ErrorInfoCollectorFactory.createCollector(errorInfoCollectorConfig);

        int i = -1;
        while (++i < 10) {
            Random random = new Random(1243122);
            int is = random.nextInt(3);
            TimeUnit.SECONDS.sleep(is);
            collector.collect(LogType.STATUS,LogLevel.COMMON,LogSource.BIGDATA,"test_12_28_05_" + i,"测试收集器_12_28_05_" + i,null);
        }
//        while (true) {
//            System.out.println("while true");
//        }
//        int i = -1;
//        while (i < 9999999) {
//            Random random = new Random(1243122);
//            int is = random.nextInt(3);
//            TimeUnit.SECONDS.sleep(is);
//            collector.collect(ErrorInfoType.ILLEGAL_DATA_ERROR, String.format("测试错误: %s", i));
//            i++;
//        }
    }

}
