package com.wsd.bookstoreapi.domain.user.controller;

import com.wsd.bookstoreapi.domain.user.dto.AdminUserResponse;
import com.wsd.bookstoreapi.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/admin/users")
public class AdminUserController {

    private final UserService userService;

    /**
     * 관리자용 - 유저 목록 조회 (페이지네이션)
     * GET /api/v1/admin/users?page=0&size=20&sort=createdAt,desc
     */
    @GetMapping
    public ResponseEntity<Page<AdminUserResponse>> getUsers(Pageable pageable) {
        Page<AdminUserResponse> page = userService.getUsersForAdmin(pageable);
        return ResponseEntity.ok(page);
    }

    /**
     * 관리자용 - 특정 유저 조회
     * GET /api/v1/admin/users/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<AdminUserResponse> getUser(@PathVariable Long id) {
        AdminUserResponse response = userService.getUserByIdForAdmin(id);
        return ResponseEntity.ok(response);
    }

    /**
     * 관리자용 - 유저 비활성화
     * PATCH /api/v1/admin/users/{id}/deactivate
     */
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateUser(@PathVariable Long id) {
        userService.deactivateUser(id);
        return ResponseEntity.noContent().build();
    }
    /**
     * 관리자용 - 유저 활성화
     * PATCH /api/v1/admin/users/{id}/activate
     *
     * 응답: 204 No Content
     */
    @PatchMapping("/{id}/activate")
    public ResponseEntity<Void> activateUser(@PathVariable Long id) {
        userService.activateUser(id);
        return ResponseEntity.noContent().build();
    }

}
