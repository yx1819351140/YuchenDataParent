package com.yuchen.etl.runtime.java.rules;

import com.alibaba.fastjson.JSONObject;
import com.yuchen.common.enums.LangType;
import com.yuchen.etl.core.java.config.ConfigFactory;
import com.yuchen.etl.core.java.config.FlinkJobConfig;
import com.yuchen.etl.core.java.config.TaskConfig;
import com.yuchen.etl.core.java.flink.FlinkSupport;
import com.yuchen.etl.core.java.flink.KafkaDeserialization;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.Map;

/**
 * @Author: xiaozhennan
 * @Date: 2023/2/6 13:09
 * @Package: com.yuchen.etl.runtime.java.rules
 * @ClassName: Hbase2KafkaTest
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

//        // 自定义的序列化器
//        MyKafkaDeserialization myKafkaDeserialization = new MyKafkaDeserialization(true, true);
//        // 消费多个kafka数据
//        // 根据不同数据来源,分发到不同的hive数据表
//        KafkaSource<JSONObject> source = getKafkaSource(bootstrapServers, groupId, topics, myKafkaDeserialization);
//
//        DataStreamSource<JSONObject> kafkaSource = env.fromSource(source, WatermarkStrategy.noWatermarks(), "Kafka读取");
//
//        // 消费打印kafka数据
//        kafkaSource.print();

        /**
         * 使用filter进行过滤，使用map进行实体类转换：
         * 1.filter进行json格式验证等数据合法性过滤
         * 2.map算子中进行实体类转换
         */
//        kafkaSource.filter( kafkaString -> {
//
//        });



        // 实时写入kafka的测试代码
//        KafkaSink sink = KafkaSink.<JSONObject>builder()
//                .setBootstrapServers("datanode01:9092,datanode01:9092,datanode01:9092,datanode01:9092")
//                .setRecordSerializer(KafkaRecordSerializationSchema.builder()
//                        .setTopic("my_test_20221214_1")
//                        .setValueSerializationSchema(new JsonSerializationSchema())
//                        .build()
//                )
//                .setDeliverGuarantee(DeliveryGuarantee.AT_LEAST_ONCE)
//                .<JSONObject>build();
//        kafkaSource.sinkTo(sink);


//        RuleProcessFunc ruleProcessFunc = new RuleProcessFunc(taskConfig);
//        DataStream<JSONObject> process = kafkaSource.process(ruleProcessFunc);

//        DataStreamSink<JSONObject> print = process.print();

        env.execute();
    }


    private static KafkaSource<JSONObject> getKafkaSource(String bootstrapServers, String groupId, String topics, KafkaDeserialization kafkaDeserialization) {
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
                .setDeserializer(kafkaDeserialization)
                .build();
        return source;
    }
}
