package com.yuchen.test.etl.core.support;

import com.yuchen.common.enums.LangType;
import com.yuchen.etl.core.java.config.ConfigFactory;
import com.yuchen.etl.core.java.config.SparkJobConfig;
import com.yuchen.etl.core.java.spark.SparkSupport;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;

/**
 * @Author: xiaozhennan
 * @Date: 2022/11/23 9:47
 * @Package: com.yuchen.test.etl.core
 * @ClassName: TestSparkSessionCreate
 * @Description:
 **/
public class TestSparkSessionCreate {

    @Test
    public void test() throws IOException {
        SparkJobConfig sparkJobConfig = ConfigFactory.load("src/test/resources/job.json", SparkJobConfig.class);
        SparkSession sparkSession = SparkSupport.createSparkSession(sparkJobConfig, LangType.JAVA);
        JavaStreamingContext streamingForJava = SparkSupport.createStreamingForJava(sparkJobConfig);

        JavaSparkContext javaSparkContext = streamingForJava.sparkContext();
        JavaRDD<String> rdd = javaSparkContext.textFile("src/test/resources/job.json");
        rdd.map(v -> {
            System.out.println(v);
            return v;
        });

        streamingForJava.start();

    }
}
