package com.wsd.bookstoreapi.domain.cart.dto;

import com.wsd.bookstoreapi.domain.cart.entity.Cart;
import com.wsd.bookstoreapi.domain.cart.entity.CartItem;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class CartResponse {
    private Long cartId;
    private List<CartItemResponse> items;
    private int totalQuantity;
    private BigDecimal totalAmount;

    public static CartResponse from(Cart cart) {
        List<CartItemResponse> itemResponses = cart.getItems().stream()
                .map(CartItemResponse::from)
                .toList();

        int totalQty = cart.getItems().stream()
                .mapToInt(CartItem::getQuantity)
                .sum();

        BigDecimal totalAmt = cart.getItems().stream()
                .map(item -> item.getBook().getPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return CartResponse.builder()
                .cartId(cart.getId())
                .items(itemResponses)
                .totalQuantity(totalQty)
                .totalAmount(totalAmt)
                .build();
    }
}

