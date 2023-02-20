package com.yuchen.etl.runtime.java.news;

import com.alibaba.fastjson.JSONObject;
import com.yuchen.common.enums.LangType;
import com.yuchen.etl.core.java.config.ConfigFactory;
import com.yuchen.etl.core.java.config.FlinkJobConfig;
import com.yuchen.etl.core.java.config.TaskConfig;
import com.yuchen.etl.core.java.flink.FlinkSupport;
import com.yuchen.etl.core.java.flink.KafkaDeserialization;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SideOutputDataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.OutputTag;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: xiaozhennan
 * @Date: 2023/2/20 9:56
 * @Package: com.yuchen.etl.runtime.java.news
 * @ClassName: News2Es
 * @Description: 消费新闻写入到ES, 作业配置文件为flink-news2es.json
 **/
public class News2Es {
    public static void main(String[] args) throws Exception {
        FlinkJobConfig config = ConfigFactory.load(args[0], FlinkJobConfig.class);
        TaskConfig taskConfig = config.getTaskConfig();
        String bootstrapServers = taskConfig.getStringVal("bootstrap.servers");
        String groupId = taskConfig.getStringVal("group.id");
        String topics = taskConfig.getStringVal("topics");
        StreamExecutionEnvironment env = FlinkSupport.createEnvironment(config, LangType.JAVA);
        // 消费多个kafka数据
        KafkaDeserialization kafkaDeserialization = new KafkaDeserialization(true, true);
        //根据不同数据来源,分发到不同的hive数据表
        KafkaSource<JSONObject> source = getKafkaSource(bootstrapServers, groupId, topics, kafkaDeserialization);
        DataStreamSource<JSONObject> kafkaSource = env.fromSource(source, WatermarkStrategy.noWatermarks(), "Kafka读取");
        //定义旁路流Map
        Map<String, OutputTag<JSONObject>> tagMap = new ConcurrentHashMap<>();
        String[] topicArr = topics.split(",");
        for (String topic : topicArr) {
            OutputTag<JSONObject> topicTag = new OutputTag<JSONObject>(topic);
            tagMap.put(topic, topicTag);
        }

        NewsSplitOperator splitOperator = new NewsSplitOperator(tagMap);

        //分流到不同tag
        SingleOutputStreamOperator<JSONObject> mainStream = kafkaSource.process(splitOperator);
        DataStream<JSONObject> gdeltStream = null;
        DataStream<JSONObject> collectStream = null;
        DataStream<JSONObject> hsStream = null;
        DataStream<JSONObject> otherStream = null;
        //针对不同来源的数据,进行分流处理
        for (Map.Entry<String, OutputTag<JSONObject>> entry : tagMap.entrySet()) {
            String key = entry.getKey();
            OutputTag<JSONObject> tag = entry.getValue();
            if ("yuchen_news_gdelt".equalsIgnoreCase(key)) {
                SideOutputDataStream<JSONObject> yuchenNewsGdeltStream = mainStream.getSideOutput(tag);

                gdeltStream = yuchenNewsGdeltStream
                        .process(new NewsProcessOperator(new GdeltNewsProcessor(), key))
                        //这里gdelt的并行度需要给高一点
                        .setParallelism(5);
            }
            if ("yuchen_news_collect".equalsIgnoreCase(key)) {
                SideOutputDataStream<JSONObject> yuchenCollectStream = mainStream.getSideOutput(tag);
                collectStream = yuchenCollectStream
                        .process(new NewsProcessOperator(new CollectNewsProcessor(), key))
                        .setParallelism(1);
            }
            if ("yuchen_news_hs".equalsIgnoreCase(key)) {
                SideOutputDataStream<JSONObject> yuchenNewsHsStream = mainStream.getSideOutput(tag);
                hsStream = yuchenNewsHsStream.process(new NewsProcessOperator(new GenericNewsProcessor(), key))
                        .setParallelism(1);
            }
            if ("yuchen_news_other".equalsIgnoreCase(key)) {
                SideOutputDataStream<JSONObject> yuchenNewsOtherStream = mainStream.getSideOutput(tag);
                otherStream = yuchenNewsOtherStream.process(new NewsProcessOperator(new GenericNewsProcessor(), key))
                        .setParallelism(1);
            }
        }

        //不同来源新闻处理后合并为一条流
        DataStream<JSONObject> allNewsStream = mainStream.union(gdeltStream, collectStream, hsStream, otherStream);

        //写出到es
        EsShardIndexSink esShardIndexSink = new EsShardIndexSink();
        allNewsStream.addSink(esShardIndexSink);

        //按新闻类型写出到kafka
        CategoryKafkaSerialization kafkaSerialization = new CategoryKafkaSerialization();
        KafkaSink<JSONObject> kafkaSink = getKafkaSink(bootstrapServers, new Properties(), kafkaSerialization);
        allNewsStream.sinkTo(kafkaSink);

        //执行
        env.execute(config.getJobName());
    }

    private static KafkaSink<JSONObject> getKafkaSink(String bootstrapServers, Properties properties, CategoryKafkaSerialization serialization) {
        KafkaSink<JSONObject> sink = KafkaSink.<JSONObject>builder()
                .setRecordSerializer(serialization)
                .setBootstrapServers(bootstrapServers)
                .setKafkaProducerConfig(properties)
                .build();
        return sink;
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
