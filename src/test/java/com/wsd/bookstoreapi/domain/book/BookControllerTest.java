package com.wsd.bookstoreapi.domain.book;

import com.fasterxml.jackson.databind.JsonNode;
import com.wsd.bookstoreapi.domain.book.entity.Book;
import com.wsd.bookstoreapi.support.IntegrationTestSupport;
import com.wsd.bookstoreapi.support.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class BookControllerTest extends IntegrationTestSupport {

    @Autowired
    private TestDataFactory testDataFactory;

    private List<Book> sampleBooks;

    @BeforeEach
    void setUp() {
        // 검색/목록 조회용 샘플 도서 생성
        sampleBooks = testDataFactory.createSampleBooksForSearch();
    }

    @Test
    @DisplayName("도서 목록 조회 - 기본 페이지 조회 성공")
    void getBooks_success() throws Exception {
        String responseBody = mockMvc.perform(get("/api/v1/books")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.payload.content").isArray())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode root = objectMapper.readTree(responseBody);
        JsonNode content = root.path("payload").path("content");

        assertThat(content.size()).isGreaterThanOrEqualTo(1);
    }

    @Test
    @DisplayName("도서 목록 조회 - keyword로 제목 검색이 가능하다")
    void getBooks_withKeywordFilter() throws Exception {
        // "이펙티브 자바"가 포함된 책만 필터되도록 기대
        String responseBody = mockMvc.perform(get("/api/v1/books")
                        .param("keyword", "이펙티브")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode root = objectMapper.readTree(responseBody);
        JsonNode content = root.path("payload").path("content");

        assertThat(content.size()).isGreaterThanOrEqualTo(1);
        // 모든 결과의 title에 "이펙티브"가 포함되어 있는지 체크
        content.forEach(node ->
                assertThat(node.path("title").asText()).contains("이펙티브")
        );
    }

    @Test
    @DisplayName("도서 목록 조회 - category로 필터링 가능")
    void getBooks_withCategoryFilter() throws Exception {
        String responseBody = mockMvc.perform(get("/api/v1/books")
                        .param("category", "PROGRAMMING")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode root = objectMapper.readTree(responseBody);
        JsonNode content = root.path("payload").path("content");

        assertThat(content.size()).isGreaterThanOrEqualTo(1);
        content.forEach(node ->
                assertThat(node.path("category").asText()).isEqualTo("PROGRAMMING")
        );
    }

    @Test
    @DisplayName("도서 상세 조회 성공 - 존재하는 ID")
    void getBook_detail_success() throws Exception {
        Long bookId = sampleBooks.get(0).getId();

        mockMvc.perform(get("/api/v1/books/{id}", bookId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.payload.id").value(bookId))
                .andExpect(jsonPath("$.payload.title").value(sampleBooks.get(0).getTitle()));
    }

    @Test
    @DisplayName("도서 상세 조회 실패 - 존재하지 않는 ID이면 404 반환")
    void getBook_detail_notFound() throws Exception {
        Long notExistId = 999999L;

        mockMvc.perform(get("/api/v1/books/{id}", notExistId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.isSuccess").value(false))
                .andExpect(jsonPath("$.code").value("RESOURCE_NOT_FOUND"));
    }
}
