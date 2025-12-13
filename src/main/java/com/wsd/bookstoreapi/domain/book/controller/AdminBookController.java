package com.wsd.bookstoreapi.domain.book.controller;

import com.wsd.bookstoreapi.domain.book.dto.BookCreateRequest;
import com.wsd.bookstoreapi.domain.book.dto.BookResponse;
import com.wsd.bookstoreapi.domain.book.dto.BookUpdateRequest;
import com.wsd.bookstoreapi.domain.book.service.BookService;
import com.wsd.bookstoreapi.global.api.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Admin - Books", description = "관리자용 도서 관리 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/admin/books")
public class AdminBookController {

    private final BookService bookService;

    @Operation(summary = "도서 생성", description = "관리자가 새로운 도서를 등록합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "도서 생성 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": true,
                                      "message": "도서가 성공적으로 등록되었습니다.",
                                      "code": null,
                                      "payload": {
                                        "id": 15,
                                        "title": "이펙티브 자바",
                                        "author": "조슈아 블로크",
                                        "publisher": "인사이트",
                                        "isbn": "9788966262281",
                                        "category": "PROGRAMMING",
                                        "price": 36000,
                                        "stockQuantity": 100,
                                        "publishedAt": "2018-11-01",
                                        "createdAt": "2025-12-13T14:30:00",
                                        "updatedAt": "2025-12-13T14:30:00"
                                      }
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "유효하지 않은 요청 데이터",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": false,
                                      "message": "입력값이 유효하지 않습니다.",
                                      "code": "VALIDATION_FAILED",
                                      "payload": null
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": false,
                                      "message": "인증이 필요합니다.",
                                      "code": "UNAUTHORIZED",
                                      "payload": null
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "관리자 권한 없음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": false,
                                      "message": "접근 권한이 없습니다.",
                                      "code": "FORBIDDEN",
                                      "payload": null
                                    }
                                    """)
                    )
            )
    })
    @PostMapping
    public ResponseEntity<ApiResult<BookResponse>> createBook(
            @Valid @RequestBody BookCreateRequest request
    ) {
        BookResponse bookResponse = bookService.createBook(request);

        ApiResult<BookResponse> apiResult = ApiResult.success(
                bookResponse,
                "도서가 성공적으로 등록되었습니다."
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(apiResult);
    }

    @Operation(summary = "도서 수정", description = "관리자가 도서 정보를 부분 수정합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "도서 수정 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": true,
                                      "message": "도서가 성공적으로 수정되었습니다.",
                                      "code": null,
                                      "payload": {
                                        "id": 1,
                                        "title": "클린 코드 (개정판)",
                                        "author": "로버트 C. 마틴",
                                        "publisher": "인사이트",
                                        "isbn": "9788966260959",
                                        "category": "PROGRAMMING",
                                        "price": 33000,
                                        "stockQuantity": 75,
                                        "publishedAt": "2013-12-24",
                                        "createdAt": "2025-12-13T10:00:00",
                                        "updatedAt": "2025-12-13T15:20:00"
                                      }
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "유효하지 않은 요청 데이터",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": false,
                                      "message": "입력값이 유효하지 않습니다.",
                                      "code": "VALIDATION_FAILED",
                                      "payload": null
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": false,
                                      "message": "인증이 필요합니다.",
                                      "code": "UNAUTHORIZED",
                                      "payload": null
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "관리자 권한 없음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": false,
                                      "message": "접근 권한이 없습니다.",
                                      "code": "FORBIDDEN",
                                      "payload": null
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "대상 도서를 찾을 수 없음",
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
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResult<BookResponse>> updateBook(
            @PathVariable Long id,
            @Valid @RequestBody BookUpdateRequest request
    ) {
        BookResponse bookResponse = bookService.updateBook(id, request);

        ApiResult<BookResponse> apiResult = ApiResult.success(
                bookResponse,
                "도서가 성공적으로 수정되었습니다."
        );

        return ResponseEntity.ok(apiResult);
    }

    @Operation(summary = "도서 소프트 삭제", description = "관리자가 도서를 비활성화(소프트 딜리트)합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "도서 삭제(비활성화) 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": true,
                                      "message": "도서가 성공적으로 비활성화되었습니다.",
                                      "code": null,
                                      "payload": null
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": false,
                                      "message": "인증이 필요합니다.",
                                      "code": "UNAUTHORIZED",
                                      "payload": null
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "관리자 권한 없음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": false,
                                      "message": "접근 권한이 없습니다.",
                                      "code": "FORBIDDEN",
                                      "payload": null
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "대상 도서를 찾을 수 없음",
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
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResult<Void>> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);

        ApiResult<Void> apiResult = ApiResult.successMessage("도서가 성공적으로 비활성화되었습니다.");

        return ResponseEntity.ok(apiResult);
    }

    @Operation(summary = "도서 재활성화", description = "비활성화된 도서를 다시 활성 상태로 변경합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "도서 재활성화 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": true,
                                      "message": "도서가 다시 활성화되었습니다.",
                                      "code": null,
                                      "payload": null
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": false,
                                      "message": "인증이 필요합니다.",
                                      "code": "UNAUTHORIZED",
                                      "payload": null
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "관리자 권한 없음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": false,
                                      "message": "접근 권한이 없습니다.",
                                      "code": "FORBIDDEN",
                                      "payload": null
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "대상 도서를 찾을 수 없음",
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
    @PatchMapping("/{id}/activate")
    public ResponseEntity<ApiResult<Void>> activateBook(@PathVariable Long id) {
        bookService.activateBook(id);
        return ResponseEntity.ok(ApiResult.successMessage("도서가 다시 활성화되었습니다."));
    }
}
