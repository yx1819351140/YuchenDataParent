package com.yuchen.data.service.es.service;

import com.yuchen.data.service.es.vo.ETLVo.CombineVo;
import com.yuchen.data.service.utils.result.ResponseResult;

public interface CustomLabelsCombineService {

    /**
     * 查询列表
     * @return
     */
    ResponseResult combineCustomLabels(CombineVo vo);

}
