package com.wsd.bookstoreapi.domain.cart.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddCartItemRequest {

    @NotNull(message = "bookId는 필수 값입니다.")
    private Long bookId;

    @Min(value = 1, message = "quantity는 최소 1 이상이어야 합니다.")
    private int quantity;
}
