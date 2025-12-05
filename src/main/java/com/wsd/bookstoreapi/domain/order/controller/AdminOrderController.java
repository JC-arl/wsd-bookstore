package com.wsd.bookstoreapi.domain.order.controller;

import com.wsd.bookstoreapi.domain.order.dto.OrderResponse;
import com.wsd.bookstoreapi.domain.order.dto.OrderStatusUpdateRequest;
import com.wsd.bookstoreapi.domain.order.entity.OrderStatus;
import com.wsd.bookstoreapi.domain.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/admin/orders")
public class AdminOrderController {

    private final OrderService orderService;

    /**
     * 전체 주문 목록 조회 (상태 필터)
     */
    @GetMapping
    public ResponseEntity<Page<OrderResponse>> getOrders(
            @RequestParam(required = false) OrderStatus status,
            Pageable pageable
    ) {
        Page<OrderResponse> page = orderService.getOrdersForAdmin(status, pageable);
        return ResponseEntity.ok(page);
    }

    /**
     * 주문 상세
     */
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable Long id) {
        OrderResponse response = orderService.getOrderForAdmin(id);
        return ResponseEntity.ok(response);
    }

    /**
     * 주문 상태 변경
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> updateOrderStatus(
            @PathVariable Long id,
            @Valid @RequestBody OrderStatusUpdateRequest request
    ) {
        orderService.updateOrderStatus(id, request);
        return ResponseEntity.noContent().build();
    }
}
