package com.wsd.bookstoreapi.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Bookstore API")
                        .version("1.0.0")
                        .description("""
                                온라인 서점 시스템 RESTful API

                                ## 인증
                                - JWT Bearer Token 방식 사용
                                - 로그인(/auth/login)을 통해 토큰 발급
                                - Authorization 헤더에 `Bearer {token}` 형식으로 전송

                                ## API Root
                                - Local: http://localhost:8080/api/v1
                                - Production: http://113.198.66.68:10088/api/v1

                                ## 에러 응답 형식
                                모든 에러는 다음 형식으로 반환됩니다:
                                ```json
                                {
                                  "isSuccess": false,
                                  "message": "에러 메시지",
                                  "code": "ERROR_CODE",
                                  "payload": null
                                }
                                ```

                                ## 주요 에러 코드
                                - `UNAUTHORIZED`: 인증 실패 (401)
                                - `FORBIDDEN`: 권한 없음 (403)
                                - `RESOURCE_NOT_FOUND`: 리소스 없음 (404)
                                - `VALIDATION_FAILED`: 입력값 검증 실패 (400)
                                - `DUPLICATE_RESOURCE`: 중복 리소스 (409)
                                - `STATE_CONFLICT`: 상태 충돌 (409)
                                """))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("JWT 인증 토큰을 입력하세요 (Bearer 접두사 제외)"))
                        .addResponses("400BadRequest", createErrorResponse(
                                "잘못된 요청",
                                "BAD_REQUEST",
                                "요청 데이터가 올바르지 않습니다."))
                        .addResponses("401Unauthorized", createErrorResponse(
                                "인증 실패",
                                "UNAUTHORIZED",
                                "인증이 필요합니다. 로그인 후 다시 시도해주세요."))
                        .addResponses("403Forbidden", createErrorResponse(
                                "권한 없음",
                                "FORBIDDEN",
                                "접근 권한이 없습니다."))
                        .addResponses("404NotFound", createErrorResponse(
                                "리소스 없음",
                                "RESOURCE_NOT_FOUND",
                                "요청한 리소스를 찾을 수 없습니다."))
                        .addResponses("409Conflict", createErrorResponse(
                                "충돌",
                                "DUPLICATE_RESOURCE",
                                "이미 존재하는 리소스입니다."))
                        .addResponses("422UnprocessableEntity", createErrorResponse(
                                "처리 불가",
                                "UNPROCESSABLE_ENTITY",
                                "요청을 처리할 수 없습니다."))
                        .addResponses("500InternalServerError", createErrorResponse(
                                "서버 오류",
                                "INTERNAL_SERVER_ERROR",
                                "서버 내부 오류가 발생했습니다."))
                )
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }

    private ApiResponse createErrorResponse(String description, String code, String message) {
        Schema<?> errorSchema = new Schema<>()
                .type("object")
                .addProperty("isSuccess", new Schema<>().type("boolean").example(false))
                .addProperty("message", new Schema<>().type("string").example(message))
                .addProperty("code", new Schema<>().type("string").example(code))
                .addProperty("payload", new Schema<>().type("object").nullable(true).example(null));

        MediaType mediaType = new MediaType().schema(errorSchema);
        Content content = new Content().addMediaType("application/json", mediaType);

        return new ApiResponse()
                .description(description)
                .content(content);
    }
}
