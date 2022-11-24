package com.yuchen.data.service.es.entity.NestedEntity.news;

import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Field;

@Data
public class ArgumentsEntity {
    private String nid;

    private String role;

    private String words;

    @Field(value = "start_pos")
    private Integer startPos;

    @Field(value = "end_pos")
    private Integer endPos;
}
