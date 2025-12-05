package com.wsd.bookstoreapi.domain.favorite.repository;

import com.wsd.bookstoreapi.domain.book.entity.Book;
import com.wsd.bookstoreapi.domain.favorite.entity.Favorite;
import com.wsd.bookstoreapi.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    // 내 찜 목록 조회
    @EntityGraph(attributePaths = {"user", "book"})
    Page<Favorite> findByUser(User user, Pageable pageable);

    // 특정 도서의 찜 정보 (예: 통계) 조회
    @EntityGraph(attributePaths = {"user", "book"})
    Page<Favorite> findByBook(Book book, Pageable pageable);

    // 단건 조회
    @EntityGraph(attributePaths = {"user", "book"})
    Optional<Favorite> findByUserAndBook(User user, Book book);
}
