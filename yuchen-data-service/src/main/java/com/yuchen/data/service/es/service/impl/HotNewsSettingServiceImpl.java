package com.yuchen.data.service.es.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yuchen.data.service.es.dao.HotNewsDao;
import com.yuchen.data.service.es.dao.HotNewsSettingDao;
import com.yuchen.data.service.es.dao.PartATempDao;
import com.yuchen.data.service.es.entity.HotNews;
import com.yuchen.data.service.es.entity.HotNewsSetting;
import com.yuchen.data.service.es.service.HotNewsSettingService;
import com.yuchen.data.service.es.vo.HotNewsSettingVo;
import com.yuchen.data.service.utils.result.PageResult;
import com.yuchen.data.service.utils.result.ResponseResult;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.script.Script;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static com.yuchen.data.service.es.service.alarm.AlarmService.sendStatusAlarm;
import static com.yuchen.data.service.utils.DateUtils.*;

@Service
public class HotNewsSettingServiceImpl implements HotNewsSettingService {
    @Resource
    private HotNewsDao hotNewsDao;
    @Resource
    private HotNewsSettingDao hotNewsSettingDao;
    @Resource
    private ElasticsearchTemplate elasticsearchTemplate;

    @Resource
    private PartATempDao partATempDao;

    @Override
    public ResponseResult search(HotNewsSettingVo vo) {
        NativeSearchQueryBuilder builder=new NativeSearchQueryBuilder();

        // 设置核心查询逻辑
        BoolQueryBuilder mainBool = getMainBool(QueryBuilders.boolQuery(), vo);

        PageRequest of = PageRequest.of(vo.getPage(), vo.getSize());
        builder.withPageable(of)
                .withFields("id")
                .withSort(SortBuilders.fieldSort("pub_time").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("id"));

        builder.withQuery(mainBool);
        Page<HotNews> search = hotNewsDao.search(builder.build());


        // 使用流特性处理返回结果
//        List<List<String>> result = search.stream().map(
//                HotNews ->{
//                    ArrayList<String> strings = new ArrayList<>();
//                    strings.add(HotNews.getId());
//                    strings.add(HotNews.getTitle());
//                    return  strings;
//                }
//        ).collect(Collectors.toList());

        List<String> result = search.stream().map(HotNews::getId).collect(Collectors.toList());

        return new ResponseResult(new PageResult(search.getTotalElements(), vo.getPage(), vo.getSize(), result));
    }

    @Override
    public List<HotNewsSetting> searchText(HotNewsSettingVo vo) {
        NativeSearchQueryBuilder builder=new NativeSearchQueryBuilder();
        // 设置核心查询逻辑
        BoolQueryBuilder mainBool = getMainBool(QueryBuilders.boolQuery(), vo);
        builder.withQuery(mainBool);

        // 设置排序
        if (vo.getIsSort() == 1) {
            builder.withSort(SortBuilders.fieldSort("pub_time").order(SortOrder.DESC));
        }

        // 设置分页
//        PageRequest of = PageRequest.of(vo.getPage(), vo.getSize());
//        builder.withPageable(of);
        Page<HotNewsSetting> search = hotNewsSettingDao.search(builder.build());

        return search.getContent();
    }

