package com.yuchen.common.utils;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

public class JedisClusterUtil implements Serializable {

    public static Jedis getNewsConnection() {
        // redis参数，需要提取到配置文件
        Jedis jedis = new Jedis("192.168.12.222", 6379);
        jedis.select(1);
        return jedis;
    }
}
