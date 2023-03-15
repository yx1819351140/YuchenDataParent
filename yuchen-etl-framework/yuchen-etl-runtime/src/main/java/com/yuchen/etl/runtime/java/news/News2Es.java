package com.yuchen.etl.runtime.java.news;

import com.alibaba.fastjson.JSONObject;
import com.yuchen.common.enums.LangType;
import com.yuchen.etl.core.java.config.ConfigFactory;
import com.yuchen.etl.core.java.config.FlinkJobConfig;
import com.yuchen.etl.core.java.config.TaskConfig;
import com.yuchen.etl.core.java.flink.FlinkSupport;
import com.yuchen.etl.core.java.flink.KafkaDeserialization;
import com.yuchen.etl.core.java.flink.KafkaSerialization;
import com.yuchen.etl.runtime.java.news.common.NewsSource;
import com.yuchen.etl.runtime.java.news.operator.NewsProcessOperator;
import com.yuchen.etl.runtime.java.news.operator.NewsSplitOperator;
import com.yuchen.etl.runtime.java.news.process.*;
import com.yuchen.etl.runtime.java.news.sink.CategoryKafkaSerialization;
import com.yuchen.etl.runtime.java.news.sink.EsShardIndexSink;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.typeinfo.TypeInformation;
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
        //加载配置文件,flink run yarn-per-job -c com.yuchen.etl.runtime.java.news.News2Es runtime.jar ./flink-news2es.json
        FlinkJobConfig config = ConfigFactory.load(args[0], FlinkJobConfig.class);
        //获取作业配置中taskConfig
        TaskConfig taskConfig = config.getTaskConfig();
        //获取kafka配置
        Map<String, Object> kafkaConfig = taskConfig.getMap("kafkaConfig");
        //获取所有新闻topic
        String topics = taskConfig.getStringVal("news.input.topics");
        //初始化flink环境
        StreamExecutionEnvironment env = FlinkSupport.createEnvironment(config, LangType.JAVA);
        //使用自定义的KafkaDeserialization
        KafkaDeserialization kafkaDeserialization = new KafkaDeserialization(true, true);
        //消费kafka,获取数据流
        KafkaSource<JSONObject> source = getKafkaSource(kafkaConfig, topics, kafkaDeserialization);
        //获取原始kafka数据流
        DataStreamSource<JSONObject> kafkaSource = env.fromSource(source, WatermarkStrategy.noWatermarks(), "Kafka读取");

        //定义旁路流Map,按topic分流, 一个topic一个数据流DataStream一个
        Map<String, OutputTag<JSONObject>> tagMap = new ConcurrentHashMap<>();
        String[] topicArr = topics.split(",");
        for (String topic : topicArr) {
            //循环创建tag
            OutputTag<JSONObject> topicTag = new OutputTag<JSONObject>(topic, TypeInformation.of(JSONObject.class));
            tagMap.put(topic, topicTag);
        }
        //创建分流算子
        NewsSplitOperator splitOperator = new NewsSplitOperator(tagMap);
        //分流到不同tag, 获得一个主流 mainStream
        SingleOutputStreamOperator<JSONObject> mainStream = kafkaSource.process(splitOperator);
        //针对不同来源的数据,进行分流处理
        DataStream<JSONObject> allNewsStream = null;
        //将每个tag拿出来,获取对应的流,添加对应的处理逻辑
        for (Map.Entry<String, OutputTag<JSONObject>> entry : tagMap.entrySet()) {
            //这个key是kafka的topic
            String key = entry.getKey();
            //这里是根据不同的数据来源来进行不同的数据处理
            if (NewsSource.GDELT.getTopic().equalsIgnoreCase(key)) {
                DataStream<JSONObject> gdeltNewsStream = getSplitStream(mainStream, entry, new GdeltNewsProcessor(taskConfig), 3);
                if (gdeltNewsStream != null) {
                    if (allNewsStream != null) allNewsStream.union(gdeltNewsStream);
                    else allNewsStream = gdeltNewsStream;
                }
            }
            if (NewsSource.COLLECT.getTopic().equalsIgnoreCase(key)) {
                DataStream<JSONObject> collectNewsStream = getSplitStream(mainStream, entry, new CollectNewsProcessor(taskConfig), 1);
                if (collectNewsStream != null) {
                    if (allNewsStream != null) allNewsStream.union(collectNewsStream);
                    else allNewsStream = collectNewsStream;
                }
            }
            if (NewsSource.HS.getTopic().equalsIgnoreCase(key)) {
                DataStream<JSONObject> hsNewsStream = getSplitStream(mainStream, entry, new HSNewsProcessor(taskConfig), 1);
                if (hsNewsStream != null) {
                    if (allNewsStream != null) allNewsStream.union(hsNewsStream);
                    else allNewsStream = hsNewsStream;
                }
            }
            if (NewsSource.OTHER.getTopic().equalsIgnoreCase(key)) {
                DataStream<JSONObject> otherNewsStream = getSplitStream(mainStream, entry, new OtherNewsProcessor(taskConfig), 1);
                if (otherNewsStream != null) {
                    if (allNewsStream != null) allNewsStream.union(otherNewsStream);
                    else allNewsStream = otherNewsStream;
                }
            }
        }


        allNewsStream.map((MapFunction<JSONObject, JSONObject>) value -> {

            //查询es
            //simhash去重
            //合并媒体

            return null;
        }).name("文本通用处理");

        //写出到es
        EsShardIndexSink esShardIndexSink = new EsShardIndexSink(taskConfig);
        //这里声明了数据应该sink到es
        allNewsStream.addSink(esShardIndexSink).name("写入ES");

        //按新闻类型写出到kafka,这里如果区分不了
        String servers = (String) kafkaConfig.getOrDefault("bootstrap.servers", "127.0.0.1");
        KafkaSink<JSONObject> sink = KafkaSink.<JSONObject>builder()
                .setBootstrapServers(servers)
                .setRecordSerializer(new KafkaSerialization("yuchen_news_origin"))
                .build();

        allNewsStream.sinkTo(sink).name("写入Kafka");
        //执行
        env.execute(config.getJobName());
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
