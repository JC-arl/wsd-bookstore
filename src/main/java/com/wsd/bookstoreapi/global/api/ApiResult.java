package com.wsd.bookstoreapi.global.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wsd.bookstoreapi.global.error.ErrorCode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class ApiResult<T> {

    @JsonProperty("isSuccess")
    @Schema(description = "요청 성공 여부", example = "true")
    private final boolean isSuccess;

    @JsonProperty("success")
    @Schema(description = "요청 성공 여부 (호환용)", example = "true")
    private final boolean success;

    @Schema(description = "결과 메시지", example = "요청이 성공적으로 처리되었습니다.")
    private final String message;

    @Schema(description = "에러 코드 (성공 시 null)", example = "UNAUTHORIZED")
    private final String code;

    @Schema(description = "실제 응답 데이터(payload)")
    private final T payload;

    private ApiResult(boolean isSuccess,
                      boolean success,
                      String message,
                      String code,
                      T payload) {
        this.isSuccess = isSuccess;
        this.success = success;
        this.message = message;
        this.code = code;
        this.payload = payload;
    }

    /* ========= 성공 응답 ========= */

    public static <T> ApiResult<T> success(T payload, String message) {
        return new ApiResult<>(
                true,
                true,
                message,
                null,   // 에러 코드 없음
                payload
        );
    }

    public static <T> ApiResult<T> success(T payload) {
        return success(payload, "요청이 성공적으로 처리되었습니다.");
    }

    public static ApiResult<Void> successMessage(String message) {
        return new ApiResult<>(
                true,
                true,
                message,
                null,
                null
        );
    }

    /* ========= 에러 응답 ========= */

    // payload 없는 에러 응답
    public static ApiResult<Void> error(ErrorCode errorCode, String message) {
        String msg = (message != null && !message.isBlank())
                ? message
                : errorCode.getDefaultMessage();

        return new ApiResult<>(
                false,
                false,
                msg,
                errorCode.getCode(),   // "UNAUTHORIZED", "RESOURCE_NOT_FOUND" 등
                null
        );
    }

    // payload 포함 에러 응답 (예: validation errors map)
    public static <T> ApiResult<T> error(ErrorCode errorCode, String message, T payload) {
        String msg = (message != null && !message.isBlank())
                ? message
                : errorCode.getDefaultMessage();

        return new ApiResult<>(
                false,
                false,
                msg,
                errorCode.getCode(),
                payload
        );
    }

    // 단순 메시지 기반 실패 (ErrorCode 없이)
    public static ApiResult<Void> failure(String message) {
        return new ApiResult<>(
                false,
                false,
                message,
                null,
                null
        );
    }
}
