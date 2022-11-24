package com.yuchen.data.service.es.service;

import com.yuchen.data.service.es.vo.HotNewsScoreVo;

public interface HotNewsScoreService {
    Integer getIsHot(HotNewsScoreVo vo);

    Integer getIsExists(HotNewsScoreVo vo);
}
