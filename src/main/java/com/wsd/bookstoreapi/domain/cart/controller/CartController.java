package com.wsd.bookstoreapi.domain.cart.controller;

import com.wsd.bookstoreapi.domain.cart.dto.CartItemRequest;
import com.wsd.bookstoreapi.domain.cart.dto.CartResponse;
import com.wsd.bookstoreapi.domain.cart.service.CartService;
import com.wsd.bookstoreapi.global.api.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Cart", description = "장바구니 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/cart")
public class CartController {

    private final CartService cartService;

    @Operation(summary = "내 장바구니 조회", description = "로그인한 사용자의 현재 장바구니 상태를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @GetMapping
    public ResponseEntity<ApiResult<CartResponse>> getMyCart() {
        CartResponse cartResponse = cartService.getMyCart();
        ApiResult<CartResponse> apiResult = ApiResult.success(
                cartResponse,
                "장바구니 조회 성공"
        );
        return ResponseEntity.ok(apiResult);
    }

    @Operation(summary = "장바구니 항목 추가", description = "도서를 장바구니에 추가합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "추가 성공"),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 수량/도서 ID"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "도서를 찾을 수 없음")
    })
    @PostMapping("/items")
    public ResponseEntity<ApiResult<CartResponse>> addItem(
            @Valid @RequestBody CartItemRequest request
    ) {
        CartResponse cartResponse = cartService.addItemToCart(request);
        ApiResult<CartResponse> apiResult = ApiResult.success(
                cartResponse,
                "장바구니에 항목이 추가되었습니다."
        );
        return ResponseEntity.ok(apiResult);
    }

    @Operation(summary = "장바구니 항목 수량 변경", description = "특정 장바구니 항목의 수량을 변경합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 수량"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "장바구니 항목을 찾을 수 없음")
    })
    @PatchMapping("/items/{itemId}")
    public ResponseEntity<ApiResult<CartResponse>> updateItem(
            @PathVariable Long itemId,
            @RequestParam @Min(1) Integer quantity
    ) {
        CartResponse cartResponse = cartService.updateCartItem(itemId, quantity);
        ApiResult<CartResponse> apiResult = ApiResult.success(
                cartResponse,
                "장바구니 항목 수량이 변경되었습니다."
        );
        return ResponseEntity.ok(apiResult);
    }

    @Operation(summary = "장바구니 항목 삭제", description = "특정 장바구니 항목을 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "삭제 후 장바구니 상태 반환"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "장바구니 항목을 찾을 수 없음")
    })
    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<ApiResult<CartResponse>> removeItem(@PathVariable Long itemId) {
        CartResponse cartResponse = cartService.removeCartItem(itemId);
        ApiResult<CartResponse> apiResult = ApiResult.success(
                cartResponse,
                "장바구니 항목이 삭제되었습니다."
        );
        return ResponseEntity.ok(apiResult);
    }

    @Operation(summary = "장바구니 비우기", description = "장바구니의 모든 항목을 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "비우기 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @DeleteMapping
    public ResponseEntity<ApiResult<Void>> clearCart() {
        cartService.clearCart();
        ApiResult<Void> apiResult = ApiResult.successMessage("장바구니가 비워졌습니다.");
        return ResponseEntity.ok(apiResult);
    }
}
