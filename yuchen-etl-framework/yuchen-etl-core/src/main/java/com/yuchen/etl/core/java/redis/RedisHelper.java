package com.yuchen.etl.core.java.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.util.function.Consumer;

public class RedisHelper {

    private RedisHelper() {
    }
    public static void execute(JedisPool pool, Consumer<Jedis> consumer) {
        Jedis jedis = pool.getResource();
        try {
            consumer.accept(jedis);
        } catch (JedisConnectionException e) {
            // 连接异常，重试一次
            consumer.accept(jedis);
        } finally {
            jedis.close();
        }
    }
}