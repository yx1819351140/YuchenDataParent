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
public class HotEventsVo {

    private Integer  size = 10;
    private Integer page = 0;

    @ApiModelProperty(value = "国家码值")
    private List<String> countryCode;
    @ApiModelProperty(value = "事件地点")
    private List<String> relatedPlace;
    @ApiModelProperty(value = "事件id")
    private List<String> eventIds;
    @ApiModelProperty(value = "关键词")
    private String  keyWord;
    @ApiModelProperty(value = "开始时间  yyyy-MM-dd")
    private String  startTime;
    @ApiModelProperty(value = "结束时间 yyyy-MM-dd")
    private String endTime;
    @ApiModelProperty(value = "事件类型一")
    private  List<String> eventTypeLevelOne;
    @ApiModelProperty(value = "事件类型二")
    private  List<String> eventTypeLevelTwo;
    @ApiModelProperty(value = "事件类型三")
    private  List<String> eventTypeLevelThree;
    @ApiModelProperty(value = "事件等级最小")
    private Integer  min;
    @ApiModelProperty(value = "事件等级最大")
    private Integer  max;
    @ApiModelProperty(value = "排序方式，1.事件重要等级, 2,时间排序")
    private Integer  sort;
    @ApiModelProperty(value = "排序方式，1.正序，2 倒叙")
    private Integer sortType;



    @ApiModelProperty(value = "实体id")
    private List<String> entityIds;

    @ApiModelProperty(value = "事件类型")
    private  List<String> eventTypes;

    @ApiModelProperty (value = "标签")
    private List<String> labels;





}
