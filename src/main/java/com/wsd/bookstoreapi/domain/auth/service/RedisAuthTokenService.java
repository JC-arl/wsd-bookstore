package com.wsd.bookstoreapi.domain.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RedisAuthTokenService {

    private final StringRedisTemplate stringRedisTemplate;

    // key prefix 들
    private static final String REFRESH_TOKEN_KEY_PREFIX = "RT:";
    private static final String BLACKLIST_KEY_PREFIX = "BL:";

    /**
     * Refresh Token 저장
     * key: RT:<userId>
     */
    public void saveRefreshToken(Long userId, String refreshToken, long ttlMillis) {
        String key = REFRESH_TOKEN_KEY_PREFIX + userId;
        stringRedisTemplate.opsForValue().set(
                key,
                refreshToken,
                Duration.ofMillis(ttlMillis)
        );
    }

    /**
     * Refresh Token 조회
     */
    public Optional<String> getRefreshToken(Long userId) {
        String key = REFRESH_TOKEN_KEY_PREFIX + userId;
        String value = stringRedisTemplate.opsForValue().get(key);
        return Optional.ofNullable(value);
    }

    /**
     * Refresh Token 삭제 (로그아웃 등)
     */
    public void deleteRefreshToken(Long userId) {
        String key = REFRESH_TOKEN_KEY_PREFIX + userId;
        stringRedisTemplate.delete(key);
    }

    /**
     * Access Token 블랙리스트 등록
     * key: BL:<accessToken>, value: "logout"
     */
    public void blacklistAccessToken(String accessToken, long ttlMillis) {
        if (ttlMillis <= 0) {
            return; // 이미 만료임
        }
        String key = BLACKLIST_KEY_PREFIX + accessToken;
        stringRedisTemplate.opsForValue().set(
                key,
                "logout",
                Duration.ofMillis(ttlMillis)
        );
    }

    /**
     * Access Token이 블랙리스트인지 확인
     */
    public boolean isAccessTokenBlacklisted(String accessToken) {
        String key = BLACKLIST_KEY_PREFIX + accessToken;
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(key));
    }
}
