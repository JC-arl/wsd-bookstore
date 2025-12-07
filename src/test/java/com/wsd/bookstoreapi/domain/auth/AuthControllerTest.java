package com.wsd.bookstoreapi.domain.auth;

import com.fasterxml.jackson.databind.JsonNode;
import com.wsd.bookstoreapi.support.IntegrationTestSupport;
import com.wsd.bookstoreapi.support.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AuthControllerTest extends IntegrationTestSupport {

    @Autowired
    private TestDataFactory testDataFactory;

    @BeforeEach
    void setUp() {
        // 관리자 계정 생성
        testDataFactory.createAdminUser();
    }

    @Test
    @DisplayName("로그인 성공 - 올바른 이메일/비밀번호일 때 토큰 발급")
    void login_success() throws Exception {
        String requestBody = """
                {
                  "email": "admin@example.com",
                  "password": "1q2w3e4r"
                }
                """;

        String responseBody = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.payload.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.payload.refreshToken").isNotEmpty())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode root = objectMapper.readTree(responseBody);
        assertThat(root.path("payload").path("tokenType").asText())
                .isEqualTo("Bearer");
    }

    @Test
    @DisplayName("로그인 실패 - 비밀번호가 틀리면 401 반환")
    void login_fail_wrong_password() throws Exception {
        String requestBody = """
                {
                  "email": "admin@example.com",
                  "password": "WrongPassword"
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
    @DisplayName("리프레시 토큰이 유효하지 않으면 401 반환")
    void refresh_fail_invalid_token() throws Exception {
        String requestBody = """
                {
                  "refreshToken": "invalid-refresh-token"
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
