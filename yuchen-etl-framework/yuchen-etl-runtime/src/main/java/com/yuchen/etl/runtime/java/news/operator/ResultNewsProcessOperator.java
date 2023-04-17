package com.yuchen.etl.runtime.java.news.operator;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yuchen.common.pub.BaseConfig;
import com.yuchen.common.pub.ElasticSearchHelper;
import com.yuchen.common.utils.DateUtils;
import com.yuchen.common.utils.SimHashUtil;
import com.yuchen.etl.core.java.config.TaskConfig;
import com.yuchen.etl.core.java.es.EsDao;
import com.yuchen.etl.core.java.es.EsRecord;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.elasticsearch.client.RestHighLevelClient;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: xiaozhennan
 * @Date: 2023/3/20 11:14
 * @Package: com.yuchen.etl.runtime.java.news.operator
 * @ClassName: FinalNewsProcessOperator
 * @Description: 新闻数据通用处理算子
 **/
public class ResultNewsProcessOperator extends RichMapFunction<JSONObject, JSONObject> {

    private final String indexPrefix;
    private final String indexFormat;
    private final String indexAlias;
    private final String indexType;
    private final BaseConfig baseConfig;
    private TaskConfig taskConfig;
    private EsDao esDao;

    public ResultNewsProcessOperator(TaskConfig taskConfig) {
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
        // 获取相关数据和变量
        JSONObject data = value.getJSONObject("data");

        String titleId = data.getString("title_id");
        // 如果媒体不存在,就不属于final的数据,不需要发送给算法和写入es
        EsRecord record = null;
        try {
            record = esDao.searchById(indexAlias, indexType, titleId);
        } catch (Exception e) {
            System.out.println("ES查询异常");
            e.printStackTrace();
        }
        String indexName;
        if (record == null) {
            // 新数据, 直接生成indexName
            Object pubTime = data.get("pub_time");
            indexName = generateDynIndexName(pubTime);
        } else {
            indexName = record.getIndexName();
        }
        value.put("indexName", indexName);

        data.put("update_time", DateUtils.getDateStrYMDHMS(new Date()));
        value.put("isUpdate", true);
        value.put("data", data);
        // 生成indexName,如果数据已存在,则使用已经存在的indexName, 不存在则根据数据生成indexName
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
