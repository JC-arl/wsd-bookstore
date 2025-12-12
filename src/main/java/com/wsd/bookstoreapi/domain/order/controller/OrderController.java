package com.wsd.bookstoreapi.domain.order.controller;

import com.wsd.bookstoreapi.domain.order.dto.OrderCreateRequest;
import com.wsd.bookstoreapi.domain.order.dto.OrderResponse;
import com.wsd.bookstoreapi.domain.order.entity.OrderStatus;
import com.wsd.bookstoreapi.domain.order.service.OrderService;
import com.wsd.bookstoreapi.global.api.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Orders", description = "사용자 주문 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "주문 생성", description = "현재 장바구니를 기반으로 주문을 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "주문 생성 성공"),
            @ApiResponse(responseCode = "400", description = "장바구니가 비어 있음 등 잘못된 상태"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @PostMapping
    public ResponseEntity<ApiResult<OrderResponse>> createOrder(
            @Valid @RequestBody OrderCreateRequest request
    ) {
        OrderResponse orderResponse = orderService.createOrderFromCart(request);
        ApiResult<OrderResponse> apiResult = ApiResult.success(
                orderResponse,
                "주문이 성공적으로 생성되었습니다."
        );
        return ResponseEntity.ok(apiResult);
    }

    @Operation(summary = "내 주문 목록 조회", description = "로그인한 사용자의 주문 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @GetMapping
    public ResponseEntity<ApiResult<Page<OrderResponse>>> getMyOrders(
            @RequestParam(required = false) OrderStatus status,
            Pageable pageable
    ) {
        Page<OrderResponse> page = orderService.getMyOrders(status, pageable);
        ApiResult<Page<OrderResponse>> apiResult = ApiResult.success(
                page,
                "내 주문 목록 조회 성공"
        );
        return ResponseEntity.ok(apiResult);
    }

    @Operation(summary = "내 주문 상세 조회", description = "로그인한 사용자의 특정 주문 상세 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "본인의 주문이 아님"),
            @ApiResponse(responseCode = "404", description = "주문을 찾을 수 없음")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResult<OrderResponse>> getMyOrder(@PathVariable Long id) {
        OrderResponse orderResponse = orderService.getMyOrder(id);
        ApiResult<OrderResponse> apiResult = ApiResult.success(
                orderResponse,
                "주문 상세 조회 성공"
        );
        return ResponseEntity.ok(apiResult);
    }

    @Operation(summary = "내 주문 취소", description = "로그인한 사용자의 주문을 취소합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "취소 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "본인의 주문이 아님"),
            @ApiResponse(responseCode = "404", description = "주문을 찾을 수 없음"),
            @ApiResponse(responseCode = "409", description = "취소할 수 없는 주문 상태")
    })
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<ApiResult<OrderResponse>> cancelMyOrder(@PathVariable Long id) {
        OrderResponse response = orderService.cancelMyOrder(id);
        ApiResult<OrderResponse> apiResult = ApiResult.success(response, "주문이 성공적으로 취소되었습니다.");
        return ResponseEntity.ok(apiResult);
    }
}
