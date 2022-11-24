package com.yuchen.data.service.es.entity.ETLEntity;

import com.yuchen.data.service.es.entity.NestedEntity.news.RelatedMediaEntity;
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
@Document(indexName = "part_a_temp", type = "_doc")
public class PartATempEntity {


    @Id
    @Field(value = "title_id")
    private String title_id;

    @Field(value = "title")
    private String title;

    @Field(value = "context")
    private String context;

    @Field(value = "pub_time")
    private  String pub_time;

    @Field(value = "update_time")
    private  String update_time;

    @Field(value = "score")
    private  String score;

    @Field(value = "new_score")
    private  String new_score;

    @Field(value = "task_ids")
    private List<Long> task_ids;

    @Field(value = "custom_routine_labels")
    private List<Long> custom_routine_labels;

    @Field(value = "custom_labels")
    private List<Long> custom_labels;

    /**
     * 新闻发布媒体，jsonArray，由相关媒体名称、国家等组成
     */
    @Field(name="related_media")
    private List<RelatedMediaEntity> relatedMedia;
}