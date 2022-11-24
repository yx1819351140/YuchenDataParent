package com.yuchen.data.service.es.service;

import com.yuchen.data.service.es.entity.ClusterNews;
import com.yuchen.data.service.es.vo.ClusterNewsVo;

import java.util.List;

public interface ClusterNewsService {

    List<ClusterNews> searchClusterNews(ClusterNewsVo vo);
}
