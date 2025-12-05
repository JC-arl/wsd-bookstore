package com.wsd.bookstoreapi.domain.book.controller;

import com.wsd.bookstoreapi.domain.book.dto.BookCreateRequest;
import com.wsd.bookstoreapi.domain.book.dto.BookResponse;
import com.wsd.bookstoreapi.domain.book.dto.BookUpdateRequest;
import com.wsd.bookstoreapi.domain.book.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 관리자 전용 Book 관리 컨트롤러
 * URL prefix: /api/v1/admin/books
 *
 * SecurityConfig에서 /api/v1/admin/** 에 hasRole("ADMIN")이 걸려 있어
 * ROLE_ADMIN만 접근 가능합니다.
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/admin/books")
public class AdminBookController {

    private final BookService bookService;

    /**
     * 도서 생성
     * POST /api/v1/admin/books
     */
    @PostMapping
    public ResponseEntity<BookResponse> createBook(@Valid @RequestBody BookCreateRequest request) {
        BookResponse response = bookService.createBook(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 도서 수정
     * PATCH /api/v1/admin/books/{id}
     */
    @PatchMapping("/{id}")
    public ResponseEntity<BookResponse> updateBook(
            @PathVariable Long id,
            @Valid @RequestBody BookUpdateRequest request
    ) {
        BookResponse response = bookService.updateBook(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * 도서 삭제
     * DELETE /api/v1/admin/books/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }
}
