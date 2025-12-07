package com.wsd.bookstoreapi.global.rate;

import com.wsd.bookstoreapi.global.error.BusinessException;
import com.wsd.bookstoreapi.global.error.ErrorCode;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@Order(2) // LoggingFilter 다음에 동작
public class RateLimitingFilter extends OncePerRequestFilter {

    private static final int MAX_REQUESTS_PER_MINUTE = 30; // 필요에 따라 조정
    private static final long WINDOW_MILLIS = 60_000L;

    // key: IP + path, value: 카운터
    private final Map<String, WindowCounter> counters = new ConcurrentHashMap<>();

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();

        // 레이트리밋을 적용할 경로만 선별
        return !(path.startsWith("/api/v1/auth/login")
                || path.startsWith("/api/v1/auth/refresh")
                || path.startsWith("/api/v1/auth/signup"));
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String clientIp = getClientIp(request);
        String path = request.getRequestURI();
        String key = clientIp + ":" + path;

        long now = Instant.now().toEpochMilli();

        WindowCounter counter = counters.computeIfAbsent(key, k -> new WindowCounter(0, now));
        synchronized (counter) {
            // 윈도우 만료시 초기화
            if (now - counter.windowStart >= WINDOW_MILLIS) {
                counter.windowStart = now;
                counter.count = 0;
            }

            counter.count++;

            if (counter.count > MAX_REQUESTS_PER_MINUTE) {
                log.warn("Rate limit exceeded: ip={} path={}", clientIp, path);
                throw new BusinessException(
                        ErrorCode.TOO_MANY_REQUESTS,
                        "요청이 너무 많습니다. 잠시 후 다시 시도해 주세요."
                );
            }
        }

        filterChain.doFilter(request, response);
    }

    private String getClientIp(HttpServletRequest request) {
        String xf = request.getHeader("X-Forwarded-For");
        if (xf != null && !xf.isBlank()) {
            return xf.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private static class WindowCounter {
        int count;
        long windowStart;

        WindowCounter(int count, long windowStart) {
            this.count = count;
            this.windowStart = windowStart;
        }
    }
}
