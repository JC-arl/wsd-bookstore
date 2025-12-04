package com.wsd.bookstoreapi.domain.test.controller;

import com.wsd.bookstoreapi.global.error.BusinessException;
import com.wsd.bookstoreapi.global.error.ErrorCode;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/test")
public class TestErrorController {

    // 단순 BusinessException 테스트
    @GetMapping("/business-error")
    public void businessError() {
        throw new BusinessException(
                ErrorCode.BAD_REQUEST,
                "테스트용 비즈니스 예외입니다."
        );
    }

    // Validation 에러 테스트용 (추후 DTO/검증 붙일 때 사용 가능)
    // @GetMapping("/validation-error")
    // public SomeResponse validationError(@Valid @RequestBody SomeRequest req) { ... }

}
