package com.yuchen.etl.runtime.java.news.sink;

import com.alibaba.fastjson.JSONObject;
import com.yuchen.common.pub.ElasticSearchHelper;
import com.yuchen.common.utils.CheckTool;
import com.yuchen.etl.core.java.config.TaskConfig;
import com.yuchen.etl.core.java.es.EsDao;
import com.yuchen.etl.core.java.es.EsRecord;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.elasticsearch.client.RestHighLevelClient;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
    private TaskConfig taskConfig;
    private List<String> sourceFields;
    private List<String> targetFields;

    private String indexFormat;
    private String indexPrefix;
    private String indexType;
    private String dynamicField;
    private String indexAlias;

    public EsShardIndexSink(TaskConfig taskConfig) {
        this.taskConfig = taskConfig;
        //数据中的字段名称
        this.sourceFields = taskConfig.getListForSplit("news.output.source.fields", ",");
        //写入es的字段名称
        this.targetFields = taskConfig.getListForSplit("news.output.target.fields", ",");
        //index前缀
        this.indexPrefix = taskConfig.getStringVal("news.output.index.prefix");
        //indextype
        this.indexType = taskConfig.getStringVal("news.output.index.type");
        //index动态字段
        this.dynamicField = taskConfig.getStringVal("news.output.index.dynamic");
        //索引名称后缀格式
        this.indexFormat = taskConfig.getStringVal("news.output.index.format");
        //索引别名
        this.indexAlias = taskConfig.getStringVal("news.output.index.alias");
        //目标字段和源字段必须一致
        CheckTool.checkArgument(sourceFields.size() != targetFields.size(), "源字段数量必须和目标写出字段一致.");

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
        JSONObject data = new JSONObject();
        for (int i = 0; i <= targetFields.size(); i++) {
            Object o = value.get(sourceFields.get(i));
            data.put(targetFields.get(i), o);
        }
        //生成索引名称
        String indexName = generateDynIndexName(value.get(dynamicField));

        //根据ID查询索引
        if (StringUtils.isNotBlank(indexName)) {
            EsRecord record = esDao.searchById(indexAlias, indexType, id);
            //如果不存在则直接插入
            if (record == null) {
                insertEs(value);
            } else {
                //如果存在则合并更新操作
                updateEs(record, value, data);
            }
        }
    }

    /**
     * @param record
     * @param oldValue
     * @param newValue
     */
    private void updateEs(EsRecord record, JSONObject oldValue, JSONObject newValue) {
        JSONObject data = record.getData();
        //需要判断什么字段需要更新,什么字段特殊处理
    }

    /**
     * 插入ES
     *
     * @param value
     */
    private void insertEs(JSONObject value) {
        //直接插入
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
        SimpleDateFormat sdf = new SimpleDateFormat(indexFormat);
        if (o instanceof Long) {
            indexSuffix = sdf.format(new Date((Long) o));
        }
        if (o instanceof String) {
            //如果是string
            Date date = new Date();
            try {
                //TODO 这里如果数据中的date_str是不规范无法解析的,就会使用当前时间
                date = sdf.parse(o.toString());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            indexSuffix = sdf.format(date);
        }
        indexName = indexPrefix + "_" + indexSuffix;
        return indexName;
    }

    @Override
    public void finish() throws Exception {
        super.finish();
    }
}
