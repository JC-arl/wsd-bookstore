package com.wsd.bookstoreapi.domain.review.service;

import com.wsd.bookstoreapi.domain.book.entity.Book;
import com.wsd.bookstoreapi.domain.book.repository.BookRepository;
import com.wsd.bookstoreapi.domain.review.dto.ReviewCreateRequest;
import com.wsd.bookstoreapi.domain.review.dto.ReviewResponse;
import com.wsd.bookstoreapi.domain.review.dto.ReviewUpdateRequest;
import com.wsd.bookstoreapi.domain.review.entity.Review;
import com.wsd.bookstoreapi.domain.review.repository.ReviewRepository;
import com.wsd.bookstoreapi.domain.user.entity.User;
import com.wsd.bookstoreapi.domain.user.repository.UserRepository;
import com.wsd.bookstoreapi.global.error.BusinessException;
import com.wsd.bookstoreapi.global.error.ErrorCode;
import com.wsd.bookstoreapi.global.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    private User getCurrentUser() {
        Long userId = SecurityUtil.getCurrentUserId();
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다."));
    }

    @Transactional
    public ReviewResponse createReview(Long bookId, ReviewCreateRequest request) {
        // 1) 현재 로그인 사용자 조회
        Long userId = SecurityUtil.getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.USER_NOT_FOUND,
                        "사용자를 찾을 수 없습니다."
                ));

        // 2) 도서 조회
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.RESOURCE_NOT_FOUND,
                        "도서를 찾을 수 없습니다."
                ));

        // 3) 이미 이 유저가 이 도서에 리뷰를 썼는지 확인
        reviewRepository.findByUserAndBook(user, book)
                .ifPresent(r -> {
                    throw new BusinessException(
                            ErrorCode.DUPLICATE_RESOURCE,
                            "이미 이 도서에 리뷰를 작성하셨습니다."
                    );
                });

        // 4) 새 리뷰 생성
        Review review = Review.builder()
                .user(user)
                .book(book)
                .rating(request.getRating())
                .content(request.getContent())   // 필드명 엔티티에 맞게
                .build();

        reviewRepository.save(review);

        return ReviewResponse.from(review);
    }


    @Transactional(readOnly = true)
    public Page<ReviewResponse> getReviewsByBook(Long bookId, Pageable pageable) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.RESOURCE_NOT_FOUND, "도서를 찾을 수 없습니다."));

        return reviewRepository.findByBook(book, pageable)
                .map(ReviewResponse::from);
    }

    @Transactional(readOnly = true)
    public Page<ReviewResponse> getMyReviews(Pageable pageable) {
        User user = getCurrentUser();
        return reviewRepository.findByUser(user, pageable)
                .map(ReviewResponse::from);
    }

    @Transactional
    public ReviewResponse updateMyReview(Long bookId, ReviewUpdateRequest request) {
        User user = getCurrentUser();

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.RESOURCE_NOT_FOUND, "도서를 찾을 수 없습니다."));

        Review review = reviewRepository.findByUserAndBook(user, book)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.RESOURCE_NOT_FOUND,
                        "해당 도서에 대한 리뷰를 찾을 수 없습니다."));

        if (request.getRating() != null) {
            review.setRating(request.getRating());
        }
        if (request.getContent() != null) {
            review.setContent(request.getContent());
        }

        return ReviewResponse.from(review);
    }

    @Transactional
    public ReviewResponse deleteMyReview(Long bookId) {
        User user = getCurrentUser();

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.RESOURCE_NOT_FOUND, "도서를 찾을 수 없습니다."));

        Review review = reviewRepository.findByUserAndBook(user, book)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.RESOURCE_NOT_FOUND,
                        "해당 도서에 대한 리뷰를 찾을 수 없습니다."));

        ReviewResponse response = ReviewResponse.from(review);
        reviewRepository.delete(review);
        return response;
    }

    @Transactional(readOnly = true)
    public Page<ReviewResponse> getReviewsForAdmin(Pageable pageable) {
        return reviewRepository.findAll(pageable)
                .map(ReviewResponse::from);
    }

    @Transactional
    public void deleteReviewForAdmin(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.RESOURCE_NOT_FOUND, "리뷰를 찾을 수 없습니다."));

        reviewRepository.delete(review);
    }
}
