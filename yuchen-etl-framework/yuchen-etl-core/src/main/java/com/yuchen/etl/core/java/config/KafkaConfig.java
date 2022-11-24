package com.yuchen.etl.core.java.config;

import com.yuchen.common.pub.AbstractConfig;

/**
 * @Author: xiaozhennan
 * @Date: 2022/11/23 17:56
 * @Package: com.yuchen.etl.core.java.config
 * @ClassName: KafkaConfig
 * @Description:
 **/
public class KafkaConfig extends AbstractConfig {


    public String getBrokerList() {
        return this.getStringVal("kafka.broker");
    }
}
