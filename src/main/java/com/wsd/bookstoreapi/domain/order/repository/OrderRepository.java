package com.wsd.bookstoreapi.domain.order.repository;

import com.wsd.bookstoreapi.domain.order.entity.Order;
import com.wsd.bookstoreapi.domain.order.entity.OrderStatus;
import com.wsd.bookstoreapi.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @EntityGraph(attributePaths = {"user", "orderItems", "orderItems.book"})
    Page<Order> findByUser(User user, Pageable pageable);

    @EntityGraph(attributePaths = {"user", "orderItems", "orderItems.book"})
    Page<Order> findByUserAndStatus(User user, OrderStatus status, Pageable pageable);

    @EntityGraph(attributePaths = {"user", "orderItems", "orderItems.book"})
    Page<Order> findByStatus(OrderStatus status, Pageable pageable);

    @EntityGraph(attributePaths = {"user", "orderItems", "orderItems.book"})
    Optional<Order> findById(Long id);
}

