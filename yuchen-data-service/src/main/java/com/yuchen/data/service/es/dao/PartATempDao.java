package com.yuchen.data.service.es.dao;

import com.yuchen.data.service.es.entity.ETLEntity.PartATempEntity;
import org.springframework.data.elasticsearch.repository.ElasticsearchCrudRepository;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface PartATempDao extends ElasticsearchCrudRepository<PartATempEntity, String>, ElasticsearchRepository<PartATempEntity, String> {
}
