package com.yuchen.data.service.es.service.impl;

import com.yuchen.data.service.es.dao.HotNewsDao;
import com.yuchen.data.service.es.entity.HotNews;
import com.yuchen.data.service.es.service.HotNewsService;
import com.yuchen.data.service.es.vo.HotNewsVo;
import com.yuchen.data.service.utils.result.PageResult;
import com.yuchen.data.service.utils.result.ResponseResult;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.ExistsQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.yuchen.data.service.utils.DateUtils.dateToStamp;
import static com.yuchen.data.service.utils.DateUtils.dateToStamp1;

@Service
public class HotNewsServiceImpl implements HotNewsService {

    @Resource
    private HotNewsDao hotNewsDao;
    @Resource
    private ElasticsearchTemplate elasticsearchTemplate;

    @Override
    public ResponseResult getTaskIds(HotNewsVo vo) {
        String idName = vo.getId();
        List<Long> task_ids = new ArrayList<Long>();
        try{
            task_ids = hotNewsDao.findById(idName).get().getTask_ids();
            return new ResponseResult(200,"查询task_ids成功", task_ids);
        } catch (Exception e) {
            return new ResponseResult(400,"查询失败，返回空列表", task_ids);
        }
    }

    @Override
    public ResponseResult getNews(HotNewsVo vo) {
        NativeSearchQueryBuilder builder=new NativeSearchQueryBuilder();
        BoolQueryBuilder mainBool = QueryBuilders.boolQuery();
//        BoolQueryBuilder boolQueryBuilder= QueryBuilders.boolQuery();
//        boolQueryBuilder.must(QueryBuilders.termQuery("name_en","Meaning"));

        Map<Integer, String> sortMap = new HashMap<Integer, String>();
        sortMap.put(1, "score");
        sortMap.put(2, "pub_time");
        sortMap.put(3, "hot_point");

        Map<Integer, SortOrder> sortTypeMap = new HashMap<Integer, SortOrder>();
        sortTypeMap.put(1, SortOrder.ASC);
        sortTypeMap.put(2, SortOrder.DESC);

        if (vo.getCountryCode() != null && vo.getCountryCode().size() > 0) {
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            for (String countryCode: vo.getCountryCode()) {
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
        if (vo.getMedias() != null && vo.getMedias().size() > 0) {
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            boolQueryBuilder.must(QueryBuilders.termsQuery("related_media.media_name_zh.keyword", vo.getMedias()));

            mainBool.must(QueryBuilders.nestedQuery("related_media", boolQueryBuilder, ScoreMode.None));
        }
        if (StringUtils.isNotEmpty(vo.getKeyWord())) {
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            boolQueryBuilder.should(QueryBuilders.matchPhraseQuery("title", vo.getKeyWord()));
            boolQueryBuilder.should(QueryBuilders.matchPhraseQuery("text", vo.getKeyWord()));

            mainBool.must(boolQueryBuilder);
        }
        if (StringUtils.isNotEmpty(vo.getStartTime()) && StringUtils.isNotEmpty(vo.getEndTime())) {
            mainBool.must(QueryBuilders.rangeQuery("pub_time_timestamp").from(dateToStamp(vo.getStartTime())).to(dateToStamp1(vo.getEndTime() + " 23:59:59")));
        }
        if (vo.getEventTypes() != null && vo.getEventTypes().size() > 0) {
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            boolQueryBuilder.must(QueryBuilders.termsQuery("related_events.event_type", vo.getEventTypes()));

            mainBool.must(QueryBuilders.nestedQuery("related_events", boolQueryBuilder, ScoreMode.None));
        }
        if (vo.getMin() != null && vo.getMax() != null) {
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            boolQueryBuilder.must(QueryBuilders.rangeQuery("related_events.event_level").from(vo.getMin()).to(vo.getMax()));
            mainBool.must(QueryBuilders.nestedQuery("related_events", boolQueryBuilder, ScoreMode.None));
        }

        if (vo.getSort() != null && vo.getSortType() != null) {
            builder.withSort(SortBuilders.fieldSort(sortMap.get(vo.getSort())).order(sortTypeMap.get(vo.getSortType())));
        }
        if (vo.getKeywordContainAny() != null && vo.getKeywordContainAny().size() > 0) {
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            for (String keyword: vo.getKeywordContainAny()) {
                boolQueryBuilder.should(QueryBuilders.matchPhraseQuery("text", keyword));
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
//        if(vo.getIsHot() != null && vo.getIsHot()){
//            ExistsQueryBuilder existsQueryBuilder = QueryBuilders.existsQuery("related_events");
//            mainBool.must(QueryBuilders.nestedQuery("related_events", existsQueryBuilder, ScoreMode.None));
//        }
        if(vo.getIsHot() != null && vo.getIsHot()){
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            boolQueryBuilder.must(QueryBuilders.termQuery("is_hot", 1));
            mainBool.must(boolQueryBuilder);
        }

        PageRequest of = PageRequest.of(vo.getPage(), vo.getSize());
        builder.withPageable(of);


        builder.withQuery(mainBool);
        builder.withSort(SortBuilders.fieldSort("id"));
        Page<HotNews> search = hotNewsDao.search(builder.build());

        NativeSearchQueryBuilder hotBuilder=new NativeSearchQueryBuilder();
        if (vo.getIsHot() != null && vo.getIsHot()) {
            hotBuilder.withQuery(mainBool);
        } else {
            ExistsQueryBuilder existsQueryBuilder = QueryBuilders.existsQuery("related_events");
            mainBool.must(QueryBuilders.nestedQuery("related_events", existsQueryBuilder, ScoreMode.None));
            hotBuilder.withQuery(mainBool);
        }
//        Page<HotNews> hotSearch = hotNewsDao.search(hotBuilder.build());


        long hotNewsCount = elasticsearchTemplate.count(hotBuilder.build(), HotNews.class);

        return new ResponseResult(new PageResult(search.getTotalElements(), hotNewsCount, vo.getPage()+1, vo.getSize(), search.getContent()));
    }
}
