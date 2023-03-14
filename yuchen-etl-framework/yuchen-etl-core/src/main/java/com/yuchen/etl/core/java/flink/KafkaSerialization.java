package com.yuchen.etl.core.java.flink;

import com.alibaba.fastjson.JSONObject;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.nio.charset.StandardCharsets;

/**
 * @Author: xiaozhennan
 * @Date: 2023/3/14 15:47
 * @Package: com.yuchen.etl.core.java.flink
 * @ClassName: KafkaSerialization
 * @Description: Kafka序列化
 **/
public class KafkaSerialization implements KafkaRecordSerializationSchema<JSONObject> {

    private String topic;

    public KafkaSerialization(String topic) {
        this.topic = topic;
    }

    @Override
    public ProducerRecord<byte[], byte[]> serialize(JSONObject jsonObject, KafkaSinkContext kafkaSinkContext, Long aLong) {
        byte[] key = jsonObject.toJSONString().getBytes(StandardCharsets.UTF_8);
        byte[] value = key;
        ProducerRecord<byte[], byte[]> producerRecord = new ProducerRecord<>(topic, key, value);
        return producerRecord;
    }
}
