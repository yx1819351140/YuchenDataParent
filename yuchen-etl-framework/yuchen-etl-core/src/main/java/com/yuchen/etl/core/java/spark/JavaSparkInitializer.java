package com.yuchen.etl.core.java.spark;

import com.yuchen.etl.core.java.config.SparkConfig;
import com.yuchen.etl.core.java.config.SparkJobConfig;
import com.yuchen.etl.core.java.constants.SparkConstant;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.internal.config.ConfigEntry;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaStreamingContext;

import java.util.Map;

/**
 * @Author: xiaozhennan
 * @Date: 2022/11/22 17:54
 * @Package: com.yuchen.etl.core.java.spark
 * @ClassName: JavaSparkInitializer
 * @Description: JavaSpark环境初始化
 **/
public class JavaSparkInitializer implements SparkInitializer {

    private SparkSession sparkSession;
    private JavaSparkContext javaSparkContext;
    private JavaStreamingContext javaStreamingContext;
    private SparkConf sparkConf = new SparkConf();

    @Override
    public void init(SparkJobConfig sparkJobConfig) {
        SparkConfig sparkConfig = sparkJobConfig.getSparkConfig();
        Map<String, Object> option = sparkConfig.getOption(SparkConfig.ADVANCED_CONFIG);
        for (String key : option.keySet()) {
            Object val = sparkConfig.getVal(key);
            if (val != null) {
                // 这里只支持字符串类型的参数
                sparkConf.set(key, String.valueOf(val));
            }
        }

        SparkSession.Builder builder = SparkSession.builder().config(sparkConf).appName(sparkJobConfig.getJobName());
        if (sparkConfig.isEnableHiveSupport()) {
            builder.enableHiveSupport();
        }
        if (sparkConfig.isLocal()) {
            builder.master(SparkConstant.LOCAL_MODE);
        }
        // 初始化SparkSession, 这里是个单例,所以如果已经存在session, 拿到的就是同一个
        this.sparkSession = builder.getOrCreate();
        //初始化JavaSparkContext
        this.javaSparkContext = JavaSparkContext.fromSparkContext(this.sparkSession.sparkContext());
        //初始化JavaStreamingContext
        this.javaStreamingContext = new JavaStreamingContext(this.javaSparkContext, Durations.seconds(sparkConfig.getStreamDuration()));
    }

    @Override
    public SparkSession javaSparkSession() throws Exception {
        return sparkSession;
    }

    @Override
    public JavaStreamingContext javaSparkStreaming() throws Exception {
        return javaStreamingContext;
    }

    @Override
    public JavaSparkContext javaSparkContext() throws Exception {
        return this.javaSparkContext;
    }
}
