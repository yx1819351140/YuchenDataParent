package com.yuchen.data.service.es.entity.NestedEntity.news;

import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Field;

@Data
public class NlpRelatedPlaceEntity {
    @Field(value = "place_lat")
    private String placeLat;

    private String country;

    @Field(value = "place_lon")
    private String placeLon;

    private String place;

    @Field(value = "place_id")
    private String placeId;
}
