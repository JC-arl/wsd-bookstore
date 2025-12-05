package com.wsd.bookstoreapi.domain.favorite.repository;

import com.wsd.bookstoreapi.domain.book.entity.Book;
import com.wsd.bookstoreapi.domain.favorite.entity.Favorite;
import com.wsd.bookstoreapi.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    List<Favorite> findByUser(User user);

    Optional<Favorite> findByUserAndBook(User user, Book book);

    void deleteByUserAndBook(User user, Book book);
}
