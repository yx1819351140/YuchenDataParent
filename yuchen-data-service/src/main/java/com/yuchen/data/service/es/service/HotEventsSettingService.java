package com.yuchen.data.service.es.service;

import com.yuchen.data.service.es.entity.HotEventsSetting;
import com.yuchen.data.service.es.vo.HotEventsSettingVo;

import java.util.List;

public interface HotEventsSettingService {
    List<HotEventsSetting> searchEvents(HotEventsSettingVo vo);
}
