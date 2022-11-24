package com.yuchen.data.service.es.dao;

import com.yuchen.data.service.es.entity.HotEventsSetting;
import org.springframework.data.elasticsearch.repository.ElasticsearchCrudRepository;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface HotEventsSettingDao extends ElasticsearchCrudRepository<HotEventsSetting, String>, ElasticsearchRepository<HotEventsSetting, String> {
}
