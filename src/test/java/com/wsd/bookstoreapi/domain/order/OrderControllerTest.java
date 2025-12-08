package com.wsd.bookstoreapi.domain.order;

import com.fasterxml.jackson.databind.JsonNode;
import com.wsd.bookstoreapi.domain.book.entity.Book;
import com.wsd.bookstoreapi.domain.order.entity.Order;
import com.wsd.bookstoreapi.domain.order.repository.OrderRepository;
import com.wsd.bookstoreapi.domain.user.entity.User;
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

class OrderControllerTest extends IntegrationTestSupport {

    @Autowired
    private TestDataFactory testDataFactory;

    @Autowired
    private OrderRepository orderRepository;

    private User user;
    private Book book;
    private String userAccessToken;

    @BeforeEach
    void setUp() throws Exception {
        // 1) 사용자 / 도서 생성
        user = testDataFactory.createNormalUser("orderuser@example.com");
        book = testDataFactory.createSampleBook("주문 테스트 도서");
        // 필요하면 재고/가격 강제 세팅
        book.setStockQuantity(100);
        book.setPrice(BigDecimal.valueOf(15000));

        // 2) 로그인해서 토큰 발급 (IntegrationTestSupport 헬퍼 사용)
        userAccessToken = obtainAccessToken("orderuser@example.com", "1q2w3e4r");
        assertThat(userAccessToken).isNotBlank();
    }

    @Test
    @DisplayName("주문 생성 성공 - 단일 도서 주문")
    void createOrder_success() throws Exception {
        String requestBody = """
                {
                  "bookId": %d,
                  "quantity": 2
                }
                """.formatted(book.getId());

        String responseBody = mockMvc.perform(post("/api/v1/orders")
                        .header("Authorization", "Bearer " + userAccessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.payload.id").isNumber())
                .andExpect(jsonPath("$.payload.status").value("CREATED"))
                .andExpect(jsonPath("$.payload.items[0].bookId").value(book.getId().intValue()))
                .andExpect(jsonPath("$.payload.items[0].quantity").value(2))
                .andReturn()
                .getResponse()
                .getContentAsString();

        // 응답에서 orderId 추출 후 DB 검증
        JsonNode root = objectMapper.readTree(responseBody);
        long orderId = root.path("payload").path("id").asLong();

        Optional<Order> savedOpt = orderRepository.findById(orderId);
        assertThat(savedOpt).isPresent();

        Order saved = savedOpt.get();
        assertThat(saved.getUser().getId()).isEqualTo(user.getId());
        assertThat(saved.getOrderItems()).hasSize(1);
        assertThat(saved.getOrderItems().get(0).getBook().getId()).isEqualTo(book.getId());
        assertThat(saved.getOrderItems().get(0).getQuantity()).isEqualTo(2);
    }

    @Test
    @DisplayName("주문 생성 실패 - 재고 부족이면 400 반환")
    void createOrder_insufficientStock() throws Exception {
        // 재고를 1로 제한
        book.setStockQuantity(1);

        String requestBody = """
                {
                  "bookId": %d,
                  "quantity": 5
                }
                """.formatted(book.getId());

        mockMvc.perform(post("/api/v1/orders")
                        .header("Authorization", "Bearer " + userAccessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.isSuccess").value(false))
                .andExpect(jsonPath("$.code").value("STATE_CONFLICT")); // 혹은 STOCK_NOT_ENOUGH 같은 새 ErrorCode
    }

    @Test
    @DisplayName("내 주문 목록 조회 성공")
    void getMyOrders_success() throws Exception {
        // GIVEN: 미리 한 건 주문 만들어두기 (테스트용 팩토리 사용 또는 직접 저장)
        testDataFactory.createOrderForUser(user, book, 3);

        String responseBody = mockMvc.perform(get("/api/v1/orders")
                        .header("Authorization", "Bearer " + userAccessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode root = objectMapper.readTree(responseBody);
        JsonNode content = root.path("payload").path("content");

        assertThat(content.isArray()).isTrue();
        assertThat(content.size()).isGreaterThanOrEqualTo(1);

        boolean found = false;
        for (JsonNode node : content) {
            long orderId = node.path("id").asLong();
            String status = node.path("status").asText();
            if (orderId > 0 && "CREATED".equals(status)) {
                found = true;
                break;
            }
        }
        assertThat(found).isTrue();
    }

    @Test
    @DisplayName("주문 상세 조회 성공 - 내 주문")
    void getOrderDetail_success() throws Exception {
        Order order = testDataFactory.createOrderForUser(user, book, 1);

        mockMvc.perform(get("/api/v1/orders/{id}", order.getId())
                        .header("Authorization", "Bearer " + userAccessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.payload.id").value(order.getId().intValue()))
                .andExpect(jsonPath("$.payload.items[0].bookId").value(book.getId().intValue()));
    }

    @Test
    @DisplayName("주문 상세 조회 실패 - 존재하지 않는 주문이면 404")
    void getOrderDetail_notFound() throws Exception {
        mockMvc.perform(get("/api/v1/orders/{id}", 999999L)
                        .header("Authorization", "Bearer " + userAccessToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.isSuccess").value(false))
                .andExpect(jsonPath("$.code").value("RESOURCE_NOT_FOUND"));
    }

    @Test
    @DisplayName("주문 생성 실패 - 로그인 하지 않으면 401")
    void createOrder_unauthorized() throws Exception {
        String requestBody = """
                {
                  "bookId": %d,
                  "quantity": 1
                }
                """.formatted(book.getId());

        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isUnauthorized());
    }
}
