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
 * @Author: xiaozhennan
 * @Date: 2023/2/20 13:53
 * @Package: com.yuchen.etl.runtime.java.news
 * @ClassName: EsShardIndexSink
 * @Description: ES分片索引Sink
 **/
public class EsShardIndexSink extends RichSinkFunction<JSONObject> {
    private EsDao esDao;
    private BaseConfig sinkConfig;
    private TaskConfig taskConfig;
    private List<String> sourceFields;
    private List<String> targetFields;

    private String indexType;
    private String dynamicField;

    public EsShardIndexSink(TaskConfig tConfig, BaseConfig sConfig) {
        this.taskConfig = tConfig;
        this.sinkConfig = sConfig;
        //数据中的字段名称
        this.sourceFields = sinkConfig.getListForSplit("news.output.source.fields", ",");
        //写入es的字段名称
        this.targetFields = sinkConfig.getListForSplit("news.output.target.fields", ",");
        //indextype
        this.indexType = sinkConfig.getStringVal("news.output.index.type");
        //index动态字段
        this.dynamicField = sinkConfig.getStringVal("news.output.index.dynamic");
        //目标字段和源字段必须一致
        CheckTool.checkArgument(sourceFields.size() == targetFields.size(), "源字段数量必须和目标写出字段一致.");


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
        JSONObject oldData = value.getJSONObject("data");
        //生成目标字段数据
        JSONObject data = new JSONObject();
        for (int i = 0; i < targetFields.size(); i++) {
            Object o = oldData.get(sourceFields.get(i));
            data.put(targetFields.get(i), o);
        }
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
