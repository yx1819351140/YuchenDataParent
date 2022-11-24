package com.yuchen.data.service.es.service.impl;

import com.yuchen.data.service.es.dao.HotNewsEntityDao;
import com.yuchen.data.service.es.entity.HotNewsEntity;
import com.yuchen.data.service.es.service.HotNewsEntityService;
import com.yuchen.data.service.es.vo.HotNewsEntityVo;
import com.yuchen.data.service.utils.result.ResponseResult;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class HotNewsEntityServiceImpl implements HotNewsEntityService {

    @Resource
    private HotNewsEntityDao hotNewsEntityDao;
    @Resource
    private ElasticsearchTemplate elasticsearchTemplate;

    @Override
    public ResponseResult getNewsEntity(HotNewsEntityVo vo) {
        List<String> labelId = vo.getLabelId();
        Integer page = vo.getPage();
        Integer size = vo.getSize();
        String entityName = vo.getEntityName();

        NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder();
        BoolQueryBuilder mainBool = QueryBuilders.boolQuery();
        // 设置实体名称
        if (StringUtils.isNotEmpty(entityName)) {
            mainBool.must(QueryBuilders.multiMatchQuery(entityName, "name_en", "name_zh"));
        }
        // 设置label_id
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(QueryBuilders.termsQuery("labels.label_id", labelId));
        NestedQueryBuilder nested = QueryBuilders.nestedQuery("labels", boolQueryBuilder, ScoreMode.None);
        mainBool.must(nested);
        // 设置分页
        PageRequest of = PageRequest.of(page, size);
        builder.withPageable(of);
        // 加载查询条件
        builder.withQuery(mainBool);
        // 返回查询结果
        Page<HotNewsEntity> search = hotNewsEntityDao.search(builder.build());
        return new ResponseResult(search.getContent());
    }
}
