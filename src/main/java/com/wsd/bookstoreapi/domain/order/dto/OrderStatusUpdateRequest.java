package com.wsd.bookstoreapi.domain.order.dto;

import com.wsd.bookstoreapi.domain.order.entity.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderStatusUpdateRequest {

    @NotNull(message = "주문 상태는 필수입니다.")
    private OrderStatus status;
}
