package com.wsd.bookstoreapi.domain.favorite.controller;

import com.wsd.bookstoreapi.domain.favorite.dto.FavoriteResponse;
import com.wsd.bookstoreapi.domain.favorite.service.FavoriteService;
import com.wsd.bookstoreapi.global.api.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Favorites", description = "찜(관심 도서) API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;

    @Operation(summary = "내 찜 목록 조회", description = "로그인한 사용자의 찜(관심 도서) 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @GetMapping
    public ResponseEntity<ApiResult<Page<FavoriteResponse>>> getMyFavorites(Pageable pageable) {
        Page<FavoriteResponse> page = favoriteService.getMyFavorites(pageable);
        ApiResult<Page<FavoriteResponse>> apiResult = ApiResult.success(
                page,
                "찜 목록 조회 성공"
        );
        return ResponseEntity.ok(apiResult);
    }

    @Operation(summary = "도서 찜 추가", description = "도서를 내 찜 목록에 추가합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "추가 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "도서를 찾을 수 없음"),
            @ApiResponse(responseCode = "409", description = "이미 찜한 도서")
    })
    @PostMapping("/{bookId}")
    public ResponseEntity<ApiResult<Void>> addFavorite(@PathVariable Long bookId) {
        favoriteService.addFavorite(bookId);
        ApiResult<Void> apiResult = ApiResult.successMessage("도서가 찜 목록에 추가되었습니다.");
        return ResponseEntity.ok(apiResult);
    }

    @Operation(summary = "도서 찜 해제", description = "도서를 찜 목록에서 제거합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "삭제 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "도서를 찾을 수 없음")
    })
    @DeleteMapping("/{bookId}")
    public ResponseEntity<ApiResult<Void>> removeFavorite(@PathVariable Long bookId) {
        favoriteService.removeFavorite(bookId);
        ApiResult<Void> apiResult = ApiResult.successMessage("도서가 찜 목록에서 제거되었습니다.");
        return ResponseEntity.ok(apiResult);
    }
}
