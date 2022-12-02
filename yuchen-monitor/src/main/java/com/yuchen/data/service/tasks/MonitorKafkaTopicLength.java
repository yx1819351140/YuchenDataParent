package com.yuchen.data.service.tasks;


import com.yuchen.data.service.config.MonitorKafkaConfig;
import com.yuchen.data.service.config.MonitorKafkaProperties;
import com.yuchen.data.service.utils.DateUtils;
import com.yuchen.data.service.utils.alarm.WeChatAlarmService;
import com.yuchen.data.service.utils.kafka.yuchenKafkaConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class MonitorKafkaTopicLength {
	@Lazy
	@Autowired
	MonitorKafkaProperties kafkaMonitor;

	@Lazy
	@Autowired
	MonitorKafkaConfig monitorConfig;

	@Scheduled(cron="${monitor.kafka.globalEvent.cron}")
	public void task() throws IOException {
		org.apache.kafka.clients.consumer.KafkaConsumer kafkaConsumer = monitorConfig.getKafkaConsumer();

		String monitorTopicName = kafkaMonitor.getGlobalEvent().getTopic();
		System.out.println(monitorTopicName);
		String monitorGroupName = kafkaMonitor.getGlobalEvent().getGroup();
		Integer nlpThresholdTime = kafkaMonitor.getGlobalEvent().getNlpThresholdTime();
		Integer bigDataThresholdTime = kafkaMonitor.getGlobalEvent().getBigDataThresholdTime();

		yuchenKafkaConsumer consumer = new yuchenKafkaConsumer(kafkaConsumer,monitorTopicName,monitorGroupName);

		long currentOffset = consumer.getConsumerOffset();

		// 一小时前的offset
		long nlpBeforeOffset = consumer.getOffsetByTimestamp(DateUtils.getBeforeByMinuteTime(-nlpThresholdTime));
		long bigDataBeforeOffset = consumer.getOffsetByTimestamp(DateUtils.getBeforeByMinuteTime(-bigDataThresholdTime));
		// 一小时前的lag
		long lag = consumer.getKafkaConsumerLag();

		// 默认正常
		String info = String.format("kafka监控：%s 数据正常, 当前offset：%d, %d分钟前offset：%d, %d分钟前offset：%d",
				DateUtils.getBeforeByHourTime(0),
				currentOffset,
				nlpThresholdTime,nlpBeforeOffset,
				bigDataThresholdTime,bigDataBeforeOffset
		);

		String errorInfo  = "";
		boolean normal = true;

		// 异常情况则覆写info
		if(nlpBeforeOffset == -1){  // 没有半小时前的消息
			if(lag != 0){ // 没有半小时前的消息，且队列内有积压的lag未消费则报警NLP服务异常
				errorInfo = String.format("kafka监控：%s NLP异常,持续%d分钟未消费数据",DateUtils.getBeforeByHourTime(0),nlpThresholdTime);
				normal = false;
			}
			// 如果没有两小时前的消息，直接报警数据推送异常
			if(bigDataBeforeOffset == -1){
				errorInfo  = String.format("kafka监控：%s 推送异常,持续%d分钟没有推送新消息",DateUtils.getBeforeByHourTime(0),bigDataThresholdTime);
				normal = false;
			}
		}

		if(normal){
			// 发送告警
			WeChatAlarmService.sendTestAlarm(info);
			System.out.println(info);
		}else{
			WeChatAlarmService.sendAlarm(errorInfo);
		}


	}
}
