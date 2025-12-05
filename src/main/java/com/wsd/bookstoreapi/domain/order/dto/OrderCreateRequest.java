package com.wsd.bookstoreapi.domain.order.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderCreateRequest {

    @NotBlank(message = "배송지는 필수입니다.")
    private String shippingAddress;

    // 장바구니 기반으로 주문 생성할 것이므로 개별 아이템 리스트는 별도로 받지 않음
}
