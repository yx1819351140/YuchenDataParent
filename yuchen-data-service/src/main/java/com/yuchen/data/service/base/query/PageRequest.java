package com.yuchen.data.service.base.query;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description 分页入参
 * @author: mazhenwei
 * @date: 2022年3月3日
 */
@Data
public class PageRequest {

    /**
     * 页码
     */
    @ApiModelProperty(value = "页码", example = "1")
    private Integer page;

    /**
     * 每页显示大小
     */
    @ApiModelProperty(value = "每页显示大小", example = "10")
    private Integer size;

    public Integer getPage() {
        return null == page ? 1 : page;
    }

    public Integer getSize() {
        return null == size ? 10 : size;
    }

}