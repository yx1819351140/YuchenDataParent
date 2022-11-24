package com.yuchen.data.service.es.entity.NestedEntity.event;

import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Field;

/**
 * @author hqp
 * @date 2022/8/29
 * @description
 */
@Data
public class EventPlaceEntity {

    @Field(value = "country")
    private String country;

    @Field(value = "place_fullname")
    private String placeFullname;

    @Field(value = "place")
    private String place;

    @Field(value = "lat")
    private String lat;

    @Field(value = "long")
    private String lon;
}
