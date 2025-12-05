package com.wsd.bookstoreapi.domain.book.controller;

import com.wsd.bookstoreapi.domain.book.dto.BookResponse;
import com.wsd.bookstoreapi.domain.book.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/books")
public class BookController {

    private final BookService bookService;

    /**
     * 도서 목록 조회 (검색/페이지네이션/정렬)
     * 예:
     *  GET /api/v1/books?keyword=java&category=IT&page=0&size=20&sort=createdAt,desc
     */
    @GetMapping
    public ResponseEntity<Page<BookResponse>> getBooks(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            Pageable pageable
    ) {
        Page<BookResponse> page = bookService.searchBooks(keyword, category, pageable);
        return ResponseEntity.ok(page);
    }

    /**
     * 도서 단건 조회
     * GET /api/v1/books/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<BookResponse> getBook(@PathVariable Long id) {
        BookResponse response = bookService.getBook(id);
        return ResponseEntity.ok(response);
    }
}
