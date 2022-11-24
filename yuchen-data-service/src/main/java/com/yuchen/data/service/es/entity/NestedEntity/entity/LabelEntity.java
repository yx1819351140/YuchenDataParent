package com.yuchen.data.service.es.entity.NestedEntity.entity;

import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Field;

/**
 * @author hqp
 * @date 2022/8/29
 * @description
 */
@Data
public class LabelEntity {

    @Field(value = "qid")
    private String qid;

    @Field(value = "label_id")
    private String label_id;

    @Field(value = "label_name_en")
    private String label_name_en;

    @Field(value = "label_name_zh")
    private String label_name_zh;

    @Field(value = "label_value")
    private String label_value;

    @Field(value = "attr1")
    private String attr1;

    @Field(value = "attr2")
    private String attr2;

    @Field(value = "attr3")
    private String attr3;

    @Field(value = "attr4")
    private String attr4;

    @Field(value = "attr5")
    private String attr5;

    @Field(value = "attr6")
    private String attr6;

    @Field(value = "attrInt")
    private String attrInt;

    @Field(value = "attrFloat")
    private String attrFloat;

    @Field(value = "attrDouble")
    private String attrDouble;

    @Field(value = "his")
    private String his;

    @Field(value = "up")
    private String up;

    @Field(value = "up_type")
    private String up_type;

    @Field(value = "version")
    private String version;
}
