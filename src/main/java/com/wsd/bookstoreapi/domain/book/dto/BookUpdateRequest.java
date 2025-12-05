package com.wsd.bookstoreapi.domain.book.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class BookUpdateRequest {

    @Size(max = 255, message = "제목은 최대 255자까지 가능합니다.")
    private String title;

    @Size(max = 255, message = "저자는 최대 255자까지 가능합니다.")
    private String author;

    @Size(max = 255, message = "출판사는 최대 255자까지 가능합니다.")
    private String publisher;

    @Size(max = 20, message = "ISBN은 최대 20자까지 가능합니다.")
    private String isbn;

    @Size(max = 100, message = "카테고리는 최대 100자까지 가능합니다.")
    private String category;

    @Min(value = 0, message = "가격은 0 이상이어야 합니다.")
    private BigDecimal price;

    @Min(value = 0, message = "재고 수량은 0 이상이어야 합니다.")
    private Integer stockQuantity;

    private String description;

    private LocalDate publishedAt;

    // active 플래그도 여기서 토글할 수 있게 하고 싶으면 Boolean active 필드를 추가해도 됨
}
