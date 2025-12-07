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

    // 관리자용 목록 조회: orders, reviews 까지 굳이 한 번에 안 가져와도 됨
    Page<User> findAll(Pageable pageable);
}
