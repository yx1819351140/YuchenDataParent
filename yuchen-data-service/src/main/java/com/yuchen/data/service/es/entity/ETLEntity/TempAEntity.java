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
@Document(indexName = "test_a", type = "_doc")
public class TempAEntity {

    @Id
    @Field(value = "id")
    private String id;

    @Field(value = "title")
    private String title;

    @Field(value = "text")
    private String text;

    @Field(value = "pub_time")
    private String pub_time;

    @Field(value = "score")
    private  String score;

    @Field(value = "new_score")
    private  String new_score;

    @Field(value = "task_ids")
    private  List<Long> task_ids;

    @Field(value = "custom_routine_labels")
    private  List<Long> custom_routine_labels;

    @Field(value = "custom_labels")
    private  List<Long> custom_labels;

    @Field(value = "stage")
    private  Integer stage;

}