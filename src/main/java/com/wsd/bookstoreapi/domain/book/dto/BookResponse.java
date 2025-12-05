package com.wsd.bookstoreapi.domain.book.dto;

import com.wsd.bookstoreapi.domain.book.entity.Book;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class BookResponse {

    private final Long id;
    private final String title;
    private final String author;
    private final String publisher;
    private final String isbn;
    private final String category;
    private final BigDecimal price;
    private final Integer stockQuantity;
    private final String description;
    private final LocalDate publishedAt;
    private final boolean active;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public static BookResponse from(Book book) {
        return BookResponse.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .publisher(book.getPublisher())
                .isbn(book.getIsbn())
                .category(book.getCategory())
                .price(book.getPrice())
                .stockQuantity(book.getStockQuantity())
                .description(book.getDescription())
                .publishedAt(book.getPublishedAt())
                .active(book.isActive())
                .createdAt(book.getCreatedAt())
                .updatedAt(book.getUpdatedAt())
                .build();
    }
}
