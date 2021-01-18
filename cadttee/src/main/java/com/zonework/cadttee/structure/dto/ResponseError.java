package com.zonework.cadttee.structure.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ResponseError {
    private final String title;
    private final int statusCode;
    private final long timestamp;
    private final Object error;
    private final String path;
}
