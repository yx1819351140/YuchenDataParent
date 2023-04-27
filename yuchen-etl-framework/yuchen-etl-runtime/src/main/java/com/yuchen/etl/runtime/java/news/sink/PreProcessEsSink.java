package com.yuchen.etl.runtime.java.news.sink;

import com.alibaba.fastjson.JSONObject;
import com.yuchen.common.pub.BaseConfig;
import com.yuchen.common.pub.ElasticSearchHelper;
import com.yuchen.common.utils.CheckTool;
import com.yuchen.etl.core.java.config.TaskConfig;
import com.yuchen.etl.core.java.es.EsDao;
import com.yuchen.etl.core.java.es.EsRecord;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.elasticsearch.client.RestHighLevelClient;

import java.util.List;
import java.util.Map;

/**
 * @Author: hqp
 * @Date: 2023/4/26 13:53
 * @Package: com.yuchen.etl.runtime.java.news
 * @ClassName: PreProcessEsSink
 * @Description: 用于将算法预处理的数据写入到ES中：主要是将targetFields写入到ES中
 **/
public class PreProcessEsSink extends RichSinkFunction<JSONObject> {
    private EsDao esDao;
    private BaseConfig sinkConfig;
    private TaskConfig taskConfig;
    private List<String> targetFields;

    private String indexType;
    private String dynamicField;

    public PreProcessEsSink(TaskConfig tConfig, BaseConfig sConfig) {
        this.taskConfig = tConfig;
        this.sinkConfig = sConfig;
        //数据中的字段名称
        //写入es的字段名称
        this.targetFields = sinkConfig.getListForSplit("news.output.target.fields", ",");
        //indextype
        this.indexType = sinkConfig.getStringVal("news.output.index.type");
        //index动态字段
        this.dynamicField = sinkConfig.getStringVal("news.output.index.dynamic");

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
    public void close() throws Exception {
    }

    @Override
    public void invoke(JSONObject value, Context context) throws Exception {
        String indexName = value.getString(dynamicField);
        Boolean isUpdate = value.getBoolean("isUpdate");
        JSONObject data = value.getJSONObject("data");

        if(isUpdate) {
            updateEs(indexName,indexType, data);
        } else {
            insertEs(indexName, indexType, data);
        }
    }


    private void updateEs(String indexName, String indexType, JSONObject value) {
        //直接插入
        EsRecord record = EsRecord.Builder
                .anEsRecord()
                .id(value.getString("id"))
                .data(value)
                .indexName(indexName)
                .indexType(indexType)
                .build();
        esDao.update(record);
    }

    /**
     * 插入ES
     *
     * @param indexType
     * @param value
     */
    private void insertEs(String indexName, String indexType, JSONObject value) {
        //直接插入
        EsRecord record = EsRecord.Builder
                .anEsRecord()
                .id(value.getString("id"))
                .data(value)
                .indexName(indexName)
                .indexType(indexType)
                .build();
        esDao.insert(record);
    }

    @Override
    public void finish() throws Exception {

    }
}
