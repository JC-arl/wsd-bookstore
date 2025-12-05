package com.wsd.bookstoreapi.domain.auth.controller;

import com.wsd.bookstoreapi.domain.auth.dto.AuthResponse;
import com.wsd.bookstoreapi.domain.auth.dto.LoginRequest;
import com.wsd.bookstoreapi.domain.auth.dto.RefreshTokenRequest;
import com.wsd.bookstoreapi.domain.auth.dto.SignUpRequest;
import com.wsd.bookstoreapi.domain.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;


@Tag(name = "Auth", description = "인증 / 토큰 발급 / 로그아웃 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    /**
     * 회원가입
     * POST /api/v1/auth/signup
     */
    @PostMapping("/signup")
    public ResponseEntity<Void> signUp(@Valid @RequestBody SignUpRequest request) {
        authService.signUp(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 로그인 (JWT 발급)
     * POST /api/v1/auth/login
     */
    @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인하고 Access/Refresh Token을 발급합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "401", description = "이메일 또는 비밀번호 불일치"),
            @ApiResponse(responseCode = "422", description = "요청 형식 오류")
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Refresh Token으로 Access Token 재발급
     * POST /api/v1/auth/refresh
     */
    @Operation(summary = "토큰 재발급", description = "유효한 Refresh Token으로 Access/Refresh Token을 재발급합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "재발급 성공"),
            @ApiResponse(responseCode = "401", description = "로그인이 필요하거나 Refresh Token이 유효하지 않음"),
            @ApiResponse(responseCode = "403", description = "비활성화된 계정"),
    })
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        AuthResponse response = authService.refreshToken(request);
        return ResponseEntity.ok(response);
    }
    /**
     * 로그아웃
     * POST /api/v1/auth/logout
     * - Authorization: Bearer <accessToken> 헤더 필요
     */
    @Operation(summary = "로그아웃", description = "현재 Access Token을 블랙리스트에 등록하고 Refresh Token을 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "로그아웃 성공"),
            @ApiResponse(responseCode = "400", description = "Authorization 헤더 형식 오류")
    })
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        authService.logout(authHeader);
        return ResponseEntity.noContent().build();
    }

}
