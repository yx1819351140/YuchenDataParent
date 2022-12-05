package com.yuchen.data.monitor.utils.kafka;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.io.Serializable;
import java.util.Properties;

/**
 * 
* Title: KafkaProducerTest
* Description: 
* kafka 生产者demo
* Version:1.0.0  
* @author pancm
* @date 2018年1月26日
 */
public class KafkaProducer implements Runnable,Serializable {
	private static final long serialVersionUID = 10086L;
	public static void main(String[] args) {

	}
	private final org.apache.kafka.clients.producer.KafkaProducer<String, String> producer;
	private final String topic;
	public KafkaProducer(String topicName) {
		Properties props = new Properties();
		props.put("bootstrap.servers", "datanode01:9092,datanode02:9092,datanode03:9092,datanode04:9092");
		props.put("acks", "all");
		props.put("retries", 0);
		props.put("batch.size", 16384);
		props.put("key.serializer", StringSerializer.class.getName());
		props.put("value.serializer", StringSerializer.class.getName());
		this.producer = new org.apache.kafka.clients.producer.KafkaProducer<>(props);
		this.topic = topicName;
	}

	public boolean sendRecord(String key, String value){
		try {
				producer.send(new ProducerRecord<>(this.topic, key, value));
			return true;
		}catch (Exception e) {
			e.printStackTrace();
			return false;
		}finally {
			producer.close();
		}
	}

	public void close() {
		try {
			this.producer.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {

	}
}