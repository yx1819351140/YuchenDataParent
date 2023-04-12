package com.yuchen.etl.core.java.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: xiaozhennan
 * @Date: 2023/4/12 9:19
 * @Package: com.yuchen.etl.core.java.redis
 * @ClassName: JedisPoolFactory
 * @Description: Jedis工厂类
 **/
public class JedisPoolFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(JedisPoolFactory.class);
    private static volatile JedisPoolFactory instance;
    private Map<String, JedisPool> jedisPoolMap = new ConcurrentHashMap<>();

    private JedisPoolFactory() {
    }

    private static JedisPoolFactory getInstance() {
        if (instance == null) {
            synchronized (JedisPoolFactory.class) {
                if (instance == null) {
                    instance = new JedisPoolFactory();
                }
            }
        }
        return instance;
    }

    public static JedisPool getPool(String host, int port) {
        String key = host + ":" + port;
        JedisPoolFactory holder = JedisPoolFactory.getInstance();
        JedisPool jedisPool = holder.jedisPoolMap.get(key);
        if (jedisPool != null) {
            return jedisPool;
        } else {
            return holder.createPool(host, port);
        }
    }

    public static JedisPool createJedisPool(String host, int port) {
        return getPool(host, port);
    }

    private JedisPool createPool(String host, int port) {
        String key = host + ":" + port;
        JedisPool jedisPool = this.jedisPoolMap.get(key);
        if (jedisPool == null) {
            JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
            jedisPoolConfig.setMaxTotal(200);
            jedisPoolConfig.setMaxIdle(10);
            jedisPoolConfig.setMaxWaitMillis(60 * 1000);
            jedisPoolConfig.setTestOnBorrow(true);
            jedisPool = new JedisPool(jedisPoolConfig, host, port);
            this.jedisPoolMap.put(key, jedisPool);
        }
        return jedisPoolMap.get(key);
    }

    public static void main(String[] args) {
        JedisPool pool = JedisPoolFactory.getPool("127.0.0.1", 6379);
        RedisHelper.execute(pool, jedis -> {

        });
    }
}
