package com.yuchen.data.service.es.service;


import com.yuchen.data.service.es.vo.HotEventsVo;
import com.yuchen.data.service.utils.result.ResponseResult;

public interface HotEventsService {
    /**
     * 查询热点事件
     * @return
     */
    ResponseResult getEvents(HotEventsVo vo);

    /**
     * 查询热点事件相关实体
     * @return
     */
    ResponseResult  getEventEntity();

}
