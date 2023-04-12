package com.yuchen.etl.runtime.java.news.source;

import com.alibaba.fastjson.JSONObject;
import com.yuchen.etl.core.java.config.TaskConfig;
import com.yuchen.etl.core.java.redis.JedisPoolFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.functions.RichFilterFunction;
import org.apache.flink.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisPool;

import java.util.Map;

/**
 * @Author: xiaozhennan
 * @Date: 2023/4/12 15:47
 * @Package: com.yuchen.etl.runtime.java.news.source
 * @ClassName: Gdelt2KafkaFilter
 * @Description: Hbase抽取Gdelt数据时过滤功能
 **/
public class Gdelt2KafkaFilter extends RichFilterFunction<JSONObject> {
    private TaskConfig taskConfig;
    private JedisPool jedisPool;
    private static final Logger logger = LoggerFactory.getLogger(Gdelt2KafkaFilter.class);

    public Gdelt2KafkaFilter(TaskConfig taskConfig) {
        this.taskConfig = taskConfig;
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        Map<String, Object> redisConfig = taskConfig.getMap("redisConfig");
        String host = (String) redisConfig.getOrDefault("redis.host", "127.0.0.1");
        Integer port = (Integer) redisConfig.getOrDefault("redis.port", 6379);
        jedisPool = JedisPoolFactory.createJedisPool(host, port);
        logger.info("Gdelt to Kafka Filter 初始化完成");
    }

    @Override
    public boolean filter(JSONObject value) throws Exception {
        String lang = value.getString("lang");
        String title = value.getString("title");
        if (StringUtils.isBlank(title)) {
            return false;
        }
        if (StringUtils.isBlank(lang)) {
            return false;
        }
        if ("en".equalsIgnoreCase(lang)) {
            return true;
        }
        if ("zh".equalsIgnoreCase(lang)) {
            return true;
        }
        if ("zh-TW".equalsIgnoreCase(lang)) {
            return true;
        }

        return false;
    }
}
