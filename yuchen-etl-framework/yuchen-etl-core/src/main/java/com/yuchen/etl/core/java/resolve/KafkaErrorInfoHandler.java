package com.yuchen.etl.core.java.resolve;

import com.alibaba.fastjson.JSONObject;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

/**
 * @Author: xiaozhennan
 * @Date: 2022/12/9 17:10
 * @Package: com.yuchen.etl.core.java.resolve
 * @ClassName: KafkaErrorInfoHandler
 * @Description: 错误消息发送Kafka
 **/
public class KafkaErrorInfoHandler extends AbstractErrorInfoCollector<ErrorInfoCollectorConfig> {
    private static final Logger logger = LoggerFactory.getLogger(KafkaErrorInfoHandler.class);
    KafkaProducer<Object, Object> producer;

    String kafkaTopic;
    @Override
    public void init(ErrorInfoCollectorConfig config) {
        try {
            kafkaTopic = config.getStringVal("kafkaTopic");
            producer = new KafkaProducer<>(config);
            logger.info("初始化kafka完成");
        } catch (Exception e) {
            logger.error("初始化kafka失败", e);
        }

    }

    @Override
    public void handler(LogInfo info) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", info.getType().getCode());
        jsonObject.put("level", info.getLevel().getCode());
        jsonObject.put("source", info.getSource().getCode());
        jsonObject.put("model", info.getModel());
        jsonObject.put("content", info.getContent());
        jsonObject.put("logTimestamp", info.getLogTimestamp());
        // 传入报错信息
        Throwable error = info.getError();
        if (error == null) {
            jsonObject.put("error", "");
        } else {
            jsonObject.put("error", info.getError().getMessage());
        }
        try {
            producer.send(new ProducerRecord<>(this.kafkaTopic, jsonObject.toJSONString()));
            logger.info("发送日志成功:" + jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public void stop() {
        try {
            Thread.sleep(5000L);
        } catch (InterruptedException e) {
            logger.error("Kafka停机前等待一段时间, 发生中断异常!", e);
        }
        producer.close();
        logger.info("关闭kafka成功");
    }

}
