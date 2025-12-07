package com.wsd.bookstoreapi.domain.user;

import com.wsd.bookstoreapi.support.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AdminUserControllerTest extends IntegrationTestSupport {

    @Test
    @DisplayName("관리자 - 회원 목록 조회 성공 (페이지네이션)")
    void get_users_for_admin() throws Exception {
        mockMvc.perform(get("/api/v1/admin/users")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.payload.content").isArray())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("관리자 - 존재하지 않는 회원 상세 조회 시 404")
    void get_user_not_found() throws Exception {
        Long notExistId = 999999L;

        mockMvc.perform(get("/api/v1/admin/users/{id}", notExistId))
                .andExpect(status().isNotFound())
                // 404 에러 응답은 ApiResult가 아니라 ErrorResponse 포맷이므로,
                // isSuccess가 아닌 code/message만 체크
                .andExpect(jsonPath("$.code").value("USER_NOT_FOUND"))
                .andExpect(jsonPath("$.message").exists());
    }
}
