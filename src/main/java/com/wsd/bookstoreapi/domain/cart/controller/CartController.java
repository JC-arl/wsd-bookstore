package com.wsd.bookstoreapi.domain.cart.controller;

import com.wsd.bookstoreapi.domain.cart.dto.CartResponse;
import com.wsd.bookstoreapi.domain.cart.service.CartService;
import com.wsd.bookstoreapi.global.api.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
    @GetMapping
    public ResponseEntity<ApiResult<CartResponse>> getMyCart() {
        CartResponse cart = cartService.getMyCart();
        ApiResult<CartResponse> apiResult = ApiResult.success(cart, "장바구니 조회 성공");
        return ResponseEntity.ok(apiResult);
    }

    @Operation(summary = "장바구니 항목 추가", description = "지정한 도서를 장바구니에 추가하거나 수량을 증가시킵니다.")
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
    @PatchMapping("/items/book/{bookId}")
    public ResponseEntity<ApiResult<CartResponse>> updateItemQuantity(
            @PathVariable Long bookId,
            @RequestBody @Valid UpdateCartItemRequest request
    ) {
        CartResponse cart = cartService.updateItemQuantity(bookId, request.getQuantity());
        ApiResult<CartResponse> apiResult = ApiResult.success(cart, "장바구니 항목 수량 변경 성공");
        return ResponseEntity.ok(apiResult);
    }

}