    public BoolQueryBuilder getMainBool(BoolQueryBuilder mainBool,HotNewsSettingVo vo){
        if (vo.getCountryCodes() != null && vo.getCountryCodes().size() > 0) {
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            for (String countryCode: vo.getCountryCodes()) {
                boolQueryBuilder.should(QueryBuilders.matchQuery("nlp_related_country", countryCode));
            }
            mainBool.must(boolQueryBuilder);
        }
        if (vo.getEntityIds() != null && vo.getEntityIds().size() > 0) {
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            for (String entityId: vo.getEntityIds()) {
                boolQueryBuilder.should(QueryBuilders.matchQuery("related_entities_list", entityId));
            }
            mainBool.must(boolQueryBuilder);
        }
        if (vo.getMediaSelectResult() != null && vo.getMediaSelectResult() == 1) {
            if (vo.getMediaNames() != null && vo.getMediaNames().size() > 0) {
                BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
                boolQueryBuilder.must(QueryBuilders.termsQuery("related_media.media_name_zh.keyword", vo.getMediaNames()));

                mainBool.must(QueryBuilders.nestedQuery("related_media", boolQueryBuilder, ScoreMode.None));
            } else {
                BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
                boolQueryBuilder.must(QueryBuilders.termsQuery("related_media.media_name_zh.keyword", ""));

                mainBool.must(QueryBuilders.nestedQuery("related_media", boolQueryBuilder, ScoreMode.None));
            }
        } else if (vo.getMediaSelectResult() != null && vo.getMediaSelectResult() == 2) {
            if (vo.getMediaNames() != null && vo.getMediaNames().size() > 0) {
                BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
                boolQueryBuilder.should(QueryBuilders.termsQuery("related_media.media_name_zh.keyword", vo.getMediaNames()));
                boolQueryBuilder.should(QueryBuilders.wildcardQuery("related_media.media_name_zh", "*"));

                mainBool.must(QueryBuilders.nestedQuery("related_media", boolQueryBuilder, ScoreMode.None));
            } else {
                BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
                boolQueryBuilder.must(QueryBuilders.termsQuery("related_media.media_name_zh.keyword", ""));

                mainBool.must(QueryBuilders.nestedQuery("related_media", boolQueryBuilder, ScoreMode.None));
            }
        }
        if (vo.getStartDate() != null && vo.getEndDate() != null) {
            mainBool.must(QueryBuilders.rangeQuery("pub_time_timestamp").from(vo.getStartDate().toString()).to(vo.getEndDate().toString()));
        }
        if (vo.getStartTime() != null && vo.getEndTime() != null) {
            mainBool.must(QueryBuilders.rangeQuery("pub_time_timestamp").from(vo.getStartTime().toString()).to(vo.getEndTime().toString()));
        }
        if (vo.getEventTypeCodes() != null && vo.getEventTypeCodes().size() > 0) {
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            boolQueryBuilder.must(QueryBuilders.termsQuery("related_events.event_type", vo.getEventTypeCodes()));

            mainBool.must(QueryBuilders.nestedQuery("related_events", boolQueryBuilder, ScoreMode.None));
        }
        if (vo.getEventGradeMin() != null && vo.getEventGradeMax() != null) {
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            boolQueryBuilder.must(QueryBuilders.rangeQuery("related_events.event_level").from(vo.getEventGradeMin()).to(vo.getEventGradeMax()));
            mainBool.must(QueryBuilders.nestedQuery("related_events", boolQueryBuilder, ScoreMode.None));
        }
        if (vo.getKeywordContainAny() != null && vo.getKeywordContainAny().size() > 0) {
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            for (String keyWord: vo.getKeywordContainAny()) {
                boolQueryBuilder.should(QueryBuilders.matchPhraseQuery("text", keyWord));
            }
            mainBool.must(boolQueryBuilder);
        }
        if (vo.getKeywordContainAll() != null && vo.getKeywordContainAll().size() > 0) {
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            for (String keyword: vo.getKeywordContainAll()) {
                boolQueryBuilder.must(QueryBuilders.matchPhraseQuery("text", keyword));
            }
            mainBool.must(boolQueryBuilder);
        }
        if (vo.getKeywordContainNone() != null && vo.getKeywordContainNone().size() > 0) {
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            for (String keyword: vo.getKeywordContainNone()) {
                boolQueryBuilder.should(QueryBuilders.matchPhraseQuery("text", keyword));
            }
            mainBool.mustNot(boolQueryBuilder);
        }
        if (vo.getLabels() != null && vo.getLabels().size() > 0) {
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            boolQueryBuilder.must(QueryBuilders.termsQuery("atom_labels.label_id", vo.getLabels()));
            mainBool.must(QueryBuilders.nestedQuery("atom_labels", boolQueryBuilder, ScoreMode.None));
        }
        if (vo.getCustomLabels() != null && vo.getCustomLabels().size() > 0) {
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            for (String customLabel: vo.getCustomLabels()) {
                boolQueryBuilder.should(QueryBuilders.matchQuery("custom_labels", customLabel));
            }
            mainBool.must(boolQueryBuilder);
        }
        return mainBool;
    }

