package com.wsd.bookstoreapi.domain.book.controller;

import com.wsd.bookstoreapi.domain.book.dto.BookResponse;
import com.wsd.bookstoreapi.domain.book.service.BookService;
import com.wsd.bookstoreapi.global.api.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Books", description = "도서 조회 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/books")
public class BookController {

    private final BookService bookService;

    @Operation(
            summary = "도서 목록 조회",
            description = "키워드/카테고리/페이지/정렬 조건으로 도서 목록을 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": true,
                                      "message": "도서 목록 조회 성공",
                                      "code": null,
                                      "payload": {
                                        "content": [
                                          {
                                            "id": 1,
                                            "title": "클린 코드",
                                            "author": "로버트 C. 마틴",
                                            "publisher": "인사이트",
                                            "isbn": "9788966260959",
                                            "category": "PROGRAMMING",
                                            "price": 29000,
                                            "stockQuantity": 50,
                                            "publishedAt": "2013-12-24",
                                            "createdAt": "2025-12-13T10:00:00",
                                            "updatedAt": "2025-12-13T10:00:00"
                                          }
                                        ],
                                        "pageable": {
                                          "pageNumber": 0,
                                          "pageSize": 10
                                        },
                                        "totalElements": 1,
                                        "totalPages": 1
                                      }
                                    }
                                    """)
                    )
            )
    })
    @GetMapping
    public ResponseEntity<ApiResult<Page<BookResponse>>> getBooks(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            Pageable pageable
    ) {
        Page<BookResponse> page = bookService.getBooks(keyword, category, pageable);

        ApiResult<Page<BookResponse>> apiResult = ApiResult.success(
                page,
                "도서 목록 조회 성공"
        );

        return ResponseEntity.ok(apiResult);
    }

    @Operation(summary = "도서 상세 조회", description = "도서 ID로 단건 도서를 조회합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": true,
                                      "message": "도서 상세 조회 성공",
                                      "code": null,
                                      "payload": {
                                        "id": 1,
                                        "title": "클린 코드",
                                        "author": "로버트 C. 마틴",
                                        "publisher": "인사이트",
                                        "isbn": "9788966260959",
                                        "category": "PROGRAMMING",
                                        "price": 29000,
                                        "stockQuantity": 50,
                                        "publishedAt": "2013-12-24",
                                        "createdAt": "2025-12-13T10:00:00",
                                        "updatedAt": "2025-12-13T10:00:00"
                                      }
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "도서를 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": false,
                                      "message": "요청한 리소스를 찾을 수 없습니다.",
                                      "code": "RESOURCE_NOT_FOUND",
                                      "payload": null
                                    }
                                    """)
                    )
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResult<BookResponse>> getBook(@PathVariable Long id) {
        BookResponse bookResponse = bookService.getBook(id);

        ApiResult<BookResponse> apiResult = ApiResult.success(
                bookResponse,
                "도서 상세 조회 성공"
        );

        return ResponseEntity.ok(apiResult);
    }
}
