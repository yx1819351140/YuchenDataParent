package com.yuchen.etl.runtime.java;

import com.alibaba.fastjson.JSONObject;
import com.yuchen.common.enums.LangType;
import com.yuchen.etl.core.java.config.ConfigFactory;
import com.yuchen.etl.core.java.config.FlinkJobConfig;
import com.yuchen.etl.core.java.config.TaskConfig;
import com.yuchen.etl.core.java.flink.FlinkSupport;
import com.yuchen.etl.core.java.flink.MyKafkaDeserialization;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SideOutputDataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: xiaozhennan
 * @Date: 2022/12/14 17:13
 * @Package: com.yuchen.etl.runtime.java
 * @ClassName: Kafka2Hive
 * @Description: Kafka2Hive程序
 **/
public class Kafka2Hive {

    private static final Logger logger = LoggerFactory.getLogger(Kafka2Hive.class);

    public static void main(String[] args) throws Exception {
        FlinkJobConfig flinkJobConfig = ConfigFactory.load(args[0], FlinkJobConfig.class);
        TaskConfig taskConfig = flinkJobConfig.getTaskConfig();
        String bootstrapServers = taskConfig.getStringVal("bootstrap.servers");
        String groupId = taskConfig.getStringVal("group.id");
        String topics = taskConfig.getStringVal("topics");
        JSONObject mapping = new JSONObject((Map<String, Object>) taskConfig.getVal("mapping", new JSONObject()));
        System.out.println(mapping);
        StreamExecutionEnvironment env = FlinkSupport.createEnvironment(flinkJobConfig, LangType.JAVA);
        // 消费多个kafka数据
        MyKafkaDeserialization myKafkaDeserialization = new MyKafkaDeserialization(true, true);
        //根据不同数据来源,分发到不同的hive数据表
        KafkaSource<JSONObject> source = KafkaSource.<JSONObject>builder()
                .setBootstrapServers(bootstrapServers)
                .setTopics(topics.split(","))
                .setGroupId(groupId)
                .setStartingOffsets(OffsetsInitializer.earliest())
                .setDeserializer(myKafkaDeserialization)
                .build();

        DataStreamSource<JSONObject> kafkaSource = env.fromSource(source, WatermarkStrategy.noWatermarks(), "Kafka读取");
        Map<String, OutputTag<JSONObject>> tagMap = new HashMap<>();
        for (String src : mapping.keySet()) {
            OutputTag<JSONObject> tag = new OutputTag<>(mapping.getString(src), TypeInformation.of(JSONObject.class));
            tagMap.put(src, tag);
        }


        SingleOutputStreamOperator<JSONObject> stream = kafkaSource.process(new ProcessFunction<JSONObject, JSONObject>() {
            @Override
            public void processElement(JSONObject value, ProcessFunction<JSONObject, JSONObject>.Context ctx, Collector<JSONObject> out) throws Exception {
                String topic = value.getString("topic");
                OutputTag<JSONObject> topicTag = tagMap.get(topic);
                logger.info("测试: {}", value);
                ctx.output(topicTag, value);
            }
        });
        stream.print();

        Collection<OutputTag<JSONObject>> values = tagMap.values();

        for (OutputTag<JSONObject> tag : values) {
            SideOutputDataStream<JSONObject> sideOutput = stream.getSideOutput(tag);
            sideOutput.addSink(new SinkFunction<JSONObject>() {
                @Override
                public void invoke(JSONObject value, Context context) throws Exception {
                    System.out.println(String.format("tag: %s, topic: %s, value: %s", tag.getId(), value.getString("topic"), value.getString("data")));
                }
            });
        }

        env.execute();

    }
}
