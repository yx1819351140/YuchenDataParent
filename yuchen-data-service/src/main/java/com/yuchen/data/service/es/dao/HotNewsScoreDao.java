package com.yuchen.data.service.es.dao;

import com.yuchen.data.service.es.entity.HotNewsScore;
import org.springframework.data.elasticsearch.repository.ElasticsearchCrudRepository;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface HotNewsScoreDao extends ElasticsearchCrudRepository<HotNewsScore, String>, ElasticsearchRepository<HotNewsScore, String> {
}
