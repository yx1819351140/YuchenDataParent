package com.yuchen.etl.runtime.java.news.operator;

import com.alibaba.fastjson.JSONObject;
import com.yuchen.common.pub.BaseConfig;
import com.yuchen.etl.core.java.config.TaskConfig;
import com.yuchen.etl.core.java.redis.JedisPoolFactory;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;
import com.yuchen.common.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.params.SetParams;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * @Author: xiaozhennan
 * @Date: 2023/3/23 15:29
 * @Package: com.yuchen.etl.runtime.java.news.operator
 * @ClassName: OriginNewsProcessOperator
 * @Description:
 **/
public class OriginNewsProcessOperator extends ProcessFunction<JSONObject, JSONObject> {

    private static final Logger logger = LoggerFactory.getLogger(OriginNewsProcessOperator.class);
    private final String indexPrefix;
    private final String indexFormat;
    private final String indexAlias;
    private final String indexType;
    private final BaseConfig baseConfig;
    private final BaseConfig redisConfig;
    private TaskConfig taskConfig;
    private JedisPool jedisPool;

    private static final String REDIS_DUPLICATE_PREFIX = "origin-";

    public OriginNewsProcessOperator(TaskConfig taskConfig) {
        this.taskConfig = taskConfig;
        this.baseConfig = taskConfig.getBaseConfig("origin_news");
        //index前缀
        this.indexPrefix = baseConfig.getStringVal("news.output.index.prefix");
        //索引名称后缀格式
        this.indexFormat = baseConfig.getStringVal("news.output.index.format");
        //索引别名
        this.indexAlias = baseConfig.getStringVal("news.output.index.alias");
        //索引类型
        this.indexType = baseConfig.getStringVal("news.output.index.type");
        this.redisConfig = taskConfig.getBaseConfig("redisConfig");
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        this.jedisPool = JedisPoolFactory.createJedisPool(redisConfig.getStringVal("redisHost"), redisConfig.getIntVal("redisPort", 3306));
    }

    /**
     * 这个方法是生成动态索引名称
     *
     * @param o 时间字段
     * @return 返回生成后的索引名称
     * 这个方法可能会有兼容性问题,需要特殊处理和考虑.
     */
    private String generateDynIndexName(Object o) {
        String indexName;
        String indexSuffix = null;
        SimpleDateFormat outSdf = new SimpleDateFormat(indexFormat);
        SimpleDateFormat inSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (o instanceof Long) {
            indexSuffix = outSdf.format(new Date((Long) o));
        }
        if (o instanceof String) {
            //如果是string
            Date date = new Date();
            try {
                //TODO 这里如果数据中的date_str是不规范无法解析的,就会使用当前时间
                date = inSdf.parse(o.toString());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            indexSuffix = outSdf.format(date);
        }
        indexName = indexPrefix + "_" + indexSuffix;
        return indexName;
    }


    @Override
    public void close() throws Exception {

    }


    @Override
    public void processElement(JSONObject value, ProcessFunction<JSONObject, JSONObject>.Context context, Collector<JSONObject> collector) throws Exception {
        // 添加data的update_time字段,便于后续数据流的trouble shooting
        JSONObject data = value.getJSONObject("data");
        String pubTime = data.getString("pub_time");
        //全局去重
        try (Jedis jedis = jedisPool.getResource()) {
            String id = data.getString("id");
            String redisKey = REDIS_DUPLICATE_PREFIX + id;
            Boolean exists = jedis.exists(redisKey);
            if (exists) {
                logger.debug("Redis去重命中, 重复的数据ID: {}", redisKey);
                return;
            } else {
                SetParams setParams = SetParams.setParams().ex(60 * 60 * 24 * 3L);
                jedis.set(redisKey, pubTime, setParams);
            }
        } catch (Exception e) {
            logger.error("新闻数据Redis去重时错误, Redis出现问题.", e);
            return;
        }

        data.put("update_time", DateUtils.getDateStrYMDHMS(new Date()));
        String indexName = generateDynIndexName(pubTime);
        value.put("isUpdate", true);
        value.put("indexName", indexName);
        value.put("data", data);
        collector.collect(value);
    }
}