//package com.wsd.bookstoreapi.domain.user.controller;
//
//import com.wsd.bookstoreapi.domain.user.dto.UserMeResponse;
//import com.wsd.bookstoreapi.domain.user.dto.UserUpdateRequest;
//import com.wsd.bookstoreapi.domain.user.service.UserService;
//import com.wsd.bookstoreapi.global.api.ApiResult;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import io.swagger.v3.oas.annotations.responses.ApiResponse;
//import io.swagger.v3.oas.annotations.responses.ApiResponses;
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//@Tag(name = "Users - Me", description = "내 정보 조회/수정/삭제 API")
//@RestController
//@RequestMapping("/api/v1/users/me")
//@RequiredArgsConstructor
//public class UserMeController {
//
//    private final UserService userService;
//
//    @Operation(summary = "내 정보 조회", description = "로그인한 사용자의 프로필 정보를 조회합니다.")
//    @ApiResponses({
//            @ApiResponse(responseCode = "200", description = "조회 성공"),
//            @ApiResponse(responseCode = "401", description = "인증 실패")
//    })
//    @GetMapping
//    public ResponseEntity<ApiResult<UserMeResponse>> getMyInfo() {
//        UserMeResponse me = userService.getMyInfo();
//        ApiResult<UserMeResponse> apiResult = ApiResult.success(
//                me,
//                "내 정보 조회 성공"
//        );
//        return ResponseEntity.ok(apiResult);
//    }
//
//    @Operation(summary = "내 정보 수정", description = "로그인한 사용자의 이름 등 프로필을 수정합니다.")
//    @ApiResponses({
//            @ApiResponse(responseCode = "200", description = "수정 성공"),
//            @ApiResponse(responseCode = "401", description = "인증 실패")
//    })
//    @PatchMapping
//    public ResponseEntity<ApiResult<UserMeResponse>> updateMyInfo(
//            @Valid @RequestBody UserUpdateRequest request
//    ) {
//        UserMeResponse me = userService.updateMyInfo(request);
//        ApiResult<UserMeResponse> apiResult = ApiResult.success(
//                me,
//                "내 정보 수정 성공"
//        );
//        return ResponseEntity.ok(apiResult);
//    }
//
//    @Operation(summary = "내 계정 비활성화", description = "계정 상태를 INACTIVE로 변경합니다.")
//    @ApiResponses({
//            @ApiResponse(responseCode = "200", description = "비활성화 성공"),
//            @ApiResponse(responseCode = "401", description = "인증 실패")
//    })
//    @PatchMapping("/deactivate")
//    public ResponseEntity<ApiResult<Void>> deactivateMe() {
//        userService.deactivateMe();
//        ApiResult<Void> apiResult = ApiResult.successMessage("계정이 비활성화되었습니다.");
//        return ResponseEntity.ok(apiResult);
//    }
//
//    @Operation(summary = "내 계정 영구 삭제", description = "계정을 완전히 삭제합니다.")
//    @ApiResponses({
//            @ApiResponse(responseCode = "200", description = "삭제 성공"),
//            @ApiResponse(responseCode = "401", description = "인증 실패")
//    })
//    @DeleteMapping
//    public ResponseEntity<ApiResult<Void>> deleteMe() {
//        userService.deleteMe();
//        ApiResult<Void> apiResult = ApiResult.successMessage("계정이 영구 삭제되었습니다.");
//        return ResponseEntity.ok(apiResult);
//    }
//}
