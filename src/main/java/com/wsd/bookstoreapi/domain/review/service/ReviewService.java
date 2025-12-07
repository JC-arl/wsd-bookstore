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
        Long userId = SecurityUtil.getCurrentUserId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다."
                ));

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.RESOURCE_NOT_FOUND, "도서를 찾을 수 없습니다."
                ));

        Review review = Review.builder()
                .user(user)
                .book(book)
                .rating(request.getRating())
                .content(request.getContent())
                .build();

        Review saved = reviewRepository.save(review);

        return ReviewResponse.from(saved);
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
    public ReviewResponse updateMyReview(Long reviewId, ReviewUpdateRequest request) {
        User user = getCurrentUser();

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.RESOURCE_NOT_FOUND, "리뷰를 찾을 수 없습니다."));

        if (!review.getUser().getId().equals(user.getId())) {
            throw new BusinessException(
                    ErrorCode.FORBIDDEN, "본인의 리뷰만 수정할 수 있습니다.");
        }

        if (request.getRating() != null) {
            review.setRating(request.getRating());
        }
        if (request.getContent() != null) {
            review.setContent(request.getContent());
        }

        return ReviewResponse.from(review);
    }

    @Transactional
    public void deleteMyReview(Long reviewId) {
        User user = getCurrentUser();

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.RESOURCE_NOT_FOUND, "리뷰를 찾을 수 없습니다."));

        if (!review.getUser().getId().equals(user.getId())) {
            throw new BusinessException(
                    ErrorCode.FORBIDDEN, "본인의 리뷰만 삭제할 수 있습니다.");
        }

        reviewRepository.delete(review);
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
