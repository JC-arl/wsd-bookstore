package com.wsd.bookstoreapi.domain.cart.dto;

import com.wsd.bookstoreapi.domain.cart.entity.CartItem;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CartItemResponse {
    private Long id;
    private Long bookId;
    private String title;
    private Integer quantity;

    public static CartItemResponse from(CartItem item) {
        return CartItemResponse.builder()
                .id(item.getId())
                .bookId(item.getBook().getId())
                .title(item.getBook().getTitle())
                .quantity(item.getQuantity())
                .build();
    }
}