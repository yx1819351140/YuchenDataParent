package com.yuchen.data.service.es.dao;

import com.yuchen.data.service.es.entity.HotNewsEntity;
import org.springframework.data.elasticsearch.repository.ElasticsearchCrudRepository;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface HotNewsEntityDao extends ElasticsearchCrudRepository<HotNewsEntity, String>, ElasticsearchRepository<HotNewsEntity, String> {
}
