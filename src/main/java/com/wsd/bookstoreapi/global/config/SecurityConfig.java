package com.wsd.bookstoreapi.global.config;

import com.wsd.bookstoreapi.global.security.JwtAuthenticationEntryPoint;
import com.wsd.bookstoreapi.global.security.jwt.JwtAuthenticationFilter;
import com.wsd.bookstoreapi.global.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.wsd.bookstoreapi.domain.auth.service.RedisAuthTokenService;
import org.springframework.http.HttpMethod;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final RedisAuthTokenService redisAuthTokenService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                )
                .authorizeHttpRequests(auth -> auth
                        // 헬스체크, 문서, 인증용 엔드포인트는 항상 허용
                        .requestMatchers(
                                "/health",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/api/v1/auth/**"
                        ).permitAll()

                        // 관리자 전용 API
                        .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/books/**").permitAll()
                        // 그 외 모든 API는 인증 필요
                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults());

        // JWT 필터 연결
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}