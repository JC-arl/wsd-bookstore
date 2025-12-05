package com.wsd.bookstoreapi.domain.order.repository;

import com.wsd.bookstoreapi.domain.order.entity.Order;
import com.wsd.bookstoreapi.domain.order.entity.OrderStatus;
import com.wsd.bookstoreapi.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Page<Order> findByUser(User user, Pageable pageable);

    Page<Order> findByUserAndStatus(User user, OrderStatus status, Pageable pageable);

    Page<Order> findByStatus(OrderStatus status, Pageable pageable);
}
