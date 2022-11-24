package com.yuchen.etl.core.java.spark;

import com.yuchen.etl.core.java.config.SparkJobConfig;
import org.apache.hadoop.hive.metastore.api.InvalidOperationException;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.streaming.StreamingContext;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.elasticsearch.spark.streaming.api.java.JavaEsSparkStreaming;

/**
 * @Author: xiaozhennan
 * @Date: 2022/11/22 18:01
 * @Package: com.yuchen.etl.core.java.spark
 * @ClassName: SparkInitializer
 * @Description:
 **/
public interface SparkInitializer {

    void init(SparkJobConfig sparkJobConfig) throws Exception;

    default SparkSession javaSparkSession() throws Exception {
        throw new IllegalAccessException("不支持的操作");
    }

    default JavaStreamingContext javaSparkStreaming() throws Exception {
        throw new IllegalAccessException("不支持的操作");
    }

    default SparkSession scalaSparkSession() throws Exception {
        throw new IllegalAccessException("不支持的操作");
    }

    default StreamingContext scalaSparkStreaming() throws Exception {
        throw new IllegalAccessException("不支持的操作");
    }

    default JavaSparkContext javaSparkContext() throws Exception {
        throw new IllegalAccessException("不支持的操作");
    }

    default SparkContext scalaSparkContext() throws Exception {
        throw new IllegalAccessException("不支持的操作");
    }
}

