package com.wsd.bookstoreapi.domain.book.repository;

import com.wsd.bookstoreapi.domain.book.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookRepository extends JpaRepository<Book, Long> {

    /**
     * keyword: title, author, description에 LIKE 검색
     * category: 정확 일치
     *
     * 둘 중 하나 혹은 둘 다 null이면 해당 조건은 무시
     */
    @Query("""
           SELECT b
           FROM Book b
           WHERE (:keyword IS NULL
                  OR LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
                  OR LOWER(b.author) LIKE LOWER(CONCAT('%', :keyword, '%'))
                  OR LOWER(b.description) LIKE LOWER(CONCAT('%', :keyword, '%')))
             AND (:category IS NULL OR b.category = :category)
           """)
    Page<Book> searchBooks(
            String title,
            String category,
            Pageable pageable
    );
}
