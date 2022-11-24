package com.yuchen.data.service.es.dao;

import com.yuchen.data.service.es.entity.EntityType;
import org.springframework.data.elasticsearch.repository.ElasticsearchCrudRepository;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface EntityTypeDao extends ElasticsearchCrudRepository<EntityType, String>, ElasticsearchRepository<EntityType, String> {
}
