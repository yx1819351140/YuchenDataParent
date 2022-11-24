package com.yuchen.data.service.es.dao;

import com.yuchen.data.service.es.entity.HotEvent;
import org.springframework.data.elasticsearch.repository.ElasticsearchCrudRepository;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface HotEventsDao extends ElasticsearchCrudRepository<HotEvent, String>, ElasticsearchRepository<HotEvent, String> {

}
