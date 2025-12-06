package com.wsd.bookstoreapi.domain.cart.repository;

import com.wsd.bookstoreapi.domain.cart.entity.Cart;
import com.wsd.bookstoreapi.domain.user.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    // 현재 로그인한 유저의 장바구니 조회
    @EntityGraph(attributePaths = {"user", "items", "items.book"})
    Optional<Cart> findByUser(User user);
}
