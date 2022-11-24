package com.yuchen.data.service.es.entity.NestedEntity.event;

import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Field;

/**
 * @author hqp
 * @date 2022/8/29
 * @description
 */
@Data
public class MentionArgumentsEntity {

    @Field(value = "nid")
    private String nid;

    @Field(value = "role")
    private String role;

    @Field(value = "words")
    private String words;

    @Field(value = "end_pos")
    private long endPos;

    @Field(value = "start_pos")
    private long startPos;
}
