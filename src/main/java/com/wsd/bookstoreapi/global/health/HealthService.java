package com.wsd.bookstoreapi.global.health;

import com.wsd.bookstoreapi.global.health.dto.HealthResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class HealthService {

    private final JdbcTemplate jdbcTemplate;
    private final RedisTemplate<String, String> redisTemplate;

    @Value("${spring.application.name:bookstore-backend}")
    private String appName;

    @Value("${app.version:1.0.0}")
    private String version;

    @Value("${spring.profiles.active:local}")
    private String profile;

    private final Instant startedAt = Instant.now();

    public HealthResponse getHealth() {
        String dbStatus = checkDb();
        String redisStatus = checkRedis();

        return HealthResponse.builder()
                .appName(appName)
                .version(version)
                .profile(profile)
                .startedAt(startedAt.toString())
                .dbStatus(dbStatus)
                .redisStatus(redisStatus)
                .build();
    }

    private String checkDb() {
        try {
            jdbcTemplate.execute("SELECT 1");
            return "UP";
        } catch (Exception ex) {
            return "DOWN";
        }
    }

    private String checkRedis() {
        try {
            String pingResult = redisTemplate.getConnectionFactory()
                    .getConnection()
                    .ping();
            return ("PONG".equalsIgnoreCase(pingResult)) ? "UP" : "DOWN";
        } catch (Exception ex) {
            return "DOWN";
        }
    }
}
