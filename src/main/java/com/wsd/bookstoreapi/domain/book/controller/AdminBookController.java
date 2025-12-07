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
            @ApiResponse(responseCode = "201", description = "도서 생성 성공"),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 요청 데이터"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "관리자 권한 없음")
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
            @ApiResponse(responseCode = "200", description = "도서 수정 성공"),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 요청 데이터"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "관리자 권한 없음"),
            @ApiResponse(responseCode = "404", description = "대상 도서를 찾을 수 없음")
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
            @ApiResponse(responseCode = "200", description = "도서 삭제(비활성화) 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "관리자 권한 없음"),
            @ApiResponse(responseCode = "404", description = "대상 도서를 찾을 수 없음")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResult<Void>> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);

        ApiResult<Void> apiResult = ApiResult.successMessage("도서가 성공적으로 비활성화되었습니다.");

        return ResponseEntity.ok(apiResult);
    }

    @Operation(summary = "도서 재활성화", description = "비활성화된 도서를 다시 활성 상태로 변경합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "도서 재활성화 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "관리자 권한 없음"),
            @ApiResponse(responseCode = "404", description = "대상 도서를 찾을 수 없음")
    })
    @PatchMapping("/{id}/activate")
    public ResponseEntity<ApiResult<Void>> activateBook(@PathVariable Long id) {
        bookService.activateBook(id);
        return ResponseEntity.ok(ApiResult.successMessage("도서가 다시 활성화되었습니다."));
    }
}
