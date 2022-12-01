package com.yuchen.data.service.config;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class MonitorConfig {
    @Autowired
    MonitorProperties kafkaMonitor;

    @Bean
    public KafkaConsumer getKafkaConsumer () {
        Properties props = new Properties();
        props.put("bootstrap.servers", kafkaMonitor.getBase().getServers());
        props.put("group.id", kafkaMonitor.getGlobalEvent().getGroup());
        props.put("enable.auto.commit", kafkaMonitor.getBase().getEnableAutoCommit());
        props.put("auto.commit.interval.ms", kafkaMonitor.getBase().getAutoCommitIntervalMs());
        props.put("session.timeout.ms", kafkaMonitor.getBase().getSessionTimeoutMs());
        props.put("auto.offset.reset", kafkaMonitor.getBase().getAutoOffsetReset());
        props.put("key.deserializer", kafkaMonitor.getBase().getKeyDeserializer());
        props.put("value.deserializer", kafkaMonitor.getBase().getValueDeserializer());
        KafkaConsumer consumer = new org.apache.kafka.clients.consumer.KafkaConsumer<>(props);
        return consumer;
    }
}
