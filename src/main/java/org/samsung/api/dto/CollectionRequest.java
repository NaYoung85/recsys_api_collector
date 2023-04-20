package org.samsung.api.dto;

import lombok.*;
import org.springframework.data.annotation.Id;

@ToString
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CollectionRequest {

    // add Requset body
    private String guid;

    private String visitorId;

    private String event;

    private String pageType;

    private String modelCode;

    private String consent;
}
