package com.wsd.bookstoreapi.global.health.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class HealthResponse {

    @Schema(description = "애플리케이션 이름", example = "bookstore-backend")
    private final String appName;

    @Schema(description = "어플리케이션 버전", example = "1.0.0")
    private final String version;

    @Schema(description = "현재 프로파일", example = "local")
    private final String profile;

    @Schema(description = "빌드/시작 시각 (문자열)", example = "2025-12-07T03:00:00")
    private final String startedAt;

    @Schema(description = "DB 연결 상태", example = "UP")
    private final String dbStatus;

    @Schema(description = "Redis 연결 상태", example = "UP")
    private final String redisStatus;
}
