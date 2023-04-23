package com.yuchen.etl.runtime.java.news;

import com.alibaba.fastjson.JSONObject;
import com.yuchen.common.enums.LangType;
import com.yuchen.common.pub.BaseConfig;
import com.yuchen.etl.core.java.config.ConfigFactory;
import com.yuchen.etl.core.java.config.FlinkJobConfig;
import com.yuchen.etl.core.java.config.TaskConfig;
import com.yuchen.etl.core.java.flink.FlinkSupport;
import com.yuchen.etl.core.java.flink.KafkaDeserialization;
import com.yuchen.etl.core.java.flink.KafkaSerialization;
import com.yuchen.etl.runtime.java.news.operator.*;
import com.yuchen.etl.runtime.java.news.process.*;
import com.yuchen.etl.runtime.java.news.sink.CategoryKafkaSerialization;
import com.yuchen.etl.runtime.java.news.sink.EsShardIndexSink;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.KafkaSourceBuilder;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SideOutputDataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.OutputTag;

import java.util.Map;
import java.util.Properties;

/**
 * @Author: xiaozhennan
 * @Date: 2023/2/20 9:56
 * @Package: com.yuchen.etl.runtime.java.news
 * @ClassName: News2Origin
 * @Description: 消费新闻写入到ES, 作业配置文件为flink-news2final.json
 **/
public class News2Final {
    public static void main(String[] args) throws Exception {
        //加载配置文件,flink run yarn-per-job -c com.yuchen.etl.runtime.java.news.News2Origin runtime.jar ./flink-news2origin.json
        FlinkJobConfig config = ConfigFactory.load(args[0], FlinkJobConfig.class);
        //获取作业配置中taskConfig
        TaskConfig taskConfig = config.getTaskConfig();
        //获取kafka配置
        Map<String, Object> kafkaConfig = taskConfig.getMap("kafkaConfig");
        //kafka的输入输出Topic
        String inputTopics = taskConfig.getStringVal("news.input.topics");
        String outputTopic = taskConfig.getStringVal("news.output.topic");

        //初始化flink环境
        StreamExecutionEnvironment env = FlinkSupport.createEnvironment(config, LangType.JAVA);
        //使用自定义的KafkaDeserialization
        KafkaDeserialization kafkaDeserialization = new KafkaDeserialization(true, true);
        //获取KafkaSource
        KafkaSource<JSONObject> source = getKafkaSource(kafkaConfig, inputTopics, kafkaDeserialization);
        //获取kafka数据流
        DataStreamSource<JSONObject> kafkaSource = env.fromSource(source, WatermarkStrategy.noWatermarks(), "Kafka读取");

        //更新标志位 + final标志位生成
        FinalNewsProcessOperator finalNewsProcessOperator = new FinalNewsProcessOperator(taskConfig);
        SingleOutputStreamOperator<JSONObject> finalNewsStream = kafkaSource.map(finalNewsProcessOperator).name("Origin新闻转换为Final通用处理");

        //写出到origin_news_xxxx
        BaseConfig yuchenNewsConfig = taskConfig.getBaseConfig("yuchen_news");
        EsShardIndexSink sinkYuchenNews = new EsShardIndexSink(taskConfig, yuchenNewsConfig);
        finalNewsStream.addSink(sinkYuchenNews).name("写入ES-yuchen_news");

        //发送Kafka的数据过滤处理
        FinalNewsSinkKafkaFilter finalNewsSinkKafkaFilter = new FinalNewsSinkKafkaFilter();
        SingleOutputStreamOperator<JSONObject> filterFinalNewsStream = finalNewsStream.filter(finalNewsSinkKafkaFilter);
        //写出新闻到Kafka
        KafkaSink<JSONObject> sink = getKafkaSink(kafkaConfig, outputTopic);
        filterFinalNewsStream.sinkTo(sink).name("写入kafka-yuchen_news_final");
        //执行
        env.execute(config.getJobName());
        System.out.println("执行结束");
    }

    private static KafkaSink<JSONObject> getKafkaSink(Map<String, Object> kafkaConfig, String topic) {
        String servers = (String) kafkaConfig.getOrDefault("bootstrap.servers", "127.0.0.1");
        KafkaSink<JSONObject> sink = KafkaSink.<JSONObject>builder()
                .setBootstrapServers(servers)
                .setRecordSerializer(new KafkaSerialization(topic))
                .build();
        return sink;
    }

    private static DataStream<JSONObject> getSplitStream(SingleOutputStreamOperator<JSONObject> mainStream, Map.Entry<String, OutputTag<JSONObject>> entry, NewsProcessor processor, int parallelism) {
        String key = entry.getKey();
        OutputTag<JSONObject> tag = entry.getValue();
        //这一行就是 通过主流获取tag对应的side流
        SideOutputDataStream<JSONObject> newsStream = mainStream.getSideOutput(tag);
        if (tag != null) {
            TaskConfig taskConfig = processor.getTaskConfig();
            return newsStream
                    .process(new NewsProcessOperator(taskConfig, processor, key))
                    .setParallelism(parallelism);
        }
        return null;
    }

    private static KafkaSink<JSONObject> getKafkaSink(String bootstrapServers, Properties properties, CategoryKafkaSerialization serialization) {
        KafkaSink<JSONObject> sink = KafkaSink.<JSONObject>builder()
                .setRecordSerializer(serialization)
                .setBootstrapServers(bootstrapServers)
                .setKafkaProducerConfig(properties)
                .build();
        return sink;
    }

    private static KafkaSource<JSONObject> getKafkaSource(Map<String, Object> kafkaConfig, String topics, KafkaDeserialization kafkaDeserialization) {
        String bootstrapServers = (String) kafkaConfig.get("bootstrap.servers");
        String groupId = (String) kafkaConfig.get("group.id");
        Object startingOffsets = kafkaConfig.get("starting.offsets");

        KafkaSourceBuilder<JSONObject> builder = KafkaSource.<JSONObject>builder()
                .setBootstrapServers(bootstrapServers)
                .setTopics(topics.split(","))
                .setGroupId(groupId);

        if (startingOffsets != null && startingOffsets instanceof String) {
            if ("latest".equalsIgnoreCase((String) startingOffsets)) {
                builder.setStartingOffsets(OffsetsInitializer.latest());
            }
            if ("earliest".equalsIgnoreCase((String) startingOffsets)) {
                builder.setStartingOffsets(OffsetsInitializer.earliest());
            }
        } else if (startingOffsets != null && startingOffsets instanceof Long) {
            builder.setStartingOffsets(OffsetsInitializer.timestamp((Long) startingOffsets));
        } else {
            builder.setStartingOffsets(OffsetsInitializer.latest());
        }


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
        KafkaSource<JSONObject> source = builder.setDeserializer(kafkaDeserialization)
                .build();
        return source;
    }
}
