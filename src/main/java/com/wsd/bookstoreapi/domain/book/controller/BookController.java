package com.wsd.bookstoreapi.domain.book.controller;

import com.wsd.bookstoreapi.domain.book.dto.BookResponse;
import com.wsd.bookstoreapi.domain.book.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@Tag(name = "Books", description = "도서 조회 API")
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
    @Operation(summary = "도서 목록 조회",
            description = "키워드/카테고리/페이지/정렬 조건으로 도서 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 필요")
    })
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
    @Operation(summary = "도서 상세 조회", description = "도서 ID로 단건 도서를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "도서를 찾을 수 없음")
    })
    @GetMapping("/{id}")
    public ResponseEntity<BookResponse> getBook(@PathVariable Long id) {
        BookResponse response = bookService.getBook(id);
        return ResponseEntity.ok(response);
    }
}
