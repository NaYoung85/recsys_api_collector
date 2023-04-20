package org.samsung.api.dto;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@ToString
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@RedisHash(value = "collection", timeToLive = 12000) // 120:2분
public class CollectionArray {

    @Id
    private String uniqueID;

    private String country;

    private String x_date;

    @Indexed
    private String guid;

    private String visitorId;

    private String event;

    private String pageType;

    private String modelCode;

    private String consent;

    @Builder
    public CollectionArray(String uniqueID, String country, String x_date, String guid, String visitorId, String event, String pageType, String modelCode, String consent) {
        this.uniqueID = uniqueID;
        this.country = country;
        this.x_date = x_date;
        this.guid = guid;
        this.visitorId = visitorId;
        this.event = event;
        this.pageType = pageType;
        this.modelCode = modelCode;
        this.consent = consent;
    }
}
