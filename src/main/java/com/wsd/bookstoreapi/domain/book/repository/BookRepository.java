package com.wsd.bookstoreapi.domain.book.repository;

import com.wsd.bookstoreapi.domain.book.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {

    boolean existsByIsbn(String isbn);

    /**
     * 활성 도서(is_active = true)만 대상으로
     * - keyword: 제목 LIKE 검색
     * - category: 정확 일치
     * 둘 중 null 이면 해당 조건은 무시
     */
    @Query("""
        SELECT b
        FROM Book b
        WHERE b.is_active = true
          AND (:keyword IS NULL OR LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')))
          AND (:category IS NULL OR b.category = :category)
        """)
    Page<Book> searchBooks(
            @Param("keyword") String keyword,
            @Param("category") String category,
            Pageable pageable
    );

    Optional<Book> findByIsbn(String isbn);
}
