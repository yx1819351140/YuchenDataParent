package com.yuchen.etl.runtime.java.news.operator;

import com.alibaba.fastjson.JSONObject;
import com.yuchen.common.pub.BaseConfig;
import com.yuchen.common.pub.ElasticSearchHelper;
import com.yuchen.etl.core.java.config.TaskConfig;
import com.yuchen.etl.core.java.es.EsDao;
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
public class CoEvents2EsProcessOperator extends RichMapFunction<JSONObject, JSONObject> {

    private final String indexPrefix;
    private final String indexFormat;
    private final String indexAlias;
    private final String indexType;
    private final BaseConfig baseConfig;
    private TaskConfig taskConfig;
    private EsDao esDao;

    public CoEvents2EsProcessOperator(TaskConfig taskConfig) {
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
     * 基于原始文章的初始ETL,需要先进行一下预处理将title_id作为id,然后进行: ①标题MD5去重, ②通过simHash语义去重, ③媒体合并, ④字段补充
     * @param value
     * @return
     * @throws Exception
     */
    @Override
    public JSONObject map(JSONObject value) throws Exception {
        JSONObject newData = new JSONObject();
        JSONObject data = value.getJSONObject("data");
        data.getJSONObject("data").remove("avg_embedding");
        String id = data.getJSONObject("data").getString("_id");
        String createTime = data.getString("update_time");
        String updateTime = data.getJSONObject("data").getString("last_update_time");
        newData.put("id", id);
        newData.put("raw_co_events_result", data.toJSONString()); // 原始nlp结果存成一个json字符串
        newData.put("create_time", createTime);
        newData.put("update_time", updateTime);

        String s = generateDynIndexName(updateTime);
        if(s.equals("yuchen_co_events_null")){
            value.put("isUpdate", false);
        }else {
            value.put("isUpdate", true);
        }

        value.put("indexName", s);
        value.put("data", newData);
        return value;
    }

    // 去重测试
    public static void main(String[] args) {
        String dataStr = "{\"report_media\":[{\"mediaLang\":\"en\",\"countryCode\":\"US\",\"domain\":\"businessinsider.com\",\"mediaSector\":\"\",\"countryName\":\"U.S.A\",\"countryNameZh\":\"美国\",\"reportUrl\":\"https://www.businessinsider.com/big-banks-analyzing-where-to-cut-costs-2023-3\",\"mediaNameZh\":\"商业内幕\",\"countryId\":\"158\",\"mediaName\":\"Business Insider\",\"reportTime\":\"2023-03-28 12:02:00\"}]}";
        String recordDataStr = "{\"report_media\":[{\"mediaLang\":\"en\",\"countryCode\":\"US\",\"domain\":\"businessinsider.com\",\"mediaSector\":\"\",\"countryName\":\"U.S.A\",\"countryNameZh\":\"美国\",\"reportUrl\":\"https://www.businessinsider.com/big-banks-analyzing-where-to-cut-costs-2023-3\",\"mediaNameZh\":\"商业内幕\",\"countryId\":\"158\",\"mediaName\":\"Business Insider\",\"reportTime\":\"2023-03-28 12:02:00\"}]}";
//        String recordDataStr = "{\"report_media\":[{\"mediaLang\":\"en\",\"countryCode\":\"US\",\"domain\":\"businessinsider.com\",\"mediaSector\":\"\",\"countryName\":\"U.S.A\",\"countryNameZh\":\"美国\",\"reportUrl\":\"https://www.businessinsider.com/big-banks-analyzing-where-to-cut-costs-2023-4\",\"mediaNameZh\":\"商业内幕\",\"countryId\":\"158\",\"mediaName\":\"Business Insider\",\"reportTime\":\"2023-03-28 12:02:00\"}]}";
        JSONObject data = JSONObject.parseObject(dataStr);
        JSONObject recordData = JSONObject.parseObject(recordDataStr);
        System.out.println("---------------");
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
