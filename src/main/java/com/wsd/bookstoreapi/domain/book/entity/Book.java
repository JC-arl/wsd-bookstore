package com.wsd.bookstoreapi.domain.book.entity;

import com.wsd.bookstoreapi.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(name = "books",
        indexes = {
                @Index(name = "idx_books_title", columnList = "title"),
                @Index(name = "idx_books_category", columnList = "category")
        })
public class Book extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false, length = 255)
    private String author;

    @Column(length = 255)
    private String publisher;

    @Column(name = "isbn", length = 20, unique = true)
    private String isbn;

    @Column(name = "category", length = 100)
    private String category;

    @Column(name = "price", nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Column(name = "stock_quantity", nullable = false)
    private Integer stockQuantity;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "published_at")
    private java.time.LocalDate publishedAt;

    @Column(name = "is_active", nullable = false)
    private boolean active;
}
