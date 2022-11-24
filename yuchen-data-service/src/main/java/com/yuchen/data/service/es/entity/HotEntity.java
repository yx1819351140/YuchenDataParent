package com.yuchen.data.service.es.entity;

import com.yuchen.data.service.es.entity.NestedEntity.entity.LabelEntity;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.List;

/**
 * @author hqp
 * @date 2022/8/179:25
 * @description
 */
@Data
@Document(indexName = "hot_entity_test_03", type = "_doc")
public class HotEntity {

    @Id
    @Field(value = "id")
    private String id;

    @Field(value = "name_en")
    private String nameEn;

    @Field(value = "name_zh")
    private String nameZh;

    @Field(value = "alias")
    private  List<String> alias;

    @Field(value = "labels",type = FieldType.Nested)
    private List<LabelEntity> labels;

}