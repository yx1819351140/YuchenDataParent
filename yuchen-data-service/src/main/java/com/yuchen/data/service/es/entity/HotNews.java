package com.yuchen.data.service.es.entity;

import com.yuchen.data.service.es.entity.NestedEntity.news.*;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

import java.util.List;

/**
 * @author yangliqiang
 * @date 2022/8/179:25
 * @description
 */
@Data
@Document(indexName = "hot_news_test_03", type = "_doc")
public class HotNews {

    @Id
    private String id;

    private String title;

    private String text;

    /**
     * 标题中文
     */
    @Field(name="title_zh")
    private String titleZh;

    /**
     * 文章摘要
     */
    private String summary;

    /**
     * base_url
     */
    private String url;

    /**
     * 新闻发布媒体，jsonArray，由相关媒体名称、国家等组成
     */
    @Field(name="related_media")
    private List<RelatedMediaEntity> relatedMedia;

    /**
     * 新闻热度（转发量）
     */
    @Field(name="hot_point")
    private Integer hotPoint;

    /**
     * 新闻情感程度
     */
    @Field(name="v1_5tone_tone")
    private Double v15toneTone;

    /**
     * 新闻图片
     */
    @Field(name="image_url")
    private String imageUrl;

    /**
     * 新闻相关标签
     */
    @Field(name="news_category")
    private List<String> newsCategory;

    /**
     * 新闻创建日期，年月日时分秒
     */
    @Field(name="pub_time")
    private String pubTime;

    /**
     * 新闻创建日期，时分秒
     */
    @Field(name="pub_time_hms")
    private String pubTimeHms;

    /**
     * 新闻创建时间，时间戳
     */
    @Field(name="pubTime_timestamp")
    private Long pubTimeTimestamp;

    /**
     * 新闻相关国家
     */
    @Field(name="related_country")
    private List<String> relatedCountry;

    /**
     * 新闻相关地点
     */
    @Field(name="related_location")
    private List<String> relatedLocation;

    /**
     * 新闻相关组织
     */
    @Field(name="related_organ")
    private List<String> relatedOrgan;

    /**
     * 新闻相关人物
     */
    @Field(name="related_person")
    private List<String> relatedPerson;

    @Field(name="related_propername")
    private List<String> relatedPropername;

    /**
     * 新闻相关url
     */
    @Field(name="related_url")
    private List<String> relatedUrl;

    /**
     * 记录创建时间
     */
    @Field(name="create_time")
    private String createTime;

    /**
     * 记录创建时间日期分区字段，年月日
     */
    @Field(name="partition_date")
    private String partitionDate;

    /**
     * 新闻权重评分
     */
    private Double score;

    /**
     * 文章中文
     */
    @Field(name="text_zh")
    private String textZh;

    /**
     * 相关实体
     */
    @Field(name="related_entities")
    private List<RelatedEntitiesEntity> relatedEntities;

    /**
     * 相关事件
     */
    @Field(name="related_events")
    private List<RelatedEventsEntity> relatedEvents;

    /**
     * 相关实体之间的关系
     */
    @Field(name="entity_relations")
    private List<EntityRelationsEntity> entityRelations;

    /**
     * 相关事件对应实体id
     */
    @Field(name="related_event_entities_list")
    private List<String> relatedEventEntitiesList;

    /**
     * 相关实体id
     */
    @Field(name="related_entities_list")
    private List<String> relatedEntitiesList;

    /**
     * 相关事件id
     */
    @Field(name="related_events_list")
    private List<String> relatedEventsList;

    /**
     * 相关主要事件id
     */
    @Field(name="related_main_events_list")
    private List<String> relatedMainEventsList;

    /**
     * nlp处理之后新闻相关国家
     */
    @Field(name="nlp_related_country")
    private List<String> nlpRelatedCountry;

    /**
     * nlp处理之后新闻相关地点
     */
    @Field(name="nlp_related_location")
    private List<String> nlpRelatedLocation;

    /**
     * nlp处理之后新闻相关组织
     */
    @Field(name="nlp_related_organ")
    private List<String> nlpRelatedOrgan;

    /**
     * nlp处理之后新闻相关人物
     */
    @Field(name="nlp_related_person")
    private List<String> nlpRelatedPerson;

    /**
     * nlp处理之后新闻相关地点（详细信息）
     */
    @Field(name="nlp_related_place")
    private List<NlpRelatedPlaceEntity> nlpRelatedPlace;

    /**
     * nlp处理之后新闻相关空中目标
     */
    @Field(name="nlp_related_air_target")
    private List<String> nlpRelatedAirTarget;

    /**
     * nlp处理之后新闻相关海上目标
     */
    @Field(name="nlp_related_sea_target")
    private List<String> nlpRelatedSeaTarget;

    /**
     * nlp处理之后新闻相关军事目标
     */
    @Field(name="nlp_related_mil_target")
    private List<String> nlpRelatedMilTarget;

    /**
     * nlp处理之后新闻相关基地
     */
    @Field(name="nlp_related_base")
    private List<String> nlpRelatedBase;

    /**
     * nlp处理之后新闻相关军队
     */
    @Field(name="nlp_related_army")
    private List<String> nlpRelatedArmy;

    /**
     * nlp处理之后新闻相关武器
     */
    @Field(name="nlp_related_weapon")
    private List<String> nlpRelatedWeapon;

    /**
     * nlp处理之后新闻相关建筑
     */
    @Field(name="nlp_related_building")
    private List<String> nlpRelatedBuilding;

    /**
     * 原子标签
     */
    @Field(name="atom_labels")
    private List<AtomLabelsEntity> atomLabels;

    /**
     * 用户自定义任务id
     */
    @Field(name="task_ids")
    private List<Long> task_ids;

    /**
     * 用户自定义常规标签
     */
    @Field(name="custom_routine_labels")
    private List<Long> customRoutineLabels;

    /**
     * 用户自定义语义标签
     */
    @Field(name="custom_semantic_labels")
    private List<Long> customSemanticLabels;

    /**
     * 用户自定义标签
     */
    @Field(name="custom_labels")
    private List<Long> customLabels;

    /**
     * 记录更新时间
     */
    @Field(name="update_time")
    private String updateTime;

    /**
     * 新闻重新评分
     */
    @Field(name="new_score")
    private Double newScore;

    /**
     * 记录阶段状态
     */
    private Integer stage;
}