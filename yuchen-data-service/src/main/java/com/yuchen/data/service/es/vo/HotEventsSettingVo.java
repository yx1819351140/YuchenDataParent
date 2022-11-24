package com.yuchen.data.service.es.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class HotEventsSettingVo {

    @ApiModelProperty(value = "国家码值")
    private List<String> countryCodes;

    @ApiModelProperty(value = "实体id")
    private List<String> entityIds;

    @ApiModelProperty(value = "开始时间  时间戳")
    private Long startTime;

    @ApiModelProperty(value = "结束时间 时间戳")
    private Long endTime;

    @ApiModelProperty(value = "事件类型")
    private  List<String> eventTypeCodes;

    @ApiModelProperty(value = "事件等级最小")
    private Integer  eventGradeMin;

    @ApiModelProperty(value = "事件等级最大")
    private Integer  eventGradeMax;

    @ApiModelProperty (value = "关键词，包含全部")
    private List<String> keywordContainAll;

    @ApiModelProperty (value = "关键词，包含任意")
    private List<String> keywordContainAny;

    @ApiModelProperty (value = "关键词，不包含")
    private List<String> keywordContainNone;
    @ApiModelProperty (value = "原子标签")
    private List<String> labels;
    @ApiModelProperty (value = "自定义标签")
    private List<String> customLabels;
}
