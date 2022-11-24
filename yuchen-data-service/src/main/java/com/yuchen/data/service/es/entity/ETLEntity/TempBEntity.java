package com.yuchen.data.service.es.entity.ETLEntity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

import java.util.List;

/**
 * @author hqp
 * @date 2022/8/179:25
 * @description
 */
@Data
@Document(indexName = "test_b", type = "_doc")
public class TempBEntity {

    @Id
    @Field(value = "id")
    private String id;

    @Field(value = "custom_semantic_labels")
    private  List<Long> custom_semantic_labels;

    @Field(value = "custom_labels")
    private  List<Long> custom_labels;

}