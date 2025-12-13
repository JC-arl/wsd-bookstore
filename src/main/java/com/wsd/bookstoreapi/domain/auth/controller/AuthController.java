package com.wsd.bookstoreapi.domain.auth.controller;

import com.wsd.bookstoreapi.domain.auth.dto.AuthResponse;
import com.wsd.bookstoreapi.domain.auth.dto.LoginRequest;
import com.wsd.bookstoreapi.domain.auth.dto.RefreshTokenRequest;
import com.wsd.bookstoreapi.domain.auth.dto.SignUpRequest;
import com.wsd.bookstoreapi.domain.auth.service.AuthService;
import com.wsd.bookstoreapi.global.api.ApiResult;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.HashMap;
import java.util.Map;


@Tag(name = "Auth", description = "인증 / 토큰 발급 / 로그아웃 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;
    private final PasswordEncoder passwordEncoder;

    @Operation(summary = "회원가입", description = "새로운 사용자 계정을 생성합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "회원가입 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": true,
                                      "message": "회원가입이 완료되었습니다.",
                                      "code": null,
                                      "payload": null
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "유효하지 않은 요청 데이터",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": false,
                                      "message": "입력값 검증에 실패했습니다.",
                                      "code": "VALIDATION_FAILED",
                                      "payload": null
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "중복된 이메일",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": false,
                                      "message": "이미 존재하는 리소스입니다.",
                                      "code": "DUPLICATE_RESOURCE",
                                      "payload": null
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "요청 형식 오류",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": false,
                                      "message": "처리할 수 없는 요청입니다.",
                                      "code": "UNPROCESSABLE_ENTITY",
                                      "payload": null
                                    }
                                    """)
                    )
            )
    })
    @PostMapping("/signup")
    public ResponseEntity<ApiResult<Void>> signUp(@Valid @RequestBody SignUpRequest request) {
        authService.signUp(request);
        ApiResult<Void> apiResult = ApiResult.successMessage("회원가입이 완료되었습니다.");
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResult);
    }

    @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인하고 Access/Refresh Token을 발급합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "로그인 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": true,
                                      "message": "로그인에 성공했습니다.",
                                      "code": null,
                                      "payload": {
                                        "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c2VyQGV4YW1wbGUuY29tIn0.abc123",
                                        "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c2VyQGV4YW1wbGUuY29tIn0.def456",
                                        "tokenType": "Bearer"
                                      }
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "이메일 또는 비밀번호 불일치",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": false,
                                      "message": "이메일 또는 비밀번호가 일치하지 않습니다.",
                                      "code": "UNAUTHORIZED",
                                      "payload": null
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "요청 형식 오류",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": false,
                                      "message": "처리할 수 없는 요청입니다.",
                                      "code": "UNPROCESSABLE_ENTITY",
                                      "payload": null
                                    }
                                    """)
                    )
            )
    })
    @PostMapping("/login")
    public ResponseEntity<ApiResult<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse authResponse = authService.login(request);

        ApiResult<AuthResponse> apiResult = ApiResult.success(
                authResponse,
                "로그인에 성공했습니다."
        );
        return ResponseEntity.ok(apiResult);
    }

    @Operation(summary = "토큰 재발급", description = "유효한 Refresh Token으로 Access/Refresh Token을 재발급합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "재발급 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": true,
                                      "message": "토큰이 성공적으로 재발급되었습니다.",
                                      "code": null,
                                      "payload": {
                                        "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c2VyQGV4YW1wbGUuY29tIn0.new123",
                                        "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c2VyQGV4YW1wbGUuY29tIn0.new456",
                                        "tokenType": "Bearer"
                                      }
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "로그인이 필요하거나 Refresh Token이 유효하지 않음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": false,
                                      "message": "인증이 필요합니다.",
                                      "code": "UNAUTHORIZED",
                                      "payload": null
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "비활성화된 계정",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": false,
                                      "message": "접근 권한이 없습니다.",
                                      "code": "FORBIDDEN",
                                      "payload": null
                                    }
                                    """)
                    )
            )
    })
    @PostMapping("/refresh")
    public ResponseEntity<ApiResult<AuthResponse>> refreshToken(
            @Valid @RequestBody RefreshTokenRequest request
    ) {
        AuthResponse authResponse = authService.refreshToken(request);

        ApiResult<AuthResponse> apiResult = ApiResult.success(
                authResponse,
                "토큰이 성공적으로 재발급되었습니다."
        );
        return ResponseEntity.ok(apiResult);
    }

    @Operation(summary = "로그아웃", description = "현재 Access Token을 블랙리스트에 등록하고 Refresh Token을 삭제합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "로그아웃 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": true,
                                      "message": "로그아웃 되었습니다.",
                                      "code": null,
                                      "payload": null
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Authorization 헤더 형식 오류",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": false,
                                      "message": "잘못된 요청입니다.",
                                      "code": "BAD_REQUEST",
                                      "payload": null
                                    }
                                    """)
                    )
            )
    })
    @PostMapping("/logout")
    public ResponseEntity<ApiResult<Void>> logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        authService.logout(authHeader);

        ApiResult<Void> apiResult = ApiResult.successMessage("로그아웃 되었습니다.");
        return ResponseEntity.ok(apiResult);
    }

    // 임시 테스트 엔드포인트 - BCrypt 해시 생성 및 검증
    @Operation(summary = "[테스트] BCrypt 해시 생성 및 검증", description = "password123의 BCrypt 해시를 생성하고 기존 해시와 비교합니다.")
    @GetMapping("/test-bcrypt")
    public ResponseEntity<Map<String, Object>> testBcrypt() {
        Map<String, Object> result = new HashMap<>();

        try {
            String rawPassword = "password123";
            String dbHash = "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy";

            result.put("rawPassword", rawPassword);
            result.put("dbHash", dbHash);
            result.put("passwordEncoderExists", passwordEncoder != null);

            if (passwordEncoder != null) {
                result.put("dbHashMatches", passwordEncoder.matches(rawPassword, dbHash));
                result.put("newGeneratedHash1", passwordEncoder.encode(rawPassword));
                result.put("newGeneratedHash2", passwordEncoder.encode(rawPassword));
                result.put("newGeneratedHash3", passwordEncoder.encode(rawPassword));
            } else {
                result.put("error", "PasswordEncoder is null");
            }

            result.put("success", true);
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
            result.put("errorClass", e.getClass().getName());
        }

        return ResponseEntity.ok(result);
    }
}
