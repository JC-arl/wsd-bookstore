package com.wsd.bookstoreapi.domain.cart.dto;

import com.wsd.bookstoreapi.domain.cart.entity.CartItem;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class CartItemResponse {
    private Long id;
    private Long bookId;
    private String title;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal lineTotal;

    public static CartItemResponse from(CartItem cartItem) {
        BigDecimal unitPrice = cartItem.getBook().getPrice();
        BigDecimal lineTotal = unitPrice.multiply(BigDecimal.valueOf(cartItem.getQuantity()));

        return CartItemResponse.builder()
                .id(cartItem.getId())
                .bookId(cartItem.getBook().getId())
                .title(cartItem.getBook().getTitle())
                .quantity(cartItem.getQuantity())
                .unitPrice(unitPrice)
                .lineTotal(lineTotal)
                .build();
    }
}
