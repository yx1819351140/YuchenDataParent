package com.yuchen.data.service.es.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

@Data
@Document(indexName = "hot_entity_test_03", type = "_doc")
public class HotNewsEntity {

    @Id
    private String id;

    @Field(name="name_en")
    private String nameEn;

    @Field(name="name_zh")
    private String nameZh;

}
