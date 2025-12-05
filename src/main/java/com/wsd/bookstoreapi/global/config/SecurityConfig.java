package com.wsd.bookstoreapi.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // 헬스체크 & 문서 & 인증용 엔드포인트는 항상 허용
                        .requestMatchers(
                                "/health",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/api/v1/auth/**"
                        ).permitAll()
                        // 그 외 엔드포인트는 나중에 JWT 인증 필요하도록 수정 예정
                        .anyRequest().permitAll()   // → 다음 단계에서 .authenticated()로 변경 예정
                )
                .httpBasic(Customizer.withDefaults());

        // 나중에 여기 사이에 JWT 필터 추가 예정:
        // http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
