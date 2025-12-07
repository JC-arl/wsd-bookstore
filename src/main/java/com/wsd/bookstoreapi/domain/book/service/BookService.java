package com.wsd.bookstoreapi.domain.book.service;

import com.wsd.bookstoreapi.domain.book.dto.BookCreateRequest;
import com.wsd.bookstoreapi.domain.book.dto.BookResponse;
import com.wsd.bookstoreapi.domain.book.dto.BookUpdateRequest;
import com.wsd.bookstoreapi.domain.book.entity.Book;
import com.wsd.bookstoreapi.domain.book.repository.BookRepository;
import com.wsd.bookstoreapi.domain.cart.repository.CartItemRepository;
import com.wsd.bookstoreapi.domain.favorite.repository.FavoriteRepository;
import com.wsd.bookstoreapi.domain.order.repository.OrderItemRepository;
import com.wsd.bookstoreapi.domain.review.repository.ReviewRepository;
import com.wsd.bookstoreapi.global.error.BusinessException;
import com.wsd.bookstoreapi.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final FavoriteRepository favoriteRepository;
    private final CartItemRepository cartItemRepository;
    private final ReviewRepository reviewRepository;
    private final OrderItemRepository orderItemRepository;

    /**
     * 관리자용 - 도서 생성 (소프트 삭제 고려)
     */
    @Transactional
    public BookResponse createBook(BookCreateRequest request) {

        // 1. ISBN으로 기존 도서가 있는지 먼저 확인
        Optional<Book> optional = bookRepository.findByIsbn(request.getIsbn());

        if (optional.isPresent()) {
            Book existing = optional.get();

            if (existing.is_active()) {
                // 이미 활성된 도서 → 중복 ISBN 에러
                throw new BusinessException(
                        ErrorCode.DUPLICATE_RESOURCE,
                        "이미 존재하는 ISBN입니다: " + request.getIsbn()
                );
            }

            // 2. 비활성 도서가 존재한다면 → "부활 + 정보 업데이트"
            existing.set_active(true);
            existing.setTitle(request.getTitle());
            existing.setAuthor(request.getAuthor());
            existing.setPublisher(request.getPublisher());
            existing.setCategory(request.getCategory());
            existing.setPrice(request.getPrice());
            existing.setPublishedAt(request.getPublishedAt());
            existing.setStockQuantity(request.getStockQuantity());
            existing.setDescription(request.getDescription());

            // JPA dirty checking으로 UPDATE
            return BookResponse.from(existing);
        }

        // 3. 완전히 새로운 도서라면 INSERT
        Book book = Book.builder()
                .title(request.getTitle())
                .author(request.getAuthor())
                .publisher(request.getPublisher())
                .isbn(request.getIsbn())
                .category(request.getCategory())
                .price(request.getPrice())
                .publishedAt(request.getPublishedAt())
                .stockQuantity(request.getStockQuantity())
                .description(request.getDescription())
                .is_active(true)
                .build();

        Book saved = bookRepository.save(book);
        return BookResponse.from(saved);
    }

    /**
     * 단건 조회
     */
    @Transactional(readOnly = true)
    public BookResponse getBook(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.RESOURCE_NOT_FOUND,
                        "도서를 찾을 수 없습니다."
                ));

        return BookResponse.from(book);
    }

    /**
     * 목록 조회 (검색/페이지네이션/정렬)
     * keyword: 제목 LIKE
     * category: 정확 일치
     */
    @Transactional(readOnly = true)
    public Page<BookResponse> getBooks(String keyword, String category, Pageable pageable) {
        // 빈 문자열 → null 처리해서 조건 제거
        if (keyword != null && keyword.isBlank()) {
            keyword = null;
        }
        if (category != null && category.isBlank()) {
            category = null;
        }

        return bookRepository.searchBooks(keyword, category, pageable)
                .map(BookResponse::from);
    }

    /**
     * 관리자용 - 도서 수정 (부분 업데이트)
     */
    @Transactional
    public BookResponse updateBook(Long id, BookUpdateRequest request) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.RESOURCE_NOT_FOUND,
                        "도서를 찾을 수 없습니다."
                ));

        if (request.getTitle() != null) {
            book.setTitle(request.getTitle());
        }
        if (request.getAuthor() != null) {
            book.setAuthor(request.getAuthor());
        }
        if (request.getPublisher() != null) {
            book.setPublisher(request.getPublisher());
        }
        if (request.getIsbn() != null) {
            book.setIsbn(request.getIsbn());
        }
        if (request.getCategory() != null) {
            book.setCategory(request.getCategory());
        }
        if (request.getPrice() != null) {
            book.setPrice(request.getPrice());
        }
        if (request.getStockQuantity() != null) {
            book.setStockQuantity(request.getStockQuantity());
        }
        if (request.getDescription() != null) {
            book.setDescription(request.getDescription());
        }
        if (request.getPublishedAt() != null) {
            book.setPublishedAt(request.getPublishedAt());
        }

        return BookResponse.from(book);
    }

    /**
     * 관리자용 - 도서 삭제 (Soft delete)
     */
    @Transactional
    public void deleteBook(Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.RESOURCE_NOT_FOUND,
                        "삭제할 도서를 찾을 수 없습니다."
                ));

        // 1. 유저 관점에서 더 이상 담기지 않도록 장바구니/찜 제거
        cartItemRepository.deleteByBook(book);
        favoriteRepository.deleteByBook(book);

        // 2. 주문/리뷰는 기록이라서 남겨두는게 자연스러움 (필요시 reviewRepository.deleteByBook(book) 가능)

        // 3. 도서 비활성 처리 (Soft delete)
        book.set_active(false);
    }

    /**
     * 관리자용 - 도서 재활성화
     */
    @Transactional
    public void activateBook(Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.RESOURCE_NOT_FOUND,
                        "도서를 찾을 수 없습니다."
                ));

        if (book.is_active()) {
            throw new BusinessException(
                    ErrorCode.STATE_CONFLICT,
                    "이미 활성 상태입니다."
            );
        }

        book.set_active(true);
    }
}
