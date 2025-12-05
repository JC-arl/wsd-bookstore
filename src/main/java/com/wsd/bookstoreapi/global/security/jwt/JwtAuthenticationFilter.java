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

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

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
                // 토큰이 있고 아직 인증이 안 되어 있으면
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

                log.debug("JWT 인증 성공: userId={}, role={}, uri={}", userId, role, requestUri);
            }
        } catch (Exception e) {
            // 여기서 예외를 던지면 GlobalExceptionHandler로 전달되어 표준 에러 응답으로 변환됨
            log.warn("JWT 필터 처리 중 예외 발생: uri={}, message={}", requestUri, e.getMessage());
            // 그냥 던지면 됨 (BusinessException이면 우리가 만든 handler가 처리)
            throw e;
        }

        filterChain.doFilter(request, response);
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
