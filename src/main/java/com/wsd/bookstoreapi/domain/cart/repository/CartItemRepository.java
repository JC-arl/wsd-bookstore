package com.wsd.bookstoreapi.domain.cart.repository;

import com.wsd.bookstoreapi.domain.cart.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
}
