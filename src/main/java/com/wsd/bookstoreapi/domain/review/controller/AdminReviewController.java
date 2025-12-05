package com.wsd.bookstoreapi.domain.review.controller;

import com.wsd.bookstoreapi.domain.review.dto.ReviewResponse;
import com.wsd.bookstoreapi.domain.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/admin/reviews")
public class AdminReviewController {

    private final ReviewService reviewService;

    /**
     * 전체 리뷰 목록 (관리자)
     */
    @GetMapping
    public ResponseEntity<Page<ReviewResponse>> getReviews(Pageable pageable) {
        Page<ReviewResponse> page = reviewService.getReviewsForAdmin(pageable);
        return ResponseEntity.ok(page);
    }

    /**
     * 리뷰 삭제 (관리자)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
        reviewService.deleteReviewForAdmin(id);
        return ResponseEntity.noContent().build();
    }
}
