package com.yuchen.data.monitor.tasks;


import com.yuchen.data.monitor.config.MonitorKafkaConfig;
import com.yuchen.data.monitor.config.MonitorKafkaProperties;
import com.yuchen.data.monitor.utils.DateUtils;
import com.yuchen.data.monitor.utils.alarm.WeChatAlarmService;
import com.yuchen.data.monitor.utils.kafka.YuChenKafkaConsumer;
import lombok.val;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
@Component
public class SchedulerProd {
	private static final Logger logger = LoggerFactory.getLogger(SchedulerProd.class);
	@Lazy
	@Autowired
    MonitorKafkaProperties kafkaMonitor;
	@Lazy
	@Autowired
    MonitorKafkaConfig monitorConfig;

	@Scheduled(cron="${prod.kafka.globalEventDataPush.cron}")
	public void globalEventPushDataMonitor() throws IOException {
		org.apache.kafka.clients.consumer.KafkaConsumer kafkaConsumer = monitorConfig.getKafkaConsumer();
		val monitorObject = kafkaMonitor.getGlobalEventDataPush();
		String monitorTopicName = monitorObject.getTopic();
		logger.info("监控主题为：{}", monitorTopicName);
		String monitorGroupName = monitorObject.getGroup();
		Integer consumeThresholdTime = monitorObject.getConsumeThresholdTime();
		Integer produceThresholdTime = monitorObject.getProduceThresholdTime();
		TopicMonitor(kafkaConsumer,monitorTopicName,monitorGroupName,consumeThresholdTime,produceThresholdTime);
	}

	@Scheduled(cron="${prod.kafka.globalEventNLPResult.cron}")
	public void globalEventNLPResultMonitor() throws IOException {
		org.apache.kafka.clients.consumer.KafkaConsumer kafkaConsumer = monitorConfig.getKafkaConsumer();
		val monitorObject = kafkaMonitor.getGlobalEventNLPResult();
		String monitorTopicName = monitorObject.getTopic();
		logger.info("监控主题为：{}", monitorTopicName);
		String monitorGroupName = monitorObject.getGroup();
		Integer consumeThresholdTime = monitorObject.getConsumeThresholdTime();
		Integer produceThresholdTime = monitorObject.getProduceThresholdTime();
		TopicMonitor(kafkaConsumer,monitorTopicName,monitorGroupName,consumeThresholdTime,produceThresholdTime);
	}

	public void TopicMonitor(org.apache.kafka.clients.consumer.KafkaConsumer kafkaConsumer,
			String monitorTopicName,String monitorGroupName,Integer consumeThresholdTime,Integer produceThresholdTime) throws IOException {

		YuChenKafkaConsumer consumer = new YuChenKafkaConsumer(kafkaConsumer,monitorTopicName,monitorGroupName);

		long currentOffset = consumer.getConsumerOffset();

		// 一小时前的offset
		long consumeBeforeOffset = consumer.getOffsetByTimestamp(DateUtils.getBeforeByMinuteTime(-consumeThresholdTime));
		long produceBeforeOffset = consumer.getOffsetByTimestamp(DateUtils.getBeforeByMinuteTime(-produceThresholdTime));
		// 一小时前的lag
		long lag = consumer.getKafkaConsumerLag();

		// 默认正常
		String info = String.format("kafka topic %s 监控：%s 数据正常, 当前offset：%d, %d分钟前offset：%d, %d分钟前offset：%d",
				monitorTopicName,
				DateUtils.getBeforeByHourTime(0),
				currentOffset,
				consumeThresholdTime,consumeBeforeOffset,
				produceThresholdTime,produceBeforeOffset
		);

		String errorInfo  = "";
		boolean normal = true;

		// 异常情况则覆写info
		if(consumeBeforeOffset == -1){  // 没有半小时前的消息
			if(lag != 0){ // 没有半小时前的消息，且队列内有积压的lag未消费则报警NLP服务异常
				errorInfo = String.format("kafka topic %s 监控：%s consumer异常,持续%d分钟未消费数据",
						monitorTopicName,
						DateUtils.getBeforeByHourTime(0
						),consumeThresholdTime);
				normal = false;
			}
			// 如果没有两小时前的消息，直接报警数据推送异常
			if(produceBeforeOffset == -1){
				errorInfo  = String.format("kafka topic %s 监控：%s producer异常,持续%d分钟没有推送新消息",
						monitorTopicName,
						DateUtils.getBeforeByHourTime(0)
						,produceBeforeOffset);
				normal = false;
			}
		}

		if(normal){
			// 发送告警
			WeChatAlarmService.sendTestAlarm(info);
			logger.info(info);
		}else{
			WeChatAlarmService.sendAlarm(errorInfo);
			logger.error(errorInfo);
		}
	}
}
