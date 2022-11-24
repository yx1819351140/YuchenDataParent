package com.yuchen.data.service.es.entity.NestedEntity.news;

import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Field;

import java.util.List;

@Data
public class MentionsEntity {
    @Field(value = "doc_id")
    private String docId;

    @Field(value = "event_mention_id")
    private String eventMentionId;

    @Field(value = "event_type")
    private String eventType;

    @Field(value = "mention_type")
    private String mentionType;

    private String trigger;

    @Field(value = "trigger_start_pos")
    private Integer triggerStartPos;

    @Field(value = "trigger_end_pos")
    private Integer triggerEndPos;

    private List<ArgumentsEntity> arguments;

}
