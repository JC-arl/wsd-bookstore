package com.wsd.bookstoreapi.global.health;

import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;

@Getter
@Builder
public class HealthResponse {

    private final String status;         // "UP" / "DOWN"
    private final String application;    // 애플리케이션 이름
    private final String version;        // 어플리케이션 버전 (임시로 하드코딩 가능)
    private final OffsetDateTime timestamp;

    // 나중에 필요하다면 profile, dbStatus, redisStatus 등도 추가 가능
}
