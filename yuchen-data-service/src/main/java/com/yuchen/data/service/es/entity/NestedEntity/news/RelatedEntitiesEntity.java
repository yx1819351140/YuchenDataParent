package com.yuchen.data.service.es.entity.NestedEntity.news;

import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Field;

import java.util.List;

@Data
public class RelatedEntitiesEntity {
    @Field(value = "entity_id")
    private String entityId;

    @Field(value = "entity_type")
    private String entityType;

    @Field(value = "link_score")
    private Double linkScore;

    private String words;

    @Field(value = "entity_mentions")
    private List<EntityMentionsEntity> entityMentions;
}
