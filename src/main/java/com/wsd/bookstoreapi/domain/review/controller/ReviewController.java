package com.wsd.bookstoreapi.domain.review.controller;

import com.wsd.bookstoreapi.domain.review.dto.ReviewCreateRequest;
import com.wsd.bookstoreapi.domain.review.dto.ReviewResponse;
import com.wsd.bookstoreapi.domain.review.dto.ReviewUpdateRequest;
import com.wsd.bookstoreapi.domain.review.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class ReviewController {

    private final ReviewService reviewService;

    /**
     * 도서 리뷰 작성
     * POST /api/v1/books/{bookId}/reviews
     */
    @PostMapping("/api/v1/books/{bookId}/reviews")
    public ResponseEntity<ReviewResponse> createReview(
            @PathVariable Long bookId,
            @Valid @RequestBody ReviewCreateRequest request
    ) {
        ReviewResponse response = reviewService.createReview(bookId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * 도서별 리뷰 목록
     * GET /api/v1/books/{bookId}/reviews
     */
    @GetMapping("/api/v1/books/{bookId}/reviews")
    public ResponseEntity<Page<ReviewResponse>> getReviewsByBook(
            @PathVariable Long bookId,
            Pageable pageable
    ) {
        Page<ReviewResponse> page = reviewService.getReviewsByBook(bookId, pageable);
        return ResponseEntity.ok(page);
    }

    /**
     * 내가 쓴 리뷰 목록
     * GET /api/v1/reviews/me
     */
    @GetMapping("/api/v1/reviews/me")
    public ResponseEntity<Page<ReviewResponse>> getMyReviews(Pageable pageable) {
        Page<ReviewResponse> page = reviewService.getMyReviews(pageable);
        return ResponseEntity.ok(page);
    }

    /**
     * 내 리뷰 수정
     * PATCH /api/v1/reviews/{id}
     */
    @PatchMapping("/api/v1/reviews/{id}")
    public ResponseEntity<ReviewResponse> updateMyReview(
            @PathVariable Long id,
            @Valid @RequestBody ReviewUpdateRequest request
    ) {
        ReviewResponse response = reviewService.updateMyReview(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * 내 리뷰 삭제
     * DELETE /api/v1/reviews/{id}
     */
    @DeleteMapping("/api/v1/reviews/{id}")
    public ResponseEntity<Void> deleteMyReview(@PathVariable Long id) {
        reviewService.deleteMyReview(id);
        return ResponseEntity.noContent().build();
    }
}
