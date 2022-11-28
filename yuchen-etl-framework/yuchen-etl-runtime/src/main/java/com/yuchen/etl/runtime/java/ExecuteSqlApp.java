package com.yuchen.etl.runtime.java;

import com.yuchen.common.enums.LangType;
import com.yuchen.etl.core.java.config.ConfigFactory;
import com.yuchen.etl.core.java.config.SparkJobConfig;
import com.yuchen.etl.core.java.spark.SparkSupport;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

/**
 * @Author: xiaozhennan
 * @Date: 2022/11/25 9:48
 * @Package: com.yuchen.etl.runtime.java
 * @ClassName: ExecuteSqlApp
 * @Description: 执行SQL
 **/
public class ExecuteSqlApp {
    public static void main(String[] args) {
        SparkJobConfig sparkJobConfig = ConfigFactory.load(args[0], SparkJobConfig.class, true);
        SparkSession sparkSession = SparkSupport.createSparkSession(sparkJobConfig, LangType.JAVA);
        Dataset<Row> dataset = sparkSession.sql("show databases");
        dataset.show();
    }
}
