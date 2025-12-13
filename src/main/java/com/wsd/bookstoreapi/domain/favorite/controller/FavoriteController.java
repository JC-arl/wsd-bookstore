package com.wsd.bookstoreapi.domain.favorite.controller;

import com.wsd.bookstoreapi.domain.favorite.dto.FavoriteResponse;
import com.wsd.bookstoreapi.domain.favorite.service.FavoriteService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Favorites", description = "찜(관심 도서) API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;

    @Operation(summary = "내 찜 목록 조회", description = "로그인한 사용자의 찜(관심 도서) 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": true,
                                      "message": "찜 목록 조회 성공",
                                      "code": null,
                                      "payload": {
                                        "content": [
                                          {
                                            "id": 1,
                                            "bookId": 1,
                                            "bookTitle": "클린 코드"
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
            )
    })
    @GetMapping
    public ResponseEntity<ApiResult<Page<FavoriteResponse>>> getMyFavorites(Pageable pageable) {
        Page<FavoriteResponse> page = favoriteService.getMyFavorites(pageable);
        ApiResult<Page<FavoriteResponse>> apiResult = ApiResult.success(
                page,
                "찜 목록 조회 성공"
        );
        return ResponseEntity.ok(apiResult);
    }

    @Operation(summary = "도서 찜 추가", description = "도서를 내 찜 목록에 추가합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "추가 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": true,
                                      "message": "도서가 찜 목록에 추가되었습니다.",
                                      "code": null,
                                      "payload": {
                                        "id": 1,
                                        "bookId": 1,
                                        "bookTitle": "클린 코드"
                                      }
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
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "이미 찜한 도서",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": false,
                                      "message": "이미 존재하는 리소스입니다.",
                                      "code": "DUPLICATE_RESOURCE",
                                      "payload": null
                                    }
                                    """)
                    )
            )
    })
    @PostMapping("/{bookId}")
    public ResponseEntity<ApiResult<FavoriteResponse>> addFavorite(@PathVariable Long bookId) {
        FavoriteResponse response = favoriteService.addFavorite(bookId);
        ApiResult<FavoriteResponse> apiResult = ApiResult.success(response, "도서가 찜 목록에 추가되었습니다.");
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResult);
    }

    @Operation(summary = "도서 찜 해제", description = "도서를 찜 목록에서 제거합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "삭제 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": true,
                                      "message": "도서가 찜 목록에서 제거되었습니다.",
                                      "code": null,
                                      "payload": {
                                        "id": 1,
                                        "bookId": 1,
                                        "bookTitle": "클린 코드"
                                      }
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
    @DeleteMapping("/{bookId}")
    public ResponseEntity<ApiResult<FavoriteResponse>> removeFavorite(@PathVariable Long bookId) {
        FavoriteResponse response = favoriteService.removeFavorite(bookId);
        ApiResult<FavoriteResponse> apiResult = ApiResult.success(response, "도서가 찜 목록에서 제거되었습니다.");
        return ResponseEntity.ok(apiResult);
    }
}
