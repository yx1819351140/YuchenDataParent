package com.yuchen.data.service.es.entity.NestedEntity.news;

import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Field;

@Data
public class EntityMentionsEntity {
    @Field(value = "doc_id")
    private String docId;

    private String nid;

    @Field(value = "entity_mention_id")
    private String entityMentionId;

    @Field(value = "entity_type")
    private String entityType;

    @Field(value = "mention_type")
    private String mentionType;

    @Field(value = "sub_type")
    private String subType;

    private String words;

    @Field(value = "start_pos")
    private Integer startPos;

    @Field(value = "end_pos")
    private Integer endPos;
}
