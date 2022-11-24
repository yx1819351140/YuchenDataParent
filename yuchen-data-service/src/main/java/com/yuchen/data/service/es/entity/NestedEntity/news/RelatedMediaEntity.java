package com.yuchen.data.service.es.entity.NestedEntity.news;

import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Field;

@Data
public class RelatedMediaEntity {
    @Field(value = "media_country")
    private String mediaCountry;

    @Field(value = "media_name_zh")
    private String mediaNameZh;

    @Field(value = "media_country_code")
    private String mediaCountryCode;

    @Field(value = "media_name")
    private String mediaName;

    @Field(value = "media_url")
    private String mediaUrl;

    @Field(value = "media_language")
    private String mediaLanguage;
}
