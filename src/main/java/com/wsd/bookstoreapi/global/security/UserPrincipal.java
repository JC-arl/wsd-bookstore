package com.wsd.bookstoreapi.global.security;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
@AllArgsConstructor
@ToString
public class UserPrincipal implements UserDetails {

    private final Long userId;
    private final String email;
    private final String role; // "ROLE_USER", "ROLE_ADMIN"

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role));
    }

    @Override
    public String getPassword() {
        return null; // JWT 인증 후에는 비밀번호가 필요 없음
    }

    @Override
    public String getUsername() {
        return email != null ? email : String.valueOf(userId);
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // 필요 시 계정 상태 체크 로직 넣을 수 있음
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // 추후 잠금 기능 구현 시 변경
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // 패스워드 만료 정책이 있다면 변경
    }

    @Override
    public boolean isEnabled() {
        return true; // User.status 보고 판단하도록 수정 가능
    }
}
