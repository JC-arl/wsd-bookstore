package com.wsd.bookstoreapi.global.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Getter
@Builder
public class ErrorResponse {

    private final String timestamp;
    private final String path;
    private final int status;
    private final String code;
    private final String message;

    // 상세 필드는 없을 수도 있으므로 null인 경우는 JSON에서 제외
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final Map<String, Object> details;

    public static ErrorResponse of(String path, ErrorCode errorCode, String message, Map<String, Object> details) {
        return ErrorResponse.builder()
                .timestamp(OffsetDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME))
                .path(path)
                .status(errorCode.getHttpStatus().value())
                .code(errorCode.getCode())
                .message(message != null ? message : errorCode.getDefaultMessage())
                .details(details)
                .build();
    }

    public static ErrorResponse of(String path, ErrorCode errorCode, Map<String, Object> details) {
        return of(path, errorCode, null, details);
    }

    public static ErrorResponse of(String path, ErrorCode errorCode) {
        return of(path, errorCode, null, null);
    }
}
