package com.example.devkorproject.common.dto;

import com.example.devkorproject.common.constants.ErrorCode;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class HttpResponse {
    private final Boolean success;
    private final Integer errorCode;
    private final String message;
    public static HttpResponse of(Boolean success, Integer errorCode, String message) {
        return new HttpResponse(success, errorCode, message);
    }

    public static HttpResponse of(Boolean success, ErrorCode errorCode) {
        return new HttpResponse(success, errorCode.getCode(), errorCode.getMessage());
    }

    public static HttpResponse of(Boolean success, ErrorCode errorCode, Exception e) {
        return new HttpResponse(success, errorCode.getCode(), errorCode.getMessage(e));
    }

    public static HttpResponse of(Boolean success, ErrorCode errorCode, String message) {
        return new HttpResponse(success, errorCode.getCode(), errorCode.getMessage(message));
    }
}
