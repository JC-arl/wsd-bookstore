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
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
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
            @ApiResponse(
                    responseCode = "200",
                    description = "주문 생성 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": true,
                                      "message": "주문이 성공적으로 생성되었습니다.",
                                      "code": null,
                                      "payload": {
                                        "id": 1,
                                        "userId": 1,
                                        "status": "PENDING",
                                        "totalAmount": 50000,
                                        "shippingAddress": "서울특별시 강남구",
                                        "createdAt": "2025-12-13T10:00:00",
                                        "updatedAt": "2025-12-13T10:00:00",
                                        "items": [
                                          {
                                            "id": 1,
                                            "bookId": 1,
                                            "bookTitle": "클린 코드",
                                            "quantity": 2,
                                            "unitPrice": 25000,
                                            "lineTotal": 50000
                                          }
                                        ]
                                      }
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "장바구니가 비어 있음 등 잘못된 상태",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": false,
                                      "message": "잘못된 요청입니다.",
                                      "code": "BAD_REQUEST",
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
            )
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
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": true,
                                      "message": "내 주문 목록 조회 성공",
                                      "code": null,
                                      "payload": {
                                        "content": [
                                          {
                                            "id": 1,
                                            "userId": 1,
                                            "status": "PENDING",
                                            "totalAmount": 50000,
                                            "shippingAddress": "서울특별시 강남구",
                                            "createdAt": "2025-12-13T10:00:00",
                                            "updatedAt": "2025-12-13T10:00:00",
                                            "items": [
                                              {
                                                "id": 1,
                                                "bookId": 1,
                                                "bookTitle": "클린 코드",
                                                "quantity": 2,
                                                "unitPrice": 25000,
                                                "lineTotal": 50000
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
            )
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
                                        "userId": 1,
                                        "status": "PENDING",
                                        "totalAmount": 50000,
                                        "shippingAddress": "서울특별시 강남구",
                                        "createdAt": "2025-12-13T10:00:00",
                                        "updatedAt": "2025-12-13T10:00:00",
                                        "items": [
                                          {
                                            "id": 1,
                                            "bookId": 1,
                                            "bookTitle": "클린 코드",
                                            "quantity": 2,
                                            "unitPrice": 25000,
                                            "lineTotal": 50000
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
                    description = "본인의 주문이 아님",
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
            @ApiResponse(
                    responseCode = "200",
                    description = "취소 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": true,
                                      "message": "주문이 성공적으로 취소되었습니다.",
                                      "code": null,
                                      "payload": {
                                        "id": 1,
                                        "userId": 1,
                                        "status": "CANCELLED",
                                        "totalAmount": 50000,
                                        "shippingAddress": "서울특별시 강남구",
                                        "createdAt": "2025-12-13T10:00:00",
                                        "updatedAt": "2025-12-13T10:05:00",
                                        "items": [
                                          {
                                            "id": 1,
                                            "bookId": 1,
                                            "bookTitle": "클린 코드",
                                            "quantity": 2,
                                            "unitPrice": 25000,
                                            "lineTotal": 50000
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
                    description = "본인의 주문이 아님",
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
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "취소할 수 없는 주문 상태",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": false,
                                      "message": "상태 충돌이 발생했습니다.",
                                      "code": "STATE_CONFLICT",
                                      "payload": null
                                    }
                                    """)
                    )
            )
    })
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<ApiResult<OrderResponse>> cancelMyOrder(@PathVariable Long id) {
        OrderResponse response = orderService.cancelMyOrder(id);
        ApiResult<OrderResponse> apiResult = ApiResult.success(response, "주문이 성공적으로 취소되었습니다.");
        return ResponseEntity.ok(apiResult);
    }
}
