package com.wsd.bookstoreapi.domain.book.repository;

import com.wsd.bookstoreapi.domain.book.entity.Book;
import jakarta.validation.constraints.Size;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {

    boolean existsByIsbn(String isbn);

    /**
     * keyword: title, author, description에 LIKE 검색
     * category: 정확 일치
     *
     * 둘 중 하나 혹은 둘 다 null이면 해당 조건은 무시
     */
//    @Query("""
//    SELECT b
//    FROM Book b
//    WHERE (:keyword IS NULL OR LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')))
//      AND (:category IS NULL OR b.category = :category)
//""")

    @Query("""
        SELECT b
        FROM Book b
        WHERE b.active = true
          AND (:keyword IS NULL OR b.title LIKE %:keyword%)
          AND (:category IS NULL OR b.category = :category)
        """)
    Page<Book> searchBooks(
//            @Param("keyword") String keyword,
//            @Param("category") String category,
            String keyword,
            String title,
            String category,
            Pageable pageable
    );

    Optional<Book> findByIsbn(String isbn);
}
