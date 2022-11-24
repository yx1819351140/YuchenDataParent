package com.yuchen.data.service.es.entity;

import com.yuchen.data.service.es.entity.NestedEntity.entity.LabelEntity;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.List;

@Data
@Document(indexName = "hot_entity_test_03", type = "_doc")
public class EntityType {
    @Id
    @Field(value = "id")
    private String id;

    @Field(value = "labels", type = FieldType.Nested)
    private List<LabelEntity> labels;
}
