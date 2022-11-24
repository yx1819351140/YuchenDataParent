package com.yuchen.data.service.es.service;

import com.yuchen.data.service.es.vo.ETLVo.DeleteIndexVo;
import com.yuchen.data.service.utils.result.ResponseResult;

public interface DeleteIndexService {
    /**
     * 查询列表
     * @return
     */
    ResponseResult deleteAllData(DeleteIndexVo vo);

}
