package com.wsd.bookstoreapi.domain.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderCreateRequest {

    @Schema(description = "수령인 이름", example = "홍길동")
    @NotBlank
    private String receiverName;

    @Schema(description = "배송 주소", example = "서울특별시 강남구 테헤란로 123 101동 101호")
    @NotBlank
    private String address;

    @Schema(description = "요청 사항", example = "부재 시 경비실에 맡겨 주세요.")
    private String memo;

}
