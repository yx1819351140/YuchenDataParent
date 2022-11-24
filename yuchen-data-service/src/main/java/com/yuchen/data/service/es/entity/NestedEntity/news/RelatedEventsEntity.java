package com.yuchen.data.service.es.entity.NestedEntity.news;

import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Field;

import java.util.List;

@Data
public class RelatedEventsEntity {

    private List<ArgumentsEntity> arguments;

    @Field(value = "event_desc")
    private String eventDesc;

    @Field(value = "event_level")
    private String eventLevel;

    @Field(value = "event_id")
    private String eventId;

    @Field(value = "event_type")
    private String eventType;

    @Field(value = "event_type_level_1")
    private String eventTypeLevel1;

    @Field(value = "event_type_level_2")
    private String eventTypeLevel2;

    @Field(value = "event_type_level_3")
    private String eventTypeLevel3;

    @Field(value = "para_text")
    private String paraText;

    private String trigger;

    @Field(value = "trigger_end_pos")
    private String triggerEndPos;

    @Field(value = "trigger_start_pos")
    private String triggerStartPos;

    @Field(value = "sent_spans")
    private List<List<Integer>> sentSpans;

    private List<MentionsEntity> mentions;
}
