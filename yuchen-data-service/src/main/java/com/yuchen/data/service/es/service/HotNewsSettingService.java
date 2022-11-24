package com.yuchen.data.service.es.service;

import com.yuchen.data.service.es.entity.HotNewsSetting;
import com.yuchen.data.service.es.vo.HotNewsSettingVo;
import com.yuchen.data.service.utils.result.ResponseResult;

import java.util.List;

public interface HotNewsSettingService {
    /**
     * 查询列表
     * @return
     */
    ResponseResult search(HotNewsSettingVo vo);

    List<HotNewsSetting> searchText(HotNewsSettingVo vo);

    ResponseResult searchForScore(HotNewsSettingVo vo);
    ResponseResult searchForPartA(HotNewsSettingVo vo);

    ResponseResult searchForPartB(HotNewsSettingVo vo);

    ResponseResult searchForHistory(HotNewsSettingVo vo);

    ResponseResult searchForImportantHistory(HotNewsSettingVo vo);
    ResponseResult searchForCluster(HotNewsSettingVo vo);
}