    @Override
    public ResponseResult searchForScore(HotNewsSettingVo vo) {

        NativeSearchQueryBuilder builder=new NativeSearchQueryBuilder();
        /**
         * 增量打partA标签：符合搜索规则的全部数据，没有task_ids，没有custom_routine_labels
         * */
        BoolQueryBuilder mainBool = getMainBool(QueryBuilders.boolQuery(), vo);
        // 5小时内，没有task_ids，没有related_entities_list
//        mainBool.must(QueryBuilders.rangeQuery("pub_time_timestamp").from(getTimestamp(2)).to(getTimestamp(0)));
        mainBool.must(QueryBuilders.rangeQuery("create_time").from(getBeforeByHourTime(2)).to(getBeforeByHourTime(0)));
        // task_ids不存在
        mainBool.mustNot(QueryBuilders.existsQuery("task_ids"));
        mainBool.mustNot(QueryBuilders.scriptQuery(new Script("doc['task_ids'].size() > 0")));
        // custom_routine_labels不存在
        mainBool.mustNot(QueryBuilders.existsQuery("custom_routine_labels"));
        mainBool.mustNot(QueryBuilders.scriptQuery(new Script("doc['custom_routine_labels'].size() > 0")));
        // 三个字段必须存在
        mainBool.must(QueryBuilders.existsQuery("text"));
        mainBool.must(QueryBuilders.existsQuery("title"));
        mainBool.must(QueryBuilders.existsQuery("pub_time"));
        // 阶段且没有ETL的字段
        mainBool.must(QueryBuilders.termQuery("stage",0));
//        mainBool.mustNot(QueryBuilders.existsQuery("related_entities_list"));
//        mainBool.mustNot(QueryBuilders.scriptQuery(new Script("doc['related_entities_list'].size() > 0")));
        // 获得搜索DSL
        NativeSearchQuery searchQuery = builder
                .withQuery(mainBool)
                .withSort(SortBuilders.fieldSort("pub_time_timestamp").order(SortOrder.DESC))
//                .withPageable(of)
                .build();
        //Page<HotNewsSetting> search = hotNewsSettingDao.search(builder.build());
//        Page<HotNews> search = hotNewsDao.search(builder.build());
        Page<HotNews> search = hotNewsDao.search(searchQuery);

        List<JSONObject> result = search.stream().map(
                HotNews -> {
                    JSONObject temp =   new JSONObject();
                    temp.put("title_id",HotNews.getId());
                    temp.put("weight_score", (float) Math.random());
                    return temp;
                }
        ).collect(Collectors.toList());
        // 获得元数据score信息
//        List<JSONObject> result = elasticsearchTemplate.query(searchQuery, new ResultsExtractor<List<JSONObject>>() {
//            @Override
//            public List<JSONObject> extract(SearchResponse response) {
//                List<JSONObject> resultList = new ArrayList<>();
//                // Modify helper method with score
//                response.getHits().forEach(hit -> {
//                    JSONObject jsonObject = new JSONObject();
//                    jsonObject.put("title_id",hit.getId());
//                    jsonObject.put("weight_score", hit.getScore());
//                    resultList.add(jsonObject);
//                });
//                return resultList;
//            }
//        });
        vo.setSize(result.size());
        vo.setPage(0);
        return new ResponseResult(new PageResult(search.getTotalElements(), vo.getPage(), vo.getSize(), result));
    }
    @Override
    public ResponseResult searchForPartA(HotNewsSettingVo vo) {

        NativeSearchQueryBuilder builder=new NativeSearchQueryBuilder();
        /**
         * 增量推送kafka：2小时数据，没有task_ids，没有经过NLP处理
         * */
        BoolQueryBuilder mainBool = QueryBuilders.boolQuery();
        // 2小时内更新的24小时内的新闻
        mainBool.must(QueryBuilders.rangeQuery("pub_time_timestamp").from(getTimestampFromNow(0,0,-24)).to(getTimestampFromNow(0,0,0)));
//      mainBool.must(QueryBuilders.rangeQuery("pub_time").from(getBeforeByHourTime(24)).to(getBeforeByHourTime(0)));
//      mainBool.must(QueryBuilders.rangeQuery("pub_time").from(getBeforeByHourTime(24)).to(getBeforeByHourTime(0)));
//      stage为0
        mainBool.must(QueryBuilders.termQuery("stage",0));
        mainBool.must(QueryBuilders.termQuery("is_vector",1));

//      related_entities_list不存在
//      mainBool.mustNot(QueryBuilders.existsQuery("related_entities_list"));
//      mainBool.mustNot(QueryBuilders.scriptQuery(new Script("doc['related_entities_list'].size() > 0")));
        // 设置分页最大限制10000
        // PageRequest of = PageRequest.of(0, 10000);

        // 获得搜索DSL
        NativeSearchQuery searchQuery = builder
                .withQuery(mainBool)
                .withSort(SortBuilders.fieldSort("pub_time").order(SortOrder.DESC))
                //.withPageable(of)
                .build();

        Page<HotNews> search = hotNewsDao.search(searchQuery);

        List<JSONObject> result = search.stream().map(
                HotNews -> {
                    JSONObject temp =   new JSONObject();
                    temp.put("id",HotNews.getId());
                    temp.put("title", HotNews.getTitle());
                    temp.put("create_time", HotNews.getCreateTime());
                    temp.put("update_time", HotNews.getUpdateTime());

                    String text = HotNews.getText();
                    if(text != null){
                        temp.put("text", text);
                    }else{
                        temp.put("text", "");
                        try {
                            sendStatusAlarm("text 为空，id:"+HotNews.getId());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    String pub_time = HotNews.getPubTime();
                    if(pub_time != null){
                        temp.put("pub_time", pub_time);
                    }else{
                        temp.put("pub_time", "");
                        try {
                            sendStatusAlarm("pub_time 为空，id:"+HotNews.getId());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    Double score = HotNews.getScore();
                    if(score != null){
                        temp.put("score",score.toString());
                    }else{
                        temp.put("score","0");
                        try {
                            sendStatusAlarm("score 为空，id:"+HotNews.getId());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    if(HotNews.getNewScore() != null){
                        temp.put("new_score", HotNews.getNewScore().toString());
                    }else{
                        temp.put("new_score","0");
                    }
                    return temp;
                }
        ).collect(Collectors.toList());
        // 获得元数据score信息
//        List<JSONObject> result = elasticsearchTemplate.query(searchQuery, new ResultsExtractor<List<JSONObject>>() {
//            @Override
//            public List<JSONObject> extract(SearchResponse response) {
//                List<JSONObject> resultList = new ArrayList<>();
//                // Modify helper method with score
//                response.getHits().forEach(hit -> {
//                    JSONObject jsonObject = new JSONObject();
//                    jsonObject.put("title_id",hit.getId());
//                    jsonObject.put("weight_score", hit.getScore());
//                    resultList.add(jsonObject);
//                });
//                return resultList;
//            }
//        });
        vo.setSize(result.size());
        vo.setPage(0);
        return new ResponseResult(new PageResult(search.getTotalElements(), vo.getPage(), vo.getSize(), result));
    }

    @Override
    public ResponseResult searchForHistory(HotNewsSettingVo vo) {

        NativeSearchQueryBuilder builder=new NativeSearchQueryBuilder();
        /**
         * 增量推送kafka：2小时数据，没有task_ids，没有经过NLP处理
         * */
        BoolQueryBuilder mainBool = QueryBuilders.boolQuery();
        // 2小时内更新的24小时内的新闻
        mainBool.must(QueryBuilders.termQuery("create_time","2022-09-26 10:20:00"));
        // stage为0
        mainBool.must(QueryBuilders.termQuery("stage",0));

        // 设置分页最大限制1000
        vo.setPage(0);
        vo.setSize(1000);
        PageRequest of = PageRequest.of(vo.getPage(), vo.getSize());

        // 获得搜索DSL
        NativeSearchQuery searchQuery = builder
                .withQuery(mainBool)
                .withSort(SortBuilders.fieldSort("pub_time").order(SortOrder.DESC))
                .withPageable(of)
                .build();

        Page<HotNews> search = hotNewsDao.search(searchQuery);

        List<JSONObject> result = search.stream().map(
                HotNews -> {
                    JSONObject temp =   new JSONObject();
                    temp.put("id",HotNews.getId());
                    temp.put("title", HotNews.getTitle());
                    temp.put("text", HotNews.getText());
                    temp.put("pub_time", HotNews.getPubTime());
                    temp.put("create_time", HotNews.getCreateTime());
                    temp.put("update_time", HotNews.getUpdateTime());
                    temp.put("score", HotNews.getScore().toString());
                    if(HotNews.getNewScore() != null){
                        temp.put("new_score", HotNews.getNewScore().toString());
                    }else{
                        temp.put("new_score","0");
                    }
                    return temp;
                }
        ).collect(Collectors.toList());
        // 获得元数据score信息
//        List<JSONObject> result = elasticsearchTemplate.query(searchQuery, new ResultsExtractor<List<JSONObject>>() {
//            @Override
//            public List<JSONObject> extract(SearchResponse response) {
//                List<JSONObject> resultList = new ArrayList<>();
//                // Modify helper method with score
//                response.getHits().forEach(hit -> {
//                    JSONObject jsonObject = new JSONObject();
//                    jsonObject.put("title_id",hit.getId());
//                    jsonObject.put("weight_score", hit.getScore());
//                    resultList.add(jsonObject);
//                });
//                return resultList;
//            }
//        });
        return new ResponseResult(new PageResult(search.getTotalElements(), vo.getPage(), vo.getSize(), result));
    }

    public ResponseResult searchForImportantHistory(HotNewsSettingVo vo) {

        NativeSearchQueryBuilder builder=new NativeSearchQueryBuilder();
        /**
         * 增量推送kafka：2小时数据，没有task_ids，没有经过NLP处理
         * */
        BoolQueryBuilder mainBool = QueryBuilders.boolQuery();
        // 2小时内更新的24小时内的新闻
        mainBool.must(QueryBuilders.termQuery("create_time","2022-09-26 10:21:00"));
        // stage为0
        mainBool.must(QueryBuilders.termQuery("stage",0));

        // 设置分页最大限制1000
        vo.setPage(0);
        vo.setSize(28065);
        PageRequest of = PageRequest.of(vo.getPage(), vo.getSize());

        // 获得搜索DSL
        NativeSearchQuery searchQuery = builder
                .withQuery(mainBool)
                .withSort(SortBuilders.fieldSort("pub_time").order(SortOrder.DESC))
                .withPageable(of)
                .build();

        Page<HotNews> search = hotNewsDao.search(searchQuery);

        List<JSONObject> result = search.stream().map(
                HotNews -> {
                    JSONObject temp =   new JSONObject();
                    temp.put("id",HotNews.getId());
                    temp.put("title", HotNews.getTitle());
                    temp.put("text", HotNews.getText());
                    temp.put("pub_time", HotNews.getPubTime());
                    temp.put("create_time", HotNews.getCreateTime());
                    temp.put("update_time", HotNews.getUpdateTime());
                    temp.put("score", HotNews.getScore().toString());
                    if(HotNews.getNewScore() != null){
                        temp.put("new_score", HotNews.getNewScore().toString());
                    }else{
                        temp.put("new_score","0");
                    }
                    return temp;
                }
        ).collect(Collectors.toList());
        // 获得元数据score信息
//        List<JSONObject> result = elasticsearchTemplate.query(searchQuery, new ResultsExtractor<List<JSONObject>>() {
//            @Override
//            public List<JSONObject> extract(SearchResponse response) {
//                List<JSONObject> resultList = new ArrayList<>();
//                // Modify helper method with score
//                response.getHits().forEach(hit -> {
//                    JSONObject jsonObject = new JSONObject();
//                    jsonObject.put("title_id",hit.getId());
//                    jsonObject.put("weight_score", hit.getScore());
//                    resultList.add(jsonObject);
//                });
//                return resultList;
//            }
//        });
        return new ResponseResult(new PageResult(search.getTotalElements(), vo.getPage(), vo.getSize(), result));
    }
    @Override
    public ResponseResult searchForPartB(HotNewsSettingVo vo) {

        // 符合语义查询规则
        BoolQueryBuilder mainBool = getMainBool(QueryBuilders.boolQuery(), vo);
        /**
         * 任务检索外的过滤逻辑
         * 1.一小时之内
         * 2.包含task_ids
         * 3.task_ids长度大于0
         * 4.不包含语义标签custom_semantic_labels
         * */
        //mainBool.must(QueryBuilders.rangeQuery("update_time").from(getBeforeByHourTime(1)).to(getBeforeByHourTime(0)));
        // task_ids存在且长度不为0
        mainBool.must(QueryBuilders.existsQuery("task_ids"));
        mainBool.must(QueryBuilders.scriptQuery(new Script("doc['task_ids'].size() > 0")));
        // custom_semantic_labels不存在或者长度为0
        mainBool.mustNot(QueryBuilders.existsQuery("custom_semantic_labels"));
        mainBool.mustNot(QueryBuilders.scriptQuery(new Script("doc['custom_semantic_labels'].size() > 0")));
        // stage=2表示已经经过etl处理完
        mainBool.must(QueryBuilders.termQuery("stage",2));
//        mainBool.must(QueryBuilders.existsQuery("summary"));

        NativeSearchQuery searchQuery=new NativeSearchQueryBuilder()
                .withQuery(mainBool)
                .withSort(SortBuilders.fieldSort("pub_time").order(SortOrder.DESC))
                .withFields("id")
                .build();

        Page<HotNews> search = hotNewsDao.search(searchQuery);

        List<String> result = search.stream().map(HotNews::getId).collect(Collectors.toList());
        vo.setSize(result.size());
        vo.setPage(0);

        return new ResponseResult(new PageResult(search.getTotalElements(), vo.getPage(), vo.getSize(), result));
    }
    @Override
    public ResponseResult searchForCluster(HotNewsSettingVo vo) {

        // 增量B查询逻辑
        BoolQueryBuilder mainBool = QueryBuilders.boolQuery();
        // 搜索一个月的已经打标的新闻
        mainBool.must(QueryBuilders.rangeQuery("update_time").from(getBeforeByMonthTime(1)).to(getBeforeByHourTime(0)));
        mainBool.must(QueryBuilders.existsQuery("custom_labels"));

        // 设置分页最大限制1000
//        vo.setPage(0);
//        vo.setSize(5000);
//        PageRequest of = PageRequest.of(vo.getPage(), vo.getSize());

        NativeSearchQuery searchQuery=new NativeSearchQueryBuilder()
                .withQuery(mainBool)
                .withFields("id","title","text","pub_time","custom_labels")
                .withSort(SortBuilders.fieldSort("pub_time").order(SortOrder.DESC))
//                .withPageable(of)
                .build();
        JSONObject resultObject = new JSONObject();
        JSONArray resultList = new JSONArray();
        resultObject.put("batch_id",getBeforeByHourTime(0));
        // 新增聚类阈值
//        resultObject.put("low_threshold",100);
//        resultObject.put("up_threshold",5000);
        Page<HotNews> search = hotNewsDao.search(searchQuery);
        search.stream().forEach(HotNews -> {
            List<Long> customLabels = HotNews.getCustomLabels();
            String id = HotNews.getId();
            String title = HotNews.getTitle();
            String text = HotNews.getText();
            String pub_time = HotNews.getPubTime();

            // 遍历customLabels，每一个都添加到resultList
            customLabels.forEach(customLabel -> {
                if(customLabel == 351 || customLabel == 352|| customLabel == 353|| customLabel == 354|| customLabel == 356){
                    JSONObject jsonObject = new JSONObject();
                    if(id != null && title != null && text != null && pub_time != null) {
                        jsonObject.put("title_id",HotNews.getId());
                        jsonObject.put("title",HotNews.getTitle());
                        jsonObject.put("text",HotNews.getText());
                        jsonObject.put("pub_time",HotNews.getPubTime());
                        jsonObject.put("setting_id",customLabel);
                        resultList.add(jsonObject);
                    }
                }

            });
        });
        resultObject.put("news_list",resultList);


        return new ResponseResult(200,"success", resultObject);
    }
}
