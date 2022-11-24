package com.yuchen.data.service.es.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

import java.util.List;

@Data
@Document(indexName = "hot_events_test_03", type = "_doc")
public class HotEventsSetting {
    @Id
    private String id;

    /**
     * 用户自定义标签
     */
    @Field(name="custom_labels")
    private List<Integer> customLabels;
}
