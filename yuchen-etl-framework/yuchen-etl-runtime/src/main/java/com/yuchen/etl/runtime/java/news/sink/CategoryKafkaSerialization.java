package com.yuchen.etl.runtime.java.news.sink;

import com.alibaba.fastjson.JSONObject;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.kafka.clients.producer.ProducerRecord;

/**
 * @Author: xiaozhennan
 * @Date: 2023/2/20 14:16
 * @Package: com.yuchen.etl.runtime.java.news
 * @ClassName: CategoryKafkaSerialization
 * @Description: 根据新闻类别发送Kafka的Topic
 **/
public class CategoryKafkaSerialization implements KafkaRecordSerializationSchema<JSONObject> {
    @Override
    public ProducerRecord<byte[], byte[]> serialize(JSONObject element, KafkaSinkContext context, Long timestamp) {
        return null;
    }
}
