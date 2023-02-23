package com.yuchen.etl.runtime.java.hbase2kafka.sink;

import com.yuchen.etl.core.java.config.ConfigFactory;
import com.yuchen.etl.core.java.config.TaskConfig;
import kafka.Kafka;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Copyright (C), 2023-02-20
 * FileName: KafkaHelper
 * Author:   HQP
 * Date:     2023/2/20 16:29
 * Description: 用于批量写入kafka
 */
public class KafkaHelper {
    Map<String, Object> kafkaConfigs;

    // 构造函数用于获取kafka的配置
    public KafkaHelper(String configPath) throws IOException {
        // 根据配置文件路径创建hbase相关的配置
        TaskConfig taskConfig = ConfigFactory.load(configPath, TaskConfig.class);
        kafkaConfigs = taskConfig.getMap("kafka");
    }

    public KafkaProducer<String, String> createKafkaProducer() throws IOException, URISyntaxException {
        // 创建hbase的配置
        Properties props = new Properties();
        // 循环添加配置文件信息
        kafkaConfigs.forEach((String key, Object value) ->{
            props.put(key,String.valueOf(value));
        });
        // 返回消费者对象
        return new KafkaProducer<String, String>(props);
    }


    public static void main(String[] args) {
//        // Kafka producer configuration
//        Properties props = new Properties();
//        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
//        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
//        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
//
//        // Create Kafka producer
//        KafkaProducer<String, String> producer = new KafkaProducer<>(props);
//
//        // Create a list of messages to send
//        List<String> messages = new ArrayList<>();
//        messages.add("Message 1");
//        messages.add("Message 2");
//        messages.add("Message 3");
//
//        // Send messages in batches
//        int batchSize = 2;
//        for (int i = 0; i < messages.size(); i += batchSize) {
//            List<String> batchMessages = messages.subList(i, Math.min(i + batchSize, messages.size()));
//
//            // Create Kafka record for each message
//            List<ProducerRecord<String, String>> records = new ArrayList<>();
//            for (String message : batchMessages) {
//                ProducerRecord<String, String> record = new ProducerRecord<>("test-topic", message);
//                records.add(record);
//            }
//
//            // Send the batch of messages
//            producer.send(records, new Callback() {
//                @Override
//                public void onCompletion(RecordMetadata metadata, Exception exception) {
//                    if (exception != null) {
//                        System.out.println("Error sending message: " + exception.getMessage());
//                    } else {
//                        System.out.println("Message sent to partition " + metadata.partition() +
//                                " with offset " + metadata.offset());
//                    }
//                }
//            });
//        }
//
//        // Close the Kafka producer
//        producer.close();
    }
}
