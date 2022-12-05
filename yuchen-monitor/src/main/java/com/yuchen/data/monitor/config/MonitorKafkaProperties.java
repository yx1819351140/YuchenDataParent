package com.yuchen.data.monitor.config;


import lombok.Data;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = "monitor.kafka")
public class MonitorKafkaProperties {

	private KafkaBaseProperties base = new KafkaBaseProperties();
	private GlobalEventProperties globalEvent = new GlobalEventProperties();

	/**
	 * 异步处理配置
	 */
	@Data
	public static class KafkaBaseProperties {
		String servers;
		String enableAutoCommit;
		String autoCommitIntervalMs;
		String sessionTimeoutMs;
		String autoOffsetReset;
		String keyDeserializer = StringDeserializer.class.getName();
		String valueDeserializer = StringDeserializer.class.getName();
	}

	@Data
	public static class GlobalEventProperties {
		String topic;
		String group;
		Integer nlpThresholdTime;
		Integer bigDataThresholdTime;
		String cronExpression;
	}

}
