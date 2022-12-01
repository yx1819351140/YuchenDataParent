package com.yuchen.data.service.utils.kafka;

import com.yuchen.data.service.config.MonitorProperties;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.consumer.OffsetAndTimestamp;
import org.apache.kafka.common.TopicPartition;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * 
* Title: KafkaConsumerTest
* Description: 
*  kafka消费者 demo
* Version:1.0.0  
* @author pancm
* @date 2018年1月26日
 */
public class KafkaConsumer implements Runnable{
	private org.apache.kafka.clients.consumer.KafkaConsumer<String, String> consumer;

	private String topic;

	private String group;

	public KafkaConsumer(org.apache.kafka.clients.consumer.KafkaConsumer consumer,String topic,String group) {
		this.consumer = consumer;
		this.group = group;
		this.topic = topic;
	}

	public long getOffsetByTimestamp(String time){
		long timeStamp = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").parseMillis(time);
		long totalOffset = -1;
		// 获取该主题下的所有分区
		Map<TopicPartition,Long> timestampToSearch = new HashMap<>();
		this.consumer.partitionsFor(this.topic).forEach(partitionInfo -> {
			TopicPartition topicPartition = new TopicPartition(partitionInfo.topic(), partitionInfo.partition());
			timestampToSearch.put(topicPartition,timeStamp);
		});

		// 遍历相加获取所有分区的offset之和
		Map<TopicPartition, OffsetAndTimestamp> topicPartitionOffsetAndTimestampMap = consumer.offsetsForTimes(timestampToSearch);
		for (OffsetAndTimestamp offsetAndTimestamp : topicPartitionOffsetAndTimestampMap.values()) {
			if(offsetAndTimestamp != null){
				if(totalOffset == -1){
					totalOffset = 0;
				}
				totalOffset += offsetAndTimestamp.offset();
			}
		}
		return totalOffset;
	}

	public long getEndOffset(){

		long totalOffset = 0;
		ArrayList<TopicPartition> topicPartitions = new ArrayList<>();
		this.consumer.partitionsFor(this.topic).forEach(partitionInfo -> topicPartitions.add(new TopicPartition(partitionInfo.topic(),partitionInfo.partition())));

		// get totalOffset
		Map<TopicPartition, Long> topicPartitionLongMap = this.consumer.endOffsets(topicPartitions);
		for(long partitionTotalOffset :topicPartitionLongMap.values()){
			totalOffset += partitionTotalOffset;
		}

		return totalOffset;
	}

	public long getConsumerOffset(){

		long consumerOffset = 0;
		ArrayList<TopicPartition> topicPartitions = new ArrayList<>();
		this.consumer.partitionsFor(this.topic).forEach(partitionInfo -> topicPartitions.add(new TopicPartition(partitionInfo.topic(),partitionInfo.partition())));

		// get consumerOffset
		for ( TopicPartition topicPartition :topicPartitions) {
				OffsetAndMetadata committed = this.consumer.committed(topicPartition);
				if(committed != null){
					consumerOffset += committed.offset();
				}
		}

		return consumerOffset;
	}

	public long getKafkaConsumerLag(){
		long consumerOffset = 0;
		long totalOffset = 0;
		ArrayList<TopicPartition> topicPartitions = new ArrayList<>();
		this.consumer.partitionsFor(this.topic).forEach(partitionInfo -> topicPartitions.add(new TopicPartition(partitionInfo.topic(),partitionInfo.partition())));

		// get totalOffset
		Map<TopicPartition, Long> topicPartitionLongMap = this.consumer.endOffsets(topicPartitions);
		for(long partitionTotalOffset :topicPartitionLongMap.values()){
			totalOffset += partitionTotalOffset;
		}

		// get consumerOffset
		for ( TopicPartition topicPartition :topicPartitions) {
			if(totalOffset != 0){
				OffsetAndMetadata committed = this.consumer.committed(topicPartition);
				if(committed != null){
					consumerOffset += committed.offset();
				}
			}
		}

		return totalOffset - consumerOffset;
	}
	public void close() {
		try {
			this.consumer.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {

	}
}