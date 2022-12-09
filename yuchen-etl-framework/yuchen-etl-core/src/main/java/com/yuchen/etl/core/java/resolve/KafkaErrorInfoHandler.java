package com.yuchen.etl.core.java.resolve;

import org.apache.kafka.clients.producer.KafkaProducer;

/**
 * @Author: xiaozhennan
 * @Date: 2022/12/9 17:10
 * @Package: com.yuchen.etl.core.java.resolve
 * @ClassName: KafkaErrorInfoHandler
 * @Description: 错误消息发送Kafka
 **/
public class KafkaErrorInfoHandler extends AbstractErrorInfoCollector<ErrorInfoCollectorConfig> {
    KafkaProducer producer;
    @Override
    public void init(ErrorInfoCollectorConfig config) {
        System.out.println("初始化kafka");
        producer = new KafkaProducer(null);
    }

    @Override
    public void handler(ErrorInfo info) {
        System.out.println(info.getInfo());
//        producer.send(info);
    }

    @Override
    public void stop() {
        System.out.println("关闭kafka");
    }


}
