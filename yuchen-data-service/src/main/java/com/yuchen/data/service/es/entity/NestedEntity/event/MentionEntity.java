package com.yuchen.data.service.es.entity.NestedEntity.event;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Field;

import java.util.List;

/**
 * @author hqp
 * @date 2022/8/29
 * @description
 */
@Data
public class MentionEntity {
    @Id
    @Field(value = "event_mention_id")
    private String eventMentionId;

    /**
     * 事件类型
     */
    @Field(value = "event_type")
    private String eventType;

    /**
     * 触发词
     */
    @Field(value = "trigger")
    private String trigger;

    /**
     * 触发词起始位置
     */
    @Field(value = "trigger_start_pos")
    private Long triggerStartPos;

    /**
     * 触发词结束位置
     */
    @Field(value = "trigger_end_pos")
    private Long triggerEndPos;

    /**
     * 相关新闻ID
     */
    @Field(value = "doc_id")
    private String docId;

    /**
     * mention类型
     */
    @Field(value = "mention_type")
    private String mentionType;

    /**
     * 论元
     */
//    @Field(value = "arguments")
    private List<MentionArgumentsEntity> arguments;
}
