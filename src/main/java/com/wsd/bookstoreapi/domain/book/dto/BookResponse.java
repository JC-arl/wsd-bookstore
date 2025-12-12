package com.wsd.bookstoreapi.domain.book.dto;

import com.wsd.bookstoreapi.domain.book.entity.Book;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class BookResponse {

    @Schema(description = "도서 ID", example = "10")
    private Long id;

    @Schema(description = "도서 제목", example = "이펙티브 자바")
    private String title;

    @Schema(description = "저자명", example = "조슈아 블로크")
    private String author;

    @Schema(description = "출판사", example = "인사이트")
    private String publisher;

    @Schema(description = "ISBN 번호", example = "9788966262281")
    private String isbn;

    @Schema(description = "카테고리", example = "PROGRAMMING")
    private String category;

    @Schema(description = "가격(원)", example = "30000")
    private BigDecimal price;

    @Schema(description = "재고", example = "5")
    private Integer stockQuantity;

    @Schema(description = "출간일", example = "2025-12-06")
    private LocalDate publishedAt;

    @Schema(description = "생성 시각", example = "2025-12-06T03:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "수정 시각", example = "2025-12-06T03:10:00")
    private LocalDateTime updatedAt;

    public static BookResponse from(Book book) {
        return BookResponse.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .publisher(book.getPublisher())
                .isbn(book.getIsbn())
                .category(book.getCategory())
                .price(book.getPrice())
                .createdAt(book.getCreatedAt())
                .updatedAt(book.getUpdatedAt())
                .build();
    }
}
