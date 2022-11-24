package com.yuchen.data.service.es.service.impl;

import com.yuchen.data.service.es.dao.HotEntityDao;
import com.yuchen.data.service.es.entity.HotEntity;
import com.yuchen.data.service.es.entity.NestedEntity.entity.LabelEntity;
import com.yuchen.data.service.es.service.HotEntityService;
import com.yuchen.data.service.es.vo.HotEntityVo;
import com.yuchen.data.service.utils.result.ResponseResult;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class HotEntityServiceImpl implements HotEntityService {

    @Resource
    private HotEntityDao hotEntityDao;
    @Resource
    private ElasticsearchTemplate elasticsearchTemplate;

    @Override
    public ResponseResult getEntityName(HotEntityVo vo) {
        List<String> qids = vo.getQIDs();
        Integer page = vo.getPage();
        Integer size = vo.getSize();

        // 设置qid
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(QueryBuilders.termsQuery("id", qids));
        // 设置分页
        //PageRequest of = PageRequest.of(page, size);
        // 建造者模式构建查询
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                //.withPageable(of)
                .withQuery(boolQueryBuilder)
                .withFields("name_en")
                .build();

        // 返回查询结果
        List<HotEntity> searchContent = hotEntityDao.search(searchQuery).getContent();

        // 使用流特性处理返回结果
        List<String> result = searchContent.stream().map(HotEntity::getNameEn).collect(Collectors.toList());

        return new ResponseResult(200, result);
    }


    @Override
    public ResponseResult getAtomLabels(HotEntityVo vo) {
        List<String> qids = vo.getQIDs();
        Integer page = vo.getPage();
        Integer size = vo.getSize();

        // 设置分页
        PageRequest of = PageRequest.of(page, size);

        // 设置qid
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        boolQueryBuilder.must(QueryBuilders.termsQuery("id", qids));

        // 建造者模式构建查询
        NativeSearchQuery searchQuery  = new NativeSearchQueryBuilder()
                //.withPageable(of)
                .withQuery(boolQueryBuilder)
                .withFields("labels")
                .build();

        // 返回查询结果
        List<HotEntity> searchContent = hotEntityDao.search(searchQuery).getContent();

        // 使用流特性处理返回结果
        List<LabelEntity> result = searchContent.stream()
                .map(HotEntity::getLabels)
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        return new ResponseResult(200,result);
    }
}
