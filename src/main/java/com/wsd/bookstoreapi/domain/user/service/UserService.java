package com.wsd.bookstoreapi.domain.user.service;

import com.wsd.bookstoreapi.domain.user.dto.AdminUserResponse;
import com.wsd.bookstoreapi.domain.user.dto.UserMeResponse;
import com.wsd.bookstoreapi.domain.user.dto.UserUpdateRequest;
import com.wsd.bookstoreapi.domain.user.entity.User;
import com.wsd.bookstoreapi.domain.user.repository.UserRepository;
import com.wsd.bookstoreapi.global.error.BusinessException;
import com.wsd.bookstoreapi.global.error.ErrorCode;
import com.wsd.bookstoreapi.global.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    /**
     * 현재 로그인한 사용자 엔티티 조회
     */
    private User getCurrentUser() {
        Long userId = SecurityUtil.getCurrentUserId();
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.USER_NOT_FOUND,
                        "사용자를 찾을 수 없습니다."
                ));
    }

    /**
     * 내 정보 조회
     */
    @Transactional(readOnly = true)
    public UserMeResponse getMyInfo() {
        User user = getCurrentUser();
        return UserMeResponse.from(user);
    }

    /**
     * 내 정보 수정
     */
    @Transactional
    public UserMeResponse updateMyInfo(UserUpdateRequest request) {
        User user = getCurrentUser();
        if (request.getName() != null && !request.getName().isBlank()) {
            user.setName(request.getName());
        }
        // 필요 시 다른 필드도 업데이트
        return UserMeResponse.from(user);
    }

    /**
     * 내 계정 비활성화(소프트 삭제)
     */
    @Transactional
    public void deactivateMe() {
        User user = getCurrentUser();
        user.setStatus("INACTIVE"); // enum이면 UserStatus.INACTIVE 등으로 변경
    }

    /**
     * 내 계정 영구 삭제(하드 삭제)
     */
    @Transactional
    public void deleteMe() {
        User user = getCurrentUser();
        userRepository.delete(user);
    }

    /**
     * 관리자용 - 유저 목록 조회 (페이지네이션)
     */
    @Transactional(readOnly = true)
    public Page<AdminUserResponse> getUsersForAdmin(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(AdminUserResponse::from);
    }

    /**
     * 관리자용 - 특정 유저 상세 조회
     */
    @Transactional(readOnly = true)
    public AdminUserResponse getUserForAdmin(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.USER_NOT_FOUND,
                        "사용자를 찾을 수 없습니다."
                ));

        return AdminUserResponse.from(user);
    }

    /**
     * 관리자용 - 유저 비활성화
     */
    @Transactional
    public void deactivateUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.USER_NOT_FOUND,
                        "사용자를 찾을 수 없습니다."
                ));

        if ("INACTIVE".equalsIgnoreCase(user.getStatus())) {
            throw new BusinessException(
                    ErrorCode.STATE_CONFLICT,
                    "이미 비활성화된 사용자입니다."
            );
        }

        user.setStatus("INACTIVE");
    }

    /**
     * 관리자용 - 유저 활성화
     */
    @Transactional
    public void activateUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.USER_NOT_FOUND,
                        "사용자를 찾을 수 없습니다."
                ));

        if ("ACTIVE".equalsIgnoreCase(user.getStatus())) {
            throw new BusinessException(
                    ErrorCode.STATE_CONFLICT,
                    "이미 활성 상태인 사용자입니다."
            );
        }

        user.setStatus("ACTIVE");
    }
}
