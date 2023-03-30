package com.yuchen.etl.runtime.java.news.operator;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yuchen.common.pub.BaseConfig;
import com.yuchen.common.pub.ElasticSearchHelper;
import com.yuchen.etl.core.java.config.TaskConfig;
import com.yuchen.etl.core.java.es.EsDao;
import com.yuchen.etl.core.java.es.EsRecord;
import com.yuchen.common.utils.SimHashUtil;
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

    /**
     * 基于原始文章的初始ETL,需要先进行一下预处理将title_id作为id,然后进行: ①标题MD5去重, ②通过simHash语义去重, ③媒体合并
     * @param value
     * @return
     * @throws Exception
     */
    @Override
    public JSONObject map(JSONObject value) throws Exception {
        boolean isUpdate = false; // 用于表示是否进行媒体合并的ES更新
        // 获取相关数据和变量
        JSONObject data = value.getJSONObject("data");
        String id = data.getString("id");
        String title_id = data.getString("title_id");
        String simHashTitleId = getSimHashTitleId(data);
        // 两次点查ES
        EsRecord record = esDao.searchById(indexAlias, indexType, title_id);
        EsRecord simHashRecord = esDao.searchById(indexAlias, indexType, simHashTitleId);
        // 标题去重：将title_id作为id
        data.put("origin_id",id);
        data.put("id",title_id);

        // 媒体合并判断
        if (record != null) {
            isUpdate = true;
        } else {
            if (StringUtils.isNotBlank(simHashTitleId)) {
                if (simHashRecord != null) {
                    isUpdate = true;
                }
            }
        }

        // 媒体合并到related_media,更新ES合并后的媒体
        if (isUpdate) {
            JSONArray combineMedia;
            if (record != null){
                // record媒体合并到data
                combineMedia = combineMedia(data, record.getData());
            }else{
                // simHashRecord媒体合并到data
                combineMedia = combineMedia(data, simHashRecord.getData());
            }
            value.put("related_media", combineMedia);
        }

        // 如果媒体不存在,就不属于final的数据,不需要发送给算法和写入es
        String indexName;
        if (record == null) {
            // 新数据, 直接生成indexName
            Object pubTime = data.get("pub_time");
            indexName = generateDynIndexName(pubTime);
        } else {
            indexName = record.getIndexName();
        }

        // 如果媒体不存在,就不属于final的数据,不需要发送给算法和写入es
        value.put("isFinal", data.get("media") == null);
        value.put("isUpdate", isUpdate);
        value.put("data", data);
        value.put("indexName", indexName);
        // 生成indexName,如果数据已存在,则使用已经存在的indexName, 不存在则根据数据生成indexName
        return value;
    }

    private JSONArray combineMedia(JSONObject data,JSONObject recordData) {
        JSONArray newRelatedMedia = null;
        if(recordData.containsKey("related_media")){
            newRelatedMedia = recordData.getJSONArray("related_media");
            newRelatedMedia.addAll(data.getJSONArray("related_media"));
        }
        return newRelatedMedia;
    }

    /**
     * simHash去重实现
     * @param data 传入的是数据
     * @return 如果重复, 则返回重复的id, 通过redis进行simhash去重
     */
    private String getSimHashTitleId(JSONObject data) {
        String content = data.getString("content");
        String titleId = data.getString("title_id");

        return new SimHashUtil().isDataRepeat(content, titleId);
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
