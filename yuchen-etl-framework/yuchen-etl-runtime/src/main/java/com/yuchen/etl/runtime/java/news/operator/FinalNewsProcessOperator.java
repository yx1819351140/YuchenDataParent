package com.yuchen.etl.runtime.java.news.operator;

import com.alibaba.fastjson.JSONObject;
import com.yuchen.common.pub.BaseConfig;
import com.yuchen.common.pub.ElasticSearchHelper;
import com.yuchen.etl.core.java.config.TaskConfig;
import com.yuchen.etl.core.java.es.EsDao;
import com.yuchen.etl.core.java.es.EsRecord;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.configuration.Configuration;
import org.elasticsearch.client.RestHighLevelClient;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * @Author: xiaozhennan
 * @Date: 2023/3/20 11:14
 * @Package: com.yuchen.etl.runtime.java.news.operator
 * @ClassName: FinalNewsProcessOperator
 * @Description: 新闻数据通用处理算子
 **/
public class FinalNewsProcessOperator extends RichMapFunction<JSONObject, JSONObject> {

    private final String indexPrefix;
    private final String indexFormat;
    private final String indexAlias;
    private final String indexType;
    private final BaseConfig baseConfig;
    private TaskConfig taskConfig;
    private EsDao esDao;

    public FinalNewsProcessOperator(TaskConfig taskConfig) {
        this.taskConfig = taskConfig;
        this.baseConfig = this.taskConfig.getBaseConfig("yuchen_news");

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
        Map<String, Object> esConfig = taskConfig.getMap("esConfig");
        ElasticSearchHelper.config(esConfig);
        ElasticSearchHelper esHelper = ElasticSearchHelper.getInstance();
        RestHighLevelClient esClient = esHelper.getEsClient();
        esDao = new EsDao(esClient);
    }

    @Override
    public JSONObject map(JSONObject value) throws Exception {
        //根据ID查询Es获取是否存在
        JSONObject data = value.getJSONObject("data");
        String id = data.getString("id");
        EsRecord record = esDao.searchById(indexAlias, indexType, id);
        boolean isUpdate = false;
        if (record != null) {
            isUpdate = true;
        } else {
            String simHashId = simHashExists(data);
            if (StringUtils.isNotBlank(simHashId)) {
                record = esDao.searchById(indexAlias, indexType, simHashId);
                if (record != null) {
                    isUpdate = true;
                }
            }
        }


        if (isUpdate == true && record != null) {
            //如果数据重复,更新数据的report_media字段, 添加报道媒体,添加isUpdate=true
        }

        String indexName = null;
        if (record == null) {
            //新数据, 直接生成indexName
            Object pubTime = data.get("pub_time");
            indexName = generateDynIndexName(pubTime);
        } else {
            indexName = record.getIndexName();
        }

        //如果媒体不存在,就不属于final的数据,不需要发送给算法和写入es
        value.put("isFinal", data.get("media") == null ? true : false);
        value.put("isUpdate", isUpdate);
        value.put("data", data);
        value.put("indexName", indexName);
        //生成indexName,如果数据已存在,则使用已经存在的indexName, 不存在则根据数据生成indexName
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
