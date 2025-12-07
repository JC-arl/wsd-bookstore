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
     * 관리자용 - 도서 생성
     */
    @Transactional
    public BookResponse createBook(BookCreateRequest request) {

        // 1. ISBN으로 기존 도서가 있는지 먼저 확인
        Optional<Book> optional = bookRepository.findByIsbn(request.getIsbn());

        if (optional.isPresent()) {
            Book existing = optional.get();

            if (existing.isActive()) {
                // 이미 활성된 도서 → 중복 ISBN 에러
                throw new BusinessException(
                        ErrorCode.DUPLICATE_RESOURCE,
                        "이미 존재하는 ISBN입니다: " + request.getIsbn()
                );
            }

            // 2. 비활성 도서가 존재한다면 → "부활 + 정보 업데이트"
            existing.setActive(true);
            existing.setTitle(request.getTitle());
            existing.setAuthor(request.getAuthor());
            existing.setPublisher(request.getPublisher());
            existing.setCategory(request.getCategory());
            existing.setPrice(request.getPrice());
            existing.setPublishedAt(request.getPublishedAt());
            existing.setStockQuantity(request.getStockQuantity());

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
                .active(true)
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
     */
    @Transactional(readOnly = true)
    public Page<BookResponse> searchBooks(String keyword, String category,String title, Pageable pageable) {
        // 빈 문자열은 null로 처리해서 조건 제거
        if (keyword != null && keyword.isBlank()) {
            keyword = null;
        }
        if (category != null && category.isBlank()) {
            category = null;
        }

        return bookRepository.searchBooks(keyword, category, title, pageable)
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

        // active 토글을 추가로 지원하고 싶으면 request에 필드를 추가해서 처리하면 됨

        return BookResponse.from(book);
    }

    /**
     * 관리자용 - 도서 삭제
     */
    @Transactional
    public void deleteBook(Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.RESOURCE_NOT_FOUND,
                        "삭제할 도서를 찾을 수 없습니다."
                ));

//        // 1) 이 책을 참조하는 찜, 장바구니, 리뷰, 주문항목 삭제
//        favoriteRepository.deleteByBook(book);
//        cartItemRepository.deleteByBook(book);
//        reviewRepository.deleteByBook(book);
//        orderItemRepository.deleteByBook(book); // 정말 다 지우고 싶으면 활성화

        // 2) 마지막으로 도서 삭제
        //bookRepository.delete(book);
        // 1. 유저 관점에서 더 이상 담기지 않도록 장바구니/찜만 제거 (선택)
        cartItemRepository.deleteByBook(book);
        favoriteRepository.deleteByBook(book);

        // 2. 주문/리뷰는 기록이라서 남겨두는 게 자연스러움
        //    reviewRepository.deleteByBook(book); // 필요하면만

        // 3. 도서 비활성 처리 (Soft delete)
        book.setActive(false);
    }
    @Transactional
    public void activateBook(Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.RESOURCE_NOT_FOUND,
                        "도서를 찾을 수 없습니다."
                ));

        if (book.isActive()) {
            throw new BusinessException(
                    ErrorCode.STATE_CONFLICT,
                    "이미 활성 상태입니다."
            );
        }

        book.setActive(true);
    }


    /**
     * 키워드/카테고리 기준 도서 검색 + 페이지네이션
     */
    public Page<BookResponse> getBooks(String keyword, String category, String title, Pageable pageable) {

        String keywordFilter = (keyword == null || keyword.isBlank()) ? "" : keyword;
        String categoryFilter = (category == null || category.isBlank()) ? "" : category;

        Page<Book> books = bookRepository
                .searchBooks(
                        keywordFilter,
                        categoryFilter,
                        title,
                        pageable
                );

        return books.map(BookResponse::from);
    }
}
