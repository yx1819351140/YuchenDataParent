package com.yuchen.test.etl.core.spark;

import com.yuchen.etl.core.java.resolve.ErrorInfoCollector;
import com.yuchen.etl.core.java.resolve.ErrorInfoCollectorConfig;
import com.yuchen.etl.core.java.resolve.ErrorInfoCollectorFactory;
import com.yuchen.etl.core.java.spark.SparkBroadcastWarpper;

/**
 * @Author: xiaozhennan
 * @Date: 2023/1/3 13:50
 * @Package: com.yuchen.test.etl.core.spark
 * @ClassName: TestBroadcastWarpper
 * @Description:
 **/
public class TestBroadcastWarpper {
    public static void main(String[] args) {
        ErrorInfoCollectorConfig config = new ErrorInfoCollectorConfig(null);
        SparkBroadcastWarpper<ErrorInfoCollector> wrapper = SparkBroadcastWarpper.wrapper(() -> {
            ErrorInfoCollector collector = ErrorInfoCollectorFactory.createCollector(config);
            //注册关闭钩子
            Runtime.getRuntime().addShutdownHook(new Thread(() -> collector.close()));
            return collector;
        });
    }
}
