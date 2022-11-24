package com.yuchen.data.service.es.service;

import com.yuchen.data.service.es.vo.HotNewsVo;
import com.yuchen.data.service.utils.result.ResponseResult;

public interface HotNewsService {

    /**
     * 查询列表
     * @return
     */
    ResponseResult getNews(HotNewsVo vo);

    ResponseResult getTaskIds(HotNewsVo vo);
}
