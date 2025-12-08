
package com.wsd.bookstoreapi.domain.order;

import com.fasterxml.jackson.databind.JsonNode;
import com.wsd.bookstoreapi.domain.book.entity.Book;
import com.wsd.bookstoreapi.domain.cart.entity.Cart;
import com.wsd.bookstoreapi.domain.cart.entity.CartItem;
import com.wsd.bookstoreapi.domain.cart.repository.CartItemRepository;
import com.wsd.bookstoreapi.domain.cart.repository.CartRepository;
import com.wsd.bookstoreapi.domain.order.entity.Order;
import com.wsd.bookstoreapi.domain.order.entity.OrderStatus;
import com.wsd.bookstoreapi.domain.order.repository.OrderRepository;
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

class AdminOrderControllerTest extends IntegrationTestSupport {

    @Autowired
    private TestDataFactory testDataFactory;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    private String adminAccessToken;
    private String userAccessToken;
    private User user;
    private Book book;

    @BeforeEach
    void setUp() throws Exception {
        // 1) 관리자 계정 생성 + 토큰
        testDataFactory.createAdminUser();
        adminAccessToken = obtainAccessToken("admin@example.com", "1q2w3e4r");
        assertThat(adminAccessToken).isNotBlank();

        // 2) 주문을 만들 일반 사용자 + 도서 + 장바구니
        user = testDataFactory.createNormalUser("orderuser2@example.com");
        book = testDataFactory.createSampleBook("관리자 주문 테스트 도서");

        Cart cart = testDataFactory.createCartForUser(user);

        CartItem item = CartItem.builder()
                .cart(cart)
                .book(book)
                .quantity(1)
                .build();
        cartItemRepository.save(item);

        userAccessToken = obtainAccessToken("orderuser2@example.com", "1q2w3e4r");
        assertThat(userAccessToken).isNotBlank();

        // 3) 이 사용자로 실제 주문 하나 생성
        String createRequest = """
        {
          "receiverName": "홍길동",
          "address": "서울시 테스트로 123"
        }
        """;

        String createResponse = mockMvc.perform(post("/api/v1/orders")
                        .header("Authorization", "Bearer " + userAccessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createRequest))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode createdRoot = objectMapper.readTree(createResponse);
        long orderId = createdRoot.path("payload").path("id").asLong();
        assertThat(orderId).isPositive();
    }

    @Test
    @DisplayName("관리자 - 전체 주문 목록 조회 성공")
    void getOrdersForAdmin_success() throws Exception {
        String responseBody = mockMvc.perform(get("/api/v1/admin/orders")
                        .header("Authorization", "Bearer " + adminAccessToken))
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
    @DisplayName("관리자 - 주문 상세 조회 성공")
    void getOrderForAdmin_success() throws Exception {
        // 임의로 첫 번째 주문 하나 가져오기
        Order anyOrder = orderRepository.findAll().stream()
                .findFirst()
                .orElseThrow();

        mockMvc.perform(get("/api/v1/admin/orders/{id}", anyOrder.getId())
                        .header("Authorization", "Bearer " + adminAccessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.payload.id").value(anyOrder.getId().intValue()))
                .andExpect(jsonPath("$.payload.status").value(anyOrder.getStatus().name()));
    }

    @Test
    @DisplayName("관리자 - 주문 상태 변경 성공")
    void updateOrderStatus_success() throws Exception {
        Order order = orderRepository.findAll().stream()
                .findFirst()
                .orElseThrow();

        String updateRequest = """
                {
                  "status": "COMPLETED"
                }
                """;

        mockMvc.perform(patch("/api/v1/admin/orders/{id}/status", order.getId())
                        .header("Authorization", "Bearer " + adminAccessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true));

        Order updated = orderRepository.findById(order.getId()).orElseThrow();
        assertThat(updated.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }

    @Test
    @DisplayName("관리자 - 일반 유저 토큰으로 관리자 주문 API 호출 시 403")
    void getOrdersForAdmin_forbidden_forNormalUser() throws Exception {
        mockMvc.perform(get("/api/v1/admin/orders")
                        .header("Authorization", "Bearer " + userAccessToken))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.isSuccess").value(false))
                .andExpect(jsonPath("$.code").value("FORBIDDEN"));
    }
}

