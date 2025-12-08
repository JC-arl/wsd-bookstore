package com.wsd.bookstoreapi.domain.review;

import com.fasterxml.jackson.databind.JsonNode;
import com.wsd.bookstoreapi.domain.book.entity.Book;
import com.wsd.bookstoreapi.domain.review.entity.Review;
import com.wsd.bookstoreapi.domain.review.repository.ReviewRepository;
import com.wsd.bookstoreapi.domain.user.entity.User;
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

class ReviewControllerTest extends IntegrationTestSupport {

    @Autowired
    private TestDataFactory testDataFactory;

    @Autowired
    private ReviewRepository reviewRepository;

    private User user;
    private Book book;
    private String userAccessToken;

    @BeforeEach
    void setUp() throws Exception {
        // 1) 유저 + 도서 생성
        user = testDataFactory.createNormalUser("reviewer@example.com");
        book = testDataFactory.createSampleBook("리뷰 테스트 도서");

        // 2) 로그인 후 AccessToken 발급 (IntegrationTestSupport 헬퍼 사용)
        userAccessToken = obtainAccessToken("reviewer@example.com", "1q2w3e4r");
        assertThat(userAccessToken).isNotBlank();
    }

    @Test
    @DisplayName("리뷰 생성 성공 - 올바른 요청일 때 201과 리뷰 정보 반환")
    void createReview_success() throws Exception {
        String requestBody = """
                {
                  "rating": 5,
                  "content": "아주 좋은 책입니다."
                }
                """;

        String responseBody = mockMvc.perform(post("/api/v1/books/{bookId}/reviews", book.getId())
                        .header("Authorization", "Bearer " + userAccessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())              // 201
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.payload.id").isNumber())
                .andExpect(jsonPath("$.payload.bookId").value(book.getId().intValue()))
                .andExpect(jsonPath("$.payload.rating").value(5))
                .andExpect(jsonPath("$.payload.content").value("아주 좋은 책입니다."))
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode root = objectMapper.readTree(responseBody);
        long reviewId = root.path("payload").path("id").asLong();

        Review saved = reviewRepository.findById(reviewId).orElseThrow();
        assertThat(saved.getUser().getId()).isEqualTo(user.getId());
        assertThat(saved.getBook().getId()).isEqualTo(book.getId());
        assertThat(saved.getRating()).isEqualTo(5);
    }

    @Test
    @DisplayName("리뷰 생성 실패 - rating이 범위를 벗어나면 400")
    void createReview_invalidRating() throws Exception {
        String requestBody = """
                {
                  "rating": 6,
                  "content": "평점 6점은 허용되지 않아야 합니다."
                }
                """;

        mockMvc.perform(post("/api/v1/books/{bookId}/reviews", book.getId())
                        .header("Authorization", "Bearer " + userAccessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.isSuccess").value(false));
        // .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"));  // 너의 ErrorCode 이름에 맞게 조정
    }

    @Test
    @DisplayName("리뷰 생성 실패 - 같은 사용자가 같은 도서에 두 번 리뷰하면 409")
    void createReview_duplicate() throws Exception {
        // GIVEN: 이미 한 번 리뷰를 작성한 상태
        testDataFactory.createReview(user, book, 4, "첫 번째 리뷰입니다.");

        String requestBody = """
                {
                  "rating": 5,
                  "content": "두 번째 리뷰입니다."
                }
                """;

        mockMvc.perform(post("/api/v1/books/{bookId}/reviews", book.getId())
                        .header("Authorization", "Bearer " + userAccessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.isSuccess").value(false))
                .andExpect(jsonPath("$.code").value("DUPLICATE_RESOURCE"));
    }

    @Test
    @DisplayName("도서별 리뷰 목록 조회 성공")
    void getBookReviews_success() throws Exception {
        // GIVEN: 해당 도서에 리뷰 여러 개 생성
        testDataFactory.createReview(user, book, 5, "리뷰 A");
        User other = testDataFactory.createNormalUser("other@example.com");
        testDataFactory.createReview(other, book, 3, "리뷰 B");

        String responseBody = mockMvc.perform(get("/api/v1/books/{bookId}/reviews", book.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode root = objectMapper.readTree(responseBody);

        // payload가 Page<ReviewResponse> 라고 가정 (content 배열)
        JsonNode content = root.path("payload").path("content");
        assertThat(content.isArray()).isTrue();
        assertThat(content.size()).isGreaterThanOrEqualTo(2);
    }

    @Test
    @DisplayName("내 리뷰 목록 조회 성공")
    void getMyReviews_success() throws Exception {
        // GIVEN: 내가 쓴 리뷰 2개
        testDataFactory.createReview(user, book, 5, "내 리뷰 1");
        Book anotherBook = testDataFactory.createSampleBook("다른 책");
        testDataFactory.createReview(user, anotherBook, 4, "내 리뷰 2");

        String responseBody = mockMvc.perform(get("/api/v1/reviews/me")
                        .header("Authorization", "Bearer " + userAccessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode root = objectMapper.readTree(responseBody);
        JsonNode content = root.path("payload").path("content"); // Page<ReviewResponse> 가정

        assertThat(content.isArray()).isTrue();
        assertThat(content.size()).isGreaterThanOrEqualTo(2);
    }

    @Test
    @DisplayName("리뷰 수정 성공 - 내가 쓴 리뷰만 수정 가능")
    void updateReview_success() throws Exception {
        Review review = testDataFactory.createReview(user, book, 3, "수정 전 리뷰");

        String updateBody = """
                {
                  "rating": 5,
                  "content": "수정된 리뷰 내용"
                }
                """;

        mockMvc.perform(patch("/api/v1/reviews/{id}", review.getId())
                        .header("Authorization", "Bearer " + userAccessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.payload.rating").value(5))
                .andExpect(jsonPath("$.payload.content").value("수정된 리뷰 내용"));

        Review updated = reviewRepository.findById(review.getId()).orElseThrow();
        assertThat(updated.getRating()).isEqualTo(5);
        assertThat(updated.getContent()).isEqualTo("수정된 리뷰 내용");
    }

    @Test
    @DisplayName("리뷰 삭제 성공 - 내가 쓴 리뷰 삭제")
    void deleteReview_success() throws Exception {
        Review review = testDataFactory.createReview(user, book, 4, "삭제 대상 리뷰");

        mockMvc.perform(delete("/api/v1/reviews/{id}", review.getId())
                        .header("Authorization", "Bearer " + userAccessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true));

        assertThat(reviewRepository.findById(review.getId())).isEmpty();
    }
}
