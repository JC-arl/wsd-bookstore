package com.wsd.bookstoreapi.domain.cart;

import com.fasterxml.jackson.databind.JsonNode;
import com.wsd.bookstoreapi.domain.book.entity.Book;
import com.wsd.bookstoreapi.domain.cart.entity.Cart;
import com.wsd.bookstoreapi.domain.cart.entity.CartItem;
import com.wsd.bookstoreapi.domain.cart.repository.CartItemRepository;
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

class CartControllerTest extends IntegrationTestSupport {

    @Autowired
    private TestDataFactory testDataFactory;

    @Autowired
    private CartItemRepository cartItemRepository;

    private String userAccessToken;
    private User user;
    private Book book;
    private Cart cart;   // ✅ Cart 참조

    @BeforeEach
    void setUp() throws Exception {
        // 1) 일반 사용자 + 도서 하나 생성
        user = testDataFactory.createNormalUser("user1@example.com");
        book = testDataFactory.createSampleBook("장바구니 테스트 도서");

        // 2) 사용자 Cart 생성/보장
        cart = testDataFactory.createCartForUser(user);

        // 3) 로그인해서 토큰 발급
        userAccessToken = obtainAccessToken("user1@example.com", "1q2w3e4r");
        assertThat(userAccessToken).isNotBlank();
    }

    @Test
    @DisplayName("장바구니 - 도서 추가 성공")
    void addItem_success() throws Exception {
        String requestBody = """
                {
                  "bookId": %d,
                  "quantity": 2
                }
                """.formatted(book.getId());

        String responseBody = mockMvc.perform(post("/api/v1/cart/items")
                        .header("Authorization", "Bearer " + userAccessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.isSuccess").value(true))
                // payload.items[0].id
                .andExpect(jsonPath("$.payload.items[0].id").isNumber())
                // payload.items[0].bookId
                .andExpect(jsonPath("$.payload.items[0].bookId").value(book.getId().intValue()))
                // payload.items[0].quantity
                .andExpect(jsonPath("$.payload.items[0].quantity").value(2))
                .andReturn()
                .getResponse()
                .getContentAsString();

        // ✅ 실제 cartItemId는 payload.items[0].id에서 읽어와야 함
        JsonNode root = objectMapper.readTree(responseBody);
        JsonNode firstItem = root.path("payload").path("items").get(0);
        Long cartItemId = firstItem.path("id").asLong();
        assertThat(cartItemId).isPositive();

        // DB 검증
        CartItem saved = cartItemRepository.findById(cartItemId).orElseThrow();
        assertThat(saved.getCart().getUser().getId()).isEqualTo(user.getId());
        assertThat(saved.getBook().getId()).isEqualTo(book.getId());
        assertThat(saved.getQuantity()).isEqualTo(2);
    }

    @Test
    @DisplayName("장바구니 - 로그인하지 않으면 401")
    void addItem_unauthorized() throws Exception {
        String requestBody = """
                {
                  "bookId": %d,
                  "quantity": 1
                }
                """.formatted(book.getId());

        mockMvc.perform(post("/api/v1/cart/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isUnauthorized());
        // JwtAuthenticationEntryPoint에서 ApiResult 포맷으로 내려주면,
        // .andExpect(jsonPath("$.isSuccess").value(false))
        // .andExpect(jsonPath("$.code").value("UNAUTHORIZED"))
        // 도 추가 가능
    }

    @Test
    @DisplayName("장바구니 - 내 장바구니 조회 성공")
    void getMyCart_success() throws Exception {
        // GIVEN: 장바구니에 하나 넣어두기
        CartItem cartItem = CartItem.builder()
                .cart(cart)
                .book(book)
                .quantity(3)
                .build();
        cartItemRepository.save(cartItem);

        String responseBody = mockMvc.perform(get("/api/v1/cart")
                        .header("Authorization", "Bearer " + userAccessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode root = objectMapper.readTree(responseBody);
        JsonNode items = root.path("payload").path("items");

        assertThat(items.isArray()).isTrue();
        assertThat(items.size()).isGreaterThanOrEqualTo(1);  // 최소 1개 이상

        boolean found = false;
        for (JsonNode itemNode : items) {
            long bookId = itemNode.path("bookId").asLong();
            int quantity = itemNode.path("quantity").asInt();
            if (bookId == book.getId() && quantity == 3) {
                found = true;
                break;
            }
        }

        assertThat(found).isTrue();  // 우리가 추가한 (book, 3)이 실제로 존재하는지
    }

    @Test
    @DisplayName("장바구니 - 수량 변경 성공")
    void updateQuantity_success() throws Exception {
        // GIVEN: 장바구니 항목 하나 생성 (초기 수량 1)
        CartItem cartItem = CartItem.builder()
                .cart(cart)
                .book(book)
                .quantity(1)
                .build();
        cartItemRepository.save(cartItem);

        String updateRequest = """
                {
                  "quantity": 5
                }
                """;

        String responseBody = mockMvc.perform(patch("/api/v1/cart/items/{id}", cartItem.getId())
                        .header("Authorization", "Bearer " + userAccessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andReturn()
                .getResponse()
                .getContentAsString();

        // ✅ 응답 payload.items 배열 안에서 내가 수정한 item(id 기준)을 찾아서 quantity 검증
        JsonNode root = objectMapper.readTree(responseBody);
        JsonNode items = root.path("payload").path("items");

        boolean found = false;
        for (JsonNode itemNode : items) {
            long itemId = itemNode.path("id").asLong();
            if (itemId == cartItem.getId()) {
                int qty = itemNode.path("quantity").asInt();
                assertThat(qty).isEqualTo(5);
                found = true;
                break;
            }
        }
        assertThat(found)
                .as("응답 payload.items 안에 id=%d 인 항목이 있어야 한다", cartItem.getId())
                .isTrue();

        // DB 값도 최종 확인
        CartItem updated = cartItemRepository.findById(cartItem.getId()).orElseThrow();
        assertThat(updated.getQuantity()).isEqualTo(5);
    }
}
