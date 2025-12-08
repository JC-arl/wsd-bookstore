package com.wsd.bookstoreapi.domain.favorite;

import com.fasterxml.jackson.databind.JsonNode;
import com.wsd.bookstoreapi.domain.book.entity.Book;
import com.wsd.bookstoreapi.domain.favorite.entity.Favorite;
import com.wsd.bookstoreapi.domain.favorite.repository.FavoriteRepository;
import com.wsd.bookstoreapi.domain.user.entity.User;
import com.wsd.bookstoreapi.support.IntegrationTestSupport;
import com.wsd.bookstoreapi.support.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class FavoriteControllerTest extends IntegrationTestSupport {

    @Autowired
    private TestDataFactory testDataFactory;

    @Autowired
    private FavoriteRepository favoriteRepository;

    private String userAccessToken;
    private User user;
    private Book book1;
    private Book book2;

    @BeforeEach
    void setUp() throws Exception {
        // 1) 사용자 & 도서 2권 생성
        user = testDataFactory.createNormalUser("favuser@example.com");
        book1 = testDataFactory.createSampleBook("찜 테스트 도서 1");
        book2 = testDataFactory.createSampleBook("찜 테스트 도서 2");

        // 2) 로그인 후 토큰 발급
        userAccessToken = obtainAccessToken("favuser@example.com", "1q2w3e4r");
        assertThat(userAccessToken).isNotBlank();
    }

    @Test
    @DisplayName("찜 - 도서 찜 추가 성공")
    void addFavorite_success() throws Exception {
        mockMvc.perform(post("/api/v1/favorites/{bookId}", book1.getId())
                        .header("Authorization", "Bearer " + userAccessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())  // 컨트롤러가 200 OK를 반환
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.message").value("도서가 찜 목록에 추가되었습니다."));

        // DB 검증: user + book1 조합의 Favorite 이 존재하는지
        Optional<Favorite> savedOpt =
                favoriteRepository.findByUserAndBook(user, book1);

        assertThat(savedOpt).isPresent();
        Favorite saved = savedOpt.get();
        assertThat(saved.getUser().getId()).isEqualTo(user.getId());
        assertThat(saved.getBook().getId()).isEqualTo(book1.getId());
    }

    @Test
    @DisplayName("찜 - 로그인하지 않으면 401")
    void addFavorite_unauthorized() throws Exception {
        mockMvc.perform(post("/api/v1/favorites/{bookId}", book1.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
        // 필요하면 JwtAuthenticationEntryPoint에서 내려주는 JSON 구조까지 검증 가능
    }

    @Test
    @DisplayName("찜 - 내 찜 목록 조회 성공")
    void getMyFavorites_success() throws Exception {
        // GIVEN: 미리 2개 찜 생성
        testDataFactory.createFavorite(user, book1);
        testDataFactory.createFavorite(user, book2);

        String responseBody = mockMvc.perform(get("/api/v1/favorites")
                        .header("Authorization", "Bearer " + userAccessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode root = objectMapper.readTree(responseBody);
        JsonNode payload = root.path("payload");
        JsonNode content = payload.path("content");

        // payload.content가 배열인지 확인
        assertThat(content.isArray()).isTrue();
        assertThat(content.size()).isEqualTo(2);

        // content[*].bookId 리스트 추출
        List<Long> bookIds = content.findValues("bookId").stream()
                .map(JsonNode::asLong)
                .toList();

        assertThat(bookIds)
                .containsExactlyInAnyOrder(book1.getId(), book2.getId());
    }

    @Test
    @DisplayName("찜 - 찜 해제 성공")
    void removeFavorite_success() throws Exception {
        // GIVEN: user가 book1을 이미 찜한 상태
        testDataFactory.createFavorite(user, book1);

        mockMvc.perform(delete("/api/v1/favorites/{bookId}", book1.getId())
                        .header("Authorization", "Bearer " + userAccessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.message").value("도서가 찜 목록에서 제거되었습니다."));

        // DB 검증: user + book1 조합 Favorite 이 삭제되었는지
        Optional<Favorite> opt =
                favoriteRepository.findByUserAndBook(user, book1);

        assertThat(opt).isEmpty();
    }
}
