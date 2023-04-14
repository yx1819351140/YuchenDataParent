package com.yuchen.etl.runtime.java.news.operator;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yuchen.common.pub.BaseConfig;
import com.yuchen.common.pub.ElasticSearchHelper;
import com.yuchen.common.utils.DateUtils;
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
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
     * 基于原始文章的初始ETL,需要先进行一下预处理将title_id作为id,然后进行: ①标题MD5去重, ②通过simHash语义去重, ③媒体合并, ④字段补充
     * @param value
     * @return
     * @throws Exception
     */
    @Override
    public JSONObject map(JSONObject value) throws Exception {
        boolean isUpdate = false; // 用于表示是否进行媒体合并的ES更新
        String duplicateId = "";
        // 获取相关数据和变量
        JSONObject data = value.getJSONObject("data");
        String id = data.getString("id");
        String title_id = data.getString("title_id");
        // 两次点查ES
        EsRecord record = null;
        try {
            record = esDao.searchById(indexAlias, indexType, title_id);
        } catch (Exception e) {
            System.out.println("ES查询异常");
            e.printStackTrace();
        }

        // 媒体合并判断
        if (record != null) {
            isUpdate = true;
        } else {
            String simHashTitleId = getSimHashTitleId(data);
            if (StringUtils.isNotBlank(simHashTitleId)) {
                EsRecord simRecord = esDao.searchById(indexAlias, indexType, simHashTitleId);
                if (simRecord != null) {
                    record = simRecord;
                    isUpdate = true;
                }
            }
        }

        // 媒体合并到related_media,更新ES合并后的媒体
        if (isUpdate && record != null) {
            duplicateId = record.getId();
            value.put("report_media", combineMedia(data, record.getData()));
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

        // 字段补充
        data.put("origin_id",id);
        data.put("id",title_id);
        data.put("is_duplicate", isUpdate); // 是否重复
        data.put("duplicate_id", duplicateId); // 重复的id
        value.put("data", data);

        // 如果媒体不存在,就不属于final的数据,不需要发送给算法和写入es
        value.put("isUpdate", isUpdate);
        value.put("indexName", indexName);
        data.put("update_time", DateUtils.getDateStrYMDHMS(new Date()));
        // 生成indexName,如果数据已存在,则使用已经存在的indexName, 不存在则根据数据生成indexName
        return value;
    }

    private static JSONArray combineMedia(JSONObject data, JSONObject recordData) {
        JSONArray originMedia = data.getJSONArray("report_media");
        JSONArray recordMedia = recordData.getJSONArray("report_media");
        Map<String,JSONObject> map = new HashMap<>();
        JSONArray resultRelatedMedia = new JSONArray();

        // 使用Map存储媒体, key为媒体的origin_url, value为媒体的JSONObject,用于去重
        for (int i = 0; i < recordMedia.size(); i++) {
            JSONObject jsonObject = recordMedia.getJSONObject(i);
            map.put(jsonObject.getString("reportUrl"), jsonObject);
        }

        // 使用的map的put方法,如果key相同,则会覆盖value也就是会更新媒体的信息;key不同,则会新增
        // 如需只更新pub_time字段,在此处扩展逻辑即可
        // 实现了媒体的去重更新或新增
        for (int i = 0; i < originMedia.size(); i++) {
            JSONObject jsonObject = originMedia.getJSONObject(i);
            map.put(jsonObject.getString("reportUrl"), jsonObject);
        }

        // 将map中合并完的媒体存入到allRelatedMedia中
        for (Map.Entry<String, JSONObject> entry : map.entrySet()) {
            resultRelatedMedia.add(entry.getValue());
        }

        // recordData.put("report_media", recordData.getJSONArray("report_media"));
        // O(1)的复杂度点查ES的媒体,如果存在,则不加入,如果不存在,则加入
//        if(recordData.containsKey("report_media")){
//            JSONArray recordMedia = recordData.getJSONArray("report_media");
//            recordMedia.addAll(originMedia); // 合并
//            // 将元素构建成MediaInfo对象,然后根据MediaInfo对象的hashCode和equals方法去重并存入结果集
//            for (int i = 0; i < recordMedia.size(); i++) {
//                JSONObject jsonObject = recordMedia.getJSONObject(i);
//                MediaInfo mediaInfo = new MediaInfo();
//                // 设置媒体类的所有属性,模式其实是一样的,有没有办法O(1)的复杂度遍历set完所有的属性,也就是与属性的数量N无关
//                System.out.println(JSONObject.toJSONString(jsonObject.getOrDefault("domain","")));
//                mediaInfo.setDomain(JSONObject.toJSONString(jsonObject.getOrDefault("domain","")));
//                mediaInfo.setMediaName(JSONObject.toJSONString(jsonObject.getOrDefault("mediaName","")));
//                mediaInfo.setMediaNameZh(JSONObject.toJSONString(jsonObject.getOrDefault("mediaName","")));
//                mediaInfo.setCountryId(JSONObject.toJSONString(jsonObject.getOrDefault("countryId","")));
//                mediaInfo.setCountryCode(JSONObject.toJSONString(jsonObject.getOrDefault("countryCode","")));
//                mediaInfo.setCountryName(JSONObject.toJSONString(jsonObject.getOrDefault("countryName","")));
//                mediaInfo.setCountryNameZh(JSONObject.toJSONString(jsonObject.getOrDefault("countryNameZh","")));
//                mediaInfo.setMediaLang(JSONObject.toJSONString(jsonObject.getOrDefault("mediaLang","")));
//                mediaInfo.setMediaSector(JSONObject.toJSONString(jsonObject.getOrDefault("mediaSector","")));
//                allRelatedMedia.add(mediaInfo);
//            }
//            // 去重
//            allRelatedMedia.forEach(mediaInfo -> {
//                System.out.println(JSONObject.toJSONString(mediaInfo));
//            });
//            System.out.println(allRelatedMedia);
//            System.out.println("---------------");
//
////            allRelatedMedia.stream()
////                    .distinct()
////                    .forEach(resultRelatedMedia::add);
//        }

        return resultRelatedMedia;
    }

    private static Comparator<JSONObject> jsonObjectComparator() {
        return (o1, o2) -> {
            // Sort the keys of the two JSONObjects
            JSONArray o1Keys = new JSONArray();
            o1Keys.addAll(o1.keySet());
            o1Keys.sort(null);

            JSONArray o2Keys = new JSONArray();
            o2Keys.addAll(o2.keySet());
            o2Keys.sort(null);

            // If the two JSONObjects don't have the same keys, they are different
            if (!o1Keys.equals(o2Keys)) {
                return 1;
            }

            // Compare the values of the two JSONObjects
            for (Object key : o1Keys) {
                Object value1 = o1.get(key);
                Object value2 = o2.get(key);

                // If the values are different, the two JSONObjects are different
                if (!value1.equals(value2)) {
                    return 1;
                }
            }

            return 0;
        };
    }

    // 去重测试
    public static void main(String[] args) {
        String dataStr = "{\"report_media\":[{\"mediaLang\":\"en\",\"countryCode\":\"US\",\"domain\":\"businessinsider.com\",\"mediaSector\":\"\",\"countryName\":\"U.S.A\",\"countryNameZh\":\"美国\",\"reportUrl\":\"https://www.businessinsider.com/big-banks-analyzing-where-to-cut-costs-2023-3\",\"mediaNameZh\":\"商业内幕\",\"countryId\":\"158\",\"mediaName\":\"Business Insider\",\"reportTime\":\"2023-03-28 12:02:00\"}]}";
        String recordDataStr = "{\"report_media\":[{\"mediaLang\":\"en\",\"countryCode\":\"US\",\"domain\":\"businessinsider.com\",\"mediaSector\":\"\",\"countryName\":\"U.S.A\",\"countryNameZh\":\"美国\",\"reportUrl\":\"https://www.businessinsider.com/big-banks-analyzing-where-to-cut-costs-2023-3\",\"mediaNameZh\":\"商业内幕\",\"countryId\":\"158\",\"mediaName\":\"Business Insider\",\"reportTime\":\"2023-03-28 12:02:00\"}]}";
//        String recordDataStr = "{\"report_media\":[{\"mediaLang\":\"en\",\"countryCode\":\"US\",\"domain\":\"businessinsider.com\",\"mediaSector\":\"\",\"countryName\":\"U.S.A\",\"countryNameZh\":\"美国\",\"reportUrl\":\"https://www.businessinsider.com/big-banks-analyzing-where-to-cut-costs-2023-4\",\"mediaNameZh\":\"商业内幕\",\"countryId\":\"158\",\"mediaName\":\"Business Insider\",\"reportTime\":\"2023-03-28 12:02:00\"}]}";
        JSONObject data = JSONObject.parseObject(dataStr);
        JSONObject recordData = JSONObject.parseObject(recordDataStr);
        JSONArray objects = combineMedia(data, recordData);
        System.out.println("---------------");
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
