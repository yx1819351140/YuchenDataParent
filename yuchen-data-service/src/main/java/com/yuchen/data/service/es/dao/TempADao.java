package com.yuchen.data.service.es.dao;

import com.yuchen.data.service.es.entity.ETLEntity.TempAEntity;
import org.springframework.data.elasticsearch.repository.ElasticsearchCrudRepository;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface TempADao extends ElasticsearchCrudRepository<TempAEntity, String>, ElasticsearchRepository<TempAEntity, String> {
}
