package com.wsd.bookstoreapi.domain.user.controller;

import com.wsd.bookstoreapi.domain.user.dto.UserMeResponse;
import com.wsd.bookstoreapi.domain.user.dto.UserUpdateRequest;
import com.wsd.bookstoreapi.domain.user.service.UserService;
import com.wsd.bookstoreapi.global.api.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Users", description = "사용자 정보 조회/수정/삭제 API")
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 예: 내 정보 조회
    @Operation(summary = "내 정보 조회", description = "로그인한 사용자의 프로필 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": true,
                                      "message": "내 정보 조회 성공",
                                      "code": null,
                                      "payload": {
                                        "id": 1,
                                        "email": "user1@example.com",
                                        "name": "홍길동",
                                        "role": "ROLE_USER",
                                        "status": "ACTIVE"
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
    @GetMapping("/me")
    public ResponseEntity<ApiResult<UserMeResponse>> getMyInfo() {
        UserMeResponse me = userService.getMyInfo();
        ApiResult<UserMeResponse> apiResult = ApiResult.success(me, "내 정보 조회 성공");
        return ResponseEntity.ok(apiResult);
    }

    // 예: 내 정보 수정
    @Operation(summary = "내 정보 수정", description = "로그인한 사용자의 이름 등 프로필을 수정합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "수정 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": true,
                                      "message": "내 정보 수정 성공",
                                      "code": null,
                                      "payload": {
                                        "id": 1,
                                        "email": "user1@example.com",
                                        "name": "김철수",
                                        "role": "ROLE_USER",
                                        "status": "ACTIVE"
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
    @PatchMapping("/me")
    public ResponseEntity<ApiResult<UserMeResponse>> updateMyInfo(
            @Valid @RequestBody UserUpdateRequest request
    ) {
        UserMeResponse me = userService.updateMyInfo(request);
        ApiResult<UserMeResponse> apiResult = ApiResult.success(me, "내 정보 수정 성공");
        return ResponseEntity.ok(apiResult);
    }

    // ✅ 여기: 내 계정 소프트 삭제(비활성화)
    @Operation(summary = "내 계정 비활성화(소프트 삭제)", description = "계정 상태를 INACTIVE로 변경하여 비활성화합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "비활성화 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": true,
                                      "message": "계정이 비활성화되었습니다.",
                                      "code": null,
                                      "payload": {
                                        "id": 1,
                                        "email": "user1@example.com",
                                        "name": "홍길동",
                                        "role": "ROLE_USER",
                                        "status": "INACTIVE"
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
    @PatchMapping("/me/deactivate")
    public ResponseEntity<ApiResult<UserMeResponse>> deactivateMe() {
        UserMeResponse response = userService.deactivateMe();
        ApiResult<UserMeResponse> apiResult = ApiResult.success(response, "계정이 비활성화되었습니다.");
        return ResponseEntity.ok(apiResult);
    }

    // 예: 내 계정 영구 삭제
    @Operation(summary = "내 계정 영구 삭제", description = "계정을 완전히 삭제합니다. 복구할 수 없습니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "삭제 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": true,
                                      "message": "계정이 영구 삭제되었습니다.",
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
            )
    })
    @DeleteMapping("/me")
    public ResponseEntity<ApiResult<Void>> deleteMe() {
        userService.deleteMe();
        ApiResult<Void> apiResult = ApiResult.successMessage("계정이 영구 삭제되었습니다.");
        return ResponseEntity.ok(apiResult);
    }
    @Operation(summary = "내 계정 활성화", description = "비활성화된 계정을 다시 활성화합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "활성화 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": true,
                                      "message": "회원 계정이 활성화되었습니다.",
                                      "code": null,
                                      "payload": {
                                        "id": 1,
                                        "email": "user1@example.com",
                                        "name": "홍길동",
                                        "role": "ROLE_USER",
                                        "status": "ACTIVE"
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
                    responseCode = "409",
                    description = "이미 활성화된 계정",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": false,
                                      "message": "이미 활성화된 계정입니다.",
                                      "code": "STATE_CONFLICT",
                                      "payload": null
                                    }
                                    """)
                    )
            )
    })
    @PatchMapping("/me/activate")
    public ResponseEntity<ApiResult<UserMeResponse>> activateMe() {
        UserMeResponse response = userService.activateMe();
        ApiResult<UserMeResponse> apiResult = ApiResult.success(response, "회원 계정이 활성화되었습니다.");
        return ResponseEntity.ok(apiResult);
    }
    // 관리자용 /users (목록, 단건) 등이 같이 있다면, /admin/users와 URL 겹치지 않도록 주의
}
