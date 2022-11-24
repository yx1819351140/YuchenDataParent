package com.yuchen.data.service.es.dao;

import com.yuchen.data.service.es.entity.HotEntity;
import org.springframework.data.elasticsearch.repository.ElasticsearchCrudRepository;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface HotEventDao extends ElasticsearchCrudRepository<HotEntity, String>, ElasticsearchRepository<HotEntity, String> {

}
