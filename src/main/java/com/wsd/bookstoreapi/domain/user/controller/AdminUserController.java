package com.wsd.bookstoreapi.domain.user.controller;

import com.wsd.bookstoreapi.domain.user.dto.AdminUserResponse;
import com.wsd.bookstoreapi.domain.user.service.UserService;
import com.wsd.bookstoreapi.global.api.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Admin - Users", description = "관리자용 회원 관리 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/admin/users")
public class AdminUserController {

    private final UserService userService;

    @Operation(summary = "회원 목록 조회", description = "관리자가 전체 회원 목록을 페이지네이션으로 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "관리자 권한 없음")
    })
    @GetMapping
    public ResponseEntity<ApiResult<Page<AdminUserResponse>>> getUsers(Pageable pageable) {
        Page<AdminUserResponse> page = userService.getUsersForAdmin(pageable);
        ApiResult<Page<AdminUserResponse>> apiResult = ApiResult.success(
                page,
                "회원 목록 조회 성공"
        );
        return ResponseEntity.ok(apiResult);
    }

    @Operation(summary = "회원 상세 조회", description = "관리자가 특정 회원의 상세 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "관리자 권한 없음"),
            @ApiResponse(responseCode = "404", description = "회원을 찾을 수 없음")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResult<AdminUserResponse>> getUser(@PathVariable @Min(1) Long id) {
        AdminUserResponse user = userService.getUserForAdmin(id);
        ApiResult<AdminUserResponse> apiResult = ApiResult.success(
                user,
                "회원 상세 조회 성공"
        );
        return ResponseEntity.ok(apiResult);
    }

    @Operation(summary = "회원 비활성화", description = "특정 회원 계정을 비활성화(정지)합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "비활성화 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "관리자 권한 없음"),
            @ApiResponse(responseCode = "404", description = "회원을 찾을 수 없음")
    })
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ApiResult<Void>> deactivateUser(@PathVariable Long id) {
        userService.deactivateUser(id);
        ApiResult<Void> apiResult = ApiResult.successMessage("회원 계정이 비활성화되었습니다.");
        return ResponseEntity.ok(apiResult);
    }

    @Operation(summary = "회원 재활성화", description = "비활성화된 회원 계정을 다시 활성화합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "재활성화 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "관리자 권한 없음"),
            @ApiResponse(responseCode = "404", description = "회원을 찾을 수 없음")
    })
    @PatchMapping("/{id}/activate")
    public ResponseEntity<ApiResult<Void>> activateUser(@PathVariable Long id) {
        userService.activateUser(id);
        ApiResult<Void> apiResult = ApiResult.successMessage("회원 계정이 활성화되었습니다.");
        return ResponseEntity.ok(apiResult);
    }
}
