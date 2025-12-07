package com.wsd.bookstoreapi.domain.user.entity;

import com.wsd.bookstoreapi.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users",
        indexes = {
                @Index(name = "idx_users_email", columnList = "email", unique = true)
        })
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 로그인용 이메일
    @Column(nullable = false, unique = true, length = 255)
    private String email;

    // 패스워드 (LOCAL 계정에만 사용, OAuth 계정은 null 가능)
    @Column(name = "password", length = 255)
    private String password;

    @Column(nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserRole role;

    @Enumerated(EnumType.STRING)
    @Column(name = "provider", nullable = false, length = 20)
    @Builder.Default
    private AuthProvider provider = AuthProvider.LOCAL;

    // Kakao/Google에서 내려주는 식별자
    @Column(name = "provider_id", length = 100)
    private String providerId;

    // 계정 상태 (ACTIVE, INACTIVE 등)
    @Column(nullable = false, length = 20)
    private String status; // enum으로 빼도 됨

    // 양방향 매핑은 필요할 때만 열어두기 (지금은 최소만)
    @OneToMany(mappedBy = "user")
    @Builder.Default
    private List<com.wsd.bookstoreapi.domain.order.entity.Order> orders = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    @Builder.Default
    private List<com.wsd.bookstoreapi.domain.review.entity.Review> reviews = new ArrayList<>();
}
