package com.yuchen.data.service.es.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Data
@Document(indexName = "hot_news_test_03", type = "_doc")
public class HotNewsScore {

    @Id
    private String id;

    private Double score;
}
