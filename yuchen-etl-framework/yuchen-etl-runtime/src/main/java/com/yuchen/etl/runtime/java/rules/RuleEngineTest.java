package com.yuchen.etl.runtime.java.rules;

import com.alibaba.fastjson.JSONObject;
import com.weiwan.rule.common.EngineType;
import com.weiwan.rule.engine.RuleEngine;
import com.weiwan.rule.engine.RuleEngineFactory;
import com.yuchen.common.enums.LangType;
import com.yuchen.etl.core.java.config.ConfigFactory;
import com.yuchen.etl.core.java.config.FlinkJobConfig;
import com.yuchen.etl.core.java.config.TaskConfig;
import com.yuchen.etl.core.java.flink.FlinkSupport;
import com.yuchen.etl.core.java.flink.MyKafkaDeserialization;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSink;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.io.IOException;
import java.util.Map;

/**
 * @Author: xiaozhennan
 * @Date: 2023/2/6 13:09
 * @Package: com.yuchen.etl.runtime.java.rules
 * @ClassName: RuleEngineTest
 * @Description: 规则引擎测试程序
 **/
public class RuleEngineTest {
    public static void main(String[] args) throws Exception {
        FlinkJobConfig config = ConfigFactory.load(args[0], FlinkJobConfig.class);

        TaskConfig taskConfig = config.getTaskConfig();
        Map<String, Object> ruleEngineConfigs = taskConfig.getMap("ruleEngine");
        String bootstrapServers = taskConfig.getStringVal("bootstrap.servers");
        String groupId = taskConfig.getStringVal("group.id");
        String topics = taskConfig.getStringVal("topics");
        StreamExecutionEnvironment env = FlinkSupport.createEnvironment(config, LangType.JAVA);

        // 消费多个kafka数据
        MyKafkaDeserialization myKafkaDeserialization = new MyKafkaDeserialization(true, true);
        //根据不同数据来源,分发到不同的hive数据表
        KafkaSource<JSONObject> source = getKafkaSource(bootstrapServers, groupId, topics, myKafkaDeserialization);

        DataStreamSource<JSONObject> kafkaSource = env.fromSource(source, WatermarkStrategy.noWatermarks(), "Kafka读取");

        RuleProcessFunc ruleProcessFunc = new RuleProcessFunc(taskConfig);
        DataStream<JSONObject> process = kafkaSource.process(ruleProcessFunc);

        DataStreamSink<JSONObject> print = process.print();

        env.execute();
    }


    private static KafkaSource<JSONObject> getKafkaSource(String bootstrapServers, String groupId, String topics, MyKafkaDeserialization myKafkaDeserialization) {
        KafkaSource<JSONObject> source = KafkaSource.<JSONObject>builder()
                .setBootstrapServers(bootstrapServers)
                .setTopics(topics.split(","))
                .setGroupId(groupId)
                .setStartingOffsets(OffsetsInitializer.latest())


                /**
                 *     // 从消费组的committed offset开始，无reset策略
                 *     .setStartingOffsets(OffsetsInitializer.committedOffsets())
                 *     // 从提交的偏移量开始，如果不存在提交的偏移量，也使用 EARLIEST 作为重置策略
                 *     .setStartingOffsets(OffsetsInitializer.committedOffsets(OffsetResetStrategy.EARLIEST))
                 *     // 从时间戳大于等于某个时间戳（毫秒）的第一条记录开始
                 *     .setStartingOffsets(OffsetsInitializer.timestamp(1657256176000L))
                 *     // 从最早的偏移量开始
                 *     .setStartingOffsets(OffsetsInitializer.earliest())
                 *     // 从最近的偏移量开始
                 *     .setStartingOffsets(OffsetsInitializer.latest());
                 */
                .setDeserializer(myKafkaDeserialization)
                .build();
        return source;
    }
}
