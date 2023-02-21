package com.yuchen.etl.runtime.java.news.sink;

import com.alibaba.fastjson.JSONObject;
import com.yuchen.common.pub.ElasticSearchHelper;
import com.yuchen.etl.core.java.config.TaskConfig;
import com.yuchen.etl.core.java.es.EsDao;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.elasticsearch.client.RestHighLevelClient;

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
    private TaskConfig taskConfig;

    public EsShardIndexSink(TaskConfig taskConfig) {
        this.taskConfig = taskConfig;
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
        String id = value.getString("id");
//        JSONObject document = esDao.getDocumentById("yuchen_");
        //判断时间
//        document.get("");
//        esDao.insert(document);

    }

    @Override
    public void finish() throws Exception {
        super.finish();
    }
}
