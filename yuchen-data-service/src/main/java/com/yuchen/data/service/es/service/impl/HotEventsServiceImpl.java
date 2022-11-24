package com.yuchen.data.service.es.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.yuchen.data.service.es.dao.HotEventsDao;
import com.yuchen.data.service.es.entity.HotEvent;
import com.yuchen.data.service.es.service.HotEventsService;
import com.yuchen.data.service.es.vo.HotEventsVo;
import com.yuchen.data.service.utils.result.PageResult;
import com.yuchen.data.service.utils.result.ResponseResult;
import com.yuchen.data.service.utils.result.RestResultEnum;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

import static com.yuchen.data.service.utils.DateUtils.dateToStamp;

@Service
public class HotEventsServiceImpl implements HotEventsService {

    @Resource
    private HotEventsDao hotEventsDao;
    @Resource
    private ElasticsearchTemplate   elasticsearchTemplate;

    @Override
    public ResponseResult getEvents(HotEventsVo vo) {
        // 创建请求
        NativeSearchQueryBuilder builder=new NativeSearchQueryBuilder();
        BoolQueryBuilder mainBool = QueryBuilders.boolQuery();

        //条件查询
        //BoolQueryBuilder mainBool = new BoolQueryBuilder();
        //mainBool.must(QueryBuilders.matchQuery("name", "赵六"));
        Map<Integer, String> sortMap = new HashMap<Integer, String>();
        sortMap.put(1, "event_level");
        sortMap.put(2, "update_time");
        Map<Integer, SortOrder> sortTypeMap = new HashMap<Integer, SortOrder>();
        sortTypeMap.put(1, SortOrder.ASC);
        sortTypeMap.put(2, SortOrder.DESC);

        if(CollectionUtil.isNotEmpty(vo.getEventIds())){
            mainBool.must(QueryBuilders.termsQuery("event_id",vo.getEventIds()));
        }
        //国家
        if(CollectionUtil.isNotEmpty(vo.getCountryCode())){
//            mainBool.must(QueryBuilders.termsQuery("related_country",vo.getCountryCode()));

            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            for (String countryCode: vo.getCountryCode()) {
                boolQueryBuilder.should(QueryBuilders.matchQuery("related_country", countryCode));
            }
            mainBool.must(boolQueryBuilder);
        }
        //标签
        if(CollectionUtil.isNotEmpty(vo.getLabels())){
            mainBool.must(QueryBuilders.termsQuery("atom_labels",vo.getLabels()));
        }
        //事件类型
        if(CollectionUtil.isNotEmpty(vo.getEventTypes())){
            mainBool.must(QueryBuilders.termsQuery("event_type", vo.getEventTypes()));

        }
        //实体 ID
        if(CollectionUtil.isNotEmpty(vo.getEntityIds())){
            mainBool.must(QueryBuilders.termsQuery("related_entities.keyword",vo.getEntityIds()));
        }
        //等级
        if (vo.getMin() != null && vo.getMax() != null) {
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            mainBool.must(QueryBuilders.rangeQuery("event_level").from(vo.getMin()).to(vo.getMax()));

        }
        //事件  描述 keyword
        if (StringUtils.isNotEmpty(vo.getKeyWord())) {

            QueryBuilder keywordQueryBuilder = QueryBuilders.matchQuery("event_desc", vo.getKeyWord());
            mainBool.must(keywordQueryBuilder);
        }
        //时间区间
        if (StringUtils.isNotEmpty(vo.getStartTime()) && StringUtils.isNotEmpty(vo.getEndTime())) {
            mainBool.must(QueryBuilders.rangeQuery("update_time").from(dateToStamp(vo.getStartTime())).to(dateToStamp(vo.getEndTime())));
        }

        if (vo.getSort() != null) {
            builder.withSort(SortBuilders.fieldSort(sortMap.get(vo.getSort())).order(sortTypeMap.get(vo.getSortType())==null?SortOrder.DESC:sortTypeMap.get(vo.getSortType())));

        }

        //nested类型嵌套查询
//        NativeSearchQueryBuilder groupQuery = new NativeSearchQueryBuilder();
//        BoolQueryBuilder boolQueyBuilder = QueryBuilders.boolQuery();
//        boolQueyBuilder.must(QueryBuilders.nestedQuery("related_place", (QueryBuilders.existsQuery("related_place.place")), ScoreMode.None));
        builder.withQuery(mainBool);
        PageRequest of = PageRequest.of(vo.getPage(), vo.getSize());
        builder.withPageable(of);
        Page<HotEvent> search = hotEventsDao.search(builder.build());

        return new ResponseResult(new PageResult<>(search.getTotalElements(),vo.getPage(),vo.getSize(),search.getContent()));
    }

    @Override
    public ResponseResult getEventEntity() {
        NativeSearchQueryBuilder  builder = new NativeSearchQueryBuilder();
        BoolQueryBuilder  boolQuery= QueryBuilders.boolQuery();
        //字段名，条件
        //boolQuery.must(QueryBuilders.matchQuery("name_en","OmegaWiki Defined Meaning"));
        boolQuery.must(QueryBuilders.existsQuery("related_place"));
        Pageable   pageable= PageRequest.of(0,10);
        SearchQuery searchQuery =  builder.withQuery(boolQuery).withPageable(pageable).build();
        AggregatedPage<HotEvent> hotEventEntities = elasticsearchTemplate.queryForPage(searchQuery, HotEvent.class);

        return new ResponseResult(RestResultEnum.SUCCESS,hotEventEntities.getContent());
    }
}
