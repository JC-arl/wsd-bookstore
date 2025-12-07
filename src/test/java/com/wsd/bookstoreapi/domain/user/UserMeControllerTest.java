package com.wsd.bookstoreapi.domain.user;

import com.fasterxml.jackson.databind.JsonNode;
import com.wsd.bookstoreapi.support.IntegrationTestSupport;
import com.wsd.bookstoreapi.support.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UserMeControllerTest extends IntegrationTestSupport {

    @Autowired
    private TestDataFactory testDataFactory;

    private String userAccessToken;

    @BeforeEach
    void setUp() throws Exception {
        // 일반 유저 생성
        testDataFactory.createNormalUser("user@example.com");

        // 로그인해서 토큰 발급
        userAccessToken = obtainAccessToken("user@example.com", "1q2w3e4r");
    }

    @Test
    @DisplayName("내 정보 조회 성공 - 올바른 토큰으로 /users/me 호출 시 200과 내 정보 반환")
    void getMyInfo_success() throws Exception {
        mockMvc.perform(get("/api/v1/users/me")
                        .header("Authorization", "Bearer " + userAccessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.payload.email").value("user@example.com"));
    }

    @Test
    @DisplayName("내 정보 수정 성공 - 이름 변경")
    void updateMyInfo_success() throws Exception {
        String requestBody = """
                {
                  "name": "수정된 사용자 이름"
                }
                """;

        String responseBody = mockMvc.perform(patch("/api/v1/users/me")
                        .header("Authorization", "Bearer " + userAccessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.payload.email").value("user@example.com"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode root = objectMapper.readTree(responseBody);
        assertThat(root.path("payload").path("name").asText())
                .isEqualTo("수정된 사용자 이름");
    }

    @Test
    @DisplayName("내 계정 비활성화 성공 - /users/me/deactivate 호출 시 상태가 INACTIVE로 변경")
    void deactivateMe_success() throws Exception {
        mockMvc.perform(patch("/api/v1/users/me/deactivate")
                        .header("Authorization", "Bearer " + userAccessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.message").value("계정이 비활성화되었습니다."));

        // 이후 /me 조회 시 비활성 상태를 어떻게 표현할지에 따라 추가 검증 가능
        // 예: 서비스에서 status != ACTIVE면 BusinessException 던지게 했다면 401/403/409 등으로 나갈 수 있음
    }

    @Test
    @DisplayName("내 계정 영구 삭제 성공 - /users/me DELETE 호출")
    void deleteMe_success() throws Exception {
        mockMvc.perform(delete("/api/v1/users/me")
                        .header("Authorization", "Bearer " + userAccessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.message").value("계정이 영구 삭제되었습니다."));

        // 삭제 후 다시 /me 호출하면 인증/조회 실패가 되어야 하는데,
        // 구현에 따라 401 / 404 / 409 등으로 정의해 두었을 것이라,
        // 별도 테스트 케이스로 분리해서 검증해도 좋습니다.
    }
}
