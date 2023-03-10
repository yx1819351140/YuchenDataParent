package com.yuchen.test.etl.core.flink;

import com.alibaba.fastjson.JSONObject;
import com.yuchen.common.enums.LangType;
import com.yuchen.etl.core.java.config.ConfigFactory;
import com.yuchen.etl.core.java.config.FlinkJobConfig;
import com.yuchen.etl.core.java.config.TaskConfig;
import com.yuchen.etl.core.java.flink.FlinkSupport;
import com.yuchen.etl.core.java.flink.MyKafkaDeserialization;
import com.yuchen.etl.core.java.flink.sink.FileBucketAssigner;
import com.yuchen.etl.core.java.flink.sink.FileFormat;
import com.yuchen.etl.core.java.flink.sink.FileSystemSink;
import com.yuchen.etl.core.java.flink.sink.HiveTableSink;
import com.yuchen.etl.core.java.utils.BucketUtil;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.connector.sink2.Sink;
import org.apache.flink.api.dag.Transformation;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.connectors.hive.HiveOptions;
import org.apache.flink.streaming.api.datastream.DataStreamSink;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.catalog.hive.HiveCatalog;
import org.apache.flink.table.data.GenericRowData;
import org.apache.flink.table.data.RowData;
import org.apache.flink.types.Row;
import org.apache.log4j.BasicConfigurator;
import org.junit.Test;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

/**
 * @Author: xiaozhennan
 * @Date: 2022/12/16 14:36
 * @Package: com.yuchen.test.etl.core.flink
 * @ClassName: TestFileSink
 * @Description:
 **/
public class TestFileSink {
    public static void main(String[] args) throws Exception {
        FlinkJobConfig flinkJobConfig = getFlinkJobConfig(args);
        TaskConfig taskConfig = flinkJobConfig.getTaskConfig();
        String bootstrapServers = taskConfig.getStringVal("bootstrap.servers");
        String groupId = taskConfig.getStringVal("group.id");
        String topics = taskConfig.getStringVal("topics");
        StreamExecutionEnvironment env = FlinkSupport.createEnvironment(flinkJobConfig, LangType.JAVA);

        // ????????????kafka??????
        MyKafkaDeserialization myKafkaDeserialization = new MyKafkaDeserialization(true, true);
        //????????????????????????,??????????????????hive?????????
        KafkaSource<JSONObject> source = getKafkaSource(bootstrapServers, groupId, topics, myKafkaDeserialization);

        DataStreamSource<JSONObject> kafkaSource = env.fromSource(source, WatermarkStrategy.noWatermarks(), "Kafka??????");

        SingleOutputStreamOperator<RowData> map = kafkaSource.map(new MapFunction<JSONObject, RowData>() {
            @Override
            public RowData map(JSONObject jsonObject) throws Exception {
                System.out.println(jsonObject);
                GenericRowData genericRowData = new GenericRowData(1);
                genericRowData.setField(0, jsonObject);
                return genericRowData;
            }
        });

        Sink<RowData> fileSink = FileSystemSink.FileSystemSinkBuilder.<RowData>builder()
                .path("./data/")
                .format(FileFormat.PARQUET)
//                .inactivityInterval(1)
//                .rolloverInterval(1)
//                .maxPartSize("1k")
//                .partSuffix(".txt")
                .init().getSink();
        DataStreamSink<RowData> sink = map.sinkTo(fileSink);

        env.execute();

        System.out.println(flinkJobConfig);
    }

    private static KafkaSource<JSONObject> getKafkaSource(String bootstrapServers, String groupId, String topics, MyKafkaDeserialization myKafkaDeserialization) {
        KafkaSource<JSONObject> source = KafkaSource.<JSONObject>builder()
                .setBootstrapServers(bootstrapServers)
                .setTopics(topics.split(","))
                .setGroupId(groupId)
                .setStartingOffsets(OffsetsInitializer.latest())


                /**
                 *     // ???????????????committed offset????????????reset??????
                 *     .setStartingOffsets(OffsetsInitializer.committedOffsets())
                 *     // ??????????????????????????????????????????????????????????????????????????? EARLIEST ??????????????????
                 *     .setStartingOffsets(OffsetsInitializer.committedOffsets(OffsetResetStrategy.EARLIEST))
                 *     // ???????????????????????????????????????????????????????????????????????????
                 *     .setStartingOffsets(OffsetsInitializer.timestamp(1657256176000L))
                 *     // ???????????????????????????
                 *     .setStartingOffsets(OffsetsInitializer.earliest())
                 *     // ???????????????????????????
                 *     .setStartingOffsets(OffsetsInitializer.latest());
                 */
                .setDeserializer(myKafkaDeserialization)
                .build();
        return source;
    }

    private static FlinkJobConfig getFlinkJobConfig(String[] args) throws IOException {
        FlinkJobConfig flinkJobConfig = ConfigFactory.load(args[0], FlinkJobConfig.class);
        return flinkJobConfig;
    }


    @Test
    public void testBucketGet() {
        String format1 = "day=($create_time|yyyy-MM-dd)/hour=($create_time|HH)";
        String format2 = "day=($create_time|yyyy-MM-dd)/type=$type/day=($update_time|yyyy-MM-dd)/type=$type/day=($create_time|yyyy-MM-dd)/hour=($create_time|HH)";
        String format3 = "day=($update_time|yyyy-MM-dd)/type=$type";
        String format4 = "type=$type";
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("create_time", new Date());
        jsonObject.put("update_time", System.currentTimeMillis());
        jsonObject.put("type", "test1");
        jsonObject.put("type", "test2");
        String process1 = BucketUtil.process(jsonObject, format1);
        System.out.println(process1);
        String process2 = BucketUtil.process(jsonObject, format2);
        System.out.println(process2);
        String process3 = BucketUtil.process(jsonObject, format3);
        System.out.println(process3);
        String process4 = BucketUtil.process(jsonObject, format4);
        System.out.println(process4);
    }
}
