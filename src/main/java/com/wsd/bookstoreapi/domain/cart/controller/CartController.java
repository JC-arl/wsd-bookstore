package com.wsd.bookstoreapi.domain.cart.controller;

import com.wsd.bookstoreapi.domain.cart.dto.CartResponse;
import com.wsd.bookstoreapi.domain.cart.service.CartService;
import com.wsd.bookstoreapi.global.api.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.wsd.bookstoreapi.domain.cart.dto.AddCartItemRequest;
import com.wsd.bookstoreapi.domain.cart.dto.UpdateCartItemRequest;


@Tag(name = "Cart", description = "장바구니 조회/추가/수정/삭제 API")
@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @Operation(summary = "내 장바구니 조회", description = "현재 로그인한 사용자의 장바구니를 조회합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": true,
                                      "message": "장바구니 조회 성공",
                                      "code": null,
                                      "payload": {
                                        "cartId": 1,
                                        "items": [
                                          {
                                            "id": 1,
                                            "bookId": 1,
                                            "title": "클린 코드",
                                            "quantity": 2
                                          }
                                        ],
                                        "totalQuantity": 2,
                                        "totalAmount": 50000
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
    public ResponseEntity<ApiResult<CartResponse>> getMyCart() {
        CartResponse cart = cartService.getMyCart();
        ApiResult<CartResponse> apiResult = ApiResult.success(cart, "장바구니 조회 성공");
        return ResponseEntity.ok(apiResult);
    }

    @Operation(summary = "장바구니 항목 추가", description = "지정한 도서를 장바구니에 추가하거나 수량을 증가시킵니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "추가 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": true,
                                      "message": "장바구니 항목 추가 성공",
                                      "code": null,
                                      "payload": {
                                        "cartId": 1,
                                        "items": [
                                          {
                                            "id": 1,
                                            "bookId": 1,
                                            "title": "클린 코드",
                                            "quantity": 2
                                          }
                                        ],
                                        "totalQuantity": 2,
                                        "totalAmount": 50000
                                      }
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": false,
                                      "message": "입력값 검증에 실패했습니다.",
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
                    responseCode = "404",
                    description = "도서를 찾을 수 없음",
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
    @PostMapping("/items")
    public ResponseEntity<ApiResult<CartResponse>> addItem(
            @RequestBody AddCartItemRequest request
    ) {
        CartResponse cart = cartService.addItem(request.getBookId(), request.getQuantity());
        ApiResult<CartResponse> apiResult = ApiResult.success(cart, "장바구니 항목 추가 성공");

        // 201 Created로 응답
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(apiResult);
    }
    @Operation(summary = "장바구니 항목 수량 변경 (bookId 기준)", description = "도서 ID로 장바구니 항목의 수량을 변경합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "수량 변경 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": true,
                                      "message": "장바구니 항목 수량 변경 성공",
                                      "code": null,
                                      "payload": {
                                        "cartId": 1,
                                        "items": [
                                          {
                                            "id": 1,
                                            "bookId": 1,
                                            "title": "클린 코드",
                                            "quantity": 3
                                          }
                                        ],
                                        "totalQuantity": 3,
                                        "totalAmount": 75000
                                      }
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": false,
                                      "message": "입력값 검증에 실패했습니다.",
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
                    responseCode = "404",
                    description = "항목을 찾을 수 없음",
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
    @PatchMapping("/items/book/{bookId}")
    public ResponseEntity<ApiResult<CartResponse>> updateItemQuantity(
            @PathVariable Long bookId,
            @RequestBody @Valid UpdateCartItemRequest request
    ) {
        CartResponse cart = cartService.updateItemQuantity(bookId, request.getQuantity());
        ApiResult<CartResponse> apiResult = ApiResult.success(cart, "장바구니 항목 수량 변경 성공");
        return ResponseEntity.ok(apiResult);
    }

    @Operation(summary = "장바구니 아이템 삭제", description = "도서 ID로 장바구니에서 특정 아이템을 삭제합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "삭제 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": true,
                                      "message": "장바구니 아이템이 삭제되었습니다.",
                                      "code": null,
                                      "payload": {
                                        "cartId": 1,
                                        "items": [],
                                        "totalQuantity": 0,
                                        "totalAmount": 0
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
                    responseCode = "404",
                    description = "항목을 찾을 수 없음",
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
    @DeleteMapping("/items/book/{bookId}")
    public ResponseEntity<ApiResult<CartResponse>> removeItem(@PathVariable Long bookId) {
        CartResponse cart = cartService.removeItem(bookId);
        ApiResult<CartResponse> apiResult = ApiResult.success(cart, "장바구니 아이템이 삭제되었습니다.");
        return ResponseEntity.ok(apiResult);
    }

    @Operation(summary = "장바구니 전체 비우기", description = "장바구니의 모든 아이템을 삭제합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "전체 비우기 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": true,
                                      "message": "장바구니가 비워졌습니다.",
                                      "code": null,
                                      "payload": {
                                        "cartId": 1,
                                        "items": [],
                                        "totalQuantity": 0,
                                        "totalAmount": 0
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
    @DeleteMapping
    public ResponseEntity<ApiResult<CartResponse>> clearCart() {
        CartResponse cart = cartService.clearCart();
        ApiResult<CartResponse> apiResult = ApiResult.success(cart, "장바구니가 비워졌습니다.");
        return ResponseEntity.ok(apiResult);
    }

}
