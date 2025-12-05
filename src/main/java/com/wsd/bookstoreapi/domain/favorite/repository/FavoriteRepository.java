package com.wsd.bookstoreapi.domain.favorite.repository;

import com.wsd.bookstoreapi.domain.book.entity.Book;
import com.wsd.bookstoreapi.domain.favorite.entity.Favorite;
import com.wsd.bookstoreapi.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    // 내 찜 목록 조회 (페이지네이션)
    @EntityGraph(attributePaths = {"user", "book"})
    Page<Favorite> findByUser(User user, Pageable pageable);

    // 특정 도서의 찜 목록 조회 (페이지네이션)
    @EntityGraph(attributePaths = {"user", "book"})
    Page<Favorite> findByBook(Book book, Pageable pageable);

    // 단건 조회 (유저 + 도서)
    @EntityGraph(attributePaths = {"user", "book"})
    Optional<Favorite> findByUserAndBook(User user, Book book);

    // 내 찜 전체 조회 (페이지네이션 없이)
    @EntityGraph(attributePaths = {"user", "book"})
    List<Favorite> findByUser(User user);

    // 찜 해제 (유저 + 도서 기준 삭제)
    void deleteByUserAndBook(User user, Book book);
}
