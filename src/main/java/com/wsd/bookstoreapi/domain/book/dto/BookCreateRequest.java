package com.wsd.bookstoreapi.domain.book.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class BookCreateRequest {

    @NotBlank(message = "제목은 필수입니다.")
    @Size(max = 255, message = "제목은 최대 255자까지 가능합니다.")
    private String title;

    @NotBlank(message = "저자는 필수입니다.")
    @Size(max = 255, message = "저자는 최대 255자까지 가능합니다.")
    private String author;

    @Size(max = 255, message = "출판사는 최대 255자까지 가능합니다.")
    private String publisher;

    @Size(max = 20, message = "ISBN은 최대 20자까지 가능합니다.")
    private String isbn;

    @Size(max = 100, message = "카테고리는 최대 100자까지 가능합니다.")
    private String category;

    @NotNull(message = "가격은 필수입니다.")
    @Min(value = 0, message = "가격은 0 이상이어야 합니다.")
    private BigDecimal price;

    @NotNull(message = "재고 수량은 필수입니다.")
    @Min(value = 0, message = "재고 수량은 0 이상이어야 합니다.")
    private Integer stockQuantity;

    private String description;

    private LocalDate publishedAt;

    // active 여부는 생성 시 기본 true로 둘 것이므로 DTO에서는 생략
}
