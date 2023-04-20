package org.samsung.api.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
public class Response {

    @Getter
    @Setter
    private Integer code;

    @Getter
    @Setter
    private String message;

    @Builder
    public Response(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
