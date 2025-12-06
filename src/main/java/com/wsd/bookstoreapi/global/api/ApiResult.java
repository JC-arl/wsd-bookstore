package com.wsd.bookstoreapi.global.api;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ApiResult<T> {

    @Schema(description = "요청 성공 여부", example = "true")
    private final boolean isSuccess;

    @Schema(description = "결과 메시지", example = "요청이 성공적으로 처리되었습니다.")
    private final String message;

    @Schema(description = "실제 응답 데이터(payload)")
    private final T payload;

    public static <T> ApiResult<T> success(T payload, String message) {
        return ApiResult.<T>builder()
                .isSuccess(true)
                .message(message)
                .payload(payload)
                .build();
    }

    public static <T> ApiResult<T> success(T payload) {
        return success(payload, "요청이 성공적으로 처리되었습니다.");
    }

    public static ApiResult<Void> successMessage(String message) {
        return ApiResult.<Void>builder()
                .isSuccess(true)
                .message(message)
                .payload(null)
                .build();
    }

    public static ApiResult<Void> failure(String message) {
        return ApiResult.<Void>builder()
                .isSuccess(false)
                .message(message)
                .payload(null)
                .build();
    }
}
