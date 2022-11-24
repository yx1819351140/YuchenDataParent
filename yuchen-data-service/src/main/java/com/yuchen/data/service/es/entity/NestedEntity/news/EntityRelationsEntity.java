package com.yuchen.data.service.es.entity.NestedEntity.news;

import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Field;

@Data
public class EntityRelationsEntity {
    @Field(value = "edge_type")
    private String edgeType;

    private RelatedEntitiesEntity object;

    private RelatedEntitiesEntity subject;
}
