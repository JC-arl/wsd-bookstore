package com.wsd.bookstoreapi.support;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public abstract class IntegrationTestSupport {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected com.wsd.bookstoreapi.global.security.jwt.JwtTokenProvider jwtTokenProvider;

    // 테스트용 직접 토큰 생성 헬퍼
    protected String generateAccessToken(Long userId, String email, String role) {
        // 주의: 실제 UserPrincipal에서 "ROLE_" prefix 를 어떻게 붙이는지에 따라
        // 여기 role 값은 "USER" / "ADMIN" 이어야 합니다.
        return jwtTokenProvider.generateAccessToken(userId, email, role);
    }
    /**
     * 테스트용 로그인 → accessToken 발급 헬퍼
     *   - 테스트에서: testDataFactory.createNormalUser(email) 해 둔 뒤
     *   - obtainAccessToken(email, rawPassword) 호출해서 토큰 얻기
     */
    protected String obtainAccessToken(String email, String password) throws Exception {
        String requestBody = """
                {
                  "email": "%s",
                  "password": "%s"
                }
                """.formatted(email, password);

        String responseBody = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode root = objectMapper.readTree(responseBody);
        return root.path("payload").path("accessToken").asText();
    }
}
