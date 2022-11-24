package com.yuchen.data.service.es.entity.NestedEntity.news;

import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Field;

@Data
public class AtomLabelsEntity {
    private String attr1;

    private String attr2;

    private String attr3;

    private String attr4;

    private String attr5;

    private String attr6;

    private String attrDouble;

    private String attrFloat;

    private String attrInt;

    private String his;

    @Field(value = "label_id")
    private String labelId;

    @Field(value = "label_name_en")
    private String labelNameEn;

    @Field(value = "label_name_zh")
    private String labelNameZh;

    @Field(value = "label_type")
    private String labelType;

    @Field(value = "label_value")
    private String labelValue;

    private String qid;

    private String version;
}
