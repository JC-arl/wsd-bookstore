package com.wsd.bookstoreapi.domain.book.service;

import com.wsd.bookstoreapi.domain.book.dto.BookCreateRequest;
import com.wsd.bookstoreapi.domain.book.dto.BookResponse;
import com.wsd.bookstoreapi.domain.book.dto.BookUpdateRequest;
import com.wsd.bookstoreapi.domain.book.entity.Book;
import com.wsd.bookstoreapi.domain.book.repository.BookRepository;
import com.wsd.bookstoreapi.global.error.BusinessException;
import com.wsd.bookstoreapi.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;

    /**
     * 관리자용 - 도서 생성
     */
    @Transactional
    public BookResponse createBook(BookCreateRequest request) {
        Book book = Book.builder()
                .title(request.getTitle())
                .author(request.getAuthor())
                .publisher(request.getPublisher())
                .isbn(request.getIsbn())
                .category(request.getCategory())
                .price(request.getPrice())
                .stockQuantity(request.getStockQuantity())
                .description(request.getDescription())
                .publishedAt(request.getPublishedAt())
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
    public void deleteBook(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.RESOURCE_NOT_FOUND,
                        "도서를 찾을 수 없습니다."
                ));

        bookRepository.delete(book);
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
