package com.yuchen.data.service.es.dao;

import com.yuchen.data.service.es.entity.HotNews;
import org.springframework.data.elasticsearch.repository.ElasticsearchCrudRepository;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface HotNewsDao extends ElasticsearchCrudRepository<HotNews, String>, ElasticsearchRepository<HotNews, String> {
}
