package com.wsd.bookstoreapi.domain.review.repository;

import com.wsd.bookstoreapi.domain.book.entity.Book;
import com.wsd.bookstoreapi.domain.review.entity.Review;
import com.wsd.bookstoreapi.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    @EntityGraph(attributePaths = {"user", "book"})
    Page<Review> findByBook(Book book, Pageable pageable);

    @EntityGraph(attributePaths = {"user", "book"})
    Page<Review> findByUser(User user, Pageable pageable);

    @EntityGraph(attributePaths = {"user", "book"})
    Optional<Review> findById(Long id);

    void deleteByBook(Book book);
    Optional<Review> findByUserAndBook(User user, Book book);
}
