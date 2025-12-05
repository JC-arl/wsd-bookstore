package com.wsd.bookstoreapi.domain.user.repository;

import com.wsd.bookstoreapi.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    // 만약 관리자 화면에서 유저 + 주문 개수, 리뷰 개수 등을
    // 바로 엔티티로 로딩해서 보여준다면 이런 것도 가능 (선택)
    @EntityGraph(attributePaths = {"orders", "reviews"})
    Page<User> findAll(Pageable pageable);
}
