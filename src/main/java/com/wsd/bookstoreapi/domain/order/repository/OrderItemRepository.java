package com.wsd.bookstoreapi.domain.order.repository;

import com.wsd.bookstoreapi.domain.book.entity.Book;
import com.wsd.bookstoreapi.domain.order.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    // 해당 도서를 참조하는 주문 항목 모두 삭제
    void deleteByBook(Book book);
}
