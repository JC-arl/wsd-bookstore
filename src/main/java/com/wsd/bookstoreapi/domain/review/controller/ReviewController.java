package com.wsd.bookstoreapi.domain.review.controller;

import com.wsd.bookstoreapi.domain.review.dto.ReviewCreateRequest;
import com.wsd.bookstoreapi.domain.review.dto.ReviewResponse;
import com.wsd.bookstoreapi.domain.review.dto.ReviewUpdateRequest;
import com.wsd.bookstoreapi.domain.review.service.ReviewService;
import com.wsd.bookstoreapi.global.api.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Reviews", description = "사용자 리뷰 API")
@RequiredArgsConstructor
@RestController
public class ReviewController {

    private final ReviewService reviewService;

    @Operation(summary = "도서 리뷰 작성", description = "특정 도서에 대한 리뷰를 작성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "리뷰 작성 성공"),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 평점/내용"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "도서를 찾을 수 없음")
    })
    @PostMapping("/api/v1/books/{bookId}/reviews")
    public ResponseEntity<ApiResult<ReviewResponse>> createReview(
            @PathVariable Long bookId,
            @RequestBody @Valid ReviewCreateRequest request
    ) {
        ReviewResponse response = reviewService.createReview(bookId, request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResult.success(response, "리뷰 생성 성공"));
    }


    @Operation(summary = "도서별 리뷰 목록 조회", description = "특정 도서에 대한 리뷰 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "도서를 찾을 수 없음")
    })
    @GetMapping("/api/v1/books/{bookId}/reviews")
    public ResponseEntity<ApiResult<Page<ReviewResponse>>> getReviewsByBook(
            @PathVariable Long bookId,
            Pageable pageable
    ) {
        Page<ReviewResponse> page = reviewService.getReviewsByBook(bookId, pageable);
        ApiResult<Page<ReviewResponse>> apiResult = ApiResult.success(
                page,
                "리뷰 목록 조회 성공"
        );
        return ResponseEntity.ok(apiResult);
    }

    @Operation(summary = "내가 쓴 리뷰 목록", description = "로그인한 사용자가 작성한 리뷰 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @GetMapping("/api/v1/reviews/me")
    public ResponseEntity<ApiResult<Page<ReviewResponse>>> getMyReviews(Pageable pageable) {
        Page<ReviewResponse> page = reviewService.getMyReviews(pageable);
        ApiResult<Page<ReviewResponse>> apiResult = ApiResult.success(
                page,
                "내 리뷰 목록 조회 성공"
        );
        return ResponseEntity.ok(apiResult);
    }

    @Operation(summary = "내 리뷰 수정", description = "로그인한 사용자가 특정 도서에 작성한 본인의 리뷰를 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "본인의 리뷰가 아님"),
            @ApiResponse(responseCode = "404", description = "도서 또는 리뷰를 찾을 수 없음")
    })
    @PatchMapping("/api/v1/books/{bookId}/reviews/me")
    public ResponseEntity<ApiResult<ReviewResponse>> updateMyReview(
            @PathVariable Long bookId,
            @Valid @RequestBody ReviewUpdateRequest request
    ) {
        ReviewResponse reviewResponse = reviewService.updateMyReview(bookId, request);
        ApiResult<ReviewResponse> apiResult = ApiResult.success(
                reviewResponse,
                "리뷰가 성공적으로 수정되었습니다."
        );
        return ResponseEntity.ok(apiResult);
    }

    @Operation(summary = "내 리뷰 삭제", description = "로그인한 사용자가 본인의 리뷰를 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "삭제 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "본인의 리뷰가 아님"),
            @ApiResponse(responseCode = "404", description = "리뷰를 찾을 수 없음")
    })
    @DeleteMapping("/api/v1/books/{bookId}/reviews/me")
    public ResponseEntity<ApiResult<Void>> deleteMyReview(@PathVariable Long bookId) {
        reviewService.deleteMyReview(bookId);
        ApiResult<Void> apiResult = ApiResult.successMessage("리뷰가 성공적으로 삭제되었습니다.");
        return ResponseEntity.ok(apiResult);
    }
}
