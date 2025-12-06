package com.wsd.bookstoreapi.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateRequest {

    @Schema(description = "이름", example = "홍길동")
    @Size(min = 1, max = 50)
    private String name;

    // 필요시 닉네임, 전화번호 등 추가
    // @Schema(example = "010-1234-5678")
    // private String phone;
}
