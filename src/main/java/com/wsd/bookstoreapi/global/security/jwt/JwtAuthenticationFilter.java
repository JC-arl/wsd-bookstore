package com.wsd.bookstoreapi.global.security.jwt;

import com.wsd.bookstoreapi.global.security.UserPrincipal;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import com.wsd.bookstoreapi.domain.auth.service.RedisAuthTokenService;
import com.wsd.bookstoreapi.global.error.BusinessException;
import com.wsd.bookstoreapi.global.error.ErrorCode;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final RedisAuthTokenService redisAuthTokenService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String requestUri = request.getRequestURI();
        String authHeader = request.getHeader("Authorization");
        String token = resolveToken(authHeader);

        try {
            if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // 1) 블랙리스트 체크
                if (redisAuthTokenService.isAccessTokenBlacklisted(token)) {
                    writeUnauthorizedError(response, requestUri,
                            "로그아웃된 토큰입니다.");
                    return; // 더 이상 체인 진행 X
                }

                // 2) 토큰 유효성 검증
                jwtTokenProvider.validateToken(token);

                Long userId = jwtTokenProvider.getUserId(token);
                String role = jwtTokenProvider.getRole(token);
                String email = jwtTokenProvider.getEmail(token);

                UserPrincipal principal = new UserPrincipal(userId, email, role);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                principal,
                                null,
                                principal.getAuthorities()
                        );

                authentication.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

            filterChain.doFilter(request, response);

        } catch (BusinessException ex) {
            // JWT 관련 BusinessException도 여기서 동일 포맷으로 응답
            int status = ex.getErrorCode().getHttpStatus().value();
            writeErrorResponse(response, requestUri, status, ex.getErrorCode().name(), ex.getMessage());
        } catch (Exception ex) {
            writeErrorResponse(response, requestUri, 401, "UNAUTHORIZED", "유효하지 않은 토큰입니다.");
        }
    }
    private void writeUnauthorizedError(HttpServletResponse response,
                                        String path,
                                        String message) throws IOException {
        writeErrorResponse(response, path, 401, "UNAUTHORIZED", message);
    }

    private void writeErrorResponse(HttpServletResponse response,
                                    String path,
                                    int status,
                                    String code,
                                    String message) throws IOException {

        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");

        String body = """
            {
              "timestamp": "%s",
              "path": "%s",
              "status": %d,
              "code": "%s",
              "message": "%s",
              "details": {}
            }
            """.formatted(
                java.time.OffsetDateTime.now(),
                path,
                status,
                code,
                message.replace("\"", "\\\"")
        );

        response.getWriter().write(body);
    }

    private String resolveToken(String authHeader) {
        if (!StringUtils.hasText(authHeader)) {
            return null;
        }
        if (!authHeader.startsWith("Bearer ")) {
            return null;
        }
        return authHeader.substring(7);
    }


}
