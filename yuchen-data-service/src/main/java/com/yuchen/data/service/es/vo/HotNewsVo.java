package com.yuchen.data.service.es.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * ES 参数
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HotNewsVo {

    private Integer  size = 10;
    private Integer page = 0;

    @ApiModelProperty(value = "新闻标题id")
    private String Id;
    @ApiModelProperty(value = "国家码值")
    private List<String> countryCode;
    @ApiModelProperty(value = "实体id")
    private List<String> entityIds;
    @ApiModelProperty(value = "媒体")
    private List<String> medias;
    @ApiModelProperty(value = "关键词")
    private String  keyWord;
    @ApiModelProperty(value = "开始时间  yyyy-MM-dd")
    private String  startTime;
    @ApiModelProperty(value = "结束时间 yyyy-MM-dd")
    private String endTime;
    @ApiModelProperty(value = "事件类型")
    private  List<String> eventTypes;
    @ApiModelProperty(value = "事件等级最小")
    private Integer  min;
    @ApiModelProperty(value = "事件等级最大")
    private Integer  max;
    @ApiModelProperty(value = "排序方式，1.重要度，2 时间排序，3 热点")
    private Integer  sort;
    @ApiModelProperty(value = "排序方式，1.正序，2 倒叙")
    private Integer sortType = 1;

    @ApiModelProperty (value = "关键词，包含全部")
    private List<String> keywordContainAll;

    @ApiModelProperty (value = "关键词，包含任意")
    private List<String> keywordContainAny;

    @ApiModelProperty (value = "关键词，不包含")
    private List<String> keywordContainNone;
    @ApiModelProperty (value = "实体标签")
    private List<String> labels;

    @ApiModelProperty
    private Boolean  isHot;

}
