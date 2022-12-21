package com.yuchen.etl.core.java.spark;

import com.yuchen.common.enums.LangType;
import com.yuchen.etl.core.java.config.SparkJobConfig;
import com.yuchen.etl.core.scala.spark.ScalaSparkInitializer;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.streaming.StreamingContext;
import org.apache.spark.streaming.api.java.JavaStreamingContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: xiaozhennan
 * @Date: 2022/11/22 17:50
 * @Package: com.yuchen.etl.core.java.spark
 * @ClassName: SparkSupport
 * @Description: Spark程序开发支持类
 **/
public class SparkSupport {

    private static final Map<LangType, SparkInitializer> initializerMap = new ConcurrentHashMap<>();

    public static SparkSession createSparkSession(SparkJobConfig jobConfig, LangType langType) {
        jobConfig.printInfo();
        try {
            switch (langType) {
                case JAVA:
                    return initSparkForJava(jobConfig).javaSparkSession();
                case SCALA:
                    return initSparkForScala(jobConfig).scalaSparkSession();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public static JavaStreamingContext createStreamingForJava(SparkJobConfig sparkJobConfig) {
        SparkInitializer sparkInitializer = initSparkForJava(sparkJobConfig);
        JavaStreamingContext javaStreamingContext = null;
        try {
            javaStreamingContext = sparkInitializer.javaSparkStreaming();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return javaStreamingContext;
    }

    public static StreamingContext createStreamingForScala(SparkJobConfig sparkJobConfig) {
        SparkInitializer sparkInitializer = initSparkForScala(sparkJobConfig);
        StreamingContext streamingContext = null;
        try {
            streamingContext = sparkInitializer.scalaSparkStreaming();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return streamingContext;
    }


    private static synchronized SparkInitializer initSparkForScala(SparkJobConfig jobConfig) {
        SparkInitializer sparkInitializer = initializerMap.get(LangType.SCALA);
        if (sparkInitializer != null) return sparkInitializer;
        ScalaSparkInitializer scalaSparkInitializer = new ScalaSparkInitializer();
        scalaSparkInitializer.init(jobConfig);
        initializerMap.put(LangType.SCALA, scalaSparkInitializer);
        return scalaSparkInitializer;
    }


    private static synchronized SparkInitializer initSparkForJava(SparkJobConfig jobConfig) {
        SparkInitializer sparkInitializer = initializerMap.get(LangType.JAVA);
        if (sparkInitializer != null) return sparkInitializer;
        JavaSparkInitializer javaSparkInitializer = new JavaSparkInitializer();
        javaSparkInitializer.init(jobConfig);
        initializerMap.put(LangType.JAVA, javaSparkInitializer);
        return javaSparkInitializer;
    }

}
