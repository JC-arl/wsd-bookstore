package com.wsd.bookstoreapi.domain.book;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wsd.bookstoreapi.domain.book.entity.Book;
import com.wsd.bookstoreapi.domain.book.repository.BookRepository;
import com.wsd.bookstoreapi.support.IntegrationTestSupport;
import com.wsd.bookstoreapi.support.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AdminBookControllerTest extends IntegrationTestSupport {

    @Autowired
    private TestDataFactory testDataFactory;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private String adminAccessToken;

    @BeforeEach
    void setUp() throws Exception {
        // 1. 관리자 계정 생성
        testDataFactory.createAdminUser();

        // 2. 로그인해서 accessToken 발급
        String loginRequest = """
                {
                  "email": "admin@example.com",
                  "password": "1q2w3e4r"
                }
                """;

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
        this.adminAccessToken = root.path("payload").path("accessToken").asText();
        assertThat(adminAccessToken).isNotBlank();
    }

    @Test
    @DisplayName("관리자 - 도서 생성 성공")
    void createBook_success() throws Exception {
        String createRequest = """
                {
                  "title": "관리자 테스트 도서",
                  "author": "테스트 저자",
                  "publisher": "테스트 출판사",
                  "isbn": "ADMIN-ISBN-001",
                  "category": "PROGRAMMING",
                  "price": 32000,
                  "stockQuantity": 10,
                  "description": "관리자 도서 등록 테스트입니다."
                }
                """;

        String responseBody = mockMvc.perform(post("/api/v1/admin/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + adminAccessToken)
                        .content(createRequest))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.payload.id").isNumber())
                .andExpect(jsonPath("$.payload.title").value("관리자 테스트 도서"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode root = objectMapper.readTree(responseBody);
        long bookId = root.path("payload").path("id").asLong();

        // DB에 정말로 저장되었는지 확인
        Optional<Book> savedOpt = bookRepository.findById(bookId);
        assertThat(savedOpt).isPresent();
        assertThat(savedOpt.get().getIsbn()).isEqualTo("ADMIN-ISBN-001");
    }

    @Test
    @DisplayName("관리자 - 중복 ISBN으로 도서 생성 시 409 반환")
    void createBook_duplicateIsbn() throws Exception {
        // 먼저 특정 ISBN으로 한 권 생성
        testDataFactory.createBookWithIsbn("DUP-ISBN-001", "기존 도서", "PROGRAMMING");

        String createRequest = """
                {
                  "title": "중복 테스트 도서",
                  "author": "테스트 저자",
                  "publisher": "테스트 출판사",
                  "isbn": "DUP-ISBN-001",
                  "category": "PROGRAMMING",
                  "price": 30000,
                  "stockQuantity": 5
                }
                """;

        mockMvc.perform(post("/api/v1/admin/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + adminAccessToken)
                        .content(createRequest))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.isSuccess").value(false))
                .andExpect(jsonPath("$.code").value("DUPLICATE_RESOURCE"));
    }

    @Test
    @DisplayName("관리자 - 도서 수정 성공")
    void updateBook_success() throws Exception {
        // 기존 도서 하나 생성
        Book book = testDataFactory.createSampleBook("수정 전 제목");

        String updateRequest = """
                {
                  "title": "수정된 제목",
                  "price": 35000
                }
                """;

        mockMvc.perform(patch("/api/v1/admin/books/{id}", book.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + adminAccessToken)
                        .content(updateRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.payload.title").value("수정된 제목"))
                .andExpect(jsonPath("$.payload.price").value(35000));

        // DB 값 확인
        Book updated = bookRepository.findById(book.getId()).orElseThrow();
        assertThat(updated.getTitle()).isEqualTo("수정된 제목");
        assertThat(updated.getPrice()).isEqualByComparingTo(BigDecimal.valueOf(35000));
    }

    @Test
    @DisplayName("관리자 - 도서 삭제 시 soft delete 처리 (is_active=false)")
    void deleteBook_softDelete() throws Exception {
        Book book = testDataFactory.createSampleBook("삭제 대상 도서");

        mockMvc.perform(delete("/api/v1/admin/books/{id}", book.getId())
                        .header("Authorization", "Bearer " + adminAccessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true));

        Book deleted = bookRepository.findById(book.getId()).orElseThrow();
        assertThat(deleted.is_active()).isFalse();   // ★ 실제 soft delete 여부 확인
    }

    @Test
    @DisplayName("관리자 - 비활성화된 도서 재활성화 성공")
    void activateBook_success() throws Exception {
        // GIVEN: 도서 하나 만들고, 먼저 소프트 삭제
        var book = testDataFactory.createSampleBook("재활성화 도서");

        mockMvc.perform(delete("/api/v1/admin/books/{id}", book.getId())
                        .header("Authorization", "Bearer " + adminAccessToken))
                .andExpect(status().isOk());

        // WHEN: 재활성화 호출
        mockMvc.perform(patch("/api/v1/admin/books/{id}/activate", book.getId())
                        .header("Authorization", "Bearer " + adminAccessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true));

        // THEN: 다시 검색하면 목록에 나타나야 함
        String responseBody = mockMvc.perform(get("/api/v1/books")
                        .param("keyword", "재활성화 도서")
                        .param("category", "")
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode root = objectMapper.readTree(responseBody);
        int size = root.path("payload").path("content").size();
        assertThat(size).isEqualTo(1);
        assertThat(root.path("payload").path("content").get(0).path("title").asText())
                .isEqualTo("재활성화 도서");
    }
}
