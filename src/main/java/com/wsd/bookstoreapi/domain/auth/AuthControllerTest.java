package com.wsd.bookstoreapi.domain.auth;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wsd.bookstoreapi.support.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AuthControllerTest extends IntegrationTestSupport {

    @Test
    @DisplayName("로그인 성공 - 올바른 이메일/비밀번호일 때 토큰 발급")
    void login_success() throws Exception {
        // given: Flyway 시드 데이터에 있는 계정 사용 (예: admin@example.com / P@ssw0rd!)
        String requestBody = """
                {
                  "email": "admin@example.com",
                  "password": "P@ssw0rd!"
                }
                """;

        // when
        String responseBody = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.payload.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.payload.refreshToken").isNotEmpty())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // 추가적인 검증 (ObjectMapper 활용)
        ObjectMapper mapper = objectMapper;
        JsonNode root = mapper.readTree(responseBody);
        String tokenType = root.path("payload").path("tokenType").asText();
        assertThat(tokenType).isEqualTo("Bearer");
    }

    @Test
    @DisplayName("로그인 실패 - 비밀번호가 틀리면 401 반환")
    void login_fail_wrong_password() throws Exception {
        String requestBody = """
                {
                  "email": "admin@example.com",
                  "password": "wrong-password"
                }
                """;

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.isSuccess").value(false))
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
    }

    @Test
    @DisplayName("리프레시 토큰 없이 재발급 요청 시 401 반환")
    void refresh_fail_without_token() throws Exception {
        String requestBody = """
                {
                  "refreshToken": ""
                }
                """;

        mockMvc.perform(post("/api/v1/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.isSuccess").value(false))
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
    }
}
