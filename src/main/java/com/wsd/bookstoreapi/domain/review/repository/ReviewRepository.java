package com.wsd.bookstoreapi.domain.review.repository;

import com.wsd.bookstoreapi.domain.book.entity.Book;
import com.wsd.bookstoreapi.domain.review.entity.Review;
import com.wsd.bookstoreapi.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    Page<Review> findByBook(Book book, Pageable pageable);

    Page<Review> findByUser(User user, Pageable pageable);
}
