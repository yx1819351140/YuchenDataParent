package com.yuchen.data.service.es.vo.ETLVo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeleteIndexVo {

    @ApiModelProperty(value = "需要清空的索引名字")
    private String indexName;

}
