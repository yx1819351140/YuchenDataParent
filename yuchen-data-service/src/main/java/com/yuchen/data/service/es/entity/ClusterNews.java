package com.yuchen.data.service.es.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

@Data
@Document(indexName = "hot_news_test_03", type = "_doc")
public class ClusterNews {

    @Id
    private String id;

    private String title;

    private String text;

    /**
     * 发布时间
     */
    @Field(name="pub_time")
    private String pubTime;
}
