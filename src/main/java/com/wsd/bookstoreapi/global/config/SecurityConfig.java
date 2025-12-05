package com.wsd.bookstoreapi.global.config;

import com.wsd.bookstoreapi.global.security.jwt.JwtAuthenticationFilter;
import com.wsd.bookstoreapi.global.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                                .requestMatchers(
                                        "/health",
                                        "/swagger-ui/**",
                                        "/v3/api-docs/**",
                                        "/api/v1/auth/**",   // 로그인/회원가입 등은 열어 둘 예정
                                        "/api/v1/test/**"    // 테스트용은 편하게
                                ).permitAll()
                                // 이번 단계에서는 아직 전체 인증을 강제하지는 않음
                                .anyRequest().permitAll()
                        // 다음 단계에서: .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults());

        // 여기서 JWT 필터를 UsernamePasswordAuthenticationFilter 앞에 추가
        http.addFilterBefore(
                new JwtAuthenticationFilter(jwtTokenProvider),
                UsernamePasswordAuthenticationFilter.class
        );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // 로그인 시 비밀번호 해시에 사용할 예정
        return new BCryptPasswordEncoder();
    }
}
