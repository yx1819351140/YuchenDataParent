package com.yuchen.data.service.es.service;

import com.yuchen.data.service.es.vo.HotEntityVo;
import com.yuchen.data.service.utils.result.ResponseResult;

public interface HotEntityService {

    /**
     * 查询列表
     * @return
     */
    ResponseResult getEntityName(HotEntityVo vo);

    ResponseResult getAtomLabels(HotEntityVo vo);
}
