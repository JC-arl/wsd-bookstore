package com.wsd.bookstoreapi.domain.user.controller;

import com.wsd.bookstoreapi.domain.user.dto.UserMeResponse;
import com.wsd.bookstoreapi.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    /**
     * 내 정보 조회
     * GET /api/v1/users/me
     */
    @GetMapping("/me")
    public ResponseEntity<UserMeResponse> getMyProfile() {
        UserMeResponse response = userService.getMyProfile();
        return ResponseEntity.ok(response);
    }

    // 내 정보 수정(PATCH /api/v1/users/me)은 다음 단계에서 추가 가능
}
