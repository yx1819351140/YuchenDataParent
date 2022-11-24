package com.yuchen.data.service.es.entity;

import com.yuchen.data.service.es.entity.NestedEntity.event.EventPlaceEntity;
import com.yuchen.data.service.es.entity.NestedEntity.event.MentionEntity;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.List;

/**
 * @author hqp
 * @date 2022/8/29
 * @description
 */
@Data
@Document(indexName = "hot_events_test_03", type = "_doc")
public class HotEvent {

    @Id
    @Field(value = "event_id")
    private String eventId;

    /**
     * 事件一级分类
     */
    @Field(value = "event_type_level_1")
    private String eventTypeLevel1;

    /**
     * 事件二级分类
     */
    @Field(value = "event_type_level_2")
    private String eventTypeLevel2;

    /**
     * 事件三级分类
     */
    @Field(value = "event_type_level_3")
    private String eventTypeLevel3;

    /**
     * mentions
     */
    @Field(value = "mentions",type = FieldType.Nested)
    private List<MentionEntity> mentions;

    /**
     * 句子位置
     */
    @Field(value = "sent_spans")
    private List<List<String>> sentSpans;

    /**
     * 事件等级（随机1-10级）
     */
    @Field(value = "event_level")
    private Integer eventLevel;

    /**
     * 设置列表，必须是keyword类型
     */
    @Field(value = "custom_labels" ,type = FieldType.Keyword)
    private List<Integer> customLabels;

    /**
     * 事件相关新闻
     */
    @Field(value = "event_desc")
    private String eventDesc;

    /**
     * 事件相关新闻
     */
    @Field(value = "related_news_id")
    private String relatedNewsId;

    /**
     * 事件相关实体列表
     */
    @Field(value = "related_entities")
    private List<String> relatedEntities;

    /**
     * 相关地点
     */
    @Field(value = "related_place",type = FieldType.Nested)
    private List<EventPlaceEntity> relatedPlace;

    /**
     * 事件要素之相关机构
     */
    @Field(value = "related_organ")
    private List<String> relatedOrgan;

    /**
     * 事件要素之相关人物
     */
    @Field(value = "related_person")
    private String relatedPerson;

    /**
     * 相关国家
     */
    @Field(value = "related_country")
    private String relatedCountry;

    /**
     * 事件发生时间
     */
    @Field(value = "event_time")
    private String eventTime;

    /**
     * 更新时间
     */
//    @Field(value = "update_time" ,type = FieldType.Date,format = DateFormat.custom,pattern="yyyy-MM-dd HH:mm:ss || yyyy-MM-dd || date_optional_time ||epoch_millis")
//    //取出格式
//    @JsonFormat(shape =JsonFormat.Shape.STRING,pattern ="yyyy-MM-dd HH:mm:ss",timezone ="GMT+8")
    @Field(value = "update_time" )
    private String updateTime;

}