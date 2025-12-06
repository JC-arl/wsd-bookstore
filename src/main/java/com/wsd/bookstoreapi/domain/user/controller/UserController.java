package com.wsd.bookstoreapi.domain.user.controller;

import com.wsd.bookstoreapi.domain.user.dto.UserMeResponse;
import com.wsd.bookstoreapi.domain.user.dto.UserUpdateRequest;
import com.wsd.bookstoreapi.domain.user.service.UserService;
import com.wsd.bookstoreapi.global.api.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
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
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
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
            @ApiResponse(responseCode = "200", description = "비활성화 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @PatchMapping("/me/deactivate")
    public ResponseEntity<ApiResult<Void>> deactivateMe() {
        userService.deactivateMe();
        ApiResult<Void> apiResult = ApiResult.successMessage("계정이 비활성화되었습니다.");
        return ResponseEntity.ok(apiResult);
    }

    // 예: 내 계정 영구 삭제
    @Operation(summary = "내 계정 영구 삭제", description = "계정을 완전히 삭제합니다. 복구할 수 없습니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "삭제 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @DeleteMapping("/me")
    public ResponseEntity<ApiResult<Void>> deleteMe() {
        userService.deleteMe();
        ApiResult<Void> apiResult = ApiResult.successMessage("계정이 영구 삭제되었습니다.");
        return ResponseEntity.ok(apiResult);
    }

    // 관리자용 /users (목록, 단건) 등이 같이 있다면, /admin/users와 URL 겹치지 않도록 주의
}
