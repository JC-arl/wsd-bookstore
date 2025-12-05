package com.wsd.bookstoreapi.global.health;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;

@RestController
public class HealthController {

    // application.properties에서 가져오기
    @Value("${spring.application.name:bookstore-api}")
    private String applicationName;

    // 버전은 나중에 build 설정에서 주입하거나, 일단 하드코딩해도 됨
    @Value("${app.version:0.0.1-SNAPSHOT}")
    private String version;

    @GetMapping("/health")
    public HealthResponse health() {
        return HealthResponse.builder()
                .status("UP")
                .application(applicationName)
                .version(version)
                .timestamp(OffsetDateTime.now())
                .build();
    }
}
