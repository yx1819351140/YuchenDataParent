package com.yuchen.data.service.es.dao;

import com.yuchen.data.service.es.entity.HotNewsSetting;
import org.springframework.data.elasticsearch.repository.ElasticsearchCrudRepository;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface HotNewsSettingDao extends ElasticsearchCrudRepository<HotNewsSetting, String>, ElasticsearchRepository<HotNewsSetting, String> {
}
