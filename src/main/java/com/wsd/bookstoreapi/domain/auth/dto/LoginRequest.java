package com.wsd.bookstoreapi.domain.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Setter
public class LoginRequest {

    @Schema(description = "로그인 이메일", example = "user1@example.com")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    @NotBlank(message = "이메일은 필수입니다.")
    private String email;

    @Schema(description = "비밀번호", example = "P@ssw0rd!")
    @NotBlank(message = "비밀번호는 필수입니다.")
    private String password;
}
