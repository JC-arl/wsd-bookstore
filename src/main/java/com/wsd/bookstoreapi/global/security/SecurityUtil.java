package com.wsd.bookstoreapi.global.security;

import com.wsd.bookstoreapi.global.error.BusinessException;
import com.wsd.bookstoreapi.global.error.ErrorCode;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtil {

    private SecurityUtil() {
    }

    public static Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "인증 정보가 없습니다.");
        }

        Object principal = authentication.getPrincipal();

        if (!(principal instanceof UserPrincipal userPrincipal)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "유효하지 않은 인증 정보입니다.");
        }

        return userPrincipal.getUserId();
    }

    public static UserPrincipal getCurrentUserPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "인증 정보가 없습니다.");
        }

        Object principal = authentication.getPrincipal();

        if (!(principal instanceof UserPrincipal userPrincipal)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "유효하지 않은 인증 정보입니다.");
        }

        return userPrincipal;
    }
}
