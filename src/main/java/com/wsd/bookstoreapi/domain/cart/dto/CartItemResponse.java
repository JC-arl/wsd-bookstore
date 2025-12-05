package com.wsd.bookstoreapi.domain.cart.dto;

import com.wsd.bookstoreapi.domain.cart.entity.CartItem;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CartItemResponse {

    private final Long id;
    private final Long bookId;
    private final String bookTitle;
    private final Integer quantity;

    public static CartItemResponse from(CartItem item) {
        return CartItemResponse.builder()
                .id(item.getId())
                .bookId(item.getBook().getId())
                .bookTitle(item.getBook().getTitle())
                .quantity(item.getQuantity())
                .build();
    }
}
