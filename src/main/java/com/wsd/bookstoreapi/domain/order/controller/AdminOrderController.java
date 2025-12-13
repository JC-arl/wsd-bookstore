package com.wsd.bookstoreapi.domain.order.controller;

import com.wsd.bookstoreapi.domain.order.dto.OrderResponse;
import com.wsd.bookstoreapi.domain.order.dto.OrderStatusUpdateRequest;
import com.wsd.bookstoreapi.domain.order.entity.OrderStatus;
import com.wsd.bookstoreapi.domain.order.service.OrderService;
import com.wsd.bookstoreapi.global.api.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Admin - Orders", description = "관리자용 주문 관리 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/admin/orders")
public class AdminOrderController {

    private final OrderService orderService;

    @Operation(summary = "전체 주문 목록 조회", description = "관리자가 모든 주문을 상태별로 조회합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": true,
                                      "message": "주문 목록 조회 성공",
                                      "code": null,
                                      "payload": {
                                        "content": [
                                          {
                                            "id": 1,
                                            "userId": 5,
                                            "status": "PENDING",
                                            "totalAmount": 58000,
                                            "shippingAddress": "서울특별시 강남구 테헤란로 123",
                                            "createdAt": "2025-12-13T10:30:00",
                                            "updatedAt": "2025-12-13T10:30:00",
                                            "items": [
                                              {
                                                "id": 1,
                                                "bookId": 1,
                                                "bookTitle": "클린 코드",
                                                "quantity": 2,
                                                "unitPrice": 29000,
                                                "lineTotal": 58000
                                              }
                                            ]
                                          }
                                        ],
                                        "pageable": {
                                          "pageNumber": 0,
                                          "pageSize": 10
                                        },
                                        "totalElements": 1,
                                        "totalPages": 1
                                      }
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": false,
                                      "message": "인증이 필요합니다.",
                                      "code": "UNAUTHORIZED",
                                      "payload": null
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "관리자 권한 없음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": false,
                                      "message": "접근 권한이 없습니다.",
                                      "code": "FORBIDDEN",
                                      "payload": null
                                    }
                                    """)
                    )
            )
    })
    @GetMapping
    public ResponseEntity<ApiResult<Page<OrderResponse>>> getOrders(
            @RequestParam(required = false) OrderStatus status,
            Pageable pageable
    ) {
        Page<OrderResponse> page = orderService.getOrdersForAdmin(status, pageable);
        ApiResult<Page<OrderResponse>> apiResult = ApiResult.success(
                page,
                "주문 목록 조회 성공"
        );
        return ResponseEntity.ok(apiResult);
    }

    @Operation(summary = "주문 상세 조회", description = "관리자가 특정 주문의 상세 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": true,
                                      "message": "주문 상세 조회 성공",
                                      "code": null,
                                      "payload": {
                                        "id": 1,
                                        "userId": 5,
                                        "status": "PENDING",
                                        "totalAmount": 58000,
                                        "shippingAddress": "서울특별시 강남구 테헤란로 123",
                                        "createdAt": "2025-12-13T10:30:00",
                                        "updatedAt": "2025-12-13T10:30:00",
                                        "items": [
                                          {
                                            "id": 1,
                                            "bookId": 1,
                                            "bookTitle": "클린 코드",
                                            "quantity": 2,
                                            "unitPrice": 29000,
                                            "lineTotal": 58000
                                          }
                                        ]
                                      }
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": false,
                                      "message": "인증이 필요합니다.",
                                      "code": "UNAUTHORIZED",
                                      "payload": null
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "관리자 권한 없음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": false,
                                      "message": "접근 권한이 없습니다.",
                                      "code": "FORBIDDEN",
                                      "payload": null
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "주문을 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": false,
                                      "message": "요청한 리소스를 찾을 수 없습니다.",
                                      "code": "RESOURCE_NOT_FOUND",
                                      "payload": null
                                    }
                                    """)
                    )
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResult<OrderResponse>> getOrder(@PathVariable Long id) {
        OrderResponse orderResponse = orderService.getOrderForAdmin(id);
        ApiResult<OrderResponse> apiResult = ApiResult.success(
                orderResponse,
                "주문 상세 조회 성공"
        );
        return ResponseEntity.ok(apiResult);
    }

    @Operation(summary = "주문 상태 변경", description = "관리자가 주문 상태를 변경합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "상태 변경 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": true,
                                      "message": "주문 상태가 성공적으로 변경되었습니다.",
                                      "code": null,
                                      "payload": null
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "유효하지 않은 상태 값",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": false,
                                      "message": "입력값이 유효하지 않습니다.",
                                      "code": "VALIDATION_FAILED",
                                      "payload": null
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": false,
                                      "message": "인증이 필요합니다.",
                                      "code": "UNAUTHORIZED",
                                      "payload": null
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "관리자 권한 없음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": false,
                                      "message": "접근 권한이 없습니다.",
                                      "code": "FORBIDDEN",
                                      "payload": null
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "주문을 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": false,
                                      "message": "요청한 리소스를 찾을 수 없습니다.",
                                      "code": "RESOURCE_NOT_FOUND",
                                      "payload": null
                                    }
                                    """)
                    )
            )
    })
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResult<Void>> updateOrderStatus(
            @PathVariable Long id,
            @Valid @RequestBody OrderStatusUpdateRequest request
    ) {
        orderService.updateOrderStatus(id, request);
        ApiResult<Void> apiResult = ApiResult.successMessage("주문 상태가 성공적으로 변경되었습니다.");
        return ResponseEntity.ok(apiResult);
    }
}
