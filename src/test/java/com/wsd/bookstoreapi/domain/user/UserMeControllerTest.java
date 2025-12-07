package com.wsd.bookstoreapi.domain.user;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wsd.bookstoreapi.support.IntegrationTestSupport;
import com.wsd.bookstoreapi.support.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UserMeControllerTest extends IntegrationTestSupport {

    @Autowired
    private TestDataFactory testDataFactory;

    @Autowired
    private ObjectMapper objectMapper;

    private String userAccessToken;
    private final String userEmail = "user@example.com";
    private final String userPassword = "1q2w3e4r";

    @BeforeEach
    void setUp() throws Exception {
        // 1. 일반 유저 생성
        testDataFactory.createNormalUser(userEmail);

        // 2. 로그인해서 accessToken 발급
        String loginRequest = """
                {
                  "email": "%s",
                  "password": "%s"
                }
                """.formatted(userEmail, userPassword);

        String loginResponse = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.payload.accessToken").isNotEmpty())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode root = objectMapper.readTree(loginResponse);
        this.userAccessToken = root.path("payload").path("accessToken").asText();
        assertThat(userAccessToken).isNotBlank();
    }

    @Test
    @DisplayName("내 정보 조회 성공 - 올바른 토큰으로 /users/me 호출 시 200과 내 정보 반환")
    void getMyInfo_success() throws Exception {
        mockMvc.perform(get("/api/v1/users/me")
                        .header("Authorization", "Bearer " + userAccessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.payload").exists())
                .andExpect(jsonPath("$.payload.email").value(userEmail));
        // 필요하면 name, role 등 추가 검증
        // .andExpect(jsonPath("$.payload.name").value("일반 사용자"));
    }

    @Test
    @DisplayName("내 정보 조회 실패 - 토큰 없이 호출하면 401 반환")
    void getMyInfo_unauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/users/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.isSuccess").value(false))
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
    }
}
