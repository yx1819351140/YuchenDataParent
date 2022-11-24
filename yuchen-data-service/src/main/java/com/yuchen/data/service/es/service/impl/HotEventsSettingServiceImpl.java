package com.yuchen.data.service.es.service.impl;

import com.yuchen.data.service.es.dao.HotEventsSettingDao;
import com.yuchen.data.service.es.entity.HotEventsSetting;
import com.yuchen.data.service.es.service.HotEventsSettingService;
import com.yuchen.data.service.es.vo.HotEventsSettingVo;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.data.domain.Page;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class HotEventsSettingServiceImpl implements HotEventsSettingService {

    @Resource
    private HotEventsSettingDao hotEventsSettingDao;

    @Override
    public List<HotEventsSetting> searchEvents(HotEventsSettingVo vo) {
        NativeSearchQueryBuilder builder=new NativeSearchQueryBuilder();
        // 设置核心查询逻辑
        BoolQueryBuilder mainBool = getMainBool(QueryBuilders.boolQuery(), vo);
        builder.withQuery(mainBool);

        Page<HotEventsSetting> search = hotEventsSettingDao.search(builder.build());
        return search.getContent();
    }

    public BoolQueryBuilder getMainBool(BoolQueryBuilder mainBool, HotEventsSettingVo vo){
        if (vo.getCountryCodes() != null && vo.getCountryCodes().size() > 0) {
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            for (String countryCode: vo.getCountryCodes()) {
                boolQueryBuilder.should(QueryBuilders.matchQuery("related_country", countryCode));
            }
            mainBool.must(boolQueryBuilder);
        }
        if (vo.getEntityIds() != null && vo.getEntityIds().size() > 0) {
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            for (String entityId: vo.getEntityIds()) {
                boolQueryBuilder.should(QueryBuilders.matchQuery("related_entities", entityId));
            }
            mainBool.must(boolQueryBuilder);
        }
        if (vo.getStartTime() != null && vo.getEndTime() != null) {
            mainBool.must(QueryBuilders.rangeQuery("update_time").from(vo.getStartTime().toString()).to(vo.getEndTime().toString()));
        }
        if (vo.getEventTypeCodes() != null && vo.getEventTypeCodes().size() > 0) {
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            boolQueryBuilder.must(QueryBuilders.termsQuery("event_type", vo.getEventTypeCodes()));

            mainBool.must(boolQueryBuilder);
        }
        if (vo.getEventGradeMin() != null && vo.getEventGradeMax() != null) {
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            boolQueryBuilder.must(QueryBuilders.rangeQuery("event_level").from(vo.getEventGradeMin()).to(vo.getEventGradeMax()));

            mainBool.must(boolQueryBuilder);
        }
        if (vo.getKeywordContainAny() != null && vo.getKeywordContainAny().size() > 0) {
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            for (String keyWord: vo.getKeywordContainAny()) {
                boolQueryBuilder.should(QueryBuilders.matchPhraseQuery("event_desc", keyWord));
            }
            mainBool.must(boolQueryBuilder);
        }
        if (vo.getKeywordContainAll() != null && vo.getKeywordContainAll().size() > 0) {
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            for (String keyword: vo.getKeywordContainAll()) {
                boolQueryBuilder.must(QueryBuilders.matchPhraseQuery("event_desc", keyword));
            }
            mainBool.must(boolQueryBuilder);
        }
        if (vo.getKeywordContainNone() != null && vo.getKeywordContainNone().size() > 0) {
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            for (String keyword: vo.getKeywordContainNone()) {
                boolQueryBuilder.should(QueryBuilders.matchPhraseQuery("event_desc", keyword));
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
}
