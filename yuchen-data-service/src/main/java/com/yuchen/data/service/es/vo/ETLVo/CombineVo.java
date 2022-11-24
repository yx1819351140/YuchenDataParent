package com.yuchen.data.service.es.vo.ETLVo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CombineVo {

    @ApiModelProperty(value = "新闻title_id")
    private String id;

    @ApiModelProperty(value = "semantic_labels")
    private List<Long> semanticLabels;
}
