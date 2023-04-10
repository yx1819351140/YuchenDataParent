package com.yuchen.etl.runtime.java.news.operator;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yuchen.common.pub.BaseConfig;
import com.yuchen.common.pub.ElasticSearchHelper;
import com.yuchen.common.pub.HttpClientResult;
import com.yuchen.common.pub.HttpClientUtil;
import com.yuchen.etl.core.java.config.TaskConfig;
import com.yuchen.etl.core.java.es.EsDao;
import com.yuchen.etl.core.java.es.EsRecord;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.configuration.Configuration;
import org.elasticsearch.client.RestHighLevelClient;

import com.yuchen.common.utils.DateUtils;

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
public class OriginNewsProcessOperator extends RichMapFunction<JSONObject, JSONObject> {

    private final String indexPrefix;
    private final String indexFormat;
    private final String indexAlias;
    private final String indexType;
    private final BaseConfig baseConfig;
    private TaskConfig taskConfig;

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
    }

    @Override
    public void open(Configuration parameters) throws Exception {

    }

    @Override
    public JSONObject map(JSONObject value) throws Exception {
        // 添加data的update_time字段,便于后续数据流的trouble shooting
        JSONObject data = value.getJSONObject("data");
        data.put("update_time", DateUtils.getDateStrYMDHMS(new Date()));
        String pubTime = data.getString("pub_time");
        String indexName = generateDynIndexName(pubTime); //实现生成索引名
        //生成origin_news_xxxxx
        value.put("isUpdate", true);
        value.put("indexName", indexName);
        return value;
    }

    /**
     * simHash去重实现
     *
     * @param data 传入的是数据
     * @return 如果重复, 则返回重复的id, 通过redis进行simhash去重
     */
    private String simHashExists(JSONObject data) {

        return null;
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


}