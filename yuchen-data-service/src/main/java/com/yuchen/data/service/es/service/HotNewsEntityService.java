package com.yuchen.data.service.es.service;

import com.yuchen.data.service.es.vo.HotNewsEntityVo;
import com.yuchen.data.service.utils.result.ResponseResult;

public interface HotNewsEntityService {

    /**
     * 查询列表
     * @return
     */
    ResponseResult getNewsEntity(HotNewsEntityVo vo);
}
