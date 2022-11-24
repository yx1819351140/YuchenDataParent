package com.yuchen.test.etl.core.support;

import com.yuchen.etl.core.java.config.ConfigFactory;
import com.yuchen.etl.core.java.config.SparkJobConfig;
import com.yuchen.etl.core.java.spark.SparkSupport;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.junit.Test;

import java.io.IOException;

/**
 * @Author: xiaozhennan
 * @Date: 2022/11/23 10:38
 * @Package: com.yuchen.test.etl.core
 * @ClassName: TestSparkRun
 * @Description:
 **/
public class TestSparkRun {


    @Test
    public void testRun() throws IOException {

        String readme = "src/test/resources/example.txt";
        SparkJobConfig sparkJobConfig = ConfigFactory.load("src/test/resources/job.json", SparkJobConfig.class);
        JavaStreamingContext streamingForJava = SparkSupport.createStreamingForJava(sparkJobConfig);
        JavaSparkContext sc = streamingForJava.sparkContext();
        // 从指定的文件中读取数据到RDD
        JavaRDD<String> logData = sc.textFile(readme).cache();
        // 过滤包含h的字符串，然后在获取数量
        long num = logData.filter((Function<String, Boolean>) s -> s.contains("h")).count();
        System.out.println("the count of word a is " + num);
    }
}
