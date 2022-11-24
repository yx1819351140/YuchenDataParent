package com.yuchen.data.service.es.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ClusterNewsVo {

    @ApiModelProperty(value = "自定义标签")
    private String customLabels;
}
