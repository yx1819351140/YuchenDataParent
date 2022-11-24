package com.yuchen.data.service.es.dao;

import com.yuchen.data.service.es.entity.ETLEntity.TempBEntity;
import org.springframework.data.elasticsearch.repository.ElasticsearchCrudRepository;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface TempBDao extends ElasticsearchCrudRepository<TempBEntity, String>, ElasticsearchRepository<TempBEntity, String> {
}
