package com.yuchen.data.service.es.service.impl;

import com.yuchen.data.service.es.dao.ClusterNewsDao;
import com.yuchen.data.service.es.entity.ClusterNews;
import com.yuchen.data.service.es.service.ClusterNewsService;
import com.yuchen.data.service.es.vo.ClusterNewsVo;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.data.domain.Page;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class ClusterNewsServiceImpl implements ClusterNewsService {
    @Resource
    private ClusterNewsDao clusterNewsDao;

    @Override
    public List<ClusterNews> searchClusterNews(ClusterNewsVo vo) {
        try {
            NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder();
            BoolQueryBuilder mainBool = QueryBuilders.boolQuery();

            if (StringUtils.isNotEmpty(vo.getCustomLabels())) {
                mainBool.must(QueryBuilders.matchQuery("custom_labels", vo.getCustomLabels()));
            }
            builder.withQuery(mainBool);

            Page<ClusterNews> search = clusterNewsDao.search(builder.build());

            return search.getContent();
        } catch (Exception e) {
            return null;
        }
    }
}
