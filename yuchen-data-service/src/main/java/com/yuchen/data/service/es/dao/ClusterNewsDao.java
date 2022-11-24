package com.yuchen.data.service.es.dao;

import com.yuchen.data.service.es.entity.ClusterNews;
import org.springframework.data.elasticsearch.repository.ElasticsearchCrudRepository;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ClusterNewsDao extends ElasticsearchCrudRepository<ClusterNews, String>, ElasticsearchRepository<ClusterNews, String> {
}
