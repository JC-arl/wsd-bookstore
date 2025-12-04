//package com.wsd.bookstoreapi.global.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.Customizer;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.web.SecurityFilterChain;
//
//@Configuration
//public class SecurityConfig {
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//
//        http
//                // JWT 붙이기 전까지는 csrf 비활성화 + 전부 허용
//                .csrf(csrf -> csrf.disable())
//                .authorizeHttpRequests(auth -> auth
//                        .anyRequest().permitAll()
//                )
//                // 기본 로그인/로그아웃 기능 비활성화 (지금은 필요 없음)
//                .httpBasic(Customizer.withDefaults());
//
//        return http.build();
//    }
//}
