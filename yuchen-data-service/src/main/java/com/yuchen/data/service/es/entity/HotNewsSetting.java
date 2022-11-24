package com.yuchen.data.service.es.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

import java.util.List;

@Data
@Document(indexName = "hot_news_test_03", type = "_doc")
public class HotNewsSetting {
    @Id
    private String id;

    private String title;

    private String text;

    /**
     * 用户自定义常规标签
     */
    @Field(name="custom_routine_labels")
    private List<String> customRoutineLabels;

    /**
     * 用户自定义语义标签
     */
    @Field(name="custom_semantic_labels")
    private List<String> customSemanticLabels;

    /**
     * 新闻对应任务id
     */
    @Field(name="task_ids")
    private List<String> taskIds;

    /**
     * 用户自定义标签
     */
    @Field(name="custom_labels")
    private List<Integer> customLabels;

    /**
     * 状态阶段
     */
    private Integer stage;

    /**
     * 发布时间
     */
    @Field(name="pub_time")
    private String pubTime;
}
