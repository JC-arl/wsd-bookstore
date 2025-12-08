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

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class OrderControllerTest extends IntegrationTestSupport {

    @Autowired
    private TestDataFactory testDataFactory;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    private User user;
    private Book book1;
    private Book book2;
    private String userAccessToken;

    @BeforeEach
    void setUp() throws Exception {
        // 1) 일반 사용자 생성
        user = testDataFactory.createNormalUser("orderuser@example.com");

        // 2) 도서 2권 생성
        book1 = testDataFactory.createSampleBook("주문 테스트 도서 1");
        book2 = testDataFactory.createSampleBook("주문 테스트 도서 2");

        // 3) Cart 생성/보장
        Cart cart = testDataFactory.createCartForUser(user);

        // 4) 장바구니에 항목 추가 (book1: 2권, book2: 1권)
        CartItem item1 = CartItem.builder()
                .cart(cart)
                .book(book1)
                .quantity(2)
                .build();

        CartItem item2 = CartItem.builder()
                .cart(cart)
                .book(book2)
                .quantity(1)
                .build();

        cartItemRepository.save(item1);
        cartItemRepository.save(item2);

        // 5) 로그인해서 토큰 발급
        userAccessToken = obtainAccessToken("orderuser@example.com", "1q2w3e4r");
        assertThat(userAccessToken).isNotBlank();
    }

    @Test
    @DisplayName("주문 생성 성공 - 장바구니 기반으로 주문 생성")
    void createOrder_success() throws Exception {
        String requestBody = """
        {
          "receiverName": "홍길동",
          "address": "서울시 테스트로 123"
        }
        """;


        // WHEN: 주문 생성 요청
        String responseBody = mockMvc.perform(post("/api/v1/orders")
                        .header("Authorization", "Bearer " + userAccessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())                  // 컨트롤러가 200 OK를 반환
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.payload.id").isNumber())
                .andExpect(jsonPath("$.payload.status").value("PENDING"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        // THEN: DB에 주문이 생성되었는지 확인
        JsonNode root = objectMapper.readTree(responseBody);
        long orderId = root.path("payload").path("id").asLong();

        Order order = orderRepository.findById(orderId).orElseThrow();
        assertThat(order.getUser().getId()).isEqualTo(user.getId());
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PENDING);
        assertThat(order.getOrderItems()).hasSize(2);

        // 합계 금액 검증 (book1 * 2 + book2 * 1)
        BigDecimal expectedTotal =
                book1.getPrice().multiply(BigDecimal.valueOf(2))
                        .add(book2.getPrice());
        assertThat(order.getTotalAmount()).isEqualByComparingTo(expectedTotal);

        // 장바구니가 비워졌는지 확인
        Cart cart = cartRepository.findByUser(user).orElseThrow();
        assertThat(cart.getItems()).isEmpty();
    }

    @Test
    @DisplayName("주문 생성 실패 - 장바구니에 상품이 없으면 409")
    void createOrder_fail_emptyCart() throws Exception {
        // GIVEN: 장바구니가 비어있는 사용자 준비
        User emptyUser = testDataFactory.createNormalUser("emptycart@example.com");
        Cart emptyCart = testDataFactory.createCartForUser(emptyUser);
        // items 비워두기
        cartItemRepository.deleteAll(emptyCart.getItems());
        emptyCart.getItems().clear();

        String emptyUserToken = obtainAccessToken("emptycart@example.com", "1q2w3e4r");

        String requestBody = """
        {
          "receiverName": "아무개",
          "address": "서울시 어딘가"
        }
        """;

        mockMvc.perform(post("/api/v1/orders")
                        .header("Authorization", "Bearer " + emptyUserToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isConflict())                   // 409
                .andExpect(jsonPath("$.isSuccess").value(false))
                .andExpect(jsonPath("$.code").value("STATE_CONFLICT"));
    }

    @Test
    @DisplayName("내 주문 목록 조회 성공")
    void getMyOrders_success() throws Exception {
        // GIVEN: 먼저 주문 하나 생성
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

        // WHEN: 내 주문 목록 조회
        String listResponse = mockMvc.perform(get("/api/v1/orders")
                        .header("Authorization", "Bearer " + userAccessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.payload.content").isArray())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // THEN: content에 방금 생성한 주문이 포함되어 있는지 확인
        JsonNode listRoot = objectMapper.readTree(listResponse);
        JsonNode content = listRoot.path("payload").path("content");
        assertThat(content.size()).isGreaterThanOrEqualTo(1);

        boolean found = false;
        for (JsonNode node : content) {
            if (node.path("id").asLong() == orderId) {
                found = true;
                break;
            }
        }
        assertThat(found).isTrue();
    }

    @Test
    @DisplayName("내 주문 상세 조회 실패 - 다른 사용자의 주문 접근 시 403")
    void getMyOrder_forbidden_otherUser() throws Exception {
        // GIVEN: user로 주문 하나 생성
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

        // 다른 사용자 준비
        User other = testDataFactory.createNormalUser("otheruser@example.com");
        String otherToken = obtainAccessToken("otheruser@example.com", "1q2w3e4r");

        // WHEN & THEN: 다른 유저가 접근 -> 403
        mockMvc.perform(get("/api/v1/orders/{id}", orderId)
                        .header("Authorization", "Bearer " + otherToken))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.isSuccess").value(false))
                .andExpect(jsonPath("$.code").value("FORBIDDEN"));
    }

    @Test
    @DisplayName("내 주문 취소 성공 - PENDING 상태 주문 취소하면 CANCELED로 변경")
    void cancelMyOrder_success() throws Exception {
        // GIVEN: 주문 하나 생성
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

        // WHEN: 취소 요청
        mockMvc.perform(patch("/api/v1/orders/{id}/cancel", orderId)
                        .header("Authorization", "Bearer " + userAccessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true));

        // THEN: DB에서 상태가 CANCELED로 바뀌었는지 확인
        Order order = orderRepository.findById(orderId).orElseThrow();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELED);
    }

    @Test
    @DisplayName("내 주문 취소 실패 - COMPLETED 상태 주문은 409")
    void cancelMyOrder_fail_completed() throws Exception {
        // GIVEN: 주문 하나 생성
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

        // 상태를 COMPLETED로 강제 변경
        Order order = orderRepository.findById(orderId).orElseThrow();
        order.setStatus(OrderStatus.COMPLETED);
        orderRepository.save(order);

        // WHEN & THEN: 취소 요청 -> 409 STATE_CONFLICT
        mockMvc.perform(patch("/api/v1/orders/{id}/cancel", orderId)
                        .header("Authorization", "Bearer " + userAccessToken))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.isSuccess").value(false))
                .andExpect(jsonPath("$.code").value("STATE_CONFLICT"));
    }
}
