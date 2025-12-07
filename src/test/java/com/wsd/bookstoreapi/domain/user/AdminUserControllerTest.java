package com.wsd.bookstoreapi.domain.user;

import com.wsd.bookstoreapi.support.IntegrationTestSupport;
import com.wsd.bookstoreapi.support.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AdminUserControllerTest extends IntegrationTestSupport {

    @Autowired
    private TestDataFactory testDataFactory;

    private String adminAccessToken;

    @BeforeEach
    void setUp() throws Exception {
        // 1) 관리자 계정 생성
        testDataFactory.createAdminUser();

        // 2) 로그인해서 accessToken 발급
        adminAccessToken = obtainAccessToken("admin@example.com", "1q2w3e4r");
    }

    @Test
    @DisplayName("관리자 - 회원 목록 조회 성공 (페이지네이션)")
    void get_users_for_admin() throws Exception {
        mockMvc.perform(get("/api/v1/admin/users")
                        .header("Authorization", "Bearer " + adminAccessToken)
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true));
        // 필요하면 payload 내용 추가 검증
    }

    @Test
    @DisplayName("관리자 - 존재하지 않는 회원 상세 조회 시 404")
    void get_user_not_found() throws Exception {
        Long notExistUserId = 99999L;

        mockMvc.perform(get("/api/v1/admin/users/{id}", notExistUserId)
                        .header("Authorization", "Bearer " + adminAccessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.isSuccess").value(false))
                .andExpect(jsonPath("$.code").value("USER_NOT_FOUND"));
    }
    @Test
    @DisplayName("관리자 - 토큰 없이 회원 목록 조회하면 401")
    void get_users_unauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/admin/users")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.isSuccess").value(false))
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
    }

}
