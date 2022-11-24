package com.yuchen.data.service.es.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HotNewsEntityVo {

    private Integer  size = 20;
    private Integer page = 0;

    @ApiModelProperty(value = "实体名称")
    private String entityName = "";
    @ApiModelProperty(value = "实体label_id")
    private List<String> labelId;
}
