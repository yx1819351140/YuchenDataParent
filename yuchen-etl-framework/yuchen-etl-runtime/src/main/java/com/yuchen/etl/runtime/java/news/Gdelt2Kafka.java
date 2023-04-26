package com.yuchen.etl.runtime.java.news;

import com.alibaba.fastjson.JSONObject;
import com.yuchen.common.enums.LangType;
import com.yuchen.etl.core.java.config.ConfigFactory;
import com.yuchen.etl.core.java.config.FlinkJobConfig;
import com.yuchen.etl.core.java.config.TaskConfig;
import com.yuchen.etl.core.java.flink.FlinkSupport;
import com.yuchen.etl.core.java.flink.KafkaSerialization;
import com.yuchen.etl.runtime.java.news.source.Gdelt2KafkaFilter;
import com.yuchen.etl.runtime.java.news.source.HbaseScanSource;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.Map;

/**
 * @Author: xiaozhennan
 * @Date: 2023/3/14 15:30
 * @Package: com.yuchen.etl.runtime.java.news
 * @ClassName: Gdelt2Kafka
 * @Description: Gdelt新闻数据发送kafka
 **/
public class Gdelt2Kafka {

    public static void main(String[] args) throws Exception {
        //加载配置文件,flink run yarn-per-job -c com.yuchen.etl.runtime.java.news.News2Origin runtime.jar ./flink-News2Origin.json
        FlinkJobConfig config = ConfigFactory.load(args[0], FlinkJobConfig.class);
        //获取作业配置中taskConfig
        TaskConfig taskConfig = config.getTaskConfig();
        //获取kafka配置
        Map<String, Object> kafkaConfig = taskConfig.getMap("kafkaConfig");

        //获取所有新闻topic
        String topic = taskConfig.getStringVal("kafka.target.topic");
        //初始化flink环境
        StreamExecutionEnvironment env = FlinkSupport.createEnvironment(config, LangType.JAVA);
        HbaseScanSource hbaseScanSource = new HbaseScanSource(taskConfig);
        DataStreamSource<JSONObject> hbaseStream = env.addSource(hbaseScanSource, "Hbase扫描数据源");

        Gdelt2KafkaFilter gdelt2KafkaFilter = new Gdelt2KafkaFilter(taskConfig);
        SingleOutputStreamOperator<JSONObject> newsStream = hbaseStream.filter(gdelt2KafkaFilter);


        String servers = (String) kafkaConfig.getOrDefault("bootstrap.servers", "127.0.0.1");
        KafkaSink<JSONObject> sink = KafkaSink.<JSONObject>builder()
                .setBootstrapServers(servers)
                .setRecordSerializer(new KafkaSerialization(topic))
                .build();

        newsStream.sinkTo(sink);
        //执行
        env.execute(config.getJobName());
    }


}
